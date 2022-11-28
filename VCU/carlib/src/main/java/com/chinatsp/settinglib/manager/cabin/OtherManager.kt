package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class OtherManager private constructor() : BaseManager(), IOptionManager {

    /**
     * 拖车提醒开关（此信号走TBox 协议而非CAN信号，所以需要特殊处理）
     */
    private val trailerRemind: SwitchState by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        SwitchState(node.default).apply {
            val value = SettingManager.instance.getTrailerSwitch()
            val result = if (null != value) node.isOn(value) else node.default
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val batteryOptimize: SwitchState by lazy {
        val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val wirelessCharging: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val wirelessChargingLamp: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val sensitivity: RadioState by lazy {
        val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
        RadioState(node.def).apply {
            val value = SettingManager.instance.getTrailerLevel()
            val result = value ?: node.def
            doUpdateRadioValue(node, this, result)
        }
    }

    private val distance: RadioState by lazy {
        val node = RadioNode.DEVICE_TRAILER_DISTANCE
        RadioState(node.def).apply {
            val value = SettingManager.instance.getTrailerDist()
            val result = value ?: node.def
            doUpdateRadioValue(node, this, result)
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

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> trailerRemind.deepCopy()
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> batteryOptimize.deepCopy()
            SwitchNode.DRIVE_WIRELESS_CHARGING -> wirelessCharging.deepCopy()
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> wirelessChargingLamp.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                Timber.d("doSetSwitchOption node:$node, status:$status start")
                val result = SettingManager.instance.setTrailerRemind(node.value(status))
                if (result) {
                    trailerRemind.set(status)
                    doSwitchChanged(node, trailerRemind)
                }
                Timber.d("doSetSwitchOption node:$node, status:$status, result:$result end")
                result
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

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> distance.deepCopy()
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> sensitivity.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        Timber.d("doSetRadioOption node:$node, value:$value start")
        val result = when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> {
                val result = SettingManager.instance.setTrailerDistance(value)
                if (result) {
                    distance.set(value)
                    doOptionChanged(node, distance)
                }
                result
            }
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> {
                val result = SettingManager.instance.setTrailerSensitivity(value)
                if (result) {
                    distance.set(value)
                    doOptionChanged(node, distance)
                }
                result
            }
            else -> false
        }
        Timber.d("doSetRadioOption node:$node, value:$value, result:$result end")
        return result
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
                val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
                var convert = convert(property, node.get.off, 0x5)
                if (null == convert) convert = property
                onSwitchChanged(node, batteryOptimize, convert)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_WIRELESS_CHARGING, wirelessCharging, property)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP.get.signal -> {
                onSwitchChanged(
                    SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, wirelessChargingLamp, property)
            }
            else -> {}
        }
    }

    fun onTrailerRemindChanged(onOff: Int, level: Int, dist: Int) {
        Timber.d("onTrailerRemindChanged statusValue:%s, level:%s, distance:%s", onOff, level, dist)
        doUpdateSwitchValue(
            SwitchNode.DRIVE_TRAILER_REMIND, trailerRemind, onOff, this::doSwitchChanged)
        doUpdateRadioValue(
            RadioNode.DEVICE_TRAILER_SENSITIVITY, sensitivity, level, this::doOptionChanged)
        doUpdateRadioValue(
            RadioNode.DEVICE_TRAILER_DISTANCE, distance, dist, this::doOptionChanged)
    }


}