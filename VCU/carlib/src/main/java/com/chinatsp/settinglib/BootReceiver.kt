package com.chinatsp.settinglib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.bean.Cmd
import com.chinatsp.vehicle.controller.annotation.Model

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/18 14:46
 * @desc   :
 * @version: 1.0
 */
class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        LogManager.d(BootReceiver::class.java.simpleName, "receiver android.intent.action.BOOT_COMPLETED")
    }
}