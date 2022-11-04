package com.chinatsp.settinglib.manager.cabin

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.AirCmdParcel
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAir
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import kotlin.math.min

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 19:49
 * @desc   :
 * @version: 1.0
 */
class AirSupplier(private val airManager: ACManager) : IAirMaster {

    private val handler: Handler by lazy {
        val looperThread = HandlerThread("")
        looperThread.start()
        Handler(looperThread.looper, MessageInvoke())
    }

    private val airSetter: AirSetter by lazy {
        AirSetter(airManager, airGetter)
    }

    private val airGetter: AirGetter by lazy {
        AirGetter(airManager)
    }

    private val delayed: Int get() = 300

    override fun doLaunchConditioner(parcel: AirCmdParcel): Boolean {
        val command = parcel.cmd
        val isRetry = parcel.isRetry()
        val isCareCmd = Action.OPEN == command.action
        if (airGetter.isConditioner()) {//空调已开启
            if (isCareCmd) {
                command.message = if (isRetry) "空调已经打开了" else  "好的，已为您打开空调"
                parcel.callback?.onCmdHandleResult(command)
            }
            return true
        }
        if (!isRetry) {
            if (isCareCmd) {
                command.message = Keywords.COMMAND_FAILED
                parcel.callback?.onCmdHandleResult(command)
            }
            return false
        } else {
            val status = airSetter.doSwitchConditioner(true)
            if (!isCareCmd) return !status
            if (status) {
                command.message = if (isRetry) "空调开启中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您打开空调"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
            return !status
        }
    }

    override fun doCeaseConditioner(parcel: AirCmdParcel) {
        val command = parcel.cmd
        val isCareCmd = Action.CLOSE == command.action
        val isRetry = parcel.isRetry()
        if (!airGetter.isConditioner()) {//空调已关闭
            if (isCareCmd) {
                command.message = if (isRetry) "空调已经关闭了" else  "好的，已为您关闭空调"
                parcel.callback?.onCmdHandleResult(command)
            }
            return
        }
        if (!isRetry) {
            if (isCareCmd) {
                command.message = Keywords.COMMAND_FAILED
                parcel.callback?.onCmdHandleResult(command)
            }
            return
        }
        val status = airSetter.doSwitchConditioner(false)
        if (!isCareCmd) return
        if (!status) {
            command.message = "好的，已为您关闭空调"
        } else {
            command.message = if (isRetry) "空调关闭中……" else Keywords.COMMAND_FAILED
        }
        if (!status || !isRetry) {
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        }
        if (status && isRetry) {
            sendAirMessage(parcel, delayed)
        }
    }

    override fun doAdjustAirDirection(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(command.action, parcel, delayed)
            } else {
                command.message = "空调未打开，不能调节风向"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            //调节风向
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        }
    }

    override fun doAdjustAirWindSpeed(parcel: AirCmdParcel) {

    }

    override fun doAdjustAirTemperature(parcel: AirCmdParcel) {

    }

    private fun sendAirMessage(@Action what: Int, param: Any, delayed: Int = Constant.INVALID) {
        handler.removeMessages(what)
        val message = handler.obtainMessage(what)
        message.obj = param
        if (Constant.INVALID == delayed) {
            handler.sendMessage(message)
            return
        }
        handler.sendMessageDelayed(message, delayed.toLong())
    }

    private fun sendAirMessage(parcel: AirCmdParcel, delayed: Int = Constant.INVALID) {
        sendAirMessage(parcel.cmd.action, parcel, delayed)
    }

    inner class MessageInvoke : Handler.Callback {
        override fun handleMessage(message: Message): Boolean {
            val obj = message.obj
            if (obj is AirCmdParcel) {
                if (obj.cmd.action == message.what) {
                    obj.retryCount -= 1
                }
                doAirControlCommand(obj)
            }
            return true
        }
    }

    private fun interruptCommand(
        command: AirCmd,
        callback: ICmdCallback?,
        coreEngine: Boolean = false,
    ): Boolean {
        val result = if (coreEngine) {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        } else {
            !VcuUtils.isPower()
        }
        if (result) {
            command.message = "操作没有成功，请先启动发动机"
            callback?.onCmdHandleResult(command)
        }
        return result
    }


