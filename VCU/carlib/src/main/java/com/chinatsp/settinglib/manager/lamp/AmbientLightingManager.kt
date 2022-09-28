package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.IProgressManager
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


class AmbientLightingManager private constructor() : BaseManager(), IOptionManager,
    IProgressManager {

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

                add(SwitchNode.SPEED_RHYTHM.get.signal)
                add(SwitchNode.MUSIC_RHYTHM.get.signal)
                add(SwitchNode.COLOUR_BREATHE.get.signal)

                /**【反馈】全车氛围灯亮度响应反馈*/
                add(Progress.AMBIENT_LIGHT_BRIGHTNESS.get.signal)
                add(Progress.AMBIENT_LIGHT_COLOR.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val alcDoorHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_DOOR_HINT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }
    private val alcLockHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcBreatheHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcComingHint: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_COMING_HINT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcRelatedTopics: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val frontLighting: AtomicBoolean by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val backLighting: AtomicBoolean by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcSmartMode: AtomicBoolean by lazy {
        val node = SwitchNode.ALC_SMART_MODE
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val speedRhythm: AtomicBoolean by lazy {
        val node = SwitchNode.SPEED_RHYTHM
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val musicRhythm: AtomicBoolean by lazy {
        val node = SwitchNode.MUSIC_RHYTHM
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val colourBreathe: AtomicBoolean by lazy {
        val node = SwitchNode.COLOUR_BREATHE
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val ambientBrightness: AtomicInteger by lazy {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        AtomicInteger(node.def).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            val value = if (result in node.min..node.max) result else node.def
            Timber.d("getBrightness node:$node, result:$result,, value:$value")
            this.set(value)
        }
    }

    private val ambientColor: AtomicInteger by lazy {
        val node = Progress.AMBIENT_LIGHT_COLOR
        AtomicInteger(node.def).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            Timber.d("getAmbientColor signal:%s, value:%s", node.get.signal, value)
            doUpdateProgress(node, this, value, instance::doProgressChanged)
        }
    }


    override fun doGetRadioOption(node: RadioNode): Int {
        return Constant.INVALID
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }

    override fun doGetProgress(node: Progress): Int {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                ambientBrightness.get()
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                ambientColor.get()
            }
            else -> -1
        }
    }

    override fun doSetProgress(node: Progress, value: Int): Boolean {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                val set = node.set
                val result = writeProperty(set.signal, value, set.origin)
                Timber.tag(TAG).d(
                    "setBrightness signal:%s, newValue:%s, result:%s",
                    set.signal, value, result
                )
                true
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                val set = node.set
                val result = writeProperty(set.signal, value, set.origin)
                Timber.tag(TAG).d(
                    "setBrightness signal:%s, newValue:%s, result:%s",
                    set.signal, value, result
                )
                true
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
            SwitchNode.SPEED_RHYTHM -> {
                speedRhythm.get()
            }
            SwitchNode.MUSIC_RHYTHM -> {
                musicRhythm.get()
            }
            SwitchNode.COLOUR_BREATHE -> {
                colourBreathe.get()
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
            SwitchNode.SPEED_RHYTHM -> {
                writeProperty(node, status, speedRhythm)
            }
            SwitchNode.MUSIC_RHYTHM -> {
                writeProperty(node, status, musicRhythm)
            }
            SwitchNode.COLOUR_BREATHE -> {
                writeProperty(node, status, colourBreathe)
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

            SwitchNode.SPEED_RHYTHM.get.signal -> {
                onSwitchChanged(SwitchNode.SPEED_RHYTHM, speedRhythm, property)
            }
            SwitchNode.MUSIC_RHYTHM.get.signal -> {
                onSwitchChanged(SwitchNode.MUSIC_RHYTHM, musicRhythm, property)
            }
            SwitchNode.COLOUR_BREATHE.get.signal -> {
                onSwitchChanged(SwitchNode.COLOUR_BREATHE, colourBreathe, property)
            }
            Progress.AMBIENT_LIGHT_BRIGHTNESS.get.signal -> {
                onAmbientBrightnessChanged(property)
            }
            Progress.AMBIENT_LIGHT_COLOR.get.signal -> {
                onAmbientColorChanged(property)
            }
            else -> {}
        }
    }

    private fun onAmbientBrightnessChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientBrightnessChanged value%s", value)
            doUpdateProgress(
                Progress.AMBIENT_LIGHT_BRIGHTNESS,
                ambientBrightness,
                value,
                this::doProgressChanged
            )
        }
    }

    private fun onAmbientColorChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientBrightnessChanged value%s", value)
            doUpdateProgress(
                Progress.AMBIENT_LIGHT_COLOR,
                ambientColor,
                value,
                this::doProgressChanged
            )
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

//    fun doGetBrightnessValue(): Int {
//        val signal = CarCabinManager.ID_ALC_AL_RESPONSE_BRIGHTNESS
//        val value = readIntProperty(signal, Origin.CABIN)
//        Timber.tag(TAG).d("doGetBrightnessValue signal:%s, value:%s", signal, value)
//        return value
//    }
//
//    fun doSetBrightnessValue(newValue: Int): Boolean {
//        val signal = CarCabinManager.ID_ALC_HUM_ALC_BRIGHTNESS_GRADE
//        val result = writeProperty(signal, newValue, Origin.CABIN)
//        Timber.tag(TAG).d(
//            "doSetBrightnessValue signal:%s, newValue:%s, result:%s",
//            signal, newValue, result
//        )
//        return result
//    }


}