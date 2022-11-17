package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.*
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */
class WindowManager private constructor() : BaseManager(), ISwitchManager, ICmdExpress {
//    天窗开启控制语音开启的判定条件：
//    车速小于120KM/H(车速使用仪表的车速！！)，
//    超速时，用户触发语音开启指令后弹窗及语音播报提示“车速过快，建议不要开启天窗” 此时不用说“好的”
//    此功能仅针对天窗的打开信号进行判断，关闭指令不受影响

    private var lucyReference: WeakReference<CommandParcel>? = null
    private var louverReference: WeakReference<CommandParcel>? = null

    private fun getCabinSignalValue(signal: Int): Int {
        return readIntProperty(signal, Origin.CABIN)
    }

    private val autoCloseWinInRain: SwitchState by lazy {
        val node = SwitchNode.WIN_CLOSE_WHILE_RAIN
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val autoCloseWinAtLock: SwitchState by lazy {
        val node = SwitchNode.WIN_CLOSE_FOLLOW_LOCK
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val winRemoteControl: SwitchState by lazy {
        val node = SwitchNode.WIN_REMOTE_CONTROL
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val rainWiperRepair: SwitchState by lazy {
        val node = SwitchNode.RAIN_WIPER_REPAIR
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    companion object : ISignal {
        override val TAG: String = WindowManager::class.java.simpleName
        val instance: WindowManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WindowManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.WIN_CLOSE_WHILE_RAIN.get.signal)
                add(SwitchNode.WIN_CLOSE_FOLLOW_LOCK.get.signal)
                add(SwitchNode.WIN_REMOTE_CONTROL.get.signal)
                add(SwitchNode.RAIN_WIPER_REPAIR.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> autoCloseWinInRain.copy()
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> autoCloseWinAtLock.copy()
            SwitchNode.WIN_REMOTE_CONTROL -> winRemoteControl.copy()
            SwitchNode.RAIN_WIPER_REPAIR -> rainWiperRepair.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.WIN_REMOTE_CONTROL -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.RAIN_WIPER_REPAIR -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is ISwitchListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore[serial] = WeakReference(listener)
            } finally {
                writeLock.unlock()
            }
            return serial
        }
        return -1
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            SwitchNode.WIN_CLOSE_WHILE_RAIN.get.signal -> {
                onSwitchChanged(SwitchNode.WIN_CLOSE_WHILE_RAIN, autoCloseWinInRain, property)
            }
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK.get.signal -> {
                onSwitchChanged(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, autoCloseWinAtLock, property)
            }
            SwitchNode.WIN_REMOTE_CONTROL.get.signal -> {
                val node = SwitchNode.WIN_REMOTE_CONTROL
                var convert = convert(property, node.get.on, 0x0)
                if (null == convert) convert = property
                onSwitchChanged(node, winRemoteControl, convert)
//                onSwitchChanged(SwitchNode.WIN_REMOTE_CONTROL, winRemoteControl, property)
            }
            SwitchNode.RAIN_WIPER_REPAIR.get.signal -> {
                onSwitchChanged(SwitchNode.RAIN_WIPER_REPAIR, rainWiperRepair, property)
            }
            else -> {}
        }
    }

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
    }

