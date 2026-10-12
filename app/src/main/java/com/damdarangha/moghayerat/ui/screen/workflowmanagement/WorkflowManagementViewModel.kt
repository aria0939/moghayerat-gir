package com.damdarangha.moghayerat.ui.screen.workflowmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.Workflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkflowManagementUiState(
    val workflows: List<Workflow> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingWorkflow: Workflow? = null,
    val message: String? = null
)

class WorkflowManagementViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    private val _editingWorkflow = MutableStateFlow<Workflow?>(null)
    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WorkflowManagementUiState> = combine(
        app.workflowRepository.observeAllWorkflows(),
        _showAddDialog,
        _editingWorkflow,
        _message
    ) { workflows, showAdd, editing, msg ->
        WorkflowManagementUiState(
            workflows = workflows,
            showAddDialog = showAdd,
            editingWorkflow = editing,
            message = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkflowManagementUiState()
    )

    fun showAddDialog() {
        _showAddDialog.value = true
        _editingWorkflow.value = null
    }

    fun showEditDialog(workflow: Workflow) {
        _editingWorkflow.value = workflow
        _showAddDialog.value = true
    }

    fun hideDialog() {
        _showAddDialog.value = false
        _editingWorkflow.value = null
    }

    fun addOrUpdateWorkflow(name: String, displayOrder: Int) {
        viewModelScope.launch {
            try {
                val editing = _editingWorkflow.value
                if (editing != null) {
                    val updated = editing.copy(name = name, displayOrder = displayOrder)
                    app.workflowRepository.updateWorkflow(updated)
                    _message.value = "گردش کار به‌روزرسانی شد"
                } else {
                    val newWorkflow = Workflow(
                        name = name,
                        displayOrder = displayOrder
                    )
                    app.workflowRepository.addWorkflow(newWorkflow)
                    _message.value = "گردش کار جدید اضافه شد"
                }
                hideDialog()
            } catch (e: Exception) {
                _message.value = "خطا: ${e.message}"
            }
        }
    }

    fun toggleActive(workflow: Workflow) {
        viewModelScope.launch {
            if (workflow.isActive) {
                app.workflowRepository.deactivate(workflow.id)
                _message.value = "گردش کار غیرفعال شد"
            } else {
                app.workflowRepository.activate(workflow.id)
                _message.value = "گردش کار فعال شد"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WorkflowManagementViewModel(app) as T
                }
            }
    }
}
