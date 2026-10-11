package com.damdarangha.moghayerat.ui.screen.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val todayStatsCount: Int = 0,
    val isLoading: Boolean = true
)

class ProductListViewModel(
    private val app: MoghayeratApplication,
    private val group: ProductGroup
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _todayStatsCount = MutableStateFlow(0)

    val uiState: StateFlow<ProductListUiState> = combine(
        app.productRepository.observeActiveProductsByGroup(group.name),
        _searchQuery,
        _todayStatsCount
    ) { products, query, statsCount ->
        val filtered = if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
        ProductListUiState(
            products = filtered,
            searchQuery = query,
            todayStatsCount = statsCount,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState()
    )

    init {
        loadTodayStatsCount()
    }

    private fun loadTodayStatsCount() {
        viewModelScope.launch {
            val today = JalaliDateConverter.todayIso()
            _todayStatsCount.value = app.dailyStatRepository.countStatsForDate(today)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    companion object {
        fun factory(app: MoghayeratApplication, group: ProductGroup): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProductListViewModel(app, group) as T
                }
            }
    }
}