    private fun doControlWiper(parcel: CommandParcel) {
//        AVN request front washer and wiper on or off[0x1,0,0x0,0x3]
//        0x0: Inactive   0x1: On  0x2: Off(Not used)   0x3: Not used
        val command = parcel.command
        var value = Constant.INVALID
        var actionName = Keywords.COMMAND_FAILED
        if (Action.TURN_ON == command.action) {
            value = 0x1
            actionName = "打开"
        } else if (Action.TURN_OFF == command.action) {
            value = 0x2
            actionName = "关闭"
        }
        if (Constant.INVALID == value) {
            command.message = actionName
        } else {
            var fStatus = IPart.HEAD != (IPart.HEAD and command.part)
            var bStatus = IPart.TAIL != (IPart.TAIL and command.part)
            if (!fStatus) {
                val signal = CarCabinManager.ID_AVN_FRONT_WIPER_REQ
                fStatus = writeProperty(signal, value, Origin.CABIN)
            }
            if (!bStatus) {
                val signal = CarCabinManager.ID_AVN_REAR_WASHER_WIPER
                bStatus = writeProperty(signal, value, Origin.CABIN)
            }
            val message = if (fStatus or bStatus) "好的, ${command.slots?.name}${actionName}了"
            else "好的, ${command.slots?.name}已经${actionName}了"
            command.message = message
        }
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doControlSwitchWindow(parcel: CommandParcel) {
        var expect = Constant.INVALID
        val minValue = 0x01
        val maxValue = 0xC9
        val command = parcel.command
        if (Action.FIXED == command.action) {
            expect = (command.value.toFloat() * 2).roundToInt() + minValue
        }
        if (Action.OPEN == command.action || Action.MAX == command.action) {
            expect = maxValue
        }
        if (Action.CLOSE == command.action || Action.MIN == command.action) {
            expect = minValue
        }
        if (expect > maxValue) expect = maxValue
        //当 expect < 0x6 时，即开度小于10%（为关），
        if (expect < minValue) expect = minValue
        if (minValue == expect) {
            doControlSwitchWindow(parcel, false)
            return
        }
        if (maxValue == expect) {
            doControlSwitchWindow(parcel, true)
            return
        }
        command.message = Keywords.COMMAND_FAILED
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doControlSwitchWindow(parcel: CommandParcel, status: Boolean) {
        val pair = updateWindowSwitch(parcel, status)
        val name = parcel.command.slots?.name ?: Keywords.WINDOW
        val append: String = if (status) "打开" else "关闭"
        if (pair.first) {
            parcel.command.status = IStatus.RUNNING
            parcel.command.message = "好的，${name}${append}了, ${pair.second}"
        } else {
            parcel.command.status = IStatus.SUCCESS
            parcel.command.message = "${name}已经${append}了, ${pair.second}"
        }
        parcel.callback?.onCmdHandleResult(parcel.command)
    }


    private fun doControlWindowLevel(command: CarCmd, callback: ICmdCallback?) {
        var expect = Constant.INVALID
        val minValue = 0x01
        val maxValue = 0xC9
        if (Action.FIXED == command.action) {
            expect = (command.value.toFloat() * 2).roundToInt() + minValue
        }
        if (Action.OPEN == command.action || Action.MAX == command.action) {
            expect = maxValue
        }
        if (Action.CLOSE == command.action || Action.MIN == command.action) {
            expect = minValue
        }
        if (Action.PLUS == command.action) {
            val position = obtainWindowDegree(command.part)
            expect = position + minValue + command.step
        }
        if (Action.MINUS == command.action) {
            val position = obtainWindowDegree(command.part)
            expect = position + minValue - command.step
        }
        if (expect > maxValue) expect = maxValue
        //当 expect < 0x6 时，即开度小于10%（为关），
        if (expect < minValue) expect = minValue
        doControlWindowLevel(command, callback, expect)
    }

    private fun doControlWindowLevel(command: CarCmd, callback: ICmdCallback?, value: Int) {
        Timber.e("doControlWindow value:$value command:$command")
        val result = updateWindowLevel(value, command.part)
        val name = command.slots?.name ?: Keywords.WINDOW
        val append: String = when (value) {
            0x01 -> "关闭"
            0xC9 -> "打开"
            else -> "调整到${(value - 0x01) / 2}%"
        }
        if (result) {
            command.status = IStatus.RUNNING
            command.message = "好的，${name}${append}了"
        } else {
            command.status = IStatus.SUCCESS
            command.message = "${name}已经${append}了"
        }
        callback?.onCmdHandleResult(command)
    }

    /**
     * 天窗控制
     */
    private fun doControlLouverSwitch(parcel: CommandParcel) {
        val command = parcel.command as CarCmd
        var expect = Constant.INVALID
        if (Action.FIXED == command.action) {
            val degree = (command.value.toFloat() / 10).roundToInt()
            expect = if (degree == 0) 0x1 else (degree + 0x5)
        }
        val positions = arrayOf(0x1, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF)

        if (Action.OPEN == command.action || Action.MAX == command.action) {
            expect = positions.last()
        }
        if (Action.CLOSE == command.action || Action.MIN == command.action) {
            expect = positions.first()
        }
        val status = when (expect) {
            positions.last() -> true
            positions.first() -> false
            else -> Keywords.COMMAND_FAILED
        }
        if (status is String) {
            command.message = status
            parcel.callback?.onCmdHandleResult(command)
            return
        }
        doControlLouverSwitch(parcel, status as Boolean)
    }

    /**
     * 天窗控制
     */
    private fun doControlLouverSwitch(parcel: CommandParcel, status: Boolean) {
//        控制天窗全部打开[0x1,-1,0x0,0xf]
//        0x0: Inactive; 0x1: No command
//        0x2: global close; 0x3: global open; 0x4: global tilt
//        0x5: stop; 0x6: global close glass only; 0x7: global open Rollo only
//        0x8~0xF: reserved
        val signal = CarCabinManager.ID_AVN_BCM_COM_REQ_RCM
        val part = parcel.command.part
        if (IStatus.INIT == parcel.command.status) {
            parcel.retryCount = 20
            parcel.command.lfCount = 1
            writeProperty(signal, 0x5, Origin.CABIN)
            parcel.command.status = IStatus.PREPARED
            ShareHandler.loopParcel(parcel, ShareHandler.SEC_DELAY)
            return
        }
        val lucyLevel = obtainLoveLucyLevel()
        val louverLevel = obtainSkylightLevel()
        val lucyStandard = isStandard(lucyLevel, if (status) 0xB else 0x1)
        val louverStandard = isStandard(louverLevel, if (status) 0xF else 0x1)
        val isRetry = parcel.isRetry()
        val command = parcel.command
        Timber.e("------------louverStandard:$louverStandard, --lucyStandard:$lucyStandard")
        if (IPart.TOP == part) {
            val name = command.slots?.name ?: "天窗"
            val action = if (status) "打开" else "关闭"
            val isStandard = if (status) (louverStandard && lucyStandard) else louverStandard
            if (isStandard) {
                command.message = "${name}已经${action}了"
                parcel.callback?.onCmdHandleResult(command)
                louverReference = null
                return
            }
            if (!isRetry) {
                if (status) {
                    if (!lucyStandard && !louverStandard) {
                        command.message = Keywords.COMMAND_FAILED
                    } else if (lucyStandard && !louverStandard) {
                        command.message = "遮阳帘已经${action}了，但天窗${action}没有成功"
                    } else if (!lucyStandard && louverStandard) {
                        command.message = "天窗已经${action}了，但遮阳帘${action}没有成功"
                    }
                } else {
                    if (!louverStandard) {
                        command.message = Keywords.COMMAND_FAILED
                    }
                }
                parcel.callback?.onCmdHandleResult(command)
                louverReference = null
            } else {
                if (!command.isSent()) {
                    cleanCommandParcel(part, status)
//                  0x2: global close; 0x3: global open; 0x4: global tilt
//                  0x5: stop; 0x6: global close glass only; 0x7: global open Rollo only
                    val value = if (status) 0x3 else 0x6
                    writeProperty(signal, value, Origin.CABIN)
                    command.sent()
                    louverReference = WeakReference(parcel)
                }
                ShareHandler.loopParcel(parcel, ShareHandler.SEC_DELAY)
                return
            }
        } else if (IPart.BOTTOM == part) {
            val name = command.slots?.name ?: "遮阳帘"
            val action = if (status) "打开" else "关闭"
            val isStandard = if (status) lucyStandard  else louverStandard && lucyStandard
            if (isStandard) {
                command.message = "${name}已经${action}了"
                parcel.callback?.onCmdHandleResult(command)
                lucyReference = null
                return
            }
            if (!isRetry) {
                if (status) {
                    if (!lucyStandard) {
                        command.message = Keywords.COMMAND_FAILED
                    }
                } else {
                    if (!lucyStandard && !louverStandard) {
                        command.message = Keywords.COMMAND_FAILED
                    } else if (lucyStandard && !louverStandard) {
                        command.message = "遮阳帘已经${action}了，但天窗${action}没有成功"
                    } else if (!lucyStandard && louverStandard) {
                        command.message = "天窗已经${action}了，但遮阳帘${action}没有成功"
                    }
                }
                parcel.callback?.onCmdHandleResult(command)
                lucyReference = null
            } else {
                Timber.e("!command.isSent()-------------------: ${!command.isSent()}")
                if (!command.isSent()) {
                    cleanCommandParcel(part, status)
//                  0x2: global close; 0x3: global open; 0x4: global tilt
//                  0x5: stop; 0x6: global close glass only; 0x7: global open Rollo only
                    val value = if (status) 0x7 else 0x2
                    writeProperty(signal, value, Origin.CABIN)
                    command.sent()
                    lucyReference = WeakReference(parcel)
                }
                ShareHandler.loopParcel(parcel, ShareHandler.SEC_DELAY)
                return
            }
        }
    }

    private fun cleanCommandParcel(@IPart part: Int, status: Boolean) {
        ShareHandler.dumpParcel(lucyReference?.get())
        ShareHandler.dumpParcel(louverReference?.get())
//        if (IPart.TOP == part) {
//        }
//        if (IPart.BOTTOM == part) {
//        }
    }

    /**
     * 天窗控制
     */
    private fun doControlLouverLevel(command: CarCmd, callback: ICmdCallback?) {
        val positions = arrayOf(0x1, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF)
        var expect = Constant.INVALID
        var mask = IPart.TOP
        val isLouver = mask == (mask and command.part)
        mask = IPart.BOTTOM
        val isLoveLucy = mask == (mask and command.part)
        var obtainLevelFunction: (() -> Int)? = null
        if (isLouver) {
            obtainLevelFunction = this::obtainSkylightLevel
        }
        if (isLoveLucy) {
            obtainLevelFunction = this::obtainLoveLucyLevel
        }
        if (null == obtainLevelFunction) {
            Timber.e("is not louver or loveLucy command!")
            return
        }
        if (Action.FIXED == command.action) {
            val degree = (command.value.toFloat() / 10).roundToInt()
            expect = if (degree == 0) 0x1 else (degree + 0x5)
        }
        if (Action.OPEN == command.action || Action.MAX == command.action) {
            expect = positions.last()
        }
        if (Action.CLOSE == command.action || Action.MIN == command.action) {
            expect = positions.first()
        }
        if (Action.PLUS == command.action) {
            if (isLouver) {
                val position = obtainLevelFunction()
                expect = position + command.step
            }
            if (isLoveLucy) {
                val position = obtainLevelFunction()
                expect = position + command.step
            }
        }
        if (Action.MINUS == command.action) {
            if (isLouver) {
                val position = obtainLevelFunction()
                expect = position - command.step
            }
            if (isLoveLucy) {
                val position = obtainLevelFunction()
                expect = position - command.step
            }
        }
        if (expect > positions.last()) expect = positions.last()
        //当 expect < 0x6 时，即开度小于10%（为关），
        if (expect < positions[2]) expect = positions.first()
        doControlLouverLevel(command, callback, expect)
    }

    /**
     * 天窗控制
     */
    private fun doControlLouverLevel(command: CarCmd, callback: ICmdCallback?, value: Int) {
        Timber.e("doControlLouverLevel value:$value command:$command")
        var status1 = false
        var status2 = false
        var mask = IPart.TOP
        val isLouver = mask == (mask and command.part)
        mask = IPart.BOTTOM
        val isLoveLucy = mask == (mask and command.part)
        if (isLouver) {
            status1 = updateSkylightLevel(value)
        }
        if (isLoveLucy) {
            status2 = updateLoveLucyLevel(value)
        }
        val result = status1 || status2
        val name = command.slots?.name ?: (if (isLouver) Keywords.SKYLIGHT else Keywords.ABAT_VENT)
        val append: String = when (value) {
            1 -> "关闭"
            0xf -> "打开"
            else -> "调整到${(value - 0x5) * 10}%"
        }
        if (result) {
            command.status = IStatus.RUNNING
            command.message = "好的，${name}${append}了"
        } else {
            command.status = IStatus.SUCCESS
            command.message = "${name}已经${append}了"
        }
        callback?.onCmdHandleResult(command)
    }

    private fun obtainLouverState(): Int {
//        Glass Operation state,天窗运行状态，天窗采集到的Glass 开关状态OHC开关状态
//        0x0: Idle  Not pressed  0x1: Manual  open  0x2: Manual  close
//        0x3: Auto  open  0x4: Auto  close/Tilt down  0x5: Auto tilt open
//        0x6: Auto tilt close（Reserved ） 0x7: Full close（Reserved ）
//        0x8: Tilte（Reserved ） 0x9: Open position 1（Reserved ）
//        0xA: Open position 2（Reserved ） 0xB: Open position 3（Reserved ）
//        0xC: Open position 4（Reserved ）0xD: Open manual（Reserved ）
//        0xE~0xF: Not used
        return readIntProperty(CarCabinManager.ID_BCM_SUNROOF_BTN_STS, Origin.CABIN)
    }

    private fun obtainSkylightLevel(): Int {
//        Actual Sunroof position,实际天窗位置
//        0x0: Position unknwon/invalid  0x1: Completely closed
//        0x2~0x4: Reserved 0x5: Tilted 100%
//        0x6: Open 10%  0x7: Open 20%  0x8: Open 30%  0x9: Open 40%
//        0xA: Open 50%  0xB: Open 60%  0xC: Open 70%
//        0xD: Open 80%  0xE: Open 90%  0xF: Open 100%
        return readIntProperty(CarCabinManager.ID_BCM_SUNROOF_POS, Origin.CABIN)
    }

    private fun obtainLoveLucyState(): Int {
//        Rollo Operation state,遮阳帘运行状态
//        0x0: Idle / Not pressed  0x1: Manual open  0x2: Auto open
//        0x3: Manual close  0x4: Auto close  0x5~0x7: Reserved
        return readIntProperty(CarCabinManager.ID_BCM_ROLLO_BTN_STS, Origin.CABIN)
    }

    private fun isStandard(actual: Int, expect: Int): Boolean = actual == expect

    private fun obtainLoveLucyLevel(): Int {
//        Actual Rollo Position,遮阳帘实际位置，
//        0x0: Position unknown  0x1: Full close  0x2: Open 10%  0x3: Open 20%
//        0x4: Open 30%  0x5: Open 40%  0x6: Open 50% 0x7: Open 60%
//        0x8: Open 70%  0x9: Open 80%  0xA: Open 90%  0xB: Open 100%
//        0xC~0xF: Reserved
        return readIntProperty(CarCabinManager.ID_BCM_ROLLO_POS, Origin.CABIN)
    }

    private fun updateSkylightLevel(expect: Int): Boolean {
//        【设置】HUM_BCM_SUNROOF[0x1,0,0x0,0xF]
//        0x0: Inactive
//        0x1: Completely closed
//        0x2~0x4: reserved
//        0x5: Tilted 100%
//        0x6: Open 10% ………… 0xF: Open 100%
        val actual = obtainSkylightLevel()
        val result = actual != expect
        Timber.d("updateLouverPosition actual:$actual, expect:$expect, result:$result")
        if (result) {
            writeProperty(CarCabinManager.ID_HUM_BCM_SUNROOF, expect, Origin.CABIN)
        }
        return result
    }

    private fun updateLoveLucyLevel(expect: Int): Boolean {
//        Actual Rollo Position,遮阳帘实际位置，
//        0x0: Position unknown  0x1: Full close  0x2: Open 10%  0x3: Open 20%
//        0x4: Open 30%  0x5: Open 40%  0x6: Open 50% 0x7: Open 60%
//        0x8: Open 70%  0x9: Open 80%  0xA: Open 90%  0xB: Open 100%
//        0xC~0xF: Reserved
        val actual = obtainLoveLucyLevel()
        val result = actual != expect
        Timber.d("updateLoveLucyPosition actual:$actual, expect:$expect, result:$result")
        if (result) {
            writeProperty(CarCabinManager.ID_BCM_ROLLO_POS, expect, Origin.CABIN)
        }
        return result
    }

    /**
     * 获取车窗开度
     * @param part
     * Relative position of window in percent门窗位置信号，防夹模块发出，
     * BCM收到POS_VIT_FL之后转发至CANvalid value from 0 to 200, which means 0 ~ 100%
     * 0x0到0xC8, which means 0 ~ 100%  精度：0.5
     */
    private fun obtainWindowDegree(@IPart part: Int): Int {
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_BCM_POS_VIT_FL
            IPart.R_F -> CarCabinManager.ID_BCM_POS_VIT_FR
            IPart.L_B -> CarCabinManager.ID_BCM_POS_VIT_RL
            IPart.R_B -> CarCabinManager.ID_BCM_POS_VIT_RR
            else -> CarCabinManager.ID_BCM_POS_VIT_FL
        }
        if (Constant.INVALID == signal) {
            return signal
        }
        return getCabinSignalValue(signal)
    }

    /**
     * 设置车窗开度
     */
    private fun updateWindowLevel(value: Int, @IPart part: Int): Boolean {
        var result = false
        var mask = IPart.L_F
        if (mask == (mask and part)) {
            result = result or updateWindowLevel(CarCabinManager.ID_FRNTLEWINPOSNSET, value, mask)
        }
        mask = IPart.R_F
        if (mask == (mask and part)) {
            result = result or updateWindowLevel(CarCabinManager.ID_FRNTRIWINPOSNSET, value, mask)
        }
        mask = IPart.L_B
        if (mask == (mask and part)) {
            result = result or updateWindowLevel(-1, value, mask)
        }
        mask = IPart.R_B
        if (mask == (mask and part)) {
            result = result or updateWindowLevel(-1, value, mask)
        }
        return result
    }

    /**
     * 设置车窗开度
     */
    private fun updateWindowSwitch(parcel: CommandParcel, status: Boolean): Pair<Boolean, String> {
        var result = false
        val part = parcel.command.part
        val lfAct = IPart.L_F == (IPart.L_F and part)
        val rfAct = IPart.R_F == (IPart.R_F and part)
        val lbAct = IPart.L_B == (IPart.L_B and part)
        val rbAct = IPart.R_B == (IPart.R_B and part)
        val builder = StringBuilder()
        if (lfAct) {
            val pair = doWindowSwitch(IPart.L_F, lfAct, status)
            result = result || pair.first
            if (!pair.second) {
                builder.append("前排左车窗异常,")
            }
        }
        if (rfAct) {
            val pair = doWindowSwitch(IPart.R_F, rfAct, status)
            result = result || pair.first
            if (!pair.second) {
                builder.append("前排右车窗异常,")
            }
        }
        if (lbAct) {
            val pair = doWindowSwitch(IPart.L_B, lbAct, status)
            result = result || pair.first
            if (!pair.second) {
                builder.append("后排左车窗异常,")
            }
        }
        if (rbAct) {
            val pair = doWindowSwitch(IPart.R_B, rbAct, status)
            result = result || pair.first
            if (!pair.second) {
                builder.append("后排右车窗异常")
            }
        }
        return Pair(result, builder.toString())
    }

    private fun doWindowSwitch(
        part: Int,
        active: Boolean,
        status: Boolean,
    ): Pair<Boolean, Boolean> {
        var result = false
        var reliable = true
        if (active) {
            val signal = obtainWindowSignal(part)
            reliable = isReliableWindow(part)
            if (reliable) {
                result = updateWindowSwitch(signal, status, part)
            }
        }
        return Pair(result, reliable)
    }

    private fun isReliableWindow(@IPart part: Int): Boolean {
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_BCM_DCM_ERROR_FL
            IPart.R_F -> CarCabinManager.ID_BCM_DCM_ERROR_FR
            IPart.L_B -> CarCabinManager.ID_BCM_DCM_ERROR_RL
            IPart.R_B -> CarCabinManager.ID_BCM_DCM_ERROR_RR
            else -> CarCabinManager.ID_BCM_DCM_ERROR_FL
        }
//        门窗模块无故障反馈0x0: No Error; 0x1: Error
        return 0x0 == readIntProperty(signal, Origin.CABIN)
    }

