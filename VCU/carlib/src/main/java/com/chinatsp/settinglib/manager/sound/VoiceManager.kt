package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.DownStatus
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class VoiceManager private constructor() : BaseManager(), IConcernChanged, ISoundManager {

    private val identity by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    companion object : ISignal {

        override val TAG: String = VoiceManager::class.java.simpleName

        val instance: VoiceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VoiceManager()
        }

    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val mcuSet = HashSet<Int>().apply {
                /**【反馈】返回设置音源音量信息*/
                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
            }
            put(SignalOrigin.MCU_SIGNAL, mcuSet)
            val cabinSet = HashSet<Int>().apply {
                add(CarCabinManager.ID_AMP_LOUD_SW_STS)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
            SignalOrigin.MCU_SIGNAL -> {
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
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is ISoundListener) {
                        listener.onSoundVolumeChanged(pos, serial)
                    }
                }
        }
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.let {
                if (it.containsKey(serial)) it else null
            }?.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
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
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
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
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
    }

    /**
     *
     * @param switchNode 开关选项
     * @param isStatus 开关期望状态
     */
    fun doSwitchOption(switchNode: SwitchNode, isStatus: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.SE_LOUDNESS -> {
                val signal = CarCabinManager.ID_HUM_LOUD_SW
                doSetProperty(signal, switchNode.obtainValue(isStatus), SignalOrigin.CABIN_SIGNAL)
            }
            else -> false
        }
    }


    override fun onPropertyChanged(type: SignalOrigin, property: CarPropertyValue<*>) {

    }

    private fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    private fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
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
                        listener.onACSwitchStatusChanged(status, type)
                    }
                }
        }
    }

}