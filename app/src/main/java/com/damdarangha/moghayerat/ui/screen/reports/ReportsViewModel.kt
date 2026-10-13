package com.damdarangha.moghayerat.ui.screen.reports

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.Workflow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ReportsUiState(
    val workflows: List<Workflow> = emptyList(),
    val selectedWorkflow: Workflow? = null,
    val isoDate: String = "",
    val jalaliDate: String = "",
    val stats: List<DailyStatEntity> = emptyList(),
    val products: List<Product> = emptyList(),
    val zeroVarianceCount: Int = 0,
    val deficitCount: Int = 0,
    val surplusCount: Int = 0,
    val totalDeficit: Int = 0,
    val totalSurplus: Int = 0,
    val isLoading: Boolean = true,
    val isExporting: Boolean = false,
    val reportFile: File? = null,
    val message: String? = null
)

class ReportsViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadWorkflows()
        loadToday()
    }

    private fun loadWorkflows() {
        viewModelScope.launch {
            app.workflowRepository.observeActiveWorkflows().collect { workflows ->
                _uiState.update { it.copy(workflows = workflows) }
                if (_uiState.value.selectedWorkflow == null && workflows.isNotEmpty()) {
                    selectWorkflow(workflows.first())
                }
            }
        }
    }

    private fun loadToday() {
        val today = com.damdarangha.moghayerat.util.JalaliDateConverter.todayIso()
        val jalali = com.damdarangha.moghayerat.util.JalaliDateConverter.gregorianToJalaliString(today)
        _uiState.update { it.copy(isoDate = today, jalaliDate = jalali) }
    }

    /**
     * انتخاب گردش کار و بارگذاری آمار آن.
     * reportFile قبلی را پاک می‌کند تا فایل نامرتبط نمایش داده نشود.
     */
    fun selectWorkflow(workflow: Workflow) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedWorkflow = workflow,
                    isLoading = true,
                    reportFile = null  // پاک‌کردن فایل قبلی
                )
            }
            val today = _uiState.value.isoDate

            val products = app.productRepository.observeActiveProductsByWorkflow(workflow.id).first()
            val productIds = products.map { it.id }.toSet()

            val allStats = app.dailyStatRepository.getStatsForDate(today)
            val workflowStats = allStats.filter { it.productId in productIds }

            _uiState.update {
                // فیلتر کردن: فقط رکوردهایی که آخر کار ثبت شده (currentStockTotal != null)
                // در محاسبات مغایرت روزانه شرکت می‌کنند تا آمار ثبت‌نشده به‌اشتباه
                // «مغایرت صفر» محسوب نشوند.
                val registeredStats = workflowStats.filter { s -> s.currentStockTotal != null }
                it.copy(
                    stats = workflowStats,
                    products = products,
                    zeroVarianceCount = registeredStats.count { s -> s.closingVariance == 0 },
                    deficitCount = registeredStats.count { s -> s.closingVariance > 0 },
                    surplusCount = registeredStats.count { s -> s.closingVariance < 0 },
                    totalDeficit = registeredStats.filter { s -> s.closingVariance > 0 }.sumOf { it.closingVariance },
                    totalSurplus = registeredStats.filter { s -> s.closingVariance < 0 }.sumOf { -it.closingVariance },
                    isLoading = false
                )
            }
        }
    }

    fun refreshReport() {
        val workflow = _uiState.value.selectedWorkflow ?: return
        selectWorkflow(workflow)
    }

    /**
     * تولید PDF در Dispatchers.IO.
     * isExporting state برای جلوگیری از تولید هم‌زمان.
     */
    fun exportToPdf(reportType: ReportType) {
        // جلوگیری از تولید هم‌زمان
        if (_uiState.value.isExporting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            try {
                val state = _uiState.value
                val workflow = state.selectedWorkflow
                if (workflow == null) {
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            message = "لطفاً ابتدا یک گردش کار انتخاب کنید"
                        )
                    }
                    return@launch
                }

                val ctx = app.applicationContext

                // خواندن داده‌های تازه از دیتابیس در IO
                val (statsToExport, productsToExport) = withContext(Dispatchers.IO) {
                    val products = app.productRepository.observeActiveProductsByWorkflow(workflow.id).first()
                    if (products.isEmpty()) {
                        null to emptyList<Product>()
                    } else {
                        val productIds = products.map { it.id }.toSet()
                        val allStats = app.dailyStatRepository.getStatsForDate(state.isoDate)
                        val workflowStats = allStats.filter { it.productId in productIds }

                        // فیلتر بر اساس نوع گزارش
                        val (filteredStats, filteredProducts) = when (reportType) {
                            ReportType.FULL -> workflowStats to products
                            ReportType.REGISTERED_ONLY -> {
                                // فقط رکوردهایی که آخر کار واقعاً ثبت شده (currentStockTotal != null)
                                val registered = workflowStats.filter { s -> s.currentStockTotal != null }
                                registered to products.filter { p ->
                                    registered.any { it.productId == p.id }
                                }
                            }
                            ReportType.VARIANCE_ONLY -> {
                                // فقط رکوردهایی که آخر کار ثبت شده و مغایرت غیر صفر دارند
                                val filtered = workflowStats.filter { s ->
                                    s.currentStockTotal != null && s.closingVariance != 0
                                }
                                filtered to products.filter { p ->
                                    filtered.any { it.productId == p.id }
                                }
                            }
                        }
                        filteredStats to filteredProducts
                    }
                }

                if (productsToExport.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            message = "هیچ محصولی در این گردش کار وجود ندارد"
                        )
                    }
                    return@launch
                }

                // تولید PDF در IO
                val pdfFile = withContext(Dispatchers.IO) {
                    ReportExporter.exportWorkflowReportToPdf(
                        ctx, workflow.name, statsToExport!!, productsToExport, state.isoDate
                    )
                }

                _uiState.update {
                    it.copy(
                        isExporting = false,
                        reportFile = pdfFile,
                        message = "گزارش PDF ساخته شد: ${pdfFile.name}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        message = "خطا در ساخت PDF: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * ذخیره دائمی فایل PDF در مقصد انتخاب‌شده.
     */
    fun savePdfToDestination(destinationUri: Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val sourceFile = _uiState.value.reportFile
                if (sourceFile == null || !ReportExporter.validateFile(sourceFile)) {
                    onResult(false, "فایل PDF معتبر نیست. ابتدا گزارش را تولید کنید.")
                    return@launch
                }
                val ctx = app.applicationContext
                val success = withContext(Dispatchers.IO) {
                    ReportExporter.copyToDestination(ctx, sourceFile, destinationUri)
                }
                if (success) {
                    onResult(true, "فایل PDF با موفقیت در محل انتخاب‌شده ذخیره شد")
                } else {
                    onResult(false, "خطا در کپی فایل به مقصد")
                }
            } catch (e: Exception) {
                onResult(false, "خطا در ذخیره فایل: ${e.message}")
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    /**
     * نمایش پیام خطا از UI layer.
     */
    fun showError(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReportsViewModel(app) as T
                }
            }
    }
}

enum class ReportType(val label: String) {
    FULL("گزارش کامل گردش کار"),
    REGISTERED_ONLY("فقط محصولات ثبت‌شده"),
    VARIANCE_ONLY("فقط محصولات دارای مغایرت")
}