    private fun updateWindowLevel(signal: Int, expect: Int, @IPart part: Int): Boolean {
        val actual = obtainWindowDegree(part)
        val result = expect != actual
        if (result) {
            writeProperty(signal, expect, Origin.CABIN)
        }
        return result
    }

    private fun updateWindowSwitch(signal: Int, status: Boolean, @IPart part: Int): Boolean {
//        val actual = obtainWindowStatus(part)
//        AVN request driver side window up/down.[0x1,0,0x0,0x7]
//        0x0: Inactive  0x1: Manual Up(reserved)  0x2: Manual Down(reserved)
//        0x3~0x4: reserved  0x5: Auto Up  0x6: Auto Down 0x7: reserved
        val result = true
        val expect = if (status) 0x6 else 0x5
        if (result) {
            writeProperty(signal, expect, Origin.CABIN)
        }
        return result
    }

    private fun obtainWindowSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> CarCabinManager.ID_AVN_BCM_WLM_DRIVER
            IPart.R_F -> CarCabinManager.ID_AVN_BCM_WLM_PASSENGER
            IPart.L_B -> CarCabinManager.ID_AVN_BCM_WLM_REARLEFT
            IPart.R_B -> CarCabinManager.ID_AVN_BCM_WLM_REARRIGHT
            else -> Constant.INVALID
        }
    }

    private fun interruptCommand(parcel: CommandParcel, coreEngine: Boolean = false): Boolean {
        val result = if (!coreEngine) {
            !VcuUtils.isPower()
        } else {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        }
        if (result) {
            parcel.command.message = Keywords.NEED_START_ENGINE
            parcel.callback?.onCmdHandleResult(parcel.command)
        }
        return result
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        if (Model.ACCESS_WINDOW != command.model) {
            return
        }
        if (interruptCommand(parcel, false)) {
            return
        }
        if (ICar.WINDOWS == command.car) {
            doControlSwitchWindow(parcel)
            return
        }
        if (ICar.LOUVER == command.car) {
            doControlLouverSwitch(parcel)
            return
        }
        if (ICar.WIPER == command.car) {
            doControlWiper(parcel)
            return
        }
    }

}