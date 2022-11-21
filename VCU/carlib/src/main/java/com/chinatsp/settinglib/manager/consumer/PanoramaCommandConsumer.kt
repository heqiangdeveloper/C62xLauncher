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
import kotlin.random.Random

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
    private fun consumeViewCutCommand(parcel: CommandParcel): Boolean {
        val command = parcel.command as CarCmd
        val consume = Action.CHANGED == command.action
        if (!consume) {
            return consume
        }
        val isAvm = isAvmEngine()
        val isRetry = parcel.isRetry()
        val isInit = IStatus.INIT == command.status
        if (isInit) {
            command.resetSent()
            parcel.retryCount = if (isAvm) 0x1 else 0x2
        }
        if (!isAvm) {
            attemptLaunchPanorama(isInit, isRetry, parcel)
            return consume
        }
        val mode3D = isMode3D()
        var expect = Constant.INVALID
        val actual = obtainCameraView()
        if (IPart.HEAD == command.part) {
            expect = if (mode3D) 0x9 else 0x1
        } else if (IPart.TAIL ==  command.part) {
            expect = if (mode3D) 0xA else 0x2
        } else if (IPart.L_F == command.part) {
            expect = if (mode3D) 0xD else 0x3
        } else if (IPart.L_B == command.part) {
            expect = if (mode3D) 0xF else 0x5
        } else if (IPart.R_F == command.part) {
            expect = if (mode3D) 0xE else 0x4
        } else if (IPart.R_B == command.part) {
            expect = if (mode3D) 0x10 else 0x6
        } else if ((IPart.L_F or IPart.L_B) == command.part) {
            expect = if (mode3D) 0xB else 0x5
        } else if ((IPart.R_F or IPart.R_B) == command.part) {
            expect = if (mode3D) 0xC else 6
        } else if (IPart.RANDOM == command.part) {
            expect = randomCameraView(mode3D)
        } else if (IPart.TOP == command.part) {
            expect = 0x14
        }
        val areaName = command.slots?.direction ?: analysisAreaName(expect)
        if (actual == expect) {
            command.message = if (!command.isSent()) {
                "不用再切了，目前是${areaName}视角了"
            } else {
                "好的，已为您切换到${areaName}视角"
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

    private fun randomCameraView(mode3D: Boolean): Int {
        return if (mode3D) {
            Random.nextInt(0x1, 0x7)
        } else {
            Random.nextInt(0x7, 0x11)
        }
    }

    private fun analysisAreaName(areaId: Int): String {
        return when (areaId) {
             0x1, 0x9 -> "前"
             0x2, 0xA -> "后"
             0xB, 0x7 -> "左"
             0xC, 0x8 -> "右"
             0x3, 0xD -> "左前"
             0x5, 0xF -> "左后"
             0x4, 0xE -> "右前"
             0x6, 0x10 -> "右后"
             0x14 -> "顶部"
            else -> "该"
        }
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
        val isAvm = isAvmEngine()
        val isRetry = parcel.isRetry()
        val isInit = IStatus.INIT == command.status
        if (isInit) {
            parcel.retryCount = if (isAvm) 2 else 3
            command.resetSent()
        }
        if (!isAvm) {
            attemptLaunchPanorama(isInit, isRetry, parcel)
            return consume
        }
        val actual = isMode3D()
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

    private fun attemptLaunchPanorama(isInit: Boolean, isRetry: Boolean, parcel: CommandParcel) {
        val command = parcel.command
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
            command.resetSent()
            parcel.retryCount = 3
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
//        Indicate the current display requirement of AVM
//        0x0: Initial
//        0x1: Request to display normal view
//        0x2: Request to display off view
//        0x3: Request to display error view
//        0x4: Request to display EVM view
//        0x5: Request to display EVM view for fault and reduction of speed  reminding  请求开启EVM故障降速提示界面
//        0x6: Request to display single  Left side view 请求单独显示左侧影像界面
//        0x7: Request to display single Right side view 请求单独显示右侧影像界面
        val signal = CarCabinManager.ID_AVM_AVM_DISP_REQ
        val value = manager.readIntProperty(signal, Origin.CABIN)
        return value == 0x1 || value == 0x4 || value == 0x5 || value == 0x6 || value == 0x7
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
            consumed = consumeViewCutCommand(parcel)
        }
        if (!consumed) {
            Timber.d("enter panorama consume but command not consumed!!!")
            command.message = Keywords.COMMAND_FAILED
            parcel.callback?.onCmdHandleResult(command)
        }
    }

}