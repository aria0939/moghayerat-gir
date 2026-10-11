package com.damdarangha.moghayerat.ui.screen.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.backup.RestoreResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupUiState(
    val backups: List<File> = emptyList(),
    val isWorking: Boolean = false,
    val message: String? = null,
    val pendingRestore: File? = null,
    val lastRestoreResult: RestoreResult? = null
)

class BackupViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        refreshBackups()
    }

    fun refreshBackups() {
        val backups = app.backupManager.listBackups()
        _uiState.update { it.copy(backups = backups) }
    }

    fun createBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true) }
            try {
                val file = app.backupManager.createBackup()
                _uiState.update {
                    it.copy(
                        isWorking = false,
                        message = "پشتیبان ساخته شد: ${file.name}"
                    )
                }
                refreshBackups()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isWorking = false,
                        message = "خطا در ساخت پشتیبان: ${e.message}"
                    )
                }
            }
        }
    }

    fun confirmRestore(file: File) {
        _uiState.update { it.copy(pendingRestore = file) }
    }

    fun cancelRestore() {
        _uiState.update { it.copy(pendingRestore = null) }
    }

    fun executeRestore() {
        val file = _uiState.value.pendingRestore ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, pendingRestore = null) }
            try {
                val result = app.backupManager.restoreBackup(file, confirm = true)
                _uiState.update {
                    it.copy(
                        isWorking = false,
                        lastRestoreResult = result,
                        message = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isWorking = false,
                        message = "خطا در بازیابی: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteBackup(file: File) {
        if (app.backupManager.deleteBackup(file)) {
            _uiState.update { it.copy(message = "فایل پشتیبان حذف شد") }
            refreshBackups()
        }
    }

    fun shareBackup(file: File) {
        try {
            com.damdarangha.moghayerat.ui.screen.reports.ReportExporter.shareFile(
                app.applicationContext, file, "application/json"
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "خطا در اشتراک‌گذاری: ${e.message}") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BackupViewModel(app) as T
                }
            }
    }
}
