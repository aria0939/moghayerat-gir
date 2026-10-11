package com.damdarangha.moghayerat.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.data.datastore.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoSaveEnabled: Boolean = true,
    val carryOverConfirmed: Boolean = false
)

class SettingsViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            app.settingsDataStore.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            app.settingsDataStore.autoSaveEnabled.collect { enabled ->
                _uiState.update { it.copy(autoSaveEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            app.settingsDataStore.carryOverConfirmed.collect { confirmed ->
                _uiState.update { it.copy(carryOverConfirmed = confirmed) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            app.settingsDataStore.setThemeMode(mode)
        }
    }

    fun setAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            app.settingsDataStore.setAutoSave(enabled)
        }
    }

    fun setCarryOverConfirmed(confirmed: Boolean) {
        viewModelScope.launch {
            app.settingsDataStore.setCarryOverConfirmed(confirmed)
        }
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(app) as T
                }
            }
    }
}
