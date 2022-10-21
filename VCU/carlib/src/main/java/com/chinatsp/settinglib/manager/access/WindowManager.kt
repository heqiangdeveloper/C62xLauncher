package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import timber.log.Timber
import java.lang.ref.WeakReference

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

    val positions = arrayOf(0x1, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF)

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
                onSwitchChanged(SwitchNode.WIN_REMOTE_CONTROL, winRemoteControl, property)
            }
            SwitchNode.RAIN_WIPER_REPAIR.get.signal -> {
                onSwitchChanged(SwitchNode.RAIN_WIPER_REPAIR, rainWiperRepair, property)
            }
            else -> {}
        }
    }

    override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
        Timber.d("doOuterControlCommand $cmd")
        if (Model.ACCESS_WINDOW == cmd.model) {
            cmd.status = IStatus.RUNNING
            doControlLouver(cmd, callback)
        }

    }

    /**
     * 天窗控制
     */
    private fun doControlLouver(cmd: CarCmd, callback: ICmdCallback?) {
        if (Action.OPEN == cmd.action || Action.MAX == cmd.action) {

        }
        if (Action.FIXED == cmd.action) {
            val value = cmd.value * (positions.size - 2).toFloat() / 100f
            val expect = value.toInt() + 2
            Timber.d("doControlLouver------cmd.value-${cmd.value}, value:$value, expect:$expect")
            updateLouverPosition(expect)
            cmd.status = IStatus.SUCCESS
            cmd.message = "天窗调整到$expect"
            callback?.onCmdHandleResult(cmd)
            return
        }

        if (Action.OPEN == cmd.action) {
            if (speed >= 120) {
                cmd.status = IStatus.FAILED
                cmd.message = "车速过快，建议不要开启天窗"
                callback?.onCmdHandleResult(cmd)
            } else {
                cmd.status = IStatus.SUCCESS
                cmd.message = "天窗已打开"
                updateLouverPosition(positions.last())
                callback?.onCmdHandleResult(cmd)
            }
        } else if (Action.CLOSE == cmd.action) {
            cmd.status = IStatus.SUCCESS
            cmd.message = "天窗已关闭"
            updateLouverPosition(positions.first())
            callback?.onCmdHandleResult(cmd)
        }

        Timber.d("doControlLouver $cmd")
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

    fun obtainLouverPosition(): Int {
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

    fun obtainLoveLucyPosition(): Int {
//        Actual Rollo Position,遮阳帘实际位置，
//        0x0: Position unknown  0x1: Full close  0x2: Open 10%  0x3: Open 20%
//        0x4: Open 30%  0x5: Open 40%  0x6: Open 50% 0x7: Open 60%
//        0x8: Open 70%  0x9: Open 80%  0xA: Open 90%  0xB: Open 100%
//        0xC~0xF: Reserved
        val value = readIntProperty(CarCabinManager.ID_BCM_ROLLO_POS, Origin.CABIN)
        return value
    }

    fun updateLouverPosition(value: Int): Boolean {
//        【设置】HUM_BCM_SUNROOF[0x1,0,0x0,0xF]
//        0x0: Inactive
//        0x1: Completely closed
//        0x2~0x4: reserved
//        0x5: Tilted 100%
//        0x6: Open 10%
//        0x7: Open 20%
//        0x8: Open 30%
//        0x9: Open 40%
//        0xA: Open 50%
//        0xB: Open 60%
//        0xC: Open 70%
//        0xD: Open 80%
//        0xE: Open 90%
//        0xF: Open 100%
        val position = obtainLouverPosition()
        if (value != position) {
//            writeProperty(CarMcuManager.ID_HUM_BCM_SUNROOF)
            return writeProperty(-1, value, Origin.CABIN)
        }
        return false
    }

}