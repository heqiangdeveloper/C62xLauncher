package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.CarSign
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

class SeatManager private constructor(): BaseManager(), IConcernChanged {

    private val autoAridProperty = CarCabinManager.ID_ACSELFSTSDISP

    private val autoWindAdvanceProperty = CarCabinManager.ID_ACPREVENTNDISP

    private val autoComfortProperty = CarCabinManager.ID_ACCMFTSTSDISP

    private val autoDemistProperty = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST

    private val selfSerial by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }


    val aridStatus: AtomicBoolean by lazy {
        AtomicBoolean(false)
    }

    val demistStatus: AtomicBoolean by lazy {
        AtomicBoolean(false)
    }

    val windStatus: AtomicBoolean by lazy {
        AtomicBoolean(false)
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }



    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {

        return false
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
            else -> null
        }
    }

    private fun issueCabinIntProperty(id: Int, value: Int, area: Area = Area.GLOBAL): Boolean {
        val settingManager = SettingManager.getInstance()
        return settingManager.doSetCabinProperty(id, value, area)
    }

    private fun issueHvacIntProperty(id: Int, value: Int, area: Area = Area.GLOBAL): Boolean {
        val settingManager = SettingManager.getInstance()
        return settingManager.doSetHvacProperty(id, value, area)
    }

    /**
     * 更新空调舒适性
     * @param value (空调舒适性状态显示
     * 0x0: Reserved
     * 0x1: Gentle
     * 0x2: Standard
     * 0x3: Powerful
     * 0x4~0x6: Reserved
     * 0x7: Invalid)
     */
    fun doUpdateACComfort(value: Int): Boolean {
        val invalidValue = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!invalidValue) {
            return false
        }
        return issueCabinIntProperty(CarCabinManager.ID_ACCMFTSTSDISP, value)
    }

    /**
     *
     * @param switchNode 开关选项
     * @param isStatus 开关期望状态
     */
    fun doSwitchACOption(switchNode: SwitchNode, isStatus: Boolean): Boolean {
        val status = if (isStatus) {
            Status1.ON
        } else {
            Status1.OFF
        }
        return when (switchNode) {
            SwitchNode.AC_AUTO_ARID -> {
                issueCabinIntProperty(autoAridProperty, status.value)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                issueHvacIntProperty(autoDemistProperty, status.value)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                issueCabinIntProperty(autoWindAdvanceProperty, status.value)
            }
        }
    }

    override fun onPropertyChanged(type: SignalOrigin, property: CarPropertyValue<*>) {

    }

    fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            autoDemistProperty -> {
                onAutoDemistStatusChanged(property.value)
            }
            else -> {}
        }
    }

    fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            autoAridProperty -> {
                onAutoAridStatusChanged(property.value)
            }
            //预通风功能
            autoWindAdvanceProperty -> {
                onAdvanceHairStatusChanged(property.value)
            }
            //自动空调舒适性
            CarCabinManager.ID_ACCMFTSTSDISP -> {

            }
            else -> {}
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
//                        listener.onACSwitchStatusChanged(status, type)
                    }
                }
        }
    }

    companion object: ISignal{

        override val TAG: String = SeatManager::class.java.simpleName

        val instance: SeatManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SeatManager()
        }

        override val mcuConcernedSerial: Set<Int>by lazy {
            val hashSet = HashSet<Int>()
            hashSet.apply {
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

}