    fun doAirControlCommand(parcel: AirCmdParcel) {
        if (interruptCommand(parcel.cmd, parcel.callback)) {
            return
        }
        when (parcel.cmd.action) {
            Action.OPEN -> {
                doLaunchConditioner(parcel)
            }
            Action.CLOSE -> {
                doCeaseConditioner(parcel)
            }
            Action.PLUS -> {
                tryPlusValue(parcel)
            }
            Action.MINUS -> {
                tryMinusValue(parcel)
            }
            Action.MIN -> {
                tryMinValue(parcel)
            }
            Action.MAX -> {
                tryMaxValue(parcel)
            }
            Action.FIXED -> {
                tryFixedValue(parcel)
            }
            Action.OPTION -> {
                changeAirOption(parcel)
            }
            Action.TURN_ON -> {//撕开制冷 或 制热模式
                launchFunction(parcel)
            }
            Action.TURN_OFF -> {
                closeFunction(parcel)
            }
            else -> {}
        }
    }

    private fun changeAirOption(parcel: AirCmdParcel) {
        val command = parcel.cmd
        if (IAir.AIR_FLOW == command.air) {
            var status = true
            val command = parcel.cmd
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法使用此功能"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                val result = airSetter.doUpdateAirFlowing(command.orien, command.part)
                command.message = result
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        }

    }

    private fun closeFunction(parcel: AirCmdParcel) {
        do {
            val air = parcel.cmd.air
            var mask = IAir.MODE_COLD_HEAT
            if (mask == (air and mask)) {
                val value = parcel.cmd.value
                if (value == (mask shl 1)) {
                    closeCompressor(parcel)
                }
                if (value == (mask shl 2)) {
                    closeHeater(parcel)
                }
                break
            }
            mask = IAir.AIR_PURGE
            if (mask == (air and mask)) {
                switchAirCleaning(parcel, false)
                break
            }
            mask = IAir.LOOP_MODE
            if (mask == (air and mask)) {
                val value = parcel.cmd.value
                if (value == (mask shl 1)) {
                    switchLoopInner(parcel, false)
                }
                if (value == (mask shl 2)) {
                    switchLoopOuter(parcel, false)
                }
                if (value == (mask shl 3)) {
                    switchLoopAuto(parcel, false)
                }
                break
            }
            mask = IAir.AIR_DEFROST
            if (mask == (air and mask)) {
                switchGlassDefrost(parcel, false)
                break
            }
            mask = IAir.AIR_DOUBLE
            if (mask == (air and mask)) {
                switchDoubleMode(parcel, false)
                break
            }
            mask = IAir.AUTO_MODE
            if (mask == (air and mask)) {
                switchAutoMode(parcel, false)
                break
            }
        } while (false)
    }

    private fun switchAutoMode(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val name = "自动模式"
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启自动模式"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchAutoMode(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "${name}${onOff}中……" else "${name}${onOff}失败"
                } else {
                    command.message = "${name}已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchAutoMode(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun dependConditioner(@Action action: Int): Boolean {
        return (Action.OPEN == (Action.OPEN and action))
    }

    private fun launchFunction(parcel: AirCmdParcel) {
        do {
            val air = parcel.cmd.air
            var mask = IAir.MODE_COLD_HEAT
            if (mask == (air and mask)) {
                val value = parcel.cmd.value
                if (value == (mask shl 1)) {
                    launchCompressor(parcel)
                }
                if (value == (mask shl 2)) {
                    launchHeater(parcel)
                }
                break
            }

            mask = IAir.AIR_PURGE
            if (mask == (air and mask)) {
                switchAirCleaning(parcel, true)
                break
            }

            mask = IAir.LOOP_MODE
            if (mask == (air and mask)) {
                val value = parcel.cmd.value
                if (value == (mask shl 1)) {
                    switchLoopInner(parcel, true)
                }
                if (value == (mask shl 2)) {
                    switchLoopOuter(parcel, true)
                }
                if (value == (mask shl 3)) {
                    switchLoopAuto(parcel, true)
                }
                break
            }
            mask = IAir.AIR_DEFROST
            if (mask == (air and mask)) {
                switchGlassDefrost(parcel, true)
                break
            }

            mask = IAir.AIR_DOUBLE
            if (mask == (air and mask)) {
                switchDoubleMode(parcel, true)
                break
            }
            mask = IAir.AUTO_MODE
            if (mask == (air and mask)) {
                switchAutoMode(parcel, true)
                break
            }
        } while (false)
    }

