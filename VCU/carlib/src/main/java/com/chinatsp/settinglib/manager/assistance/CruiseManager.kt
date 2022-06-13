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
import com.chinatsp.settinglib.sign.Origin
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
        val switchNode = SwitchNode.ADAS_IACC
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }
    private val targetPromptFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_TARGET_PROMPT
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }

    private val limberLeaveFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_LIMBER_LEAVE
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }

    private val limberLeaveRadio: AtomicInteger by lazy {
        AtomicInteger(-1).apply {
            val signal = -1
            val result = readIntProperty(signal, Origin.CABIN, Area.GLOBAL)
            set(result)
        }
    }


    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
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

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
                limberLeaveRadio.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
                val signal = -1
                writeProperty(signal, value, Origin.CABIN)
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

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_IACC -> {
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

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_IACC -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_TARGET_PROMPT -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_LIMBER_LEAVE -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }
}