package com.chinatsp.settinglib.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.chinatsp.settinglib.LogManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 20:53
 * @desc   :
 * @version: 1.0
 */
class VehicleService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        LogManager.d("onCreate=========================")
    }

}