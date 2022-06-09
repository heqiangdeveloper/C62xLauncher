package com.chinatsp.settinglib.manager.assistance

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class CruiseManager : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = CruiseManager::class.java.simpleName
        val instance: CruiseManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CruiseManager()
        }
    }

    private val cruiseAssistFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_CRUISE_ASSIST
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }
    private val targetPromptFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_TARGET_PROMPT
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }

    private val limberLeaveFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_LIMBER_LEAVE
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = -1
            val result = doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            doUpdateSwitchStatus(switchNode, this, result)
        }
    }

    private val limberLeaveRadio: AtomicInteger by lazy {
        AtomicInteger(-1).apply {
            val signal = -1
            val result = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
            set(result)
        }
    }


    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
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
            RadioNode.ADAS_LIMBER_LEAVE -> {
                limberLeaveRadio.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        return when (radioNode) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
                val signal = -1
                doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL)
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            result = serial
        }
        return result
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
            SwitchNode.ADAS_CRUISE_ASSIST -> {
                cruiseAssistFunction.get()
            }
            SwitchNode.ADAS_TARGET_PROMPT -> {
                targetPromptFunction.get()
            }
            SwitchNode.ADAS_LIMBER_LEAVE -> {
                limberLeaveFunction.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.ADAS_CRUISE_ASSIST -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_TARGET_PROMPT -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_LIMBER_LEAVE -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
    }
}