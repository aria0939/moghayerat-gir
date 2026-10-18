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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * داده آمار هر محصول برای نمایش در کارت.
 */
data class ProductStatInfo(
    val status: StatStatus,
    val startVariance: Int?,
    val endCumulativeVariance: Int?,
    val nightShiftVariance: Int?
)

data class ProductListUiState(
    val workflowName: String = "",
    val products: List<Product> = emptyList(),
    val statStatuses: Map<Long, StatStatus> = emptyMap(),
    val statInfos: Map<Long, ProductStatInfo> = emptyMap(),
    val searchQuery: String = "",
    val todayStatsCount: Int = 0,
    val startCount: Int = 0,
    val endCount: Int = 0,
    val shiftReviewEnabled: Boolean = false,
    val isLoading: Boolean = true
)

class ProductListViewModel(
    private val app: MoghayeratApplication,
    private val workflowId: Long
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _statInfos = MutableStateFlow<Map<Long, ProductStatInfo>>(emptyMap())
    private val _workflowName = MutableStateFlow("")
    private val _shiftReviewEnabled = MutableStateFlow(false)

    val uiState: StateFlow<ProductListUiState> = combine(
        app.productRepository.observeActiveProductsByWorkflow(workflowId),
        _searchQuery,
        _statInfos,
        _workflowName,
        _shiftReviewEnabled
    ) { products, query, infos, wfName, shiftReview ->
        val filtered = if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }

        val statuses = infos.mapValues { it.value.status }
        // شمارش اول کار: فقط رکوردهایی که واقعاً اول کار ثبت شده (startVariance != null)
        // یا حداقل یکی از فیلدهای اول کار مقادیر غیر صفر دارد
        val startCount = infos.count {
            it.value.startVariance != null
        }
        // شمارش آخر کار: فقط رکوردهایی که currentStock ثبت شده (endCumulativeVariance != null)
        val endCount = infos.count { it.value.endCumulativeVariance != null }

        // اگر بررسی پایان شیفت فعال است، مرتب‌سازی کن
        val sortedProducts = if (shiftReview) {
            filtered.sortedByDescending { product ->
                val info = infos[product.id]
                when {
                    info == null || info.status == StatStatus.NOT_REGISTERED -> 0
                    info.nightShiftVariance != null && info.nightShiftVariance != 0 -> 3
                    info.status == StatStatus.DRAFT -> 2
                    else -> 1
                }
            }
        } else filtered

        ProductListUiState(
            workflowName = wfName,
            products = sortedProducts,
            statStatuses = statuses,
            statInfos = infos,
            searchQuery = query,
            todayStatsCount = startCount,
            startCount = startCount,
            endCount = endCount,
            shiftReviewEnabled = shiftReview,
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
            // استفاده از «تاریخ فعال» — اگر آمار غیر نهایی‌شده وجود دارد،
            // تاریخ آن استفاده می‌شود تا وقتی شیفت از نیمه‌شب عبور می‌کند آمار پاک نشود.
            val activeDate = app.dailyStatRepository.getActiveDate()
            app.dailyStatRepository.observeStatsForDate(activeDate).collect { _ ->
                refreshStatuses(activeDate)
            }
        }
    }

    private suspend fun refreshStatuses(today: String) {
        val products = app.productRepository.observeActiveProductsByWorkflow(workflowId).first()
        val infos = mutableMapOf<Long, ProductStatInfo>()
        products.forEach { product ->
            val stat = app.dailyStatRepository.getStat(product.id, today)
            infos[product.id] = if (stat == null) {
                ProductStatInfo(StatStatus.NOT_REGISTERED, null, null, null)
            } else {
                val hasStartVariance = stat.startVariance != null
                val hasEndVariance = stat.endCumulativeVariance != null
                val hasNightVariance = stat.nightShiftVariance != null
                val status = when {
                    stat.isFinalized -> StatStatus.FINALIZED
                    hasEndVariance && stat.closingVariance != 0 -> StatStatus.HAS_VARIANCE
                    hasStartVariance || hasEndVariance || hasNightVariance -> StatStatus.REGISTERED
                    else -> StatStatus.DRAFT
                }
                ProductStatInfo(
                    status = status,
                    startVariance = stat.startVariance,
                    endCumulativeVariance = stat.endCumulativeVariance,
                    nightShiftVariance = stat.nightShiftVariance
                )
            }
        }
        _statInfos.value = infos
    }

    fun refreshStatStatuses() {
        viewModelScope.launch {
            val activeDate = app.dailyStatRepository.getActiveDate()
            refreshStatuses(activeDate)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShiftReview() {
        _shiftReviewEnabled.value = !_shiftReviewEnabled.value
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
