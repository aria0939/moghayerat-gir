package com.damdarangha.moghayerat.ui.screen.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsUiState(
    val isoDate: String = "",
    val jalaliDate: String = "",
    val stats: List<DailyStatEntity> = emptyList(),
    val products: Map<Long, Product> = emptyMap(),
    val zeroVarianceCount: Int = 0,
    val deficitCount: Int = 0,
    val surplusCount: Int = 0,
    val totalDeficit: Int = 0,
    val totalSurplus: Int = 0,
    val isLoading: Boolean = true,
    val filterType: ReportFilter = ReportFilter.ALL,
    val reportFile: java.io.File? = null,
    val message: String? = null
)

enum class ReportFilter(val label: String) {
    ALL("همه"),
    ZERO("مغایرت صفر"),
    DEFICIT("کسری"),
    SURPLUS("اضافه")
}

class ReportsViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports(com.damdarangha.moghayerat.util.JalaliDateConverter.todayIso())
    }

    fun loadReports(isoDate: String) {
        viewModelScope.launch {
            val jalaliDate = com.damdarangha.moghayerat.util.JalaliDateConverter.gregorianToJalaliString(isoDate)
            val stats = app.dailyStatRepository.getStatsForDate(isoDate)
            val productsList = app.productRepository.observeAllProducts()
            // برای سادگی، از DAO مستقیم استفاده می‌کنیم
            val allProducts = app.database.productDao().getAllProducts().map { it.toDomain() }
            val productsMap = allProducts.associateBy { it.id }

            _uiState.update {
                it.copy(
                    isoDate = isoDate,
                    jalaliDate = jalaliDate,
                    stats = stats,
                    products = productsMap,
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

    fun setFilter(filter: ReportFilter) {
        _uiState.update { it.copy(filterType = filter) }
    }

    fun exportToPdf() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val ctx = app.applicationContext
                val productsList = state.products.values.toList()
                val pdfFile = ReportExporter.exportDailyReportToPdf(
                    ctx, state.stats, productsList, state.isoDate
                )
                _uiState.update { it.copy(reportFile = pdfFile, message = "گزارش PDF ساخته شد") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "خطا در ساخت PDF: ${e.message}") }
            }
        }
    }

    fun exportToCsv() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val ctx = app.applicationContext
                val productsList = state.products.values.toList()
                val csvFile = ReportExporter.exportDailyReportToCsv(
                    ctx, state.stats, productsList, state.isoDate
                )
                _uiState.update { it.copy(reportFile = csvFile, message = "گزارش CSV ساخته شد") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "خطا در ساخت CSV: ${e.message}") }
            }
        }
    }

    fun shareReport() {
        val file = _uiState.value.reportFile ?: return
        try {
            val ctx = app.applicationContext
            val mimeType = if (file.name.endsWith(".pdf")) "application/pdf" else "text/csv"
            ReportExporter.shareFile(ctx, file, mimeType)
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
