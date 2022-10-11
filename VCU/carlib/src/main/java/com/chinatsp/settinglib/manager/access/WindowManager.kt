package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
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
                listenerStore.put(serial, WeakReference(listener))
            } finally {
                writeLock.unlock()
            }
            return serial
        }
        return -1
    }

    /**
     *
     * @param switchNode 开关选项
     */
    fun doGetSwitchStatus(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = readIntProperty(signal, Origin.CABIN, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> {
                val signal = CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS
                val value = readIntProperty(signal, Origin.CABIN, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.WIN_REMOTE_CONTROL -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = readIntProperty(signal, Origin.CABIN, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.RAIN_WIPER_REPAIR -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = readIntProperty(signal, Origin.CABIN, switchNode.area)
                switchNode.isOn(value)
            }
            else -> false
        }
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

        if (Action.CLOSE == cmd.action) {
            cmd.status = IStatus.SUCCESS
            cmd.message = "天窗已打开"
            callback?.onCmdHandleResult(cmd)
        }
        if (Action.OPEN == cmd.action) {
            if (speed >= 120) {
                cmd.status = IStatus.FAILED
                cmd.message = "车速过快，建议不要开启天窗"
                callback?.onCmdHandleResult(cmd)
            } else {
                cmd.status = IStatus.SUCCESS
                cmd.message = "天窗已打开"
                callback?.onCmdHandleResult(cmd)
            }
        } else if (Action.CLOSE == cmd.action) {
            cmd.status = IStatus.SUCCESS
            cmd.message = "天窗已打开"
            callback?.onCmdHandleResult(cmd)
        }

        Timber.d("doControlLouver $cmd")
    }

}