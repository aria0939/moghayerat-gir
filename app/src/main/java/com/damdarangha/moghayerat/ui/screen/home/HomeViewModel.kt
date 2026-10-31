package com.damdarangha.moghayerat.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkflowWithStats(
    val id: Long,
    val name: String,
    val totalProducts: Int,
    val registeredToday: Int,
    val startCount: Int = 0,
    val endCount: Int = 0
)

data class HomeUiState(
    val todayJalali: String = "",
    val todayIso: String = "",
    val workflows: List<WorkflowWithStats> = emptyList(),
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
            // استفاده از «تاریخ فعال» — اگر آمار غیر نهایی‌شده وجود دارد،
            // تاریخ آن استفاده می‌شود تا آمار پس از نیمه‌شب پاک نشود.
            val activeIso = app.dailyStatRepository.getActiveDate()
            val activeJalali = run {
                val parts = activeIso.split("-")
                if (parts.size == 3) {
                    try {
                        JalaliDateConverter.gregorianToJalali(
                            parts[0].toInt(), parts[1].toInt(), parts[2].toInt()
                        )
                    } catch (_: Exception) {
                        JalaliDateConverter.todayJalali()
                    }
                } else {
                    JalaliDateConverter.todayJalali()
                }
            }

            // ترکیب دو flow: گردش‌کارها + آمارهای تاریخ فعال (ری‌اکتیو)
            combine(
                app.workflowRepository.observeActiveWorkflows(),
                app.dailyStatRepository.observeStatsForDate(activeIso)
            ) { workflows, stats ->
                val statsByProduct = stats.associateBy { it.productId }
                val workflowStats = workflows.map { workflow ->
                    // بهینه‌سازی: یک‌بار محصولات هر گردش‌کار را بگیر
                    val products = app.productRepository
                        .observeActiveProductsByWorkflow(workflow.id).first()
                    val workflowProductIds = products.map { it.id }.toSet()
                    val workflowStatsList = stats.filter { it.productId in workflowProductIds }

                    WorkflowWithStats(
                        id = workflow.id,
                        name = workflow.name,
                        totalProducts = products.size,
                        registeredToday = workflowStatsList.size,
                        startCount = workflowStatsList.count { it.startVariance != null },
                        endCount = workflowStatsList.count { it.currentStockTotal != null }
                    )
                }
                workflowStats
            }.collect { workflowStats ->
                _uiState.update {
                    it.copy(
                        todayJalali = activeJalali.toDisplayString(),
                        todayIso = activeIso,
                        workflows = workflowStats,
                        isLoading = false
                    )
                }
            }
        }
    }

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
