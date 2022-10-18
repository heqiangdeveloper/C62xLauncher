package com.chinatsp.settinglib.manager.consumer

import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.bean.CarCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 17:11
 * @desc   :
 * @version: 1.0
 */
class PanoramaCommandConsumer(val manager: GlobalManager) {

    private fun sendCabinValue(signal: Int, value: Int): Boolean {
        return manager.writeProperty(signal, value, Origin.CABIN)
    }

    fun consumerCommand(command: CarCmd, callback: ICmdCallback?) {
        if (IStatus.INIT == command.status) {
            consumerSwitchCommand(command)
        }
        if (IStatus.INIT == command.status) {
            consumeModeCommand(command)
        }
        callback?.onCmdHandleResult(command)
    }

    private fun consumeModeCommand(command: CarCmd) {
        val mask = ICar.MODE_3D_2D
//        The AVM 2D/3D view set request signal.2D/3D模式切换
//        0x0: Inactive; 0x1: 2D; 0x2: 3D; 0x3: Invalid
        if ((mask == command.car) && (Action.OPTION == command.action)) {
            if (command.value == (mask shl 1)) {
                command.status = IStatus.RUNNING
                sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_MOD_SET, 0x1)
                command.message = "已切换到2D模式"
            }
            if (command.value == (mask shl 2)) {
                command.status = IStatus.RUNNING
                sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_MOD_SET, 0x2)
                command.message = "已切换到3D模式"
            }
        }
    }

    //            0x1: ON; 0x2: OFF
    private fun consumerSwitchCommand(command: CarCmd) {
        if (Action.OPEN == command.action) {
            command.status = IStatus.RUNNING
            sendCabinValue(CarCabinManager.ID_APA_AVM_DISP_SWT, 0x1)
            command.message = "全景开启成功"
        }
        if (Action.CLOSE == command.action) {
            command.status = IStatus.RUNNING
            sendCabinValue(CarCabinManager.ID_APA_AVM_DISP_SWT, 0x2)
            command.message = "全景关闭成功"
        }
    }
}