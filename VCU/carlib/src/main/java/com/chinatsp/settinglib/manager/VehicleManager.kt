package com.chinatsp.settinglib.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.chinatsp.settinglib.service.VehicleService
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/1 11:05
 * @desc   :
 * @version: 1.0
 */
class VehicleManager private constructor(): ServiceConnection {

    private lateinit var context: Context

    companion object {
        val TAG: String = VehicleManager::class.java.simpleName

        val instance: VehicleManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VehicleManager()
        }
    }

    fun initVehicleConnect(context: Context) {
        this.context = context
        val intent = Intent(context, VehicleService::class.java)
        context.startService(intent)
        bindService(intent)
    }

    private fun bindService(intent: Intent) {
//        intent.component = ComponentName(context, VehicleService::class.java)
        val result = context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        Timber.d("bindService result:$result")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Timber.d("onServiceConnected !!!!!!!!!!!!!!!")

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Timber.d("onServiceDisconnected !!!!!!!!!!!!!!!")
    }
}