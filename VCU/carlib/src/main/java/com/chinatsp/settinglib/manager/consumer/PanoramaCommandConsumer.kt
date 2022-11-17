package com.chinatsp.settinglib.manager.consumer

import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.ShareHandler
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 17:11
 * @desc   :
 * @version: 1.0
 */
class PanoramaCommandConsumer(val manager: GlobalManager) : ICmdExpress {

    private fun sendCabinValue(signal: Int, value: Int): Boolean {
        return manager.writeProperty(signal, value, Origin.CABIN)
    }

    fun consumerCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
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
    private fun consumeCameraChangeCommand(parcel: CommandParcel): Boolean {
        val command = parcel.command as CarCmd
        val consume = Action.CHANGED == command.action
        if (!consume) {
            return consume
        }
        val isRetry = parcel.isRetry()
        val isAvm = isAvmEngine()
        val isInit = IStatus.INIT == command.status
        if (isInit) {
            val count = if (isAvm) 0x1 else 0x2
            parcel.retryCount = count
            command.resetSent(sendCount = count)
        }
        if (!isAvm) {
            if (isInit) {
                sendCabinValue(CarCabinManager.ID_APA_AVM_SWT, 0x1)
                command.status = IStatus.PREPARED
            }
            if (isRetry) {
                ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
            } else {
                command.message = Keywords.COMMAND_FAILED
                parcel.callback?.onCmdHandleResult(command)
            }
            return consume
        }

        var aera = "前"

        val mode3D = isMode3D()
        var expect = Constant.INVALID
        val actual = obtainCameraView()

        if (IPart.HEAD == command.part) {
            aera = "前"
            expect = if (mode3D) 0x9 else 0x1
        } else if (IPart.TAIL ==  command.part) {
            aera = "后"
            expect = if (mode3D) 0xA else 0x2
        }

        else if (IPart.L_F == command.part) {
            aera = "左前"
            expect = if (mode3D) 0xD else 0x3
        } else if (IPart.L_B == command.part) {
            aera = "左后"
            expect = if (mode3D) 0xF else 0x5
        }

        else if (IPart.R_F == command.part) {
            aera = "右前"
            expect = if (mode3D) 0xE else 0x4
        } else if (IPart.R_B == command.part) {
            aera = "右后"
            expect = if (mode3D) 0x10 else 0x6
        }

        else if ((IPart.L_F or IPart.L_B) == command.part) {
            aera = "左"
            expect = if (mode3D) 0xB else 0x5
        } else if ((IPart.R_F or IPart.R_B) == command.part) {
            aera = "右"
            expect = if (mode3D) 0xC else 6
        }

        if (actual == expect) {
            command.message = if (!command.isSent()) {
                "不用再切了，目前是${aera}视角了"
            } else {
                "好的，已为您切换到${aera}视角"
            }
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!isRetry) {
            command.message = Keywords.COMMAND_FAILED
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!command.isSent()) {
            sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_SET, expect)
            command.status = IStatus.RUNNING
            command.sent()
        }
        ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
        return consume
    }

    private fun consumeModeCommand(parcel: CommandParcel): Boolean {
//        The AVM 2D/3D view set request signal.2D/3D模式切换
//        0x0: Inactive; 0x1: 2D; 0x2: 3D; 0x3: Invalid
        val mask = Action.OPTION
        val command = parcel.command as CarCmd
        val consume = mask == command.action
                && ((command.value == (mask shl 1)) || (command.value == (mask shl 2)))
        if (!consume) {
            return consume
        }
        val isInit = IStatus.INIT == command.status
        if (isInit) {
            command.resetSent(sendCount = 2)
        }
        val actual = isMode3D()
        val isRetry = parcel.isRetry()
        val expect = command.value == (mask shl 2)
        if (actual == expect) {
            val statusName = if (isInit) {
                "${if (expect) "3D模式" else "2D模式"}已经打开了"
            } else {
                "已为您切换到${if (expect) "3D模式" else "2D模式"}"
            }
            command.message = statusName
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!isRetry) {
            command.message = Keywords.COMMAND_FAILED
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!command.isSent()) {
            val value = if (expect) 0x2 else 0x1
            sendCabinValue(CarCabinManager.ID_APA_AVM_VIEW_MOD_SET, value)
            command.status = IStatus.RUNNING
            command.sent()
        }
        ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
        return consume
    }

//  0x1: ON; 0x2: OFF
    private fun consumerSwitchCommand(parcel: CommandParcel): Boolean {
        val command = parcel.command as CarCmd
        val consume = Action.TURN_ON == command.action || Action.TURN_OFF == command.action
        if (!consume) {
            return consume
        }
        val isInit = IStatus.INIT == command.status
        if (isInit) {
            command.resetSent(sendCount = 1)
        }
        val actual = isAvmEngine()
        val isRetry = parcel.isRetry()
        val expect = Action.TURN_ON == command.action
        if (actual == expect) {
            val name = command.slots?.name ?: "全景"
            val statusName = if (isInit) {
                "已经${if (expect) "打开" else "关闭"}了"
            } else {
                "${if (expect) "打开" else "关闭"}了"
            }
            command.message = "${name}${statusName}"
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!isRetry) {
            command.message = Keywords.COMMAND_FAILED
            parcel.callback?.onCmdHandleResult(command)
            return consume
        }
        if (!command.isSent()) {
            val value = if (expect) 0x1 else 0x2
            sendCabinValue(CarCabinManager.ID_APA_AVM_SWT, value)
            command.status = IStatus.RUNNING
            command.sent()
        }
        ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
        return consume
    }

//    The view state of AVM mode.通过车机坐标识别。在车机上切换的360视角
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
//    0x15：EVM View
//    0x16：Customer Select Slot View
//    0x17：Single  Left side view
//    0x18：Single Right side view
//    0x19~0x1F: Reserved
    private fun obtainCameraView(): Int {
        val signal = CarCabinManager.ID_AVM_VIEW_MOD
        return manager.readIntProperty(signal, Origin.CABIN)
    }

    private fun isMode3D(): Boolean {
        val signal = Constant.INVALID
        val value = manager.readIntProperty(signal, Origin.CABIN)
        return value == 0x2
    }

    private fun isAvmEngine(): Boolean {
//        ID_APA_AVM_DISP_SWT
//        AVM display set request signal. 全景影像界面退出开关。
//        0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Invalid
        val signal = -1
        val value = manager.readIntProperty(signal, Origin.CABIN)
        return 0x1 == value
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        var consumed = false
        if (!consumed) {
            consumed = consumerSwitchCommand(parcel)
        }
        if (!consumed) {
            consumed = consumeModeCommand(parcel)
        }
        if (!consumed) {
            consumed = consumeCameraChangeCommand(parcel)
        }
        if (!consumed) {
            Timber.d("enter panorama consume but command not consumed!!!")
            command.message = Keywords.COMMAND_FAILED
            parcel.callback?.onCmdHandleResult(command)
        }
    }

}