package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class WindowManager private constructor() : BaseManager(), ISwitchManager {

    private val autoCloseWinInRain: AtomicBoolean by lazy {
        val node = SwitchNode.WIN_CLOSE_WHILE_RAIN
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val autoCloseWinAtLock: AtomicBoolean by lazy {
        val node = SwitchNode.WIN_CLOSE_FOLLOW_LOCK
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val winRemoteControl: AtomicBoolean by lazy {
        val node = SwitchNode.WIN_REMOTE_CONTROL
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val rainWiperRepair: AtomicBoolean by lazy {
        val node = SwitchNode.RAIN_WIPER_REPAIR
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
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
            val cabinSet = HashSet<Int> ().apply {
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

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> {
                autoCloseWinInRain.get()
            }
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> {
                autoCloseWinAtLock.get()
            }
            SwitchNode.WIN_REMOTE_CONTROL -> {
                winRemoteControl.get()
            }
            SwitchNode.RAIN_WIPER_REPAIR -> {
                rainWiperRepair.get()
            }
            else -> false
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
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
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

//    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
//        when (switchNode) {
//            /**
//             * 车门车窗-车窗-雨天自动关窗 (状态下发)
//             * 0x0: Disable
//             * 0x1: Enable
//             */
//            SwitchNode.WIN_CLOSE_WHILE_RAIN -> {
//                var value = property.value
//                if (value is Int) {
//                    value += 1
//                    autoCloseWinInRain.set(switchNode.isOn(value))
//                    onSwitchChanged(switchNode, autoCloseWinInRain.get())
//                }
//            }
//            /**
//             * 车门车窗-车窗-锁车自动关窗 (状态上报)
//             * 0x0: No action when locking（default）
//             * 0x1: close windows when locking door
//             * 0x2: reserved
//             * 0x3: Reserved
//             */
//            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> {
//                var value = property.value
//                if (value is Int) {
//                    value += 1
//                    autoCloseWinAtLock.set(switchNode.isOn(value))
//                    onSwitchChanged(switchNode, autoCloseWinAtLock.get())
//                }
//            }
//            else -> {}
//        }
//    }
//
//    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
//        synchronized(listenerStore) {
//            listenerStore.filterValues { null != it.get() }
//                .forEach{
//                    val listener = it.value.get()
//                    if (null != listener && listener is ISwitchListener) {
//                        listener.onSwitchOptionChanged(status, switchNode)
//                    }
//                }
//        }
//    }

}