    private fun switchDoubleMode(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val name = "双区模式"
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法${onOff}${name}"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchDoubleMode(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "${name}${onOff}中……" else "${name}${onOff}失败"
                } else {
                    command.message = "${name}已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchDoubleMode(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun switchGlassDefrost(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val isHead = IPart.HEAD == (IPart.HEAD and command.part)
        val isTail = IPart.TAIL == (IPart.TAIL and command.part)
        var name = getActionFunctionName(isHead, isTail)
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启${name}"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                var isHeadStatus = !isHead
                var isTailStatus = !isTail
                if (isHead) {
                    isHeadStatus = airSetter.doSwitchHeadDefrost(expect)
                }
                if (isTail) {
                    isTailStatus = airSetter.doSwitchTailDefrost(expect)
                }
                status = isHeadStatus && isTailStatus
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "${name}开启中……"
                    } else {
                        command.message = "${name}开启失败"
                    }
                } else {
                    command.message = "${name}已开启"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            var isHeadStatus = !isHead
            var isTailStatus = !isTail
            if (isHead) {
                isHeadStatus = airSetter.doSwitchHeadDefrost(expect)
            }
            if (isTail) {
                isTailStatus = airSetter.doSwitchTailDefrost(expect)
            }
            status = isHeadStatus && isTailStatus
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "${name}关闭中……"
                } else {
                    command.message = "${name}关闭失败"
                }
            } else {
                command.message = "${name}已关闭"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun getActionFunctionName(head: Boolean, tail: Boolean): String {
        if (head && tail) {
            return "除霜"
        }
        if (head) {
            return "前除霜"
        }
        if (tail) {
            return "后除霜"
        }
        return "除霜"
    }

    private fun switchLoopAuto(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法${onOff}自动循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchAutoLooper(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "空调自动循环${onOff}中……" else "空调自动循环${onOff}失败"
                } else {
                    command.message = "空调自动循环已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchAutoLooper(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "空调自动循环${onOff}中……" else "空调自动循环${onOff}失败"
            } else {
                command.message = "空调自动循环已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }

    }

    private fun switchLoopOuter(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法${onOff}外循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchOuterLooper(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "空调外循环${onOff}中……" else "空调外循环${onOff}失败"
                } else {
                    command.message = "空调外循环已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchOuterLooper(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "空调外循环${onOff}中……" else "空调外循环${onOff}失败"
            } else {
                command.message = "空调外循环已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }

    }

    private fun switchLoopInner(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法${onOff}内循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchInnerLooper(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "空调内循环${onOff}中……" else "空调内循环${onOff}失败"
                } else {
                    command.message = "空调内循环已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchInnerLooper(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "空调内循环${onOff}中……" else "空调内循环${onOff}失败"
            } else {
                command.message = "空调内循环已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun switchAirCleaning(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法${onOff}空气净化"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchAirClean(expect)
                if (status) {
                    command.message = if (parcel.isRetry()) "空气净化${onOff}中……" else Keywords.COMMAND_FAILED
                } else {
                    command.message = "空气净化已${onOff}"
                }
                if (!status || !parcel.isRetry()) {
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchAirClean(expect)
            if (status) {
                command.message = if (parcel.isRetry()) "空气净化${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}空气净化"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun closeCompressor(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        val onOff = "关闭"
        if (dependConditioner(command.action)) {
            status = airGetter.isConditioner()
            command.message = "空调未开启"
        }
        if (!status) {
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            return
        }
        status = airGetter.isCompressor()
        if (!status) {
            command.message = "好的，已为您${onOff}制冷模式"
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        } else {
            status = airSetter.doSwitchCompressor(false)
            if (status) {
                command.message = if (parcel.isRetry())  "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}制冷模式"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun launchHeater(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        val onOff = "打开"
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法${onOff}制热模式"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            status = airSetter.doSwitchHeater(true)
            if (status) {
                command.message = if (parcel.isRetry())  "制热模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}制热模式"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun closeHeater(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        val onOff = "关闭"
        if (dependConditioner(command.action)) {
            status = airGetter.isConditioner()
            command.message = "空调未开启，不需要${onOff}制热模式"
        }
        if (!status) {
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            return
        }
        status = airGetter.isHeater()
        if (!status) {
            command.message = "制热模式已经${onOff}"
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        } else {
            status = airSetter.doSwitchHeater(false)
            if (status) {
                command.message = if (parcel.isRetry())  "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}制热模式"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun launchCompressor(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        val onOff = "打开"
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法${onOff}制冷模式"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            status = airSetter.doSwitchCompressor(true)
            if (status) {
                command.message = if (parcel.isRetry())  "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "制冷模式已${onOff}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun tryFixedValue(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMaxValue(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinValue(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinusValue(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryPlusValue(parcel: AirCmdParcel) {
        do {
            val command = parcel.cmd
            var mask = IAir.AIR_WIND
            if (mask == (mask and command.air)) {
                adjustBlowerLevel(parcel)
                break
            }
            mask = IAir.AIR_TEMP
            if (mask == (mask and command.air)) {
                adjustAirTemperature(parcel)
                break
            }
        } while (false)
    }

    /**
     * 左温度。依据功能规范，低于17℃时显示Low，高于31℃时显示High。
    if not set ,the value of signal is 0x0(inactive)
    0x00:Inactive
    0x01 : No Temperature Display
    0x02~0x0F : Reserved
    0x10:Low
    0x11 :17℃
    0x12 :18℃
    0x13 : 19℃
    0x14 : 20℃
    0x15 : 21℃
    0x16 : 22℃
    0x17 : 23℃
    0x18 : 24℃
    0x19 : 25℃
    0x1A : 26℃
    0x1B : 27℃
    0x1C : 28℃
    0x1D : 29℃
    0x1E : 30℃
    0x1F : 31℃
    0x20 : High
     */
    private fun adjustAirTemperature(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法调整空调温度"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            val min = airGetter.tempRange.first
            val max = airGetter.tempRange.last
            var mask = IPart.LEFT_FRONT
            val isLeft = mask == (mask and command.part)
            mask = IPart.RIGHT_FRONT
            val isRight = mask == (mask and command.part)
            if (Constant.INVALID == command.expect) {
                var left = min
                var right = min
                if (isLeft) {
                    left = operationTemperature(command, min, max, IPart.LEFT_FRONT)
                }
                if (isRight) {
                    right = operationTemperature(command, min, max, IPart.RIGHT_FRONT)
                }
                if (isLeft && isRight) {
                    command.expect = min(left, right)
                } else {
                    if (isLeft) {
                        command.expect = left
                    }
                    if (isRight) {
                        command.expect = right
                    }
                }
            }
            val expect = command.expect
            status = airSetter.doUpdateTemperature(expect, expect, isLeft, isRight)
            if (status) {
                command.message = if (parcel.isRetry())  "温度调节中……" else Keywords.COMMAND_FAILED
            } else {
                val append = obtainAppend(expect, min, max)
                command.message = "好的，温度已经调到${append}"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun obtainAppend(value: Int, min: Int, max: Int): String {
        if (min == value) {
            return "最低"
        }
        if (max == value) {
            return "最高"
        }
        return value.toString()
    }

    private fun obtainActualTemperature(@IPart part: Int): Int {
        return when (part) {
            IPart.LEFT_FRONT -> airGetter.getDriverTemperature()
            IPart.RIGHT_FRONT -> airGetter.getCopilotTemperature()
            else -> airGetter.getDriverTemperature()
        }
    }

    private fun operationTemperature(cmd: AirCmd, min: Int, max: Int, @IPart part: Int): Int {
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val current = obtainActualTemperature(part)
            expect = current + cmd.step
        } else if (Action.MINUS == cmd.action) {
            val current = obtainActualTemperature(part)
            expect = current - cmd.step
        } else if (Action.FIXED == cmd.action) {
            expect = cmd.value
        } else if (Action.MIN == cmd.action) {
            expect = min
        } else if (Action.MAX == cmd.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        return expect
    }

    private fun adjustBlowerLevel(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法调整风量"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            if (Constant.INVALID == command.expect) {
                command.expect = operationBlowerLevel(command)
            }
            val expect = command.expect + 1
            status = airSetter.doUpdateBlowerLevel(expect, command.part)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "风量调节中……"
                } else {
                    command.message = Keywords.COMMAND_FAILED
                }
            } else {
                command.message = "好的，风量已经调到${expect - 1}了"
            }
            if (!status || !parcel.isRetry()) {
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun operationBlowerLevel(command: AirCmd): Int {
        var expect = 0
        if (Action.PLUS == command.action) {
            val level = airGetter.getBlowerLevel()
            expect = level + command.step
        } else if (Action.MINUS == command.action) {
            val level = airGetter.getBlowerLevel()
            expect = level - command.step
        } else if (Action.FIXED == command.action) {
            expect = command.value
        } else if (Action.MIN == command.action) {
            expect = airGetter.blowerRange.first
        } else if (Action.MAX == command.action) {
            expect = airGetter.blowerRange.last
        }
        if (expect > airGetter.blowerRange.last) expect = airGetter.blowerRange.last
        if (expect < airGetter.blowerRange.first) expect = airGetter.blowerRange.first
        return expect
    }

}