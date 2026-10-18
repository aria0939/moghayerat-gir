package com.damdarangha.moghayerat.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val workflowRegisteredCounts: Map<Long, Int> = emptyMap(),
    val workflowStartCounts: Map<Long, Int> = emptyMap(),
    val workflowEndCounts: Map<Long, Int> = emptyMap(),
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
            // استفاده از «تاریخ فعال» به‌جای امروز — اگر آمار غیر نهایی‌شده وجود دارد،
            // تاریخ آن نشان داده می‌شود تا وقتی شیفت از نیمه‌شب عبور می‌کند آمار پاک نشود.
            val activeIso = app.dailyStatRepository.getActiveDate()
            val activeJalali = JalaliDateConverter.gregorianToJalaliString(activeIso)
            val activeJalaliDate = run {
                val parts = activeIso.split("-")
                if (parts.size == 3) {
                    JalaliDateConverter.gregorianToJalali(
                        parts[0].toInt(),
                        parts[1].toInt(),
                        parts[2].toInt()
                    )
                } else {
                    today
                }
            }

            // جمع‌آوری گردش کارها
            app.workflowRepository.observeActiveWorkflows().collect { workflows ->
                val workflowStats = mutableListOf<WorkflowWithStats>()
                val registeredCounts = mutableMapOf<Long, Int>()
                val startCounts = mutableMapOf<Long, Int>()
                val endCounts = mutableMapOf<Long, Int>()

                // دریافت تمام آمارهای تاریخ فعال به‌صورت یک‌باره
                val allActiveStats = app.dailyStatRepository.getStatsForDate(activeIso)

                workflows.forEach { workflow ->
                    val total = app.productRepository.countActiveByWorkflow(workflow.id)
                    val workflowProducts = app.productRepository
                        .observeActiveProductsByWorkflow(workflow.id).first()
                    val workflowProductIds = workflowProducts.map { it.id }.toSet()
                    val workflowStatsList = allActiveStats.filter { it.productId in workflowProductIds }

                    val startCount = workflowStatsList.count { it.startVariance != null }
                    val endCount = workflowStatsList.count { it.currentStockTotal != null }
                    val registered = workflowStatsList.size

                    workflowStats.add(
                        WorkflowWithStats(
                            id = workflow.id,
                            name = workflow.name,
                            totalProducts = total,
                            registeredToday = registered,
                            startCount = startCount,
                            endCount = endCount
                        )
                    )
                    registeredCounts[workflow.id] = registered
                    startCounts[workflow.id] = startCount
                    endCounts[workflow.id] = endCount
                }

                _uiState.update {
                    it.copy(
                        todayJalali = activeJalaliDate.toDisplayString(),
                        todayIso = activeIso,
                        workflows = workflowStats,
                        workflowRegisteredCounts = registeredCounts,
                        workflowStartCounts = startCounts,
                        workflowEndCounts = endCounts,
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
