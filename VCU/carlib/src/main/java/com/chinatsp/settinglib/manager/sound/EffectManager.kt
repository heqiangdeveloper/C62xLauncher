package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.mcu.CarMcuManager
import android.car.media.CarAudioManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
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


class EffectManager private constructor() : BaseManager(), ISoundManager {

    companion object : ISignal {
        override val TAG: String = EffectManager::class.java.simpleName
        val instance: EffectManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EffectManager()
        }
    }

    private val toneAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }
    private val huaweiAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val offsetAtomic: SwitchState by lazy {
        val node = SwitchNode.SPEED_VOLUME_OFFSET
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val loudnessAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val audioEffectStatus: SwitchState by lazy {
        val node = SwitchNode.AUDIO_ENVI_AUDIO
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val audioEffectOption: RadioState by lazy {
        val node = RadioNode.AUDIO_ENVI_AUDIO
//        AtomicInteger(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, result)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

     val eqMode: RadioState by lazy {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        RadioState(node.def).apply {
            val eqId = getDefaultEqSerial()
            set(eqId)
//            doUpdateRadioValue(node, this, eqId, instance::doOptionChanged)
        }
    }

    public fun getDefaultEqSerial(): Int {
        val eqId = SettingManager.instance.getEQ()
        var index: Int
        val eqIdArray = getEqIdArray()
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        if (null != eqId) {
            index = eqIdArray.indexOf(eqId)
            if (index !in 0..node.get.values.size) {
                index = if (VcuUtils.isAmplifier) 1 else 0
            }
        } else {
            index = if (VcuUtils.isAmplifier) 1 else 0
        }
        Timber.d("getDefaultEqSerial eqId:$eqId, " +
                "index:$index, VcuUtils.isAmplifier:${VcuUtils.isAmplifier}")
        return node.get.values[index]
    }

    private val insetArray: IntArray by lazy {
        intArrayOf(
            CarAudioManager.EQ_MODE_FLAT, //"默认"
            CarAudioManager.EQ_MODE_CLASSIC, //"典型的"
            CarAudioManager.EQ_MODE_POP, //"流行"
            CarAudioManager.EQ_MODE_JAZZ, //"爵士"
            CarAudioManager.EQ_MODE_BEATS, //"打击"
            CarAudioManager.EQ_MODE_ROCK, //"摇滚"
            CarAudioManager.EQ_MODE_CUSTOM //"自定义"
        )
    }

    private val outsetArray: IntArray by lazy {
        intArrayOf(
            CarAudioManager.EQ_MODE_AMP_BEGIN, //"默认"
            CarAudioManager.EQ_MODE_AMP_CLASSIC, //"典型的"
            CarAudioManager.EQ_MODE_AMP_POP, //"流行"
            CarAudioManager.EQ_MODE_AMP_JAZZ, //"爵士"
            CarAudioManager.EQ_MODE_AMP_BEATS, //"打击"
            CarAudioManager.EQ_MODE_AMP_ROCK, //"摇滚"
            CarAudioManager.EQ_MODE_AMP_CUSTOM //"自定义"
        )
    }

     fun getEqIdArray(): IntArray {
        val amplifier = VcuUtils.isAmplifier
        return if (amplifier) insetArray else outsetArray
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.AUDIO_ENVI_AUDIO.get.signal)
                add(SwitchNode.AUDIO_SOUND_LOUDNESS.get.signal)
                add(RadioNode.AUDIO_ENVI_AUDIO.get.signal)
                add(RadioNode.SYSTEM_SOUND_EFFECT.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onHandleSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        when (origin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            Origin.HVAC -> {
                onHvacPropertyChanged(property)
            }
            Origin.MCU -> {
                onMcuPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun onMcuPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            CarMcuManager.ID_AUDIO_VOL_SETTING_INFO -> {
                onMcuVolumeChanged(property)
            }
            else -> {}
        }
    }

    private fun onMcuVolumeChanged(property: CarPropertyValue<*>) {
        val value = property.value
        value?.let {
            if (it is Array<*>) {
                if (it.size >= 5) {
                    onMcuVolumeChanged(it.elementAt(0) as Int, "MEDIA")
                    onMcuVolumeChanged(it.elementAt(1) as Int, "PHONE")
                    onMcuVolumeChanged(it.elementAt(2) as Int, "VOICE")
                    onMcuVolumeChanged(it.elementAt(3) as Int, "NAVI")
                    onMcuVolumeChanged(it.elementAt(4) as Int, "SYSTEM")
                }
            }
        }
    }

    private fun onMcuVolumeChanged(pos: Int, serial: String) {
//        synchronized(listenerStore) {
//            listenerStore.filter { null != it.value.get() }
//                .forEach {
//                    val listener = it.value.get()
//                    if (listener is ISoundListener) {
//                        listener.onSoundVolumeChanged(pos, serial)
//                    }
//                }
//        }
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
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

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> eqMode.deepCopy()
            RadioNode.AUDIO_ENVI_AUDIO -> audioEffectOption.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> {
                Timber.d("doSetRadioOption SYSTEM_SOUND_EFFECT value:$value")
                val result = doUpdateSoundEffect(node, value)
                if (result) doUpdateRadioValue(node, eqMode, value, this::doOptionChanged)
                result
            }
            RadioNode.AUDIO_ENVI_AUDIO -> {
                writeProperty(node.set.signal, value, node.set.origin)
            }
            else -> false
        }
    }

    private fun doUpdateSoundEffect(node: RadioNode, value: Int): Boolean {
        doSetEQ(value)
        return true
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> toneAtomic.deepCopy()
            SwitchNode.AUDIO_SOUND_HUAWEI -> huaweiAtomic.deepCopy()
            SwitchNode.SPEED_VOLUME_OFFSET -> offsetAtomic.deepCopy()
            SwitchNode.AUDIO_SOUND_LOUDNESS -> loudnessAtomic.deepCopy()
            SwitchNode.AUDIO_ENVI_AUDIO -> audioEffectStatus.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.SPEED_VOLUME_OFFSET -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.AUDIO_ENVI_AUDIO -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            else -> false
        }
    }

    override fun doGetVolume(progress: Progress): Volume? {
        return null
    }

    override fun doSetVolume(progress: Progress, position: Int): Boolean {
        return false
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.AUDIO_ENVI_AUDIO.get.signal -> {
                onSwitchChanged(SwitchNode.AUDIO_ENVI_AUDIO, audioEffectStatus, property)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS.get.signal -> {
                onSwitchChanged(SwitchNode.AUDIO_SOUND_LOUDNESS, loudnessAtomic, property)
            }
            RadioNode.AUDIO_ENVI_AUDIO.get.signal -> {
                onRadioChanged(RadioNode.AUDIO_ENVI_AUDIO, audioEffectOption, property)
            }
            RadioNode.SYSTEM_SOUND_EFFECT.get.signal -> {
                Timber.d("onCabinPropertyChanged SYSTEM_SOUND_EFFECT value:${property.value}")
//                onRadioChanged(RadioNode.SYSTEM_SOUND_EFFECT, eqMode, property)
            }
            else -> {}
        }
    }


    fun doSetEQ(mode: Int, eqValues: IntArray) {
        val manager = SettingManager.instance
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val eqModeId = findEqIdSerial(node, mode)
        manager.setAudioEQ(eqModeId, mode, eqValues)
    }

    private fun doSetEQ(mode: Int) {
        val manager = SettingManager.instance
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val eqModeId = findEqIdSerial(node, mode)
        manager.setAudioEQ(eqModeId)
    }

    private fun findEqIdSerial(node: RadioNode, value: Int): Int {
        val values = node.set.values
        val index = values.indexOf(value)
        val eqIdArray = getEqIdArray()
        return if (index in 0..values.size) {
            eqIdArray[index]
        } else {
            eqIdArray[1]
        }
    }

    fun setAudioBalance(uiBalanceLevelValue: Int, uiFadeLevelValue: Int) {
        val manager = SettingManager.instance
        manager.setAudioBalance(uiBalanceLevelValue, uiFadeLevelValue)
    }

    fun getAudioBalance(): Int {
        val manager = SettingManager.instance
        return manager.getAudioBalance()
    }

    fun getAudioVoice(id: Int): Int {
        val manager = SettingManager.instance
        return manager.getAudioVoice(id)
    }

    fun audioFade(): Int {
        val manager = SettingManager.instance
        return manager.audioFade
    }


}