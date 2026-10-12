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
            val stats = app.dailyStatRepository.getStatsForDate(today)
            val products = app.productRepository.observeActiveProductsByWorkflow(workflow.id)
                .let { flow ->
                    val list = mutableListOf<Product>()
                    flow.collect { productList ->
                        list.clear()
                        list.addAll(productList)
                        return@collect
                    }
                    list
                }

            _uiState.update {
                it.copy(
                    stats = stats.filter { s -> products.any { p -> p.id == s.productId } },
                    products = products,
                    zeroVarianceCount = stats.count { s -> s.closingVariance == 0 },
                    deficitCount = stats.count { s -> s.closingVariance > 0 },
                    surplusCount = stats.count { s -> s.closingVariance < 0 },
                    totalDeficit = stats.filter { s -> s.closingVariance > 0 }.sumOf { it.closingVariance },
                    totalSurplus = stats.filter { s -> s.closingVariance < 0 }.sumOf { -it.closingVariance },
                    isLoading = false
                )
            }
        }
    }

    fun exportToPdf(reportType: ReportType) {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val workflow = state.selectedWorkflow ?: return@launch
                val ctx = app.applicationContext

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
                _uiState.update { it.copy(reportFile = pdfFile, message = "گزارش PDF ساخته شد") }
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
