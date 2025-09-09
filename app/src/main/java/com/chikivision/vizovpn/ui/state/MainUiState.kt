package com.chikivision.vizovpn.ui.state

import com.chikivision.vizovpn.data.ServerEntity

data class MainUiState(
    val isConnected: Boolean = false,
    val statusText: String = "Not connected",
    val selectedServer: String = "United States",
    val primaryButtonText: String = "Connect",
    val serverList: List<ServerEntity> = emptyList() // ✅ ADD THIS LINE
)