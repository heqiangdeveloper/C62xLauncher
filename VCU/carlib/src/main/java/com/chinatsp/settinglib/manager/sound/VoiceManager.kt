package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.media.CarAudioManager
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
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


class VoiceManager private constructor() : BaseManager(), ISoundManager {

    private var manager: CarAudioManager? = null

    companion object : ISignal {
        override val TAG: String = VoiceManager::class.java.simpleName
        val instance: VoiceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VoiceManager()
        }
    }

    fun injectAudioManager(audioManager: CarAudioManager) {
        this.manager = audioManager
        manager?.registerVolumeChangeObserver(volumeListener)
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val mcuSet = HashSet<Int>().apply {
                if (VcuUtils.isAmplifier) {
                    add(SwitchNode.SPEED_VOLUME_OFFSET_INSERT.get.signal)
                }
                /**【反馈】返回设置音源音量信息*/
//                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
                add(RadioNode.ICM_VOLUME_LEVEL.get.signal)
            }
            put(Origin.MCU, mcuSet)
            val cabinSet = HashSet<Int>().apply {
//                add(CarCabinManager.ID_AMP_LOUD_SW_STS)
                add(SwitchNode.AUDIO_SOUND_TONE.get.signal)
                add(SwitchNode.AUDIO_SOUND_HUAWEI.get.signal)
                //当为外置功放时注册
                if (!VcuUtils.isAmplifier) {
                    add(SwitchNode.SPEED_VOLUME_OFFSET.get.signal)
                }
                add(SwitchNode.AUDIO_SOUND_LOUDNESS.get.signal)

                add(RadioNode.NAVI_AUDIO_MIXING.get.signal)
                add(RadioNode.SPEED_VOLUME_OFFSET.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val toneAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val touchTone: SwitchState by lazy {
        val node = SwitchNode.TOUCH_PROMPT_TONE
        SwitchState(node.default).apply {
            val result = getPromptToneLevel(node)
            val status = node.isOn(result)
            Timber.d("initAtomicBoolean node:$node, status:$status, result:$result, current:${get()}")
            if (status xor this.get()) {
                this.set(node.isOn(result))
            }
        }
    }

    val volumeSpeedSwitch: SwitchNode by lazy {
        if (VcuUtils.isAmplifier) {
            SwitchNode.SPEED_VOLUME_OFFSET_INSERT
        } else {
            SwitchNode.SPEED_VOLUME_OFFSET
        }
    }

    private val huaweiAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val offsetAtomic: SwitchState by lazy {
        val node = volumeSpeedSwitch
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }
    private val loudnessAtomic: SwitchState by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val icmVolumeLevel: RadioState by lazy {
        val node = RadioNode.ICM_VOLUME_LEVEL
        RadioState(node.def).apply {
            val array = readIntArray(node)
            val value = pickIndexValue(array, 4)
            if (null != value) {
                doUpdateRadioValue(node, this, value)
            }
        }
    }

    private val naviAudioMixing: RadioState by lazy {
        val node = RadioNode.NAVI_AUDIO_MIXING
        RadioState(node.def).apply {
            val value = VcuUtils.getConfigParameters(OffLine.NAVI_MIXING, 2)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val speedVolumeOffset: RadioState by lazy {
        val node = RadioNode.SPEED_VOLUME_OFFSET
        RadioState(node.def).apply {
            val value = readIntValue(node)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val naviVolume: Volume by lazy {
        initVolume(Progress.NAVI)
    }

    private val mediaVolume: Volume by lazy {
        initVolume(Progress.MEDIA)
    }

    private val voiceVolume: Volume by lazy {
        initVolume(Progress.VOICE)
    }

    private val phoneVolume: Volume by lazy {
        initVolume(Progress.PHONE)
    }

    private val systemVolume: Volume by lazy {
        initVolume(Progress.SYSTEM)
    }

    private val volumeListener: ContentObserver
        get() = object : ContentObserver(BaseApp.instance.mainHandler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (null == uri) {
                    return
                }
                when (ContentUris.parseId(uri)) {
                    0L -> {
                        Timber.d("navi")
                        val progress = Progress.NAVI
                        val method = instance::doProgressChanged
                        doUpdateProgress(naviVolume, obtainVolume(progress), true, method)
                    }
                    1L -> {
                        Timber.d("media")
                        val progress = Progress.MEDIA
                        val method = instance::doProgressChanged
                        doUpdateProgress(mediaVolume, obtainVolume(progress), true, method)
                    }
                    2L -> {
                        Timber.d("phone")
                        val progress = Progress.PHONE
                        val method = instance::doProgressChanged
                        doUpdateProgress(phoneVolume, obtainVolume(progress), true, method)
                    }
                    3L -> {
                        Timber.d("tts")
                        val progress = Progress.VOICE
                        val method = instance::doProgressChanged
                        doUpdateProgress(voiceVolume, obtainVolume(progress), true, method)
                    }
                    4L -> {
                        Timber.d("cruise")
                        val progress = Progress.SYSTEM
                        val method = instance::doProgressChanged
                        doUpdateProgress(systemVolume, obtainVolume(progress), true, method)
                    }
                    else -> {}
                }
            }
        }


    private fun readIntValue(node: RadioNode): Int {
        return readIntProperty(node.get.signal, node.get.origin, node.area)
    }

    private fun readIntArray(node: RadioNode): IntArray {
        return readIntArrayProperty(node.get.signal, node.get.origin, node.area)
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

    private fun pickIndexValue(array: IntArray, index: Int): Int? {
        if (index in array.indices) {
            return array[index]
        }
        return null
    }

    override fun onMcuPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            RadioNode.ICM_VOLUME_LEVEL.get.signal -> {
//                onMcuVolumeChanged(property)
                val array = (property.value as Array<*>).map { it as Int }.toIntArray()
                val value = pickIndexValue(array, 4)
                if (null != value) {
                    onRadioChanged(RadioNode.ICM_VOLUME_LEVEL, icmVolumeLevel, value)
                }
            }
            volumeSpeedSwitch.get.signal -> {
                val value = property.value
                if (value is Array<*> && value.size >= 15) {
                    updateVolumeFollowSpeedSwitch(value[14])
                }
            }
            else -> {}
        }
    }

    private fun updateVolumeFollowSpeedSwitch(value: Any?) {
        if (value !is Int) {
            Timber.e("updateVolumeFollowSpeedSwitch but value is not Int!")
            return
        }
        val node = volumeSpeedSwitch
        val atomic = offsetAtomic
        onSwitchChanged(node, atomic, value, this::doUpdateSwitch, this::doSwitchChanged)
    }

    private fun initVolume(type: Progress): Volume {
        var pos = obtainVolume(type)
        val max = getVolumeMaximum(type)
        if (pos < type.min) pos = type.min
        if (pos > max) pos = max
        return Volume(type, type.min, max, pos)
    }

    private fun onMcuVolumeChanged(values: IntArray) {
        if (6 == values.size) {
            updateVolumePosition(mediaVolume, values[0])
            updateVolumePosition(phoneVolume, values[1])
            updateVolumePosition(systemVolume, values[2])
            updateVolumePosition(naviVolume, values[3])
            updateVolumePosition(voiceVolume, values[5])
            Timber.d("return volume media:${values[0]}, phone:${values[1]}, voice:${values[5]}, navi:${values[3]}, system:${values[2]}")
            onMcuVolumeChanged()
        }
    }

    private fun updateVolumePosition(volume: Volume, value: Int) {
        volume.takeIf { it.pos != value }?.pos = value
    }


    private fun onMcuVolumeChanged() {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is ISoundListener) {
                        listener.onSoundVolumeChanged(
                            naviVolume, mediaVolume, phoneVolume, voiceVolume, systemVolume
                        )
                    }
                }
        } finally {
            readLock.unlock()
        }
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
            RadioNode.ICM_VOLUME_LEVEL -> icmVolumeLevel.deepCopy()
            RadioNode.NAVI_AUDIO_MIXING -> naviAudioMixing.deepCopy()
            RadioNode.SPEED_VOLUME_OFFSET -> speedVolumeOffset.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> {
                val intArray = intArrayOf(0x1F, value)
                writeProperty(node, intArray, icmVolumeLevel)
            }
            RadioNode.NAVI_AUDIO_MIXING -> {
                writeNaviMixing(node, value, naviAudioMixing)
            }
            RadioNode.SPEED_VOLUME_OFFSET -> {
                writeProperty(node, value, speedVolumeOffset)
            }
            else -> false
        }
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> toneAtomic.deepCopy()
            SwitchNode.AUDIO_SOUND_HUAWEI -> huaweiAtomic.deepCopy()
            SwitchNode.AUDIO_SOUND_LOUDNESS -> loudnessAtomic.deepCopy()
            SwitchNode.TOUCH_PROMPT_TONE -> touchTone.deepCopy()
            SwitchNode.SPEED_VOLUME_OFFSET_INSERT, SwitchNode.SPEED_VOLUME_OFFSET -> offsetAtomic.deepCopy()
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
            volumeSpeedSwitch -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.TOUCH_PROMPT_TONE -> {
                switchTouchTone(node, status)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        super.onHvacPropertyChanged(property)
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            volumeSpeedSwitch.get.signal -> {
                if (!VcuUtils.isAmplifier) {
                    onSwitchChanged(volumeSpeedSwitch, offsetAtomic, property)
                }
            }
            SwitchNode.AUDIO_SOUND_TONE.get.signal -> {
                onSwitchChanged(SwitchNode.AUDIO_SOUND_TONE, toneAtomic, property)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS.get.signal -> {
                onSwitchChanged(SwitchNode.AUDIO_SOUND_LOUDNESS, loudnessAtomic, property)
            }
            SwitchNode.AUDIO_SOUND_HUAWEI.get.signal -> {
                onSwitchChanged(SwitchNode.AUDIO_SOUND_HUAWEI, huaweiAtomic, property)
            }
            RadioNode.SPEED_VOLUME_OFFSET.get.signal -> {
                onRadioChanged(RadioNode.SPEED_VOLUME_OFFSET, speedVolumeOffset, property)
            }
            RadioNode.NAVI_AUDIO_MIXING.get.signal -> {
                onRadioChanged(RadioNode.NAVI_AUDIO_MIXING, naviAudioMixing, property)
            }
            else -> {}
        }
    }


    override fun doGetVolume(progress: Progress): Volume? {
        return when (progress) {
            Progress.NAVI -> naviVolume
            Progress.VOICE -> voiceVolume
            Progress.MEDIA -> mediaVolume
            Progress.PHONE -> phoneVolume
            Progress.SYSTEM -> systemVolume
            else -> null
        }
    }

    override fun doSetVolume(progress: Progress, value: Int): Boolean {
        return when (progress) {
            Progress.NAVI, Progress.VOICE, Progress.MEDIA,
            Progress.PHONE, Progress.SYSTEM,
            -> {
                val volume = doGetVolume(progress)
                if (value != volume?.pos) {
                    setVolumePosition(progress.set.signal, value)
                }
//                doGetVolume(type)?.pos = value
                true
            }
            else -> false
        }
    }

    private fun obtainVolume(type: Progress): Int {
        try {
            val value = manager?.let {
                it.getGroupVolume(it.getVolumeGroupIdForUsage(type.get.signal))
            } ?: 12
            val result = if (value < type.min) type.min else value
            Timber.d("getVolumePosition type:$type, value:$value, result:$result")
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 12
    }

    private fun getVolumeMaximum(type: Progress): Int {
        try {
            return manager?.let {
                it.getGroupMaxVolume(it.getVolumeGroupIdForUsage(type.get.signal))
            } ?: type.max
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return type.max
    }

    private fun getPromptToneLevel(node: SwitchNode): Int {
        if (node.get.origin == Origin.SPECIAL) {
            val result = manager?.beepLevel ?: node.get.off
            Timber.d("doActionSignal GET: node:%s, result:%s", node, result)
            return result
        }
        return -1
    }

    private fun switchTouchTone(node: SwitchNode, status: Boolean): Boolean {
        try {
            if (node.set.origin == Origin.SPECIAL) {
                val result = manager?.let {
                    val expect = if (status) node.set.on else node.set.off
                    it.beepLevel = expect
                    val actual = it.beepLevel
                    doUpdateSwitch(node, touchTone, actual, null)
                    Timber.d("doActionSignal SET node:$node, status:$status, expect:$expect, actual:$actual")
                    return@let actual == expect
                } ?: false
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun setVolumePosition(type: Int, value: Int) {
        try {
            manager?.let {
                it.setGroupVolume(it.getVolumeGroupIdForUsage(type), value, 0)
                Timber.d("setVolumePosition type:$type, value:$value")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: RadioState): Boolean {
        //        if (success && develop) {
//            val newValue = node.obtainSelectValue(value, false)
//            doUpdateRadioValue(node, atomic, newValue) { _node, _value ->
//                doOptionChanged(_node, _value)
//            }
//        }
        return (node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin))
    }

    private fun writeProperty(node: RadioNode, value: IntArray, atomic: RadioState): Boolean {
        return writeProperty(node.set.signal, value, node.set.origin)
    }

    private fun writeNaviMixing(node: RadioNode, value: Int, atomic: RadioState): Boolean {
        //        if (success && develop) {
//            val newValue = node.obtainSelectValue(value, false)
//            doUpdateRadioValue(node, atomic, newValue) { _node, _value ->
//                doOptionChanged(_node, _value)
//            }
//        }
        return (node.isValid(value, false)
                && VcuUtils.setConfigParameters(OffLine.NAVI_MIXING, value))
    }

    fun resetDeviceVolume() {
        val default = 12
        doSetVolume(Progress.NAVI, default)
        doSetVolume(Progress.MEDIA, default)
        doSetVolume(Progress.PHONE, default)
        doSetVolume(Progress.VOICE, default)
        doSetVolume(Progress.SYSTEM, default)
    }


}