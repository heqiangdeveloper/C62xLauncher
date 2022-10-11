package com.chinatsp.settinglib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.IDataResolver
import com.chinatsp.vehicle.controller.IOuterController
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.CarCmd
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 20:53
 * @desc   :
 * @version: 1.0
 */
class VehicleService : Service() {

    private val controller: OuterControllerImpl by lazy { OuterControllerImpl() }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val packageName = it.`package`
            Timber.d("onBind packageName:$packageName")
        }
        return controller
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val action = it.action
            val data = it.getStringExtra("data")
            Timber.d("receive action:$action, data:$data")
            if ("com.chinatsp.vcu.actions.USER_SETTING_RECOVE" == action) {

            } else if ("com.chinatsp.vcu.actions.ACOUSTIC_CONTROLER" == action) {
                controller.doParseSourceData(data)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class OuterControllerImpl : IOuterController.Stub() {

        private var resolver: IDataResolver? = null

        override fun doBindDataResolver(resolver: IDataResolver?) {
            this.resolver = resolver
        }

        override fun doAirControlCommand(cmd: AirCmd, callback: ICmdCallback?) {
            GlobalManager.instance.doAirControlCommand(cmd, callback)
        }

        override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
            GlobalManager.instance.doCarControlCommand(cmd, callback)
        }

        override fun isEngineStatus(packageName: String?): Boolean {
            return Constant.ENGINE_STATUS
        }

        fun doParseSourceData(data: String?) {
            if (!TextUtils.isEmpty(data)) {
                resolver?.doResolverData(data)
            }
        }

    }

}