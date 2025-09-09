package com.chikivision.vizovpn.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chikivision.vizovpn.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// This data class will hold the state for our UI
data class SettingsUiState(
    val isKillSwitchEnabled: Boolean = false
)

// This sealed class defines events the user can trigger
sealed class SettingsEvent {
    data class OnKillSwitchToggled(val isEnabled: Boolean) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    // The UI state is created by reading the Flow from our SettingsManager
    val uiState = settingsManager.isKillSwitchEnabled.map { isEnabled ->
        SettingsUiState(isKillSwitchEnabled = isEnabled)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    // This function handles events from the UI
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnKillSwitchToggled -> {
                viewModelScope.launch {
                    settingsManager.setKillSwitchEnabled(event.isEnabled)
                }
            }
        }
    }
}