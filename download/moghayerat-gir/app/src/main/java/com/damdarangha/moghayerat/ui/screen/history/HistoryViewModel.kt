package com.damdarangha.moghayerat.ui.screen.history

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

data class HistoryUiState(
    val stats: List<DailyStatEntity> = emptyList(),
    val products: Map<Long, Product> = emptyMap(),
    val isLoading: Boolean = true
)

class HistoryViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val allProducts = app.database.productDao().getAllProducts().map { it.toDomain() }
            val productsMap = allProducts.associateBy { it.id }

            // گرفتن ۳۰ روز اخیر (برای سادگی، همه آمارها را برمی‌گردانیم)
            val today = com.damdarangha.moghayerat.util.JalaliDateConverter.todayIso()
            val thirtyDaysAgo = calculateDateNDaysAgo(today, 30)
            app.dailyStatRepository.observeStatsForDateRange(thirtyDaysAgo, today)
                .collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                            products = productsMap,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun calculateDateNDaysAgo(isoDate: String, days: Int): String {
        try {
            val parts = isoDate.split("-")
            val cal = java.util.Calendar.getInstance()
            cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            cal.add(java.util.Calendar.DAY_OF_MONTH, -days)
            val year = cal.get(java.util.Calendar.YEAR)
            val month = cal.get(java.util.Calendar.MONTH) + 1
            val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
            return "%04d-%02d-%02d".format(year, month, day)
        } catch (_: Exception) {
            return isoDate
        }
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(app) as T
                }
            }
    }
}
