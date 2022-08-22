package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
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


class EffectManager private constructor() : BaseManager(), ISoundManager {

    companion object : ISignal {
        override val TAG: String = EffectManager::class.java.simpleName
        val instance: EffectManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EffectManager()
        }
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
        val node = SwitchNode.SPEED_VOLUME_OFFSET
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

    private val audioEffectStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AUDIO_ENVI_AUDIO
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val audioEffectOption: AtomicInteger by lazy {
        val node = RadioNode.AUDIO_ENVI_AUDIO
        AtomicInteger(node.default).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, result)
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
//            val mcuSet = HashSet<Int>().apply {
//                /**【反馈】返回设置音源音量信息*/
//                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
//            }
//            put(Origin.MCU, mcuSet)
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.AUDIO_ENVI_AUDIO.get.signal)
                add(SwitchNode.AUDIO_SOUND_LOUDNESS.get.signal)
                add(RadioNode.AUDIO_ENVI_AUDIO.get.signal)
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

    private fun onMcuPropertyChanged(property: CarPropertyValue<*>) {
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
            listenerStore.put(serial, WeakReference(listener))
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> {
                readIntProperty(node.get.signal, node.get.origin)
            }
            RadioNode.AUDIO_ENVI_AUDIO -> {
                audioEffectOption.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> {
                writeProperty(node.set.signal, value, node.set.origin)
            }
            RadioNode.AUDIO_ENVI_AUDIO -> {
                writeProperty(node.set.signal, value, node.set.origin)
            }
            else -> -1
        }
        return true
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                toneAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                huaweiAtomic.get()
            }
            SwitchNode.SPEED_VOLUME_OFFSET -> {
                offsetAtomic.get()
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                loudnessAtomic.get()
            }
            SwitchNode.AUDIO_ENVI_AUDIO -> {
                audioEffectStatus.get()
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

    override fun doGetVolume(type: Progress): Volume? {
        TODO("Not yet implemented")
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        TODO("Not yet implemented")
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
            RadioNode.AUDIO_ENVI_AUDIO.get.signal -> {
                onRadioChanged(RadioNode.AUDIO_ENVI_AUDIO, audioEffectOption, property)
            }
            else -> {}
        }
    }

}