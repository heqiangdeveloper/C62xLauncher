package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.DownStatus
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
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


class SoundAudioManager private constructor() : BaseManager(), IConcernChanged, IAcManager {

    /**
     * 【反馈】self-desiccation
     * self-desiccation 自干燥功能状态显示 0x0:ON 0x1:OFF
     * AcSelfStsDisp
     */
    private val cabinAridSignal = CarCabinManager.ID_ACSELFSTSDISP

    /**
     * 【反馈】解锁预通风功能开启状态
     * 解锁预通风功能开启状态 0x0:ON 0x1:OFF
     * VehicleArea:GLOBAL
     * AcPreVentnDisp
     */
    private val cabinWindSignal = CarCabinManager.ID_ACPREVENTNDISP

    /**
     * 【反馈】空调舒适性状态显示
     * 空调舒适性状态显示 0x0: Reserved 0x1: Gentle 0x2:
     *  Standard 0x3: Powerful 0x4~0x6: Reserved 0x7: Invalid
     * AcCmftStsDisp
     */
    private val cabinComfortSignal = CarCabinManager.ID_ACCMFTSTSDISP

    private val hvacDemistSignal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST

    private val identity by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    companion object : ISignal {

        override val TAG: String = SoundAudioManager::class.java.simpleName


        val INSTANCE: SoundAudioManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SoundAudioManager()
        }

        override val mcuConcernedSerial: Set<Int> by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
                /**【反馈】返回设置音源音量信息*/
                add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
            }
        }

        override val cabinConcernedSerial: Set<Int> by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
                add(CarCabinManager.ID_ACSELFSTSDISP)/**空调自干燥*/
                add(CarCabinManager.ID_ACPREVENTNDISP)/**预通风功能*/
                add(CarCabinManager.ID_ACCMFTSTSDISP) /**空调舒适性状态显示*/
            }
        }
        override val hvacConcernedSerial: Set<Int> by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {}
        }
    }

    val aridStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoAridStatus()) }
    }

    val demistStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoDemistStatus()) }
    }

    val windStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoWindStatus()) }
    }

    val comfortOption: AtomicInteger by lazy {
        AtomicInteger(0).also { it.set(obtainAutoComfortOption()) }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onPropertyChanged(signalOrigin, property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onPropertyChanged(signalOrigin, property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        return when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                cabinConcernedSerial.contains(signal)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                hvacConcernedSerial.contains(signal)
            }
            else -> false
        }
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int>? {
        return when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> cabinConcernedSerial
            SignalOrigin.HVAC_SIGNAL -> hvacConcernedSerial
            SignalOrigin.MCU_SIGNAL -> mcuConcernedSerial
            else -> null
        }
    }


    override fun obtainAutoAridStatus(): Boolean {
        val value = signalService.doGetIntProperty(cabinAridSignal, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
        LogManager.d("obtainAutoAridStatus value:$value")
        return Status1.ON.value == value
    }

    override fun obtainAutoWindStatus(): Boolean {
        val value = signalService.doGetIntProperty(cabinWindSignal, SignalOrigin.CABIN_SIGNAL ,Area.GLOBAL)
        LogManager.d("obtainAutoWindStatus value:$value")
        return Status1.ON.value == value
    }

    override fun obtainAutoDemistStatus(): Boolean {
        val value = signalService.doGetIntProperty(hvacDemistSignal, SignalOrigin.HVAC_SIGNAL, Area.GLOBAL)
        return Status1.ON.value == value
    }

    override fun obtainAutoComfortOption(): Int {
        return signalService.doGetIntProperty(cabinComfortSignal, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
    }


    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
//            listenerStore.let {
//                if (it.containsKey(serial)) it else null
//            }?.remove(serial)
            val contains = listenerStore.containsKey(serial)
            if (contains) listenerStore.remove(serial)
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
     * 更新空调舒适性
     * @param value (自动舒适模式开关AC Auto Comfort Switch
    0x0: Inactive
    0x1: Gentle
    0x2: Standard
    0x3: Powerful
    0x4~0x6: Reserved
    0x7: Invalid
     */
    fun doUpdateAcComfort(value: Int): Boolean {
        val invalidValue = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!invalidValue) {
            return false
        }
        val signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT
        return doSetProperty(signal, value, SignalOrigin.HVAC_SIGNAL, Area.GLOBAL)
    }

    /**
     *
     * @param switchNode 开关选项
     * @param isStatus 开关期望状态
     */
    fun doSwitchACOption(switchNode: SwitchNode, isStatus: Boolean): Boolean {
        val status = if (isStatus) {
            DownStatus.ENABLED
        } else {
            DownStatus.DISABLED
        }
        return when (switchNode) {
            SwitchNode.AC_AUTO_ARID -> {
                val signal = CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT
                doSetProperty(signal, status.value, SignalOrigin.HVAC_SIGNAL)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                val signal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST
                doSetProperty(signal, status.value, SignalOrigin.HVAC_SIGNAL)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                val signal = CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE
                doSetProperty(signal, status.value, SignalOrigin.HVAC_SIGNAL)
            }
        }
    }

    override fun onPropertyChanged(type: SignalOrigin, property: CarPropertyValue<*>) {
        when (type) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
        }
    }

    private fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            hvacDemistSignal -> {
                onAutoDemistStatusChanged(property.value)
            }
            else -> {}
        }
    }

    private fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            cabinAridSignal -> {
                onAutoAridStatusChanged(property.value)
            }
            //预通风功能
            cabinWindSignal -> {
                onAdvanceHairStatusChanged(property.value)
            }
            //自动空调舒适性
            CarCabinManager.ID_ACCMFTSTSDISP -> {
                onComfortOptionChanged(property.value)
            }
            else -> {}
        }
    }

    private fun onComfortOptionChanged(value: Any?) {
        if (value is Int) {
            when (value) {
                0x01 -> {}
                0x02 -> {}
                0x03 -> {}
                else -> {}
            }
        }
    }


    private fun onAutoAridStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAutoAridStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (aridStatus.get() xor status) {
                aridStatus.set(status)
                notifySwitchStatus(aridStatus.get(), SwitchNode.AC_AUTO_ARID)
            }
        }
    }

    private fun onAutoDemistStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAutoDemistStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (demistStatus.get() xor status) {
                demistStatus.set(status)
                notifySwitchStatus(demistStatus.get(), SwitchNode.AC_AUTO_DEMIST)
            }
        }
    }

    private fun onAdvanceHairStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAdvanceHairStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (windStatus.get() xor status) {
                windStatus.set(status)
                notifySwitchStatus(windStatus.get(), SwitchNode.AC_ADVANCE_WIND)
            }
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