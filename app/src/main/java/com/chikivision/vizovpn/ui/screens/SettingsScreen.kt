package com.chikivision.vizovpn.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chikivision.vizovpn.ui.viewmodels.SettingsViewModel
import com.chikivision.vizovpn.ui.viewmodels.SettingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel() // ✅ 1. Get an instance of the ViewModel
) {
    // ✅ 2. Collect the state from the ViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // ✅ 3. Connect the Switch to the ViewModel's state and events
            SettingItem(
                name = "Kill Switch",
                isChecked = state.isKillSwitchEnabled,
                onCheckedChange = { isEnabled ->
                    viewModel.onEvent(SettingsEvent.OnKillSwitchToggled(isEnabled))
                }
            )
            Divider()
            // You can add other settings here in the same way
            SettingItem(
                name = "Auto-Reconnect",
                isChecked = false, // Placeholder for now
                onCheckedChange = { /* TODO */ }
            )
            Divider()
        }
    }
}