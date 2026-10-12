package com.damdarangha.moghayerat.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.util.JalaliDateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkflowWithStats(
    val id: Long,
    val name: String,
    val totalProducts: Int,
    val registeredToday: Int
)

data class HomeUiState(
    val todayJalali: String = "",
    val todayIso: String = "",
    val workflows: List<WorkflowWithStats> = emptyList(),
    val workflowRegisteredCounts: Map<Long, Int> = emptyMap(),
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

            // جمع‌آوری گردش کارها
            app.workflowRepository.observeActiveWorkflows().collect { workflows ->
                val workflowStats = mutableListOf<WorkflowWithStats>()
                val registeredCounts = mutableMapOf<Long, Int>()

                workflows.forEach { workflow ->
                    val total = app.productRepository.countActiveByWorkflow(workflow.id)
                    val registered = app.dailyStatRepository.countStatsForDate(todayIso)
                    workflowStats.add(
                        WorkflowWithStats(
                            id = workflow.id,
                            name = workflow.name,
                            totalProducts = total,
                            registeredToday = registered
                        )
                    )
                    registeredCounts[workflow.id] = registered
                }

                _uiState.update {
                    it.copy(
                        todayJalali = today.toFullDisplayString(),
                        todayIso = todayIso,
                        workflows = workflowStats,
                        workflowRegisteredCounts = registeredCounts,
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
