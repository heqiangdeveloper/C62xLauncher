package com.chinatsp.settinglib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.SwitchNode
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/18 14:46
 * @desc   :
 * @version: 1.0
 */
class BootReceiver : BroadcastReceiver() {
    val SCREEN_ON = "android.intent.action.SCREEN_ON"
//    val SCREEN_OFF = "android.intent.action.SCREEN_OFF"
    val FACTORY_RESET = "chinatsp.intent.action.FACTORY_RESET"

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("receiver broadcast action:${intent?.action}")
        if (FACTORY_RESET == intent?.action) {
            val node = SwitchNode.AUDIO_SOUND_LOUDNESS
            VoiceManager.instance.doSetSwitchOption(node, node.def)
        }
    }

}