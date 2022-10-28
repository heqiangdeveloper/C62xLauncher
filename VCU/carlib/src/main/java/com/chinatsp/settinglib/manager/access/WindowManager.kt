package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
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
class WindowManager private constructor() : BaseManager(), ISwitchManager {
//    天窗开启控制语音开启的判定条件：
//    车速小于120KM/H(车速使用仪表的车速！！)，
//    超速时，用户触发语音开启指令后弹窗及语音播报提示“车速过快，建议不要开启天窗” 此时不用说“好的”
//    此功能仅针对天窗的打开信号进行判断，关闭指令不受影响

    private var speed: Int = 0

    private val positions = arrayOf(0x1, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF)

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
                /**雨天自动关窗*/
                add(SwitchNode.WIN_CLOSE_WHILE_RAIN.get.signal)
                /**锁车自动关窗*/
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

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?) {
        if (Model.ACCESS_WINDOW != command.model) {
            return
        }
        Timber.d("doOuterControlCommand $command")
        if (ICar.WINDOWS == command.car) {
            doControlWindow(command, callback)
        }
        if (ICar.LOUVER == command.car) {
            doControlLouver(command, callback)
        }
    }

//    private fun doControlDoors(command: CarCmd, callback: ICmdCallback?) {
//        if (IPart.HEAD == command.part) {
//            if (Action.OPEN == command.action) {
//                doSwitchHood(command, callback,true)
//                return
//            }
//            if (Action.CLOSE == command.action) {
//                doSwitchHood(command, callback,false)
//                return
//            }
//        }
//        if (IPart.TAIL == command.part) {
//            if (Action.OPEN == command.action) {
//                doSwitchTrunks(command, callback,true)
//                return
//            }
//            if (Action.CLOSE == command.action) {
//                doSwitchTrunks(command, callback,false)
//                return
//            }
//        }
//    }
//
//    private fun doSwitchHood(command: CarCmd, callback: ICmdCallback?, expect: Boolean) {
//        val onOff = if (expect) "打开" else "关闭"
//        command.message = "好的，${command.slots?.name}${onOff}了"
//        callback?.onCmdHandleResult(command)
//    }
//
//    private fun doSwitchTrunks(command: CarCmd, callback: ICmdCallback?, expect: Boolean) {
//        val onOff = if (expect) "打开" else "关闭"
//        command.message = "好的，${command.slots?.name}${onOff}了"
//        callback?.onCmdHandleResult(command)
//    }

    private fun doControlWindow(command: CarCmd, callback: ICmdCallback?) {
        var expect = Constant.INVALID
        val minValue = 0x01
        val maxValue = 0xC9
        if (Action.FIXED == command.action) {
            Timber.e("doControlWindow ----- value:${command.value}")
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
        doControlWindow(command, callback, expect)
    }

    private fun doControlWindow(command: CarCmd, callback: ICmdCallback?, value: Int) {
        Timber.e("doControlWindow value:$value command:$command")
        val result = updateWindowPosition(value, command.part)
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
    private fun doControlLouver(command: CarCmd, callback: ICmdCallback?) {
        var expect = Constant.INVALID
        var mask = IPart.SKYLIGHT
        val isLouver = mask == (mask and command.part)
        mask = IPart.LOVE_LUCY
        val isLoveLucy = mask == (mask and command.part)
        var obtainPosition: (() -> Int)? = null
        if (isLouver) {
            obtainPosition = this::obtainLouverPosition
        }
        if (isLoveLucy) {
            obtainPosition = this::obtainLoveLucyPosition
        }
        if (null == obtainPosition) {
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
                val position = obtainPosition()
                expect = position + command.step
            }
            if (isLoveLucy) {
                val position = obtainPosition()
                expect = position + command.step
            }
        }
        if (Action.MINUS == command.action) {
            if (isLouver) {
                val position = obtainPosition()
                expect = position - command.step
            }
            if (isLoveLucy) {
                val position = obtainPosition()
                expect = position - command.step
            }
        }
        if (expect > positions.last()) expect = positions.last()
        //当 expect < 0x6 时，即开度小于10%（为关），
        if (expect < positions[2]) expect = positions.first()
        doControlLouver(command, callback, expect)
    }

    /**
     * 天窗控制
     */
    private fun doControlLouver(command: CarCmd, callback: ICmdCallback?, value: Int) {
        Timber.e("doControlWindow value:$value command:$command")
        if (value !in positions) {
            return
        }
        var status1 = false
        var status2 = false

        var mask = IPart.SKYLIGHT
        val isLouver = mask == (mask and command.part)
        mask = IPart.LOVE_LUCY
        val isLoveLucy = mask == (mask and command.part)
        if (isLouver) {
            status1 = updateLouverPosition(value)
        }
        if (isLoveLucy) {
            status2 = updateLoveLucyPosition(value)
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

    fun obtainLouverState(): Int {
//        Glass Operation state,天窗运行状态，天窗采集到的Glass 开关状态OHC开关状态
//        0x0: Idle  Not pressed  0x1: Manual  open  0x2: Manual  close
//        0x3: Auto  open  0x4: Auto  close/Tilt down  0x5: Auto tilt open
//        0x6: Auto tilt close（Reserved ） 0x7: Full close（Reserved ）
//        0x8: Tilte（Reserved ） 0x9: Open position 1（Reserved ）
//        0xA: Open position 2（Reserved ） 0xB: Open position 3（Reserved ）
//        0xC: Open position 4（Reserved ）0xD: Open manual（Reserved ）
//        0xE~0xF: Not used
        val value = readIntProperty(CarCabinManager.ID_BCM_SUNROOF_BTN_STS, Origin.CABIN)
        return value
    }

    private fun obtainLouverPosition(): Int {
//        Actual Sunroof position,实际天窗位置
//        0x0: Position unknwon/invalid  0x1: Completely closed
//        0x2~0x4: Reserved 0x5: Tilted 100%
//        0x6: Open 10%  0x7: Open 20%  0x8: Open 30%  0x9: Open 40%
//        0xA: Open 50%  0xB: Open 60%  0xC: Open 70%
//        0xD: Open 80%  0xE: Open 90%  0xF: Open 100%
        val value = readIntProperty(CarCabinManager.ID_BCM_SUNROOF_POS, Origin.CABIN)
        return value
    }

    fun obtainLoveLucyState(): Int {
//        Rollo Operation state,遮阳帘运行状态
//        0x0: Idle / Not pressed  0x1: Manual open  0x2: Auto open
//        0x3: Manual close  0x4: Auto close  0x5~0x7: Reserved
        val value = readIntProperty(CarCabinManager.ID_BCM_ROLLO_BTN_STS, Origin.CABIN)
        return value
    }

    private fun obtainLoveLucyPosition(): Int {
//        Actual Rollo Position,遮阳帘实际位置，
//        0x0: Position unknown  0x1: Full close  0x2: Open 10%  0x3: Open 20%
//        0x4: Open 30%  0x5: Open 40%  0x6: Open 50% 0x7: Open 60%
//        0x8: Open 70%  0x9: Open 80%  0xA: Open 90%  0xB: Open 100%
//        0xC~0xF: Reserved
        val value = readIntProperty(CarCabinManager.ID_BCM_ROLLO_POS, Origin.CABIN)
        return value
    }

    private fun updateLouverPosition(expect: Int): Boolean {
//        【设置】HUM_BCM_SUNROOF[0x1,0,0x0,0xF]
//        0x0: Inactive
//        0x1: Completely closed
//        0x2~0x4: reserved
//        0x5: Tilted 100%
//        0x6: Open 10% ………… 0xF: Open 100%
        val actual = obtainLouverPosition()
        val result = actual != expect
        Timber.d("updateLouverPosition actual:$actual, expect:$expect, result:$result")
        if (result) {
            writeProperty(CarCabinManager.ID_HUM_BCM_SUNROOF, expect, Origin.CABIN)
        }
        return result
    }

    private fun updateLoveLucyPosition(expect: Int): Boolean {
//        Actual Rollo Position,遮阳帘实际位置，
//        0x0: Position unknown  0x1: Full close  0x2: Open 10%  0x3: Open 20%
//        0x4: Open 30%  0x5: Open 40%  0x6: Open 50% 0x7: Open 60%
//        0x8: Open 70%  0x9: Open 80%  0xA: Open 90%  0xB: Open 100%
//        0xC~0xF: Reserved
        val actual = obtainLoveLucyPosition()
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
            IPart.LEFT_FRONT -> CarCabinManager.ID_BCM_POS_VIT_FL
            IPart.RIGHT_FRONT -> CarCabinManager.ID_BCM_POS_VIT_FR
            IPart.LEFT_BACK -> CarCabinManager.ID_BCM_POS_VIT_RL
            IPart.RIGHT_BACK -> CarCabinManager.ID_BCM_POS_VIT_RR
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
    private fun updateWindowPosition(value: Int, @IPart part: Int): Boolean {
        var result = false
        var mask = IPart.LEFT_FRONT
        if (mask == (mask and part)) {

            result = result or updateWindow(CarCabinManager.ID_FRNTLEWINPOSNSET, value, mask)
//            result = result or updateWindow(CarCabinManager.ID_AVN_BCM_WLM_DRIVER, value, mask)
        }
        mask = IPart.RIGHT_FRONT
        if (mask == (mask and part)) {
            result = result or updateWindow(CarCabinManager.ID_FRNTRIWINPOSNSET, value, mask)
//            result = result or updateWindow(CarCabinManager.ID_AVN_BCM_WLM_PASSENGER, value, mask)
        }
        mask = IPart.LEFT_BACK
        if (mask == (mask and part)) {
//            result = result or updateWindow(CarCabinManager.ID_AVN_BCM_WLM_REARLEFT, value, mask)
        }
        mask = IPart.RIGHT_BACK
        if (mask == (mask and part)) {
//            result = result or updateWindow(CarCabinManager.ID_AVN_BCM_WLM_REARRIGHT, value, mask)
        }
        return result
    }

    private fun updateWindow(signal: Int, expect: Int, @IPart part: Int): Boolean {
        val actual = obtainWindowDegree(part)
        val result = expect != actual
        if (result) {
            writeProperty(signal, expect, Origin.CABIN)
        }
        return result
    }

    private fun obtainPowerMode(): Int {
        //0x0: OFF 0x1: ACC 0x2: IGN ON 0x3: CRANK
        val value = readIntProperty(CarCabinManager.ID_POWER_MODE_BCM, Origin.CABIN)
        return value
    }

}