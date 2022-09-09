package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.mcu.CarMcuManager
import android.car.media.CarAudioManager
import com.chinatsp.settinglib.VcuUtils
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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

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
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val mcuSet = HashSet<Int>().apply {
                /**【反馈】返回设置音源音量信息*/
                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
                if (VcuUtils.isAmplifier) {
                    add(SwitchNode.SPEED_VOLUME_OFFSET_INSERT.get.signal)
                }
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

                add(RadioNode.ICM_VOLUME_LEVEL.get.signal)
                add(RadioNode.NAVI_AUDIO_MIXING.get.signal)
                add(RadioNode.SPEED_VOLUME_OFFSET.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val toneAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val touchTone: AtomicBoolean by lazy {
        val node = SwitchNode.TOUCH_PROMPT_TONE
        AtomicBoolean(node.default).apply {
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

    private val huaweiAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val offsetAtomic: AtomicBoolean by lazy {
        val node = volumeSpeedSwitch
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }
    private val loudnessAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val icmVolumeLevel: AtomicInteger by lazy {
        val node = RadioNode.ICM_VOLUME_LEVEL
        AtomicInteger(node.default).apply {
            val value = readIntValue(node)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val naviAudioMixing: AtomicInteger by lazy {
        val node = RadioNode.NAVI_AUDIO_MIXING
        AtomicInteger(node.default).apply {
            val value = VcuUtils.getConfigParameters(OffLine.NAVI_MIXING, 2)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val speedVolumeOffset: AtomicInteger by lazy {
        val node = RadioNode.SPEED_VOLUME_OFFSET
        AtomicInteger(node.default).apply {
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

    private fun readIntValue(node: RadioNode): Int {
        return readIntProperty(node.get.signal, node.get.origin, node.area)
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
            volumeSpeedSwitch.get.signal -> {
                val value = property.value
                if (value is Array<*> && value.size >= 15) {
                    onSwitchChanged(volumeSpeedSwitch, offsetAtomic, value[14])
                }
            }
            else -> {}
        }
    }

    fun onSwitchChanged(node: SwitchNode, atomic: AtomicBoolean, value: Any?) {
        if (value !is Int) {
            Timber.e("onSwitchChanged but value is not Int! node:$node, value:$value")
            return
        }
        Timber.d("doSwitchChanged node:$node, value:$value, status:${node.isOn(value)}")
        onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue, this::doSwitchChanged)
    }

    private fun initVolume(type: Progress): Volume {
        val pos = getVolumePosition(type)
        val max = getVolumeMaximum(type)
        return Volume(type, type.min, max, pos)
    }

    private fun onMcuVolumeChanged(property: CarPropertyValue<*>) {
        property.value?.let {
            if ((it is Array<*>) && (it.size >= 5)) {
                updateVolumePosition(mediaVolume, it.elementAt(0) as Int)
                updateVolumePosition(phoneVolume, it.elementAt(1) as Int)
                updateVolumePosition(voiceVolume, it.elementAt(2) as Int)
                updateVolumePosition(naviVolume, it.elementAt(3) as Int)
                updateVolumePosition(systemVolume, it.elementAt(4) as Int)
                Timber.d("return volume media:${it[0]}, phone:${it[1]}, voice:${it[2]}, navi:${it[3]}, system:${it[4]}")
                onMcuVolumeChanged()
            }
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
            listenerStore.put(serial, WeakReference(listener))
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> {
                icmVolumeLevel.get()
            }
            RadioNode.NAVI_AUDIO_MIXING -> {
                naviAudioMixing.get()
            }
            RadioNode.SPEED_VOLUME_OFFSET -> {
                speedVolumeOffset.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> {
                writeProperty(node, value, icmVolumeLevel)
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

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                toneAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                huaweiAtomic.get()
            }
            volumeSpeedSwitch -> {
                offsetAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                loudnessAtomic.get()
            }
            SwitchNode.TOUCH_PROMPT_TONE -> {
                touchTone.get()
                true
            }
            else -> false
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


    /**
     *
     * @param switchNode 开关选项
     * @param isStatus 开关期望状态
     */
    fun doSwitchOption(switchNode: SwitchNode, isStatus: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                val signal = CarCabinManager.ID_HUM_LOUD_SW
                writeProperty(signal, switchNode.value(isStatus), Origin.CABIN)
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
            RadioNode.ICM_VOLUME_LEVEL.get.signal -> {
                onRadioChanged(RadioNode.ICM_VOLUME_LEVEL, icmVolumeLevel, property)
            }
            else -> {}
        }
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.NAVI -> {
                naviVolume
            }
            Progress.VOICE -> {
                voiceVolume
            }
            Progress.MEDIA -> {
                mediaVolume
            }
            Progress.PHONE -> {
                phoneVolume
            }
            Progress.SYSTEM -> {
                systemVolume
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Progress, value: Int): Boolean {
        return when (type) {
            Progress.NAVI, Progress.VOICE, Progress.MEDIA,
            Progress.PHONE, Progress.SYSTEM -> {
                setVolumePosition(type.set.signal, value)
                doGetVolume(type)?.pos = value
                true
            }
            else -> false
        }
    }

    private fun getVolumePosition(type: Progress): Int {
        try {
            val value = manager?.let {
                it.getGroupVolume(it.getVolumeGroupIdForUsage(type.get.signal))
            } ?: 12
            Timber.d("getVolumePosition type:$type, value:$value")
            return value
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
            val result = manager?.let {
                it.beepLevel
            } ?: node.get.off
            Timber.d("getPromptToneLevel: node:%s, result:%s", node, result)
        }
        return -1;
    }

    private fun switchTouchTone(node: SwitchNode, status: Boolean): Boolean {
        try {
            if (node.set.origin == Origin.SPECIAL) {
                val result = manager?.let {
                    it.beepLevel = if (status) node.set.on else node.set.off
                    Timber.d(
                        "switchTouchTone node:%s, status:%s, beepLevel:%s",
                        node,
                        status,
                        it.beepLevel
                    )
                    return@let true
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

    private fun writeNaviMixing(node: RadioNode, value: Int, atomic: AtomicInteger): Boolean {
        val success = node.isValid(value, false)
                && VcuUtils.setConfigParameters(OffLine.NAVI_MIXING, value)
        if (success && develop) {
            doUpdateRadioValue(node, atomic, value) { _node, _value ->
                doOptionChanged(_node, _value)
            }
        }
        return success
    }

    fun resetDeviceVolume() {
    }


}