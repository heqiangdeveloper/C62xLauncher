package com.chinatsp.settinglib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.chinatsp.settinglib.LogManager
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.IOuterController
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.bean.Cmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 20:53
 * @desc   :
 * @version: 1.0
 */
class VehicleService: Service() {

    private val TAG: String = VehicleService::class.java.simpleName

    private val controller: IOuterController.Stub by lazy { OuterControllerImpl() }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val packageName = it.`package`
            LogManager.d(TAG, "onBind packageName:$packageName")
        }
        return controller
    }

    inner class OuterControllerImpl: IOuterController.Stub() {
        override fun doOuterControlCommand(cmd: Cmd, callback: ICmdCallback) {
            LogManager.d(TAG, "doOuterControlCommand cmd:$cmd")
            if (cmd.action == Action.OPEN) {
                cmd.status = IStatus.SUCCESS
                cmd.message = "打开成功！！"
            }
            callback.onCmdHandleResult(cmd)
        }


    }

}