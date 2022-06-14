package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.access.IWindowListener
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


class OtherManager private constructor() : BaseManager(), ISwitchManager {

    private val batteryOptimize: AtomicBoolean by lazy {
        val node = SwitchNode.AS_CLOSE_WIN_WHILE_RAIN
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val wirelessCharging: AtomicBoolean by lazy {
        val node = SwitchNode.AS_CLOSE_WIN_WHILE_LOCK
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val wirelessChargingLamp: AtomicBoolean by lazy {
        val node = SwitchNode.AS_CLOSE_WIN_WHILE_LOCK
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    companion object : ISignal {
        override val TAG: String = OtherManager::class.java.simpleName
        val instance: OtherManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OtherManager()
        }
    }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**雨天自动关窗*/
                add(CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS)
                /**锁车自动关窗*/
                add(CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: Origin
    ): Boolean {
        when (signalOrigin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            Origin.HVAC -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.let {
                if (it.containsKey(serial)) it else null
            }?.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is IWindowListener) {
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
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                batteryOptimize.get()
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                wirelessCharging.get()
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                wirelessChargingLamp.get()
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_CLOSE_WIN_WHILE_RAIN, property)
            }
            CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_CLOSE_WIN_WHILE_LOCK, property)
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        var value = property.value
        if (value is Int) {
            when (switchNode) {
                SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                    value += 1
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchValue(switchNode, batteryOptimize, value).get()
                    )
                }
                SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                    value += 1
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchValue(switchNode, wirelessCharging, value).get()
                    )
                }
                SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                    value += 1
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchValue(switchNode, wirelessChargingLamp, value).get()
                    )
                }
                else -> {}
            }
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.forEach { (_, u) ->
                val listener = u.get()
                if (listener is ISwitchListener) {
                    listener.onSwitchOptionChanged(status, switchNode)
                }
            }
        }
    }

}