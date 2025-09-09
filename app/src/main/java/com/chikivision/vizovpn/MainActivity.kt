package com.chikivision.vizovpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.chikivision.vizovpn.ui.navigation.AppNavigation
import com.chikivision.vizovpn.ui.theme.VizoVPNTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides this) {
                VizoVPNTheme {
                    AppNavigation()
                }
            }
        }
    }
}
