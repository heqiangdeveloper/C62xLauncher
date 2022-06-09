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
import com.chinatsp.settinglib.sign.SignalOrigin
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
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }


    private val _lightAlarmFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_STERN_LIGHT_ALARM
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }


    private val _audioAlarmFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_STERN_AUDIO_ALARM
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }


    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun doGetRadioOption(radioNode: RadioNode): Int {
        return when (radioNode) {
            RadioNode.ACCESS_STERN_SMART_ENTER -> {2}
            else -> -1
        }
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        var result = false
        do {
            if (RadioNode.ACCESS_STERN_SMART_ENTER != radioNode) {
                break
            }
            result = doUpdateSternDoorOption(value)
        } while (false)
        return result
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
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
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

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.AS_STERN_AUDIO_ALARM, SwitchNode.AS_STERN_LIGHT_ALARM, SwitchNode.AS_STERN_ELECTRIC -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
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
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
    }


}