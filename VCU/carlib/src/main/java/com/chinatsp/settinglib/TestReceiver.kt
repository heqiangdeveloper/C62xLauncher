package com.chinatsp.settinglib

import android.car.hardware.CarPropertyValue
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/18 14:46
 * @desc   :
 * @version: 1.0
 */
class TestReceiver : BroadcastReceiver() {

    val receiver_canbin_signal = "com.vcu.actions.receiver_canbin_signal"
    val receiver_havc_signal = "com.vcu.actions.receiver_havc_signal"

    override fun onReceive(context: Context, intent: Intent) {
        val signal = intent.getIntExtra("signal", -1)
        val area = intent.getIntExtra("area", -1)
        val value = intent.getIntExtra("value", -1)
        Timber.d("receiver ${intent.action} signal:$signal, area:$area, value:$value")
        when (intent.action) {
            receiver_havc_signal -> {}
            receiver_canbin_signal -> {
                val pro = CarPropertyValue(signal!!.toInt(), area!!.toInt(), value!!.toInt())
                GlobalManager.instance.onDispatchSignal(pro, Origin.CABIN)
            }
            else -> {}
        }
    }

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(receiver_canbin_signal)
        intentFilter.addAction(receiver_havc_signal)
        context.registerReceiver(this, intentFilter)
    }

}