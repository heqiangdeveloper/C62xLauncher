package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
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


class AmbientLightingManager private constructor() : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = AmbientLightingManager::class.java.simpleName
        val instance: AmbientLightingManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AmbientLightingManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.FRONT_AMBIENT_LIGHTING.get.signal)
                add(SwitchNode.BACK_AMBIENT_LIGHTING.get.signal)
                add(SwitchNode.ALC_DOOR_HINT.get.signal)
                add(SwitchNode.ALC_LOCK_HINT.get.signal)
                add(SwitchNode.ALC_BREATHE_HINT.get.signal)
                add(SwitchNode.ALC_COMING_HINT.get.signal)
                add(SwitchNode.ALC_RELATED_TOPICS.get.signal)
                add(SwitchNode.ALC_SMART_MODE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val alcDoorHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_DOOR_HINT
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }
    private val alcLockHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }
    private val alcBreatheHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }
    private val alcComingHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_COMING_HINT
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }
    private val alcRelatedTopics: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }

    private val frontLighting: AtomicBoolean by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }
    private val backLighting: AtomicBoolean by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }

    private val alcSmartMode: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_SMART_MODE
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }


    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {

            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            else -> false
        }
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
            SwitchNode.ALC_DOOR_HINT -> {
                alcDoorHint.get()
            }
            SwitchNode.ALC_LOCK_HINT -> {
                alcLockHint.get()
            }
            SwitchNode.ALC_BREATHE_HINT -> {
                alcBreatheHint.get()
            }
            SwitchNode.ALC_COMING_HINT -> {
                alcComingHint.get()
            }
            SwitchNode.ALC_RELATED_TOPICS -> {
                alcRelatedTopics.get()
            }
            SwitchNode.FRONT_AMBIENT_LIGHTING -> {
                frontLighting.get()
            }
            SwitchNode.BACK_AMBIENT_LIGHTING -> {
                backLighting.get()
            }
            SwitchNode.ALC_SMART_MODE -> {
                alcSmartMode.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ALC_DOOR_HINT -> {
                writeProperty(node, status, alcDoorHint)
            }
            SwitchNode.ALC_LOCK_HINT -> {
                writeProperty(node, status, alcLockHint)
            }
            SwitchNode.ALC_BREATHE_HINT -> {
                writeProperty(node, status, alcBreatheHint)
            }
            SwitchNode.ALC_COMING_HINT -> {
                writeProperty(node, status, alcComingHint)
            }
            SwitchNode.ALC_RELATED_TOPICS -> {
                writeProperty(node, status, alcRelatedTopics)
            }
            SwitchNode.FRONT_AMBIENT_LIGHTING -> {
                writeProperty(node, status, frontLighting)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING -> {
                writeProperty(node, status, backLighting)
            }
            SwitchNode.ALC_SMART_MODE -> {
                writeProperty(node, status, alcSmartMode)
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
            SwitchNode.ALC_DOOR_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_DOOR_HINT, alcDoorHint, property)
            }
            SwitchNode.ALC_LOCK_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_LOCK_HINT, alcLockHint, property)
            }
            SwitchNode.ALC_BREATHE_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_BREATHE_HINT, alcBreatheHint, property)
            }
            SwitchNode.ALC_COMING_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_COMING_HINT, alcComingHint, property)
            }
            SwitchNode.ALC_RELATED_TOPICS.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_RELATED_TOPICS, alcRelatedTopics, property)
            }
            SwitchNode.FRONT_AMBIENT_LIGHTING.get.signal -> {
                onSwitchChanged(SwitchNode.FRONT_AMBIENT_LIGHTING, frontLighting, property)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_AMBIENT_LIGHTING, backLighting, property)
            }
            SwitchNode.ALC_SMART_MODE.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_SMART_MODE, alcSmartMode, property)
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