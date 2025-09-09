package com.chikivision.vizovpn.ui.viewmodels

import android.app.Application
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chikivision.vizovpn.data.ServerEntity
import com.chikivision.vizovpn.data.ServerRepository
import com.chikivision.vizovpn.data.VpnState
import com.chikivision.vizovpn.data.VpnStateHolder
import com.chikivision.vizovpn.services.VizoVpnService
import com.chikivision.vizovpn.ui.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    serverRepository: ServerRepository
) : ViewModel() {

    // This now holds the entire selected server object, not just the name.
    private val _selectedServer = MutableStateFlow<ServerEntity?>(null)

    val uiState = combine(
        VpnStateHolder.vpnState,
        serverRepository.getAllServers(),
        _selectedServer
    ) { vpnState, servers, selected ->

        // Logic to set the first server as the default when the app starts
        if (selected == null && servers.isNotEmpty()) {
            _selectedServer.value = servers.first()
        }

        val currentServerName = selected?.country ?: "Select a Server"

        MainUiState(
            serverList = servers,
            selectedServer = currentServerName,
            isConnected = vpnState in listOf(VpnState.CONNECTED, VpnState.CONNECTING),
            statusText = when(vpnState) {
                VpnState.CONNECTED -> "Connected to $currentServerName"
                VpnState.CONNECTING -> "Connecting..."
                VpnState.DISCONNECTED -> "Not connected"
                VpnState.DISCONNECTING -> "Disconnecting..."
            },
            primaryButtonText = when(vpnState) {
                VpnState.CONNECTED -> "Disconnect"
                VpnState.CONNECTING -> "Connecting"
                VpnState.DISCONNECTED -> "Connect"
                VpnState.DISCONNECTING -> "Disconnecting"
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    private val _events = Channel<MainScreenUiEvent>()
    val events = _events.receiveAsFlow()

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.ConnectButtonClicked -> toggleConnection()
            is MainScreenEvent.ServerSelected -> {
                // ✅ When a server is selected, update our state with the full object
                _selectedServer.value = event.server
            }
        }
    }

    private fun toggleConnection() {
        if (uiState.value.isConnected) {
            stopVpnService()
        } else {
            prepareAndStartVpn()
        }
    }

    private fun prepareAndStartVpn() {
        val vpnIntent = VpnService.prepare(application)
        if (vpnIntent != null) {
            viewModelScope.launch {
                _events.send(MainScreenUiEvent.RequestVpnPermission(vpnIntent))
            }
        } else {
            onVpnPermissionResult(isGranted = true)
        }
    }

    fun onVpnPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            startVpnService()
        }
    }

    private fun startVpnService() {
        // ✅ Get the config from the selected server. If none is selected, do nothing.
        val selectedConfig = _selectedServer.value?.config ?: return

        Intent(application, VizoVpnService::class.java).also {
            it.action = VizoVpnService.ACTION_CONNECT
            // ✅ Pass the selected server's specific configuration to the service
            it.putExtra("CONFIG", selectedConfig)
            application.startService(it)
        }
    }

    private fun stopVpnService() {
        Intent(application, VizoVpnService::class.java).also {
            it.action = VizoVpnService.ACTION_DISCONNECT
            application.startService(it)
        }
    }
}

sealed class MainScreenUiEvent {
    data class RequestVpnPermission(val vpnIntent: Intent) : MainScreenUiEvent()
}

sealed class MainScreenEvent {
    data object ConnectButtonClicked : MainScreenEvent()
    data class ServerSelected(val server: ServerEntity) : MainScreenEvent()
}