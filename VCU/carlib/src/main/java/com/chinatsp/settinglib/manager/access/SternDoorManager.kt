package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
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


class SternDoorManager private constructor() : BaseManager(), IOptionManager {


    companion object : ISignal {

        override val TAG: String = SternDoorManager::class.java.simpleName

        val instance: SternDoorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SternDoorManager()
        }

    }

    private val _electricFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_STERN_ELECTRIC
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }


    private val _lightAlarmFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_STERN_LIGHT_ALARM
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }


    private val _audioAlarmFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_STERN_AUDIO_ALARM
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onHandleSignal(
        property: CarPropertyValue<*>,
        origin: Origin
    ): Boolean {
        when (origin) {
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

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.ACCESS_STERN_SMART_ENTER -> {
                2
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        var result = false
        do {
            if (RadioNode.ACCESS_STERN_SMART_ENTER != node) {
                break
            }
            result = doUpdateSternDoorOption(value)
        } while (false)
        return result
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                _electricFunction.get()
            }
            SwitchNode.AS_STERN_LIGHT_ALARM -> {
                _lightAlarmFunction.get()
            }
            SwitchNode.AS_STERN_AUDIO_ALARM -> {
                _audioAlarmFunction.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        val result = when (node) {
            SwitchNode.AS_STERN_AUDIO_ALARM -> _audioAlarmFunction
            SwitchNode.AS_STERN_LIGHT_ALARM -> _lightAlarmFunction
            SwitchNode.AS_STERN_ELECTRIC -> _electricFunction
            else -> null
        }
        return result?.let {
            val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
            if (success) {
                doUpdateSwitchValue(node, result, status)
            }
            return@let success
        } ?: false
    }


    /**
     * 【设置】电动尾门感应进入设置
     * @param value 0x1: OFF; 0x2: On Mode 1; 0x3: On Mode 2
     */
    private fun doUpdateSternDoorOption(value: Int): Boolean {
        val isValid = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarCabinManager.ID_PTM_SMT_ENTRY_SET
        return writeProperty(signal, value, Origin.CABIN, Area.GLOBAL)
    }


}