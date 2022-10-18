package com.chinatsp.settinglib.manager.cabin

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.AirCmdParcel
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAir
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.AirCmd
import timber.log.Timber

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

    private val delayed: Int get() = 100


    override fun doLaunchConditioner(parcel: AirCmdParcel): Boolean {
        val command = parcel.cmd
        val isCareCmd = Action.OPEN == command.action
        if (airGetter.isConditioner()) {//空调已开启
            if (isCareCmd) {
                //在空调已经开启的状态下 试图两次打开空调
                if (parcel.isRetry()) {
                    command.message = "空调已打开"
                } else {
                    //空调成功被打开
                    command.message = "空调打开成功"
                }
                parcel.callback?.onCmdHandleResult(command)
            }
            return true
        }
        if (parcel.isRetry()) {
            val status = airSetter.doSwitchConditioner(true)
            if (isCareCmd) {
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "空调开启中……"
                    } else {
                        command.message = "空调开启失败"
                    }
                } else {
                    command.message = "空调已打开"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }

            } else {
                command.message = "空调打开失败！"
                parcel.callback?.onCmdHandleResult(command)
            }
        }
        return false
    }

    override fun doCeaseConditioner(parcel: AirCmdParcel) {
        val isEngine = airGetter.isConditioner()
        val cmd = parcel.cmd
        val isCareCmd = Action.CLOSE == cmd.action
        Timber.d("doCeaseAirEngine ------------- isEngine:$isEngine, isCareCmd:$isCareCmd")
        if (!isEngine) {//空调已关闭
            if (isCareCmd) {
                //在空调已经开启的状态下 试图两次打开空调
                if (parcel.isRetry()) {
                    cmd.message = "空调已处在关闭状态"
                } else {
                    //空调成功被打开
                    cmd.message = "空调关闭成功"
                }
                parcel.callback?.onCmdHandleResult(cmd)
            }
            return
        }
        if (parcel.isRetry()) {
            airSetter.doSwitchConditioner(false)
            if (isCareCmd) {
                sendAirMessage(parcel.cmd.action, parcel, delayed)
                cmd.message = "好的，空调关闭中……"
                parcel.callback?.onCmdHandleResult(cmd)
            }
        } else {
            if (isCareCmd) {
                cmd.message = "空调关闭失败了！"
                parcel.callback?.onCmdHandleResult(cmd)
            }
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

    fun doAirControlCommand(parcel: AirCmdParcel) {
        Timber.d("doAirControlCommand ------------- action:${parcel.cmd.action}")
        when (parcel.cmd.action) {
            Action.OPEN -> {
                doLaunchConditioner(parcel)
            }
            Action.CLOSE -> {
                doCeaseConditioner(parcel)
            }
            Action.PLUS -> {
                tryPlusQuantity(parcel)
            }
            Action.MINUS -> {
                tryMinusQuantity(parcel)
            }
            Action.MIN -> {
                tryMinQuantity(parcel)
            }
            Action.MAX -> {
                tryMaxQuantity(parcel)
            }
            Action.FIXED -> {
                tryFixedQuantity(parcel)
            }
            Action.OPTION -> {
                changeAirOption(parcel)
            }
            Action.TURN_ON -> {//撕开制冷 或 制热模式
                launchCoolingOrHeating(parcel)
            }
            Action.TURN_OFF -> {
                ceaseCoolingOrHeating(parcel)
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

    private fun ceaseCoolingOrHeating(parcel: AirCmdParcel) {
        do {
            val air = parcel.cmd.air
            var mask = IAir.MODE_COLD
            if (mask == (air and mask)) {
                closeCompressor(parcel)
                break
            }
            mask = IAir.MODE_HOT
            if (mask == (air and mask)) {
                closeHeater(parcel)
                break
            }
            mask = IAir.AIR_PURGE
            if (mask == (air and mask)) {
                switchAirCleaning(parcel, false)
                break
            }
            mask = IAir.LOOP_INNER
            if (mask == (air and mask)) {
                switchLoopInner(parcel, false)
                break
            }
            mask = IAir.LOOP_OUTER
            if (mask == (air and mask)) {
                switchLoopOuter(parcel, false)
                break
            }
            mask = IAir.LOOP_AUTO
            if (mask == (air and mask)) {
                switchLoopAuto(parcel, false)
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
        } while (false)
    }

    private fun dependConditioner(@Action action: Int): Boolean {
        return (Action.OPEN == (Action.OPEN and action))
    }

    private fun launchCoolingOrHeating(parcel: AirCmdParcel) {
        do {
            val air = parcel.cmd.air
            var mask = IAir.MODE_COLD
            if (mask == (air and mask)) {
                launchCompressor(parcel)
                break
            }
            mask = IAir.MODE_HOT
            if (mask == (air and mask)) {
                launchHeater(parcel)
                break
            }

            mask = IAir.AIR_PURGE
            if (mask == (air and mask)) {
                switchAirCleaning(parcel, true)
                break
            }

            mask = IAir.LOOP_INNER
            if (mask == (air and mask)) {
                switchLoopInner(parcel, true)
                break
            }
            mask = IAir.LOOP_OUTER
            if (mask == (air and mask)) {
                switchLoopOuter(parcel, true)
                break
            }
            mask = IAir.LOOP_AUTO
            if (mask == (air and mask)) {
                switchLoopAuto(parcel, true)
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

        } while (false)
    }

    private fun switchDoubleMode(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启双区模式"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchDoubleMode(expect)
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "双区模式开启中……"
                    } else {
                        command.message = "双区模式开启失败"
                    }
                } else {
                    command.message = "空双区模式已开启"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchDoubleMode(expect)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "双区模式关闭中……"
                } else {
                    command.message = "双区模式关闭失败"
                }
            } else {
                command.message = "双区模式已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
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
                parcel.callback?.onCmdHandleResult(parcel.cmd)
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
            parcel.callback?.onCmdHandleResult(parcel.cmd)
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
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启自动循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchAutoLooper(expect)
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "空调自动循环开启中……"
                    } else {
                        command.message = "空调自动循环开启失败"
                    }
                } else {
                    command.message = "空调自动循环已开启"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchAutoLooper(expect)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "空调自动循环关闭中……"
                } else {
                    command.message = "空调自动循环关闭失败"
                }
            } else {
                command.message = "空调自动循环已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }

    }

    private fun switchLoopOuter(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启外循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchOuterLooper(expect)
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "空调外循环开启中……"
                    } else {
                        command.message = "空调外循环开启失败"
                    }
                } else {
                    command.message = "空调外循环已开启"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchOuterLooper(expect)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "空调外循环关闭中……"
                } else {
                    command.message = "空调外循环关闭失败"
                }
            } else {
                command.message = "空调外循环已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }

    }

    private fun switchLoopInner(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法开启内循环"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchInnerLooper(expect)
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "空调内循环开启中……"
                    } else {
                        command.message = "空调内循环开启失败"
                    }
                } else {
                    command.message = "空调内循环已开启"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchInnerLooper(expect)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "空调内循环关闭中……"
                } else {
                    command.message = "空调内循环关闭失败"
                }
            } else {
                command.message = "空调内循环已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun switchAirCleaning(parcel: AirCmdParcel, expect: Boolean) {
        var status = true
        val command = parcel.cmd
        if (expect) {
            if (dependConditioner(command.action)) {
                status = doLaunchConditioner(parcel)
            }
            if (!status) {
                if (parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                } else {
                    command.message = "空调未开启，暂无法使用空气净化"
                    parcel.callback?.onCmdHandleResult(parcel.cmd)
                }
            } else {
                status = airSetter.doSwitchAirClean(expect)
                if (status) {
                    if (parcel.isRetry()) {
                        command.message = "空气净化开启中……"
                    } else {
                        command.message = "空气净化开启失败"
                    }
                } else {
                    command.message = "空气净化已开启"
                }
                parcel.callback?.onCmdHandleResult(parcel.cmd)
                if (status && parcel.isRetry()) {
                    sendAirMessage(parcel, delayed)
                }
            }
        } else {
            status = airSetter.doSwitchAirClean(expect)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "空气净化关闭中……"
                } else {
                    command.message = "空气净化关闭失败"
                }
            } else {
                command.message = "空气净化已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun closeCompressor(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
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
            command.message = "制冷模式已关闭"
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        } else {
            status = airSetter.doSwitchCompressor(false)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "制冷模式关闭中……"
                } else {
                    command.message = "制冷模式关闭失败"
                }
            } else {
                command.message = "制冷模式已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun launchHeater(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法开启制热模式"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            status = airSetter.doSwitchHeater(true)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "制热模式开启中……"
                } else {
                    command.message = "制热模式开启失败"
                }
            } else {
                command.message = "制热模式已开启"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun closeHeater(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = airGetter.isConditioner()
            command.message = "空调未开启，不需要关闭制热模式"
        }
        if (!status) {
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            return
        }
        status = airGetter.isHeater()
        if (!status) {
            command.message = "制热模式已关闭"
            parcel.callback?.onCmdHandleResult(parcel.cmd)
        } else {
            status = airSetter.doSwitchHeater(false)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "制热模式关闭中……"
                } else {
                    command.message = "制热模式关闭失败"
                }
            } else {
                command.message = "制热模式已关闭"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun launchCompressor(parcel: AirCmdParcel) {
        var status = true
        val command = parcel.cmd
        if (dependConditioner(command.action)) {
            status = doLaunchConditioner(parcel)
        }
        if (!status) {
            if (parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            } else {
                command.message = "空调未开启，暂无法开启制冷模式"
                parcel.callback?.onCmdHandleResult(parcel.cmd)
            }
        } else {
            status = airSetter.doSwitchCompressor(true)
            if (status) {
                if (parcel.isRetry()) {
                    command.message = "制冷模式开启中……"
                } else {
                    command.message = "制冷模式开启失败"
                }
            } else {
                command.message = "制冷模式已开启"
            }
            parcel.callback?.onCmdHandleResult(parcel.cmd)
            if (status && parcel.isRetry()) {
                sendAirMessage(parcel, delayed)
            }
        }
    }

    private fun tryFixedQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerRateLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMaxQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerRateLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerRateLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinusQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerRateLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryPlusQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerRateLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
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
        val cmd = parcel.cmd
        val min = airGetter.tempRange.first
        val max = airGetter.tempRange.last
        val isLeft = IPart.LEFT_FRONT == (IPart.LEFT_FRONT and cmd.part)
        val isRight = IPart.RIGHT_FRONT == (IPart.RIGHT_FRONT and cmd.part)
        var left = min
        var right = min
        if (isLeft) {
            left = operationExpectTemperature(cmd, min, max, IPart.LEFT_FRONT)
        }
        if (isRight) {
            right = operationExpectTemperature(cmd, min, max, IPart.RIGHT_FRONT)
        }
        if (isLeft) {
            airSetter.doUpdateTemperature(left, IPart.LEFT_FRONT)
        }
        if (isRight) {
            airSetter.doUpdateTemperature(right, IPart.RIGHT_FRONT)
        }
        cmd.message = "已试图调温度到$left}"
        parcel.callback?.onCmdHandleResult(cmd)
    }

    private fun obtainActualTemperature(@IPart part: Int): Int {
        return when (part) {
            IPart.LEFT_FRONT -> airGetter.getDriverTemperature()
            IPart.RIGHT_FRONT -> airGetter.getCopilotTemperature()
            else -> airGetter.getDriverTemperature()
        }
    }

    private fun operationExpectTemperature(cmd: AirCmd, min: Int, max: Int, @IPart part: Int): Int {
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

    private fun adjustBlowerRateLevel(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val level = airGetter.getBlowerRateLevel()
            expect = level + cmd.step
        } else if (Action.MINUS == cmd.action) {
            val level = airGetter.getBlowerRateLevel()
            expect = level - cmd.step
        } else if (Action.FIXED == cmd.action) {
            expect = cmd.value
        } else if (Action.MIN == cmd.action) {
            expect = airGetter.blowerRange.first
        } else if (Action.MAX == cmd.action) {
            expect = airGetter.blowerRange.last
        }
        if (expect > airGetter.blowerRange.last) expect = airGetter.blowerRange.last
        if (expect < airGetter.blowerRange.first) expect = airGetter.blowerRange.first
        airSetter.doUpdateBlowerRate(expect, cmd.part)
        cmd.message = "已试图调风量到$expect}"
        parcel.callback?.onCmdHandleResult(cmd)
    }


}