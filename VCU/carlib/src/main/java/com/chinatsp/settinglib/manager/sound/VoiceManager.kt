package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.mcu.CarMcuManager
import android.car.media.CarAudioManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IACListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

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

    private val toneAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }
    private val huaweiAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }
    private val offsetAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_OFFSET
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }
    private val loudnessAtomic: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val naviVolume: Volume by lazy {
        initVolume(Volume.Type.NAVI)
    }

    private val mediaVolume: Volume by lazy {
        initVolume(Volume.Type.MEDIA)
    }

    private val voiceVolume: Volume by lazy {
        initVolume(Volume.Type.VOICE)
    }

    private val phoneVolume: Volume by lazy {
        initVolume(Volume.Type.PHONE)
    }

    private val systemVolume: Volume by lazy {
        initVolume(Volume.Type.SYSTEM)
    }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val mcuSet = HashSet<Int>().apply {
                /**【反馈】返回设置音源音量信息*/
                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
            }
            put(Origin.MCU, mcuSet)
            val cabinSet = HashSet<Int>().apply {
                add(CarCabinManager.ID_AMP_LOUD_SW_STS)
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
            Origin.MCU -> {
                onMcuPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    private fun onMcuPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            CarMcuManager.ID_AUDIO_VOL_SETTING_INFO -> {
                onMcuVolumeChanged(property)
            }
            else -> {}
        }
    }

    private fun initVolume(type: Volume.Type): Volume {
        val pos = getVolumePosition(type)
        val max = getVolumeMaximum(type)
        return Volume(type, 0, max, pos)
    }

    private fun onMcuVolumeChanged(property: CarPropertyValue<*>) {
        val value = property.value
        value?.let {
            if (it is Array<*>) {
                it.forEachIndexed { index, any -> LogManager.d("luohong", "index[$index]=$any") }
                if (it.size >= 5) {
                    updateVolumePosition(mediaVolume, it.elementAt(0) as Int)
                    updateVolumePosition(phoneVolume, it.elementAt(1) as Int)
                    updateVolumePosition(voiceVolume, it.elementAt(2) as Int)
                    updateVolumePosition(naviVolume, it.elementAt(3) as Int)
                    updateVolumePosition(systemVolume, it.elementAt(4) as Int)
                    onMcuVolumeChanged()
                }
            }
        }
    }

    private fun updateVolumePosition(volume: Volume, value: Int) {
        volume.takeIf { it.pos != value }?.pos = value
    }

    private fun onMcuVolumeChanged() {
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is ISoundListener) {
                        listener.onSoundVolumeChanged(
                            navi = naviVolume,
                            media = mediaVolume,
                            phone = phoneVolume,
                            voice = voiceVolume,
                            system = systemVolume
                        )
                    }
                }
        }
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        TODO("Not yet implemented")
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                toneAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                huaweiAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_OFFSET -> {
                offsetAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                loudnessAtomic.get()
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
            SwitchNode.AUDIO_SOUND_OFFSET -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            else -> false
        }
    }


    /**
     * 【设置】仪表报警音量等级开关触发
     * @param value 仪表报警音量等级开关触发[0x1,0,0x0,0x3]
    0x0: Inactive
    0x1: High
    0x2: medium
    0x3: Low
     */
    fun doUpdateAlarmOption(value: Int): Boolean {
        val isValid = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarCabinManager.ID_HUM_ICM_VOLUME_LEVEL
        return writeProperty(signal, value, Origin.CABIN, Area.GLOBAL)
    }

    /**
     * 【设置】车机混音策略[0x1,-1,0x0,0x3]
     * 车机混音策略[0x1,-1,0x0,0x3] 0x0:not used 0x1: MIX0((default)) 0x2: Mix1 0x3: Mix2 0x4~0x7: reserved
     */
    fun doUpdateRemixOption(value: Int): Boolean {
        val isValid = listOf(0x01, 0x03).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarCabinManager.ID_HUM_SOUND_MIX
        return writeProperty(signal, value, Origin.CABIN, Area.GLOBAL)
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
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    private fun notifySwitchStatus(status: Boolean, type: SwitchNode) {
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is IACListener) {
                        listener.onSwitchOptionChanged(status, type)
                    }
                }
        }
    }

    override fun doGetVolume(type: Volume.Type): Volume? {
        return when (type) {
            Volume.Type.NAVI -> {
                naviVolume
            }
            Volume.Type.VOICE -> {
                voiceVolume
            }
            Volume.Type.MEDIA -> {
                mediaVolume
            }
            Volume.Type.PHONE -> {
                phoneVolume
            }
            Volume.Type.SYSTEM -> {
                systemVolume
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Volume.Type, value: Int): Boolean {
        return when (type) {
            Volume.Type.NAVI,
            Volume.Type.VOICE,
            Volume.Type.MEDIA,
            Volume.Type.PHONE,
            Volume.Type.SYSTEM -> {
                setVolumePosition(type.id, value)
                true
            }
            else -> false
        }
    }

    private fun getVolumePosition(type: Volume.Type): Int {
        return getVolumePosition(type.id)
    }

    private fun getVolumeMaximum(type: Volume.Type): Int {
        return getVolumeMaximum(type.id)
    }

    private fun setVolumePosition(type: Int, value: Int) {
        try {
            manager?.let {
                it.setGroupVolume(it.getVolumeGroupIdForUsage(type), value, 0)
                LogManager.d(TAG, "setVolumePosition type:$type, value:$value")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getVolumePosition(type: Int): Int {
        try {
            return manager?.let {
                it.getGroupVolume(it.getVolumeGroupIdForUsage(type))
            } ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    private fun getVolumeMaximum(type: Int): Int {
        try {
            return manager?.let {
                it.getGroupMaxVolume(it.getVolumeGroupIdForUsage(type))
            } ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

}