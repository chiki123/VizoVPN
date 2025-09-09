package com.chikivision.vizovpn.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// استخدام 'object' يجعله Singleton تلقائيًا
// هذا يعني أن هناك نسخة واحدة فقط منه في التطبيق بأكمله
object VpnStateHolder {

    // متغير خاص داخلي للاحتفاظ بالحالة وتحديثها
    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)

    // متغير عام للقراءة فقط، سيراقبه ה-ViewModel
    val vpnState = _vpnState.asStateFlow()

    // دالة لتحديث الحالة من أي مكان (بشكل أساسي من الخدمة)
    fun update(newState: VpnState) {
        _vpnState.value = newState
    }
}