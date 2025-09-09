package com.chikivision.vizovpn.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.chikivision.vizovpn.ui.state.MainUiState
import com.chikivision.vizovpn.ui.theme.*
import com.chikivision.vizovpn.ui.viewmodels.MainScreenEvent
import com.chikivision.vizovpn.ui.viewmodels.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToServerList: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreenContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToServerList = onNavigateToServerList,
        onNavigateToSettings = onNavigateToSettings
    )
}

@Composable
fun MainScreenContent(
    state: MainUiState,
    onEvent: (MainScreenEvent) -> Unit,
    onNavigateToServerList: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(YellowGradientStart, BlueGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(statusText = state.statusText)
            Spacer(modifier = Modifier.height(24.dp))
            ConnectionCard(
                isConnected = state.isConnected,
                serverName = state.selectedServer,
                buttonText = state.primaryButtonText,
                onConnectClick = { onEvent(MainScreenEvent.ConnectButtonClicked) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ServerListCard(
                selectedServer = state.selectedServer,
                onNavigateToServerList = onNavigateToServerList
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToSettings() }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Footer()
        }
    }
}

@Composable
fun TopBar(statusText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("VizoVPN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(statusText, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
        Button(
            onClick = { /* TODO: Navigate to Auth Screen */ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardBackground)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Sign In", tint = DisconnectedBlue)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sign In", color = TextPrimary)
        }
    }
}

@Composable
fun ConnectionCard(
    isConnected: Boolean,
    serverName: String,
    buttonText: String,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onConnectClick,
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) ConnectedGreen else DisconnectedBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Wifi, contentDescription = "Connect", modifier = Modifier.size(50.dp), tint = TextOnButton)
                    Text(buttonText, color = TextOnButton, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (isConnected) "Connected to $serverName" else "Ready to connect",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                if (isConnected) "Your browsing is protected" else "Tap to start secure browsing",
                color = TextSecondary,
                fontSize = 12.sp
            )
            AnimatedVisibility(visible = isConnected) {
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatInfo(icon = Icons.Default.SwapHoriz, value = "120 ms", label = "Ping")
                    StatInfo(icon = Icons.Default.Security, value = "WireGuard", label = "Protocol")
                    StatInfo(icon = Icons.Default.ArrowDownward, value = "50 Mbps", label = "Speed")
                }
            }
        }
    }
}

@Composable
fun StatInfo(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = DisconnectedBlue)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun ServerListCard(selectedServer: String, onNavigateToServerList: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onNavigateToServerList() },
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Servers", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ServerItem(name = "United States", speed = "Fast", isPremium = false, isSelected = selectedServer == "United States")
            ServerItem(name = "Germany", speed = "Medium", isPremium = false, isSelected = selectedServer == "Germany")
            ServerItem(name = "Japan", speed = "Premium", isPremium = true, isSelected = selectedServer == "Japan")
        }
    }
}

@Composable
fun ServerItem(
    name: String,
    speed: String,
    isSelected: Boolean,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) DisconnectedBlue.copy(alpha = 0.1f) else Color.Transparent
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontWeight = FontWeight.Medium)
            if (isPremium) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = PremiumYellow,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
                )
            }
        }
        Text(speed, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun SettingsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            SettingItem(
                name = "Auto-Reconnect",
                isChecked = false,
                onCheckedChange = {}
            )
            SettingItem(
                name = "Kill Switch",
                isChecked = false,
                onCheckedChange = {}
            )
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, fontWeight = FontWeight.Medium, color = TextPrimary)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun Footer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("CHIKIVISION", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Ad Banner", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    VizoVPNTheme {
        MainScreenContent(
            state = MainUiState(isConnected = false, selectedServer = "United States"),
            onEvent = {},
            onNavigateToServerList = {},
            onNavigateToSettings = {}
        )
    }
}
