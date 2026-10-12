package com.damdarangha.moghayerat.ui.screen.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.Workflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                if (state().selectedWorkflow == null && workflows.isNotEmpty()) {
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

    private fun state() = _uiState.value

    fun selectWorkflow(workflow: Workflow) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedWorkflow = workflow, isLoading = true) }
            val today = _uiState.value.isoDate

            // گرفتن محصولات گردش کار انتخاب شده
            val products = mutableListOf<Product>()
            app.productRepository.observeActiveProductsByWorkflow(workflow.id).collect { productList ->
                products.clear()
                products.addAll(productList)
                return@collect
            }
            val productIds = products.map { it.id }.toSet()

            // گرفتن آمار امروز برای محصولات این گردش کار
            val allStats = app.dailyStatRepository.getStatsForDate(today)
            val workflowStats = allStats.filter { it.productId in productIds }

            _uiState.update {
                it.copy(
                    stats = workflowStats,
                    products = products,
                    zeroVarianceCount = workflowStats.count { s -> s.closingVariance == 0 },
                    deficitCount = workflowStats.count { s -> s.closingVariance > 0 },
                    surplusCount = workflowStats.count { s -> s.closingVariance < 0 },
                    totalDeficit = workflowStats.filter { s -> s.closingVariance > 0 }.sumOf { it.closingVariance },
                    totalSurplus = workflowStats.filter { s -> s.closingVariance < 0 }.sumOf { -it.closingVariance },
                    isLoading = false
                )
            }
        }
    }

    /**
     * به‌روزرسانی گزارش وقتی کاربر از صفحه ثبت آمار برمی‌گردد.
     */
    fun refreshReport() {
        val workflow = _uiState.value.selectedWorkflow ?: return
        selectWorkflow(workflow)
    }

    fun exportToPdf(reportType: ReportType) {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val workflow = state.selectedWorkflow
                if (workflow == null) {
                    _uiState.update { it.copy(message = "لطفاً ابتدا یک گردش کار انتخاب کنید") }
                    return@launch
                }
                val ctx = app.applicationContext

                // اگر هیچ محصولی وجود ندارد، هشدار بده
                if (state.products.isEmpty()) {
                    _uiState.update { it.copy(message = "هیچ محصولی در این گردش کار وجود ندارد") }
                    return@launch
                }

                // فیلتر محصولات بر اساس نوع گزارش
                val (statsToExport, productsToExport) = when (reportType) {
                    ReportType.FULL -> state.stats to state.products
                    ReportType.REGISTERED_ONLY -> {
                        val filtered = state.stats.filter { s -> s.closingVariance == 0 }
                        filtered to state.products.filter { p -> filtered.any { it.productId == p.id } }
                    }
                    ReportType.VARIANCE_ONLY -> {
                        val filtered = state.stats.filter { s -> s.closingVariance != 0 }
                        filtered to state.products.filter { p -> filtered.any { it.productId == p.id } }
                    }
                }

                val pdfFile = ReportExporter.exportWorkflowReportToPdf(
                    ctx, workflow.name, statsToExport, productsToExport, state.isoDate
                )
                _uiState.update {
                    it.copy(
                        reportFile = pdfFile,
                        message = "گزارش PDF ساخته شد: ${pdfFile.name}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "خطا در ساخت PDF: ${e.message}") }
            }
        }
    }

    fun shareReport() {
        val file = _uiState.value.reportFile ?: return
        try {
            val ctx = app.applicationContext
            ReportExporter.shareFile(ctx, file, "application/pdf")
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "خطا در اشتراک‌گذاری: ${e.message}") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
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
