package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
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
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class LightManager private constructor() : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = LightManager::class.java.simpleName
        val instance: LightManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LightManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
//                /**空调自干燥*/
//                add(CarCabinManager.ID_ACSELFSTSDISP)
//                /**预通风功能*/
//                add(CarCabinManager.ID_ACPREVENTNDISP)
//                /**空调舒适性状态显示*/
//                add(CarCabinManager.ID_ACCMFTSTSDISP)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val insideMeetLight: AtomicBoolean by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }

    private val outsideMeetLight: AtomicBoolean by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }

    private val lightDelayOut: AtomicInteger by lazy {
        val node = RadioNode.LIGHT_DELAYED_OUT
        AtomicInteger(node.default).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val lightFlicker: AtomicInteger by lazy {
        val node = RadioNode.LIGHT_FLICKER
        AtomicInteger(node.default).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, value)
        }
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
            RadioNode.LIGHT_DELAYED_OUT -> {
                lightDelayOut.get()
            }
            RadioNode.LIGHT_FLICKER -> {
                lightFlicker.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> {
                writeProperty(node, value, lightDelayOut)
            }
            RadioNode.LIGHT_FLICKER -> {
                writeProperty(node, value, lightFlicker)
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
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

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.LIGHT_INSIDE_MEET -> {
                insideMeetLight.get()
            }
            SwitchNode.LIGHT_OUTSIDE_MEET -> {
                outsideMeetLight.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.LIGHT_INSIDE_MEET -> {
                writeProperty(node, status, insideMeetLight)
            }
            SwitchNode.LIGHT_OUTSIDE_MEET -> {
                writeProperty(node, status, outsideMeetLight)
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
        when (property.propertyId) {
            SwitchNode.LIGHT_OUTSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_OUTSIDE_MEET, outsideMeetLight, property)
            }
            SwitchNode.LIGHT_INSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_INSIDE_MEET, insideMeetLight, property)
            }
            RadioNode.LIGHT_DELAYED_OUT.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_DELAYED_OUT, lightDelayOut, property)
            }
            RadioNode.LIGHT_FLICKER.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_FLICKER, lightFlicker, property)
            }
            else -> {}
        }
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: AtomicInteger): Boolean {
        val success = node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin)
        if (success && develop) {
            doUpdateRadioValue(node, atomic, value) { _node, _value ->
                doRadioChanged(_node, _value)
            }
        }
        return success
    }

    private fun writeProperty(node: SwitchNode, value: Boolean, atomic: AtomicBoolean): Boolean {
        val success = writeProperty(node.set.signal, node.value(value), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, value) { _node, _value ->
                doSwitchChanged(_node, _value)
            }
        }
        return success
    }


}