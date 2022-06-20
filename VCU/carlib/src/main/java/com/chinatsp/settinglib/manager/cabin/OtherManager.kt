package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
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


class OtherManager private constructor() : BaseManager(), ISwitchManager {

    private val trailerRemind: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val batteryOptimize: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val wirelessCharging: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val wirelessChargingLamp: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP
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

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.DRIVE_TRAILER_REMIND.get.signal)
                add(SwitchNode.DRIVE_BATTERY_OPTIMIZE.get.signal)
                add(SwitchNode.DRIVE_WIRELESS_CHARGING.get.signal)
                add(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP.get.signal)
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
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                trailerRemind.get()
            }
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

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
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


    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            SwitchNode.DRIVE_TRAILER_REMIND.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_TRAILER_REMIND, trailerRemind, property)
            }
            SwitchNode.DRIVE_BATTERY_OPTIMIZE.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_BATTERY_OPTIMIZE, batteryOptimize, property)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_WIRELESS_CHARGING, wirelessCharging, property)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP.get.signal -> {
                onSwitchChanged(
                    SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP,
                    wirelessChargingLamp,
                    property
                )
            }
            else -> {}
        }
    }

}