package com.chikivision.vizovpn.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chikivision.vizovpn.MainActivity
import com.chikivision.vizovpn.R
import com.chikivision.vizovpn.data.VpnState
import com.chikivision.vizovpn.data.VpnStateHolder
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.*

class VizoVpnService : VpnService() {

    private lateinit var backend: GoBackend
    private val tunnel = VizoTunnel()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_CONNECT = "com.chikivision.vizovpn.CONNECT"
        const val ACTION_DISCONNECT = "com.chikivision.vizovpn.DISCONNECT"
        private const val NOTIFICATION_CHANNEL_ID = "VizoVpnChannel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "VizoVpnService"
    }

    inner class VizoTunnel : Tunnel {
        override fun getName(): String = "vizo-tunnel"

        override fun onStateChange(newState: Tunnel.State) {
            Log.i(TAG, "Tunnel state changed to: $newState")
            when (newState) {
                Tunnel.State.UP -> VpnStateHolder.update(VpnState.CONNECTED)
                Tunnel.State.DOWN -> VpnStateHolder.update(VpnState.DISCONNECTED)
                else -> { /* Ignore */ }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        backend = GoBackend(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                // ✅ Read the configuration string from the intent
                val configString = intent.getStringExtra("CONFIG")
                if (configString != null) {
                    serviceScope.launch { startVpn(configString) }
                } else {
                    Log.e(TAG, "No config string provided to start VPN service.")
                    stopVpn()
                }
            }
            ACTION_DISCONNECT -> {
                serviceScope.launch { stopVpn() }
            }
        }
        return START_STICKY
    }

    // ✅ This function now accepts the config string as a parameter
    private fun startVpn(config: String) {
        VpnStateHolder.update(VpnState.CONNECTING)
        Log.i(TAG, "Attempting to start VPN...")

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }

            // ✅ Use the passed-in config string to create the tunnel config
            val tunnelConfig = Config.parse(config.byteInputStream())
            backend.setState(tunnel, Tunnel.State.UP, tunnelConfig)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
            VpnStateHolder.update(VpnState.DISCONNECTED)
            stopVpn()
        }
    }

    private fun stopVpn() {
        VpnStateHolder.update(VpnState.DISCONNECTING)
        Log.i(TAG, "Stopping VPN tunnel...")
        try {
            backend.setState(tunnel, Tunnel.State.DOWN, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop VPN", e)
        }
        stopSelf()
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "VizoVPN Service"
            val descriptionText = "VPN connection status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("VizoVPN is active")
            .setContentText("Your connection is secure")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d(TAG, "Service is destroyed.")
        stopForeground(true)
        super.onDestroy()
    }
}