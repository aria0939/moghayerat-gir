package com.damdarangha.moghayerat.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.ProductGroup
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val todayJalali: String = "",
    val todayIso: String = "",
    val sterileCount: Int = 0,
    val tubCount: Int = 0,
    val dateCount: Int = 0,
    val todayStatsCount: Int = 0,
    val todayVarianceCount: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val today = JalaliDateConverter.todayJalali()
            val todayIso = JalaliDateConverter.todayIso()

            val sterileCount = app.productRepository.countActiveByGroup(ProductGroup.STERILE.name)
            val tubCount = app.productRepository.countActiveByGroup(ProductGroup.TUB.name)
            val dateCount = app.productRepository.countActiveByGroup(ProductGroup.DATE.name)

            val todayStatsCount = app.dailyStatRepository.countStatsForDate(todayIso)
            val todayVarianceCount = app.dailyStatRepository.countVarianceStatsForDate(todayIso)

            _uiState.update {
                it.copy(
                    todayJalali = today.toFullDisplayString(),
                    todayIso = todayIso,
                    sterileCount = sterileCount,
                    tubCount = tubCount,
                    dateCount = dateCount,
                    todayStatsCount = todayStatsCount,
                    todayVarianceCount = todayVarianceCount,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() = loadInitialData()

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(app) as T
                }
            }
    }
}
