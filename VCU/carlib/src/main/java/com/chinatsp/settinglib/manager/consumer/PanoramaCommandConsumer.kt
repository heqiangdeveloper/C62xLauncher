package com.chinatsp.settinglib.manager.consumer

import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IPart
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

    fun consumerCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        if (IStatus.INIT == command.status) {
            consumerSwitchCommand(command)
        }
        if (IStatus.INIT == command.status) {
            consumeModeCommand(command)
        }
        if (IStatus.INIT == command.status) {
            consumeCameraChangeCommand(command)
        }
        callback?.onCmdHandleResult(command)
    }

//    AVM view set request signal 切换全景视图命令，Reserved
//    0x0: Inactive
//    0x1: Front view
//    0x2: Rear view
//    0x3: Front-Left view
//    0x4: Front-Right view
//    0x5: Rear-Left view
//    0x6: Rear-Right view
//    0x7: TurnLeft view-3D
//    0x8: TurnRight view-3D
//    0x9: Front view-3D
//    0xA: Rear view-3D
//    0xB: Left view-3D
//    0xC: Right view-3D
//    0xD: Front-Left view-3D
//    0xE: Front-Right view-3D
//    0xF: Rear-Left view-3D
//    0x10: Rear-Right view-3D
//    0x11: Large single view-front
//    0x12: Large single view-rear
//    0x13: EOL/Test-view
//    0x14: Top View
//    0x15~0x1F: Reserved
    private fun consumeCameraChangeCommand(command: CarCmd) {
        if (Action.CHANGED != command.action) {
            return
        }
        var aera = "前"
        var mask = IPart.HEAD
        val fAct = mask == (mask and command.part)
        mask = IPart.TAIL
        val bAct = mask == (mask and command.part)
        mask = IPart.L_F or IPart.L_B
        val lAct = mask == (mask and command.part)
        mask = IPart.R_F or IPart.R_B
        val rAct = mask == (mask and command.part)
        var value = Constant.INVALID
        val is3D = obtainCameraMode()
        if (fAct) {
            aera = "前"
            value = if (is3D) 0x9 else 0x1
        } else if (bAct) {
            aera = "后"
            value = if (is3D) 0xA else 0x2
        } else if (lAct) {
            aera = "左"
            value = if (is3D) 0xD else 0x3
        } else if (rAct) {
            aera = "右"
            value = if (is3D) 0xE else 0x4
        }
        if (!isAvmEngine()) {
            sendCabinValue(CarCabinManager.ID_APA_AVM_SWT, 0x1)
        }
        sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_SET, value)
        command.message = "好的，已为您切换到${aera}视角"
        command.status = IStatus.RUNNING
    }

    private fun consumeModeCommand(command: CarCmd) {
//        The AVM 2D/3D view set request signal.2D/3D模式切换
//        0x0: Inactive; 0x1: 2D; 0x2: 3D; 0x3: Invalid
        if (Action.OPTION == command.action) {
            if (command.value == (Action.OPTION shl 1)) {
                command.status = IStatus.RUNNING
                sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_MOD_SET, 0x1)
                command.message = "已切换到2D模式"
            }
            if (command.value == (Action.OPTION shl 2)) {
                command.status = IStatus.RUNNING
                sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_MOD_SET, 0x2)
                command.message = "已切换到3D模式"
            }
        }
    }

//            0x1: ON; 0x2: OFF
    private fun consumerSwitchCommand(command: CarCmd) {
        if (Action.TURN_ON == command.action) {
            command.status = IStatus.RUNNING
//            ID_APA_AVM_DISP_SWT
            sendCabinValue(CarCabinManager.ID_APA_AVM_SWT, 0x1)
            command.message = "全景开启成功"
        }
        if (Action.TURN_OFF == command.action) {
            command.status = IStatus.RUNNING
//            ID_APA_AVM_DISP_SWT
            sendCabinValue(CarCabinManager.ID_APA_AVM_SWT, 0x2)
            command.message = "全景关闭成功"
        }
    }

    private fun obtainCameraView(): Int {
        val signal = CarCabinManager.ID_AVM_VIEW_MOD
        val result = manager.readIntProperty(signal, Origin.CABIN)
        return result
    }

    private fun obtainCameraMode(): Boolean {
        val signal = Constant.INVALID
        val value = manager.readIntProperty(signal, Origin.CABIN)
        val result = value == 0x2
        return result
    }

    private fun isAvmEngine(): Boolean {
        val signal = Constant.INVALID
        val value = manager.readIntProperty(signal, Origin.CABIN)
        val result = value == 0x1
        return result
    }

}