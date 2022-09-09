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
import com.chinatsp.vehicle.controller.bean.Cmd
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 20:53
 * @desc   :
 * @version: 1.0
 */
class VehicleService : Service() {

    private val TAG: String = VehicleService::class.java.simpleName

    private val controller: OuterControllerImpl by lazy { OuterControllerImpl() }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val packageName = it.`package`
            Timber.d("onBind packageName:$packageName")
        }
        return controller
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.getStringExtra("data");
        Timber.d("receive data:$data")
        controller.doParseSourceData(data)
        return super.onStartCommand(intent, flags, startId)
    }

    inner class OuterControllerImpl : IOuterController.Stub() {

        private var resolver: IDataResolver? = null

        override fun doBindDataResolver(resolver: IDataResolver?) {
            this.resolver = resolver
        }

        override fun isEngineStatus(packageName: String?): Boolean {
            return Constant.ENGINE_STATUS
        }

        override fun doOuterControlCommand(cmd: Cmd, callback: ICmdCallback?) {
            Timber.d("doOuterControlCommand -------------- cmd:$cmd")
            GlobalManager.instance.doOuterControlCommand(cmd, callback)
//            if (cmd.action == Action.OPEN) {
//                cmd.status = IStatus.SUCCESS
//                cmd.message = "打开成功！！"
//            }
//            callback?.onCmdHandleResult(cmd)
        }

        fun doParseSourceData(data: String?) {
            if (!TextUtils.isEmpty(data)) {
                resolver?.let {
                    it.doResolverData(data)
                }
            }
        }

    }

}