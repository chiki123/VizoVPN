package com.chikivision.vizovpn.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chikivision.vizovpn.ui.screens.components.ServerItem // ✅ FIX: Use the shared component
import com.chikivision.vizovpn.ui.viewmodels.MainScreenEvent
import com.chikivision.vizovpn.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select a Server") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(state.serverList) { server ->
                ServerItem(
                    name = server.country,
                    // We'll use the country code for the flag later
                    speed = if (server.isPremium) "Premium" else "Fast",
                    isPremium = server.isPremium,
                    isSelected = state.selectedServer == server.country,
                    modifier = Modifier.clickable {
                        // ✅ FIX: Send the entire server object in the event
                        viewModel.onEvent(MainScreenEvent.ServerSelected(server))
                        navController.popBackStack()
                    }
                )
                Divider()
            }
        }
    }
}