package com.chinatsp.settinglib

import android.app.Application
import android.content.Intent
import android.os.Handler
import kotlin.properties.Delegates

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/21 16:03
 * @desc   :
 * @version: 1.0
 */
abstract class BaseApp : Application() {

    abstract val loadLibraries: Byte

    val mainHandler: Handler by lazy {
        Handler(mainLooper)
    }

    override fun onCreate() {
        super.onCreate()
        TestReceiver().register(context = applicationContext)
    }

    fun sendBroadcast(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    companion object {
        var instance: BaseApp by Delegates.notNull()
    }

}