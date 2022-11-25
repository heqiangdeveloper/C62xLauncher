package com.chinatsp.settinglib.manager.cabin

import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.ShareHandler
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAir
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 19:49
 * @desc   :
 * @version: 1.0
 */
class AirSupplier(private val airManager: ACManager) : IAirMaster, ICmdExpress {

    private val airSetter: AirSetter by lazy {
        AirSetter(airManager, airGetter)
    }

    private val airGetter: AirGetter by lazy {
        AirGetter(airManager)
    }

    private val delayed: Int get() = 300

    override fun doLaunchConditioner(parcel: CommandParcel): Boolean {
        val command = parcel.command as AirCmd
        val isRetry = parcel.isRetry()
        val isCareCmd =
            Action.TURN_ON == command.action && (IAir.ENGINE == (IAir.ENGINE and command.air))
        if (airGetter.isConditioner()) {//空调已开启
            if (isCareCmd) {
                command.message = if (isRetry) "空调已经打开了" else "好的，已为您打开空调"
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
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
            return !status
        }
    }

    override fun doCeaseConditioner(parcel: CommandParcel) {
        val command = parcel.command as AirCmd
        val isCareCmd =
            Action.TURN_OFF == command.action && (IAir.ENGINE == (IAir.ENGINE and command.air))
        val isRetry = parcel.isRetry()
        if (!airGetter.isConditioner()) {//空调已关闭
            if (isCareCmd) {
                command.message = if (isRetry) "空调已经关闭了" else "好的，已为您关闭空调"
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
            parcel.callback?.onCmdHandleResult(parcel.command)
        }
        if (status && isRetry) {
            ShareHandler.loopParcel(parcel, delayed)
        }
    }

    private fun interruptCommand(
        command: AirCmd, callback: ICmdCallback?, coreEngine: Boolean = false): Boolean {
        val result = if (coreEngine) {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        } else {
            !VcuUtils.isPower()
        }
        if (result) {
            command.message = if (coreEngine) {
                Keywords.NEED_START_ENGINE
            } else {
                Keywords.NEED_START_POWER
            }
            callback?.onCmdHandleResult(command)
        }
        return result
    }


    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as AirCmd
        if (interruptCommand(command, parcel.callback, coreEngine = true)) {
            return
        }
        when (command.action) {
            Action.MIN -> {
                tryMinValue(parcel)
            }
            Action.MAX -> {
                tryMaxValue(parcel)
            }
            Action.PLUS -> {
                tryPlusValue(parcel)
            }
            Action.MINUS -> {
                tryMinusValue(parcel)
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

    private fun changeAirOption(parcel: CommandParcel) {
        val command = parcel.command as AirCmd
        if (IAir.AIR_FLOW == command.air) {
            val result = airSetter.doUpdateAirFlowing(command.orien, command.part)
            command.message = result
            parcel.callback?.onCmdHandleResult(parcel.command)
        }

    }

    private fun closeFunction(parcel: CommandParcel) {
        do {
            val command = parcel.command as AirCmd
            //关空调
            var mask = IAir.ENGINE
            if (mask == (mask and command.air)) {
                doCeaseConditioner(parcel)
                break
            }
            //关制热/制冷 模式
            mask = IAir.COLD_HEAT
            if (mask == (mask and command.air)) {
                val value = command.value
                if (value == (mask shl 1)) {
                    closeCompressor(parcel)
                }
                if (value == (mask shl 2)) {
                    closeHeater(parcel)
                }
                break
            }
            //关空气净化
            mask = IAir.AIR_PURGE
            if (mask == (mask and command.air)) {
                switchAirCleaning(parcel, false)
                break
            }
            //关循环模式（内外循环）
            mask = IAir.LOOP_MODE
            if (mask == (mask and command.air)) {
                val value = parcel.command.value
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
            //关除霜模式
            mask = IAir.AIR_DEFROST
            if (mask == (mask and command.air)) {
                switchGlassDefrost(parcel, false)
                break
            }
            //关双区模式
            mask = IAir.AIR_DOUBLE
            if (mask == (mask and command.air)) {
                switchDoubleMode(parcel, false)
                break
            }
            //关自动模式
            mask = IAir.AUTO_MODE
            if (mask == (mask and command.air)) {
                switchAutoMode(parcel, false)
                break
            }
        } while (false)
    }

    private fun switchAutoMode(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val name = "自动模式"
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchAutoMode(expect)
            if (status) {
                command.message = if (isRetry) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        } else {
            status = airSetter.doSwitchAutoMode(expect)
            if (status) {
                command.message = if (isRetry) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

//    private fun dependConditioner(@Action action: Int): Boolean {
//        return (Action.OPEN == (Action.OPEN and action))
//    }

    private fun launchFunction(parcel: CommandParcel) {
        do {
            val command = parcel.command as AirCmd
            var mask = IAir.ENGINE
            if (mask == (mask and command.air)) {
                doLaunchConditioner(parcel)
                break
            }
            mask = IAir.COLD_HEAT
            if (mask == (mask and command.air)) {
                val value = command.value
                if (value == (mask shl 1)) {
                    launchCompressor(parcel)
                }
                if (value == (mask shl 2)) {
                    launchHeater(parcel)
                }
                break
            }
            mask = IAir.AIR_PURGE
            if (mask == (mask and command.air)) {
                switchAirCleaning(parcel, true)
                break
            }
            mask = IAir.LOOP_MODE
            if (mask == (mask and command.air)) {
                val value = parcel.command.value
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
            if (mask == (mask and command.air)) {
                switchGlassDefrost(parcel, true)
                break
            }
            mask = IAir.AIR_DOUBLE
            if (mask == (mask and command.air)) {
                switchDoubleMode(parcel, true)
                break
            }
            mask = IAir.AUTO_MODE
            if (mask == (mask and command.air)) {
                switchAutoMode(parcel, true)
                break
            }
        } while (false)
    }

    private fun switchDoubleMode(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val name = "双区模式"
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchDoubleMode(expect)
            if (status) {
                command.message =
                    if (isRetry) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        } else {
            status = airSetter.doSwitchDoubleMode(expect)
            if (status) {
                command.message =
                    if (isRetry) "${name}${onOff}中……" else "${name}${onOff}失败"
            } else {
                command.message = "${name}已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun switchGlassDefrost(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val isHead = IPart.HEAD == (IPart.HEAD and command.part)
        val isTail = IPart.TAIL == (IPart.TAIL and command.part)
        val name = getActionFunctionName(isHead, isTail)
        if (expect) {
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
                if (isRetry) {
                    command.message = "${name}开启中……"
                } else {
                    command.message = "${name}开启失败"
                }
            } else {
                command.message = "${name}已开启"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
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
                if (isRetry) {
                    command.message = "${name}关闭中……"
                } else {
                    command.message = "${name}关闭失败"
                }
            } else {
                command.message = "${name}已关闭"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun getActionFunctionName(head: Boolean, tail: Boolean): String {
        if (head && tail) {
            return "除霜"
        }
        if (head && !tail) {
            return "前除霜"
        }
        if (tail && !head) {
            return "后除霜"
        }
        return "除霜"
    }

    private fun switchLoopAuto(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchAutoLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调自动循环${onOff}中……" else "空调自动循环${onOff}失败"
            } else {
                command.message = "空调自动循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }

        } else {
            status = airSetter.doSwitchAutoLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调自动循环${onOff}中……" else "空调自动循环${onOff}失败"
            } else {
                command.message = "空调自动循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun switchLoopOuter(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchOuterLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调外循环${onOff}中……" else "空调外循环${onOff}失败"
            } else {
                command.message = "空调外循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        } else {
            status = airSetter.doSwitchOuterLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调外循环${onOff}中……" else "空调外循环${onOff}失败"
            } else {
                command.message = "空调外循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun switchLoopInner(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchInnerLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调内循环${onOff}中……" else "空调内循环${onOff}失败"
            } else {
                command.message = "空调内循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }

        } else {
            status = airSetter.doSwitchInnerLooper(expect)
            if (status) {
                command.message = if (isRetry) "空调内循环${onOff}中……" else "空调内循环${onOff}失败"
            } else {
                command.message = "空调内循环已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun switchAirCleaning(parcel: CommandParcel, expect: Boolean) {
        val status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = if (expect) "打开" else "关闭"
        if (expect) {
            status = airSetter.doSwitchAirClean(expect)
            if (status) {
                command.message =
                    if (isRetry) "空气净化${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "空气净化已${onOff}"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        } else {
            status = airSetter.doSwitchAirClean(expect)
            if (status) {
                command.message = if (isRetry) "空气净化${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}空气净化"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun closeCompressor(parcel: CommandParcel) {
        var status: Boolean
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = "关闭"
        status = airGetter.isCompressor()
        if (!status) {
            command.message = "好的，已为您${onOff}制冷模式"
            parcel.callback?.onCmdHandleResult(parcel.command)
        } else {
            status = airSetter.doSwitchCompressor(false)
            if (status) {
                command.message =
                    if (isRetry) "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}制冷模式"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun launchHeater(parcel: CommandParcel) {
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = "打开"
        val status: Boolean = airSetter.doSwitchHeater(true)
        if (status) {
            command.message = if (isRetry) "制热模式${onOff}中……" else Keywords.COMMAND_FAILED
        } else {
            command.message = "好的，已为您${onOff}制热模式"
        }
        if (!status || !isRetry) {
            parcel.callback?.onCmdHandleResult(parcel.command)
        }
        if (status && isRetry) {
            ShareHandler.loopParcel(parcel, delayed)
        }
    }

    private fun closeHeater(parcel: CommandParcel) {
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = "关闭"
        var status: Boolean = airGetter.isHeater()
        if (!status) {
            command.message = "制热模式已经${onOff}"
            parcel.callback?.onCmdHandleResult(parcel.command)
        } else {
            status = airSetter.doSwitchHeater(false)
            if (status) {
                command.message = if (isRetry) "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
            } else {
                command.message = "好的，已为您${onOff}制热模式"
            }
            if (!status || !isRetry) {
                parcel.callback?.onCmdHandleResult(parcel.command)
            }
            if (status && isRetry) {
                ShareHandler.loopParcel(parcel, delayed)
            }
        }
    }

    private fun launchCompressor(parcel: CommandParcel) {
        val command = parcel.command
        val isRetry = parcel.isRetry()
        val onOff = "打开"
        val status: Boolean = airSetter.doSwitchCompressor(true)
        if (status) {
            command.message = if (isRetry) "制冷模式${onOff}中……" else Keywords.COMMAND_FAILED
        } else {
            command.message = "制冷模式已${onOff}"
        }
        if (!status || !isRetry) {
            parcel.callback?.onCmdHandleResult(parcel.command)
        }
        if (status && isRetry) {
            ShareHandler.loopParcel(parcel, delayed)
        }

    }

    private fun tryFixedValue(parcel: CommandParcel) {
        val command = parcel.command as AirCmd
        var function = command.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = command.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMaxValue(parcel: CommandParcel) {
        val command = parcel.command as AirCmd
        var function = command.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = command.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinValue(parcel: CommandParcel) {
        val cmd = parcel.command as AirCmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryMinusValue(parcel: CommandParcel) {
        val cmd = parcel.command as AirCmd
        var function = cmd.air and IAir.AIR_WIND
        if (function == IAir.AIR_WIND) {
            adjustBlowerLevel(parcel)
        }
        function = cmd.air and IAir.AIR_TEMP
        if (function == IAir.AIR_TEMP) {
            adjustAirTemperature(parcel)
        }
    }

    private fun tryPlusValue(parcel: CommandParcel) {
        do {
            val command = parcel.command as AirCmd
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
     * if not set,the value of signal is 0x0(inactive) 0x00:Inactive; 0x01: No Temperature Display
     * 0x02~0x0F: Reserved; 0x10:Low; 0x11:17℃，0x12:18℃………… 0x1E: 30℃; 0x1F: 31℃; 0x20: High
     */
    private fun adjustAirTemperature(parcel: CommandParcel) {
        val command = parcel.command as AirCmd
        val min = airGetter.tempRange.first
        val max = airGetter.tempRange.last
        val isDouble = airGetter.isDoubleMode()
        var lfAct = IPart.L_F == (IPart.L_F and command.part)
        var rfAct = IPart.R_F == (IPart.R_F and command.part)
        if (IPart.VOID != command.soundDirection && lfAct && rfAct) {
            val isLeft = IPart.L_F == command.soundDirection
            if (isLeft) {
                rfAct = isDouble
            } else {
                lfAct = false
            }
        }
        if (IStatus.INIT == command.status) {
            parcel.retryCount = 3
            if (lfAct) {
                command.lfExpect = opnTempLevel(command, min, max, IPart.L_F)
                command.resetSent(IPart.L_F)
            }
            if (rfAct) {
                command.rfExpect = opnTempLevel(command, min, max, IPart.R_F)
                command.resetSent(IPart.R_F)
            }
        }
        val lfExpect = command.lfExpect
        val rfExpect = command.rfExpect
        val lfActual = if (lfAct) obtainTempeLevel(IPart.L_F) else lfExpect
        val rfActual = if (rfAct) obtainTempeLevel(IPart.R_F) else rfExpect
        val isRetry = parcel.isRetry()
        Timber.e("updateTemp lfExpect:$lfExpect, rfExpect:$rfExpect, lfActual:$lfActual, " +
                "rfActual:$rfActual, isDouble:$isDouble, isRetry:$isRetry, lfAct:$lfAct, rfAct:$rfAct")
        if (lfAct && rfAct) {
            if (isDouble) {
                if (lfExpect == rfExpect) {
                    if (lfActual == lfExpect) {
                        command.message = "好的，空调温度已经调到${tempHint(lfExpect, min, max)}了"
                    } else {
                        if (!isRetry) {
                            command.message = Keywords.COMMAND_FAILED
                        } else {
                            if (IStatus.RUNNING != command.status && !command.isSent(IPart.L_F)) {
                                airSetter.doUpdateTemp(IPart.L_F, lfExpect, command)
                                command.status = IStatus.RUNNING
                                command.message = "好的，空调温度已经调到${tempHint(lfExpect, min, max)}"
                            }
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                } else {
                    if (!isRetry) {
                        command.message = Keywords.COMMAND_FAILED
                    } else {
                        if (IStatus.INIT == command.status) {
                            airSetter.doSwitchDoubleMode(false)
                            command.status = IStatus.PREPARED
                        }
                        if (IStatus.PREPARED == command.status) {
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                }
            } else {
                val lfSuccess = lfActual == lfExpect
                val rfSuccess = rfActual == rfExpect
                if (lfExpect == rfExpect) {
                    if (lfSuccess && rfSuccess) {
                        command.message = if (IStatus.RUNNING == command.status) {
                            "好的，空调温度已经调到${tempHint(lfExpect, min, max)}了"
                        } else {
                            "您好，空调温度当前已经是${tempHint(rfExpect, min, max)}了"
                        }
                    } else {
                        if (!isRetry) {
                            command.message = if (lfSuccess && !rfSuccess) {
                                "你好，主驾温度已经调到${tempHint(lfExpect, min, max)}, 副驾温度调节没有成功"
                            } else if (!lfSuccess && rfSuccess) {
                                "你好，副驾温度已经调到${tempHint(rfExpect, min, max)}, 主驾温度调节没有成功"
                            } else {
                                Keywords.COMMAND_FAILED
                            }
                        } else {
                            if (!lfSuccess && !command.isSent(IPart.L_F)) {
                                airSetter.doUpdateTemp(IPart.L_F, lfExpect, command)
                                command.status = IStatus.RUNNING
                                ShareHandler.loopParcel(parcel, delayed)
                                return
                            }
                            if (!rfSuccess && !command.isSent(IPart.R_F)) {
                                airSetter.doUpdateTemp(IPart.R_F, rfExpect, command)
                                command.status = IStatus.RUNNING
                                ShareHandler.loopParcel(parcel, delayed)
                                return
                            }
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                } else {
                    if (lfSuccess && rfSuccess) {
                        command.message = if (IStatus.RUNNING == command.status) {
                            "好的，主驾温度已经调到${tempHint(lfExpect, min, max)}, " +
                                    "副驾温度已经调到${tempHint(rfExpect, min, max)}了"
                        } else {
                            "您好，主驾温度已经调到${tempHint(lfExpect, min, max)}, " +
                                    "副驾温度已经调到${tempHint(rfExpect, min, max)}了"
                        }
                    } else {
                        if (!isRetry) {
                            command.message = if (lfSuccess && !rfSuccess) {
                                "你好，主驾温度已经调到${tempHint(lfExpect, min, max)}, 副驾温度调节没有成功"
                            } else if (!lfSuccess && rfSuccess) {
                                "你好，副驾温度已经调到${tempHint(rfExpect, min, max)}, 主驾温度调节没有成功"
                            } else {
                                Keywords.COMMAND_FAILED
                            }
                        } else {
                            if (!lfSuccess && !command.isSent(IPart.L_F)) {
                                airSetter.doUpdateTemp(IPart.L_F, lfExpect, command)
                                command.status = IStatus.RUNNING
                                ShareHandler.loopParcel(parcel, delayed)
                                return
                            }
                            if (!rfSuccess && !command.isSent(IPart.R_F)) {
                                airSetter.doUpdateTemp(IPart.R_F, rfExpect, command)
                                command.status = IStatus.RUNNING
                                ShareHandler.loopParcel(parcel, delayed)
                                return
                            }
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                }
            }
        } else if (lfAct && !rfAct) {
            if (lfActual == lfExpect) {
                command.message = if (IStatus.RUNNING == command.status) {
                    "好的，主驾温度已经调到${tempHint(lfExpect, min, max)}了"
                } else {
                    "您好，主驾温度当前已经是${tempHint(lfExpect, min, max)}了"
                }
            } else {
                if (isDouble) {
                    if (!isRetry) {
                        command.message = Keywords.COMMAND_FAILED
                    } else {
                        if (IStatus.INIT == command.status) {
                            airSetter.doSwitchDoubleMode(false)
                            command.status = IStatus.PREPARED
                        }
                        if (IStatus.PREPARED == command.status) {
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                } else {
                    if (!isRetry) {
                        command.message = Keywords.COMMAND_FAILED
                    } else {
                        if (IStatus.RUNNING != command.status) {
                            airSetter.doUpdateTemp(IPart.L_F, lfExpect, command)
                            command.status = IStatus.RUNNING
                        }
                        ShareHandler.loopParcel(parcel, delayed)
                        return
                    }
                }
            }
        } else if (!lfAct && rfAct) {
            if (rfActual == rfExpect) {
                command.message = if (IStatus.RUNNING == command.status) {
                    "好的，副驾温度已经调到${tempHint(rfExpect, min, max)}了"
                } else {
                    "您好，副驾温度当前已经是${tempHint(rfExpect, min, max)}了"
                }
            } else {
                if (isDouble) {
                    if (!isRetry) {
                        command.message = Keywords.COMMAND_FAILED
                    } else {
                        if (IStatus.INIT == command.status) {
                            airSetter.doSwitchDoubleMode(false)
                            command.status = IStatus.PREPARED
                        }
                        if (IStatus.PREPARED == command.status) {
                            ShareHandler.loopParcel(parcel, delayed)
                            return
                        }
                    }
                } else {
                    if (!isRetry) {
                        command.message = Keywords.COMMAND_FAILED
                    } else {
                        if (IStatus.RUNNING != command.status) {
                            airSetter.doUpdateTemp(IPart.R_F, rfExpect, command)
                            command.status = IStatus.RUNNING
                        }
                        ShareHandler.loopParcel(parcel, delayed)
                        return
                    }
                }
            }
        }
        parcel.callback?.onCmdHandleResult(parcel.command)
    }

    private fun tempHint(value: Int, min: Int, max: Int): String {
        if (value <= min) {
            return "最低"
        }
        if (value >= max) {
            return "最高"
        }
        return "${value}摄氏度"
    }

    private fun obtainTempeLevel(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> airGetter.getDriverTemperature()
            IPart.R_F -> airGetter.getCopilotTemperature()
            else -> airGetter.getDriverTemperature()
        }
    }

    private fun opnTempLevel(cmd: AirCmd, min: Int, max: Int, @IPart part: Int): Int {
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val current = obtainTempeLevel(part)
            expect = current + cmd.step
        } else if (Action.MINUS == cmd.action) {
            val current = obtainTempeLevel(part)
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

    private fun adjustBlowerLevel(parcel: CommandParcel) {
        val isRetry = parcel.isRetry()
        val command = parcel.command as AirCmd
        if (Constant.INVALID == command.lfExpect) {
            command.lfExpect = operationBlowerLevel(command)
        }
        val expect = command.lfExpect + 1
        val status = airSetter.doUpdateBlowerLevel(expect, command.part)
        if (status) {
            if (isRetry) {
                command.message = "风量调节中……"
            } else {
                command.message = Keywords.COMMAND_FAILED
            }
        } else {
            command.message = "好的，风量已经调到${expect - 1}了"
        }
        if (!status || !isRetry) {
            parcel.callback?.onCmdHandleResult(parcel.command)
        }
        if (status && isRetry) {
            ShareHandler.loopParcel(parcel, delayed)
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