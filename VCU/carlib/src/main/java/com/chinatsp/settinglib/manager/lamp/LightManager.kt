package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
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


class LightManager private constructor() : BaseManager(), IOptionManager, IProgressManager {

    companion object : ISignal {
        override val TAG: String = LightManager::class.java.simpleName
        val instance: LightManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LightManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(Progress.SWITCH_BACKLIGHT_BRIGHTNESS.get.signal)
                add(RadioNode.LIGHT_DELAYED_OUT.get.signal)
                add(RadioNode.LIGHT_FLICKER.get.signal)
                add(RadioNode.LIGHT_CEREMONY_SENSE.get.signal)
                add(SwitchNode.LIGHT_CEREMONY_SENSE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val insideMeetLight: AtomicBoolean by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightCeremonySenseSwitch: AtomicBoolean by lazy {
        val node = SwitchNode.LIGHT_CEREMONY_SENSE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val outsideMeetLight: AtomicBoolean by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightDelayOut: AtomicInteger by lazy {
        val node = RadioNode.LIGHT_DELAYED_OUT
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val lightFlicker: AtomicInteger by lazy {
        val node = RadioNode.LIGHT_FLICKER
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val lightCeremonySense: AtomicInteger by lazy {
        val node = RadioNode.LIGHT_CEREMONY_SENSE
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val switchBacklight: Volume by lazy {
        initProgress(Progress.SWITCH_BACKLIGHT_BRIGHTNESS)
    }

    private fun initProgress(type: Progress): Volume {
        val value = readIntProperty(type.get.signal, type.get.origin)
        val position = findBacklightLevel(value)
        Timber.d("initProgress $type type:$type, value:$value, position:$position")
        return Volume(type, type.min, type.max, position)
    }

    private fun findBacklightLevel(value: Int): Int {
        val backlightLevel = getBacklightLevel()
        var level = 0
        backlightLevel.forEachIndexed { index, i ->
            if (i == value) level = index
        }
        return level
    }

    private fun getBacklightLevel(): IntArray {
        return intArrayOf(0x00, 0x19, 0x33, 0x4C, 0x66, 0x7F, 0x99, 0xB2, 0xCC, 0xE5, 0xFF)
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
            RadioNode.LIGHT_CEREMONY_SENSE -> {
                writeProperty(node, value, lightCeremonySense)
            }
            else -> false
        }
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                switchBacklight
            }
            else -> {
                null
            }
        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        return when (type) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                val backlightLevel = getBacklightLevel()
                if (position in 0..backlightLevel.size) {
                    val value = backlightLevel[position]
                    return writeProperty(type.set.signal, value, type.set.origin)
                }
                false
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
            listenerStore[serial] = WeakReference(listener)
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
            SwitchNode.LIGHT_CEREMONY_SENSE -> {
                lightCeremonySenseSwitch.get()
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
            SwitchNode.LIGHT_CEREMONY_SENSE -> {
                writeProperty(node, status, lightCeremonySenseSwitch)
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
            SwitchNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_CEREMONY_SENSE, lightCeremonySenseSwitch, property)
            }
            RadioNode.LIGHT_DELAYED_OUT.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_DELAYED_OUT, lightDelayOut, property)
            }
            RadioNode.LIGHT_FLICKER.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_FLICKER, lightFlicker, property)
            }
            RadioNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_CEREMONY_SENSE, lightCeremonySense, property)
            }
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS.get.signal -> {
                val value = property.value as Int
                val level = findBacklightLevel(value)
                Timber.d("onCabinPropertyChanged SWITCH_BACKLIGHT_BRIGHTNESS value:$value, level:$level")
                doUpdateProgress(switchBacklight, level, true, this::doProgressChanged)
            }
            else -> {}
        }
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: AtomicInteger): Boolean {
        val success = node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin)
        if (success && develop) {
            doUpdateRadioValue(node, atomic, value) { _node, _value ->
                doOptionChanged(_node, _value)
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