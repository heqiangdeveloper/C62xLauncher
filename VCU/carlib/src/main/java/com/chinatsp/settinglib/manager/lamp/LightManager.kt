package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.listener.lamp.ILightListener
import com.chinatsp.settinglib.listener.lamp.ILightManager
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


class LightManager private constructor() : BaseManager(), IConcernChanged, ILightManager{


    private val identity by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    companion object : ISignal {

        override val TAG: String = LightManager::class.java.simpleName

        val instance: LightManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LightManager()
        }

    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
                /**空调自干燥*/
                add(CarCabinManager.ID_ACSELFSTSDISP)
                /**预通风功能*/
                add(CarCabinManager.ID_ACPREVENTNDISP)
                /**空调舒适性状态显示*/
                add(CarCabinManager.ID_ACCMFTSTSDISP)
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
            else -> {}
        }
        return true
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
        val isValid = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!isValid) {
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
    fun doSwitchOption(switchNode: SwitchNode, isStatus: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.AC_AUTO_ARID -> {
                val signal = CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT
                doSetProperty(signal, switchNode.obtainValue(isStatus), SignalOrigin.HVAC_SIGNAL)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                val signal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST
                doSetProperty(signal, switchNode.obtainValue(isStatus), SignalOrigin.HVAC_SIGNAL)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                val signal = CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE
                doSetProperty(signal, switchNode.obtainValue(isStatus), SignalOrigin.HVAC_SIGNAL)
            }
            else -> false
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
            else -> {}
        }
    }

    private fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }


}