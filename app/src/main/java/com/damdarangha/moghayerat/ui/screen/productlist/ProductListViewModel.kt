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
        // شمارش اول کار: رکوردهایی که حداقل یکی از فیلدهای اول کار دارند
        val startCount = infos.count {
            it.value.startVariance != null || it.value.status != StatStatus.NOT_REGISTERED
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
            val today = JalaliDateConverter.todayIso()
            app.dailyStatRepository.observeStatsForDate(today).collect { _ ->
                refreshStatuses(today)
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
                // تشخیص وضعیت بر اساس داده‌های واقعی ثبت‌شده
                val hasStartVariance = stat.startVariance != null
                val hasEndVariance = stat.endCumulativeVariance != null
                val hasNightVariance = stat.nightShiftVariance != null
                val status = when {
                    stat.isFinalized -> StatStatus.FINALIZED
                    // اگر آخر کار ثبت شده و مغایرت غیر صفر دارد
                    hasEndVariance && stat.closingVariance != 0 -> StatStatus.HAS_VARIANCE
                    // اگر اول کار یا آخر کار ثبت شده ولی هنوز نهایی نشده
                    hasStartVariance || hasEndVariance || hasNightVariance -> StatStatus.REGISTERED
                    // اگر رکورد هست اما هیچ واریانسی ثبت نشده (مثلاً فقط مقادیر صفر)
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
            val today = JalaliDateConverter.todayIso()
            refreshStatuses(today)
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
