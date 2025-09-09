package com.chikivision.vizovpn.data

// هذه هي الحالات الحقيقية الممكنة لاتصال الـ VPN
enum class VpnState {
    CONNECTED,
    CONNECTING,
    DISCONNECTED,
    DISCONNECTING
}