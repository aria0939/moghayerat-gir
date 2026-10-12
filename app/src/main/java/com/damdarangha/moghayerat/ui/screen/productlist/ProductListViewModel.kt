package com.damdarangha.moghayerat.ui.screen.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.StatStatus
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
    val workflowName: String = "",
    val products: List<Product> = emptyList(),
    val statStatuses: Map<Long, StatStatus> = emptyMap(),
    val searchQuery: String = "",
    val todayStatsCount: Int = 0,
    val isLoading: Boolean = true
)

class ProductListViewModel(
    private val app: MoghayeratApplication,
    private val workflowId: Long
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _statStatuses = MutableStateFlow<Map<Long, StatStatus>>(emptyMap())
    private val _workflowName = MutableStateFlow("")

    val uiState: StateFlow<ProductListUiState> = combine(
        app.productRepository.observeActiveProductsByWorkflow(workflowId),
        _searchQuery,
        _statStatuses,
        _workflowName
    ) { products, query, statuses, wfName ->
        val filtered = if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }
        ProductListUiState(
            workflowName = wfName,
            products = filtered,
            statStatuses = statuses,
            searchQuery = query,
            todayStatsCount = statuses.count { it.value != StatStatus.NOT_REGISTERED },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState()
    )

    init {
        loadWorkflowName()
        loadStatStatuses()
    }

    private fun loadWorkflowName() {
        viewModelScope.launch {
            val workflow = app.workflowRepository.getById(workflowId)
            _workflowName.value = workflow?.name ?: ""
        }
    }

    private fun loadStatStatuses() {
        viewModelScope.launch {
            val today = JalaliDateConverter.todayIso()
            val products = app.productRepository.observeActiveProductsByWorkflow(workflowId)
            products.collect { productList ->
                val statuses = mutableMapOf<Long, StatStatus>()
                productList.forEach { product ->
                    val stat = app.dailyStatRepository.getStat(product.id, today)
                    statuses[product.id] = when {
                        stat == null -> StatStatus.NOT_REGISTERED
                        stat.isFinalized -> StatStatus.FINALIZED
                        stat.closingVariance != 0 -> StatStatus.HAS_VARIANCE
                        else -> StatStatus.REGISTERED
                    }
                }
                _statStatuses.value = statuses
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    companion object {
        fun factory(app: MoghayeratApplication, workflowId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProductListViewModel(app, workflowId) as T
                }
            }
    }
}
