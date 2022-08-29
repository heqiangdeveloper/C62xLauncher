package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
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
 * @desc   : 智能巡航
 * @version: 1.0
 */
class CruiseManager : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = CruiseManager::class.java.simpleName
        val instance: CruiseManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CruiseManager()
        }
    }

    private val iaccFunction: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_IACC
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }
    private val targetPromptFunction: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_TARGET_PROMPT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val limberLeaveFunction: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_LIMBER_LEAVE
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val limberLeaveRadio: AtomicInteger by lazy {
        val node = RadioNode.ADAS_LIMBER_LEAVE
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.ADAS_IACC.get.signal)
                add(SwitchNode.ADAS_TARGET_PROMPT.get.signal)
                add(SwitchNode.ADAS_LIMBER_LEAVE.get.signal)
                add(RadioNode.ADAS_LIMBER_LEAVE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ADAS_IACC.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_IACC, iaccFunction, property)
            }
            SwitchNode.ADAS_TARGET_PROMPT.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_TARGET_PROMPT, targetPromptFunction, property)
            }
            SwitchNode.ADAS_LIMBER_LEAVE.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_LIMBER_LEAVE, limberLeaveFunction, property)
            }
            RadioNode.ADAS_LIMBER_LEAVE.get.signal -> {
                onRadioChanged(RadioNode.ADAS_LIMBER_LEAVE, limberLeaveRadio, property)
            }
        }
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
                node.isValid(value, false) && writeProperty(node.set.signal, value, node.set.origin)
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)

            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            } finally {
                writeLock.unlock()
            }
            result = serial
        }
        return result
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_IACC -> {
                iaccFunction.get()
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