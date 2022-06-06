package com.chinatsp.settinglib.manager.cabin

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.hardware.automotive.vehicle.V2_0.VehicleArea
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
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

class SeatManager private constructor(): BaseManager() {

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

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }


    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {

        return false
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    /**
     *
     * @param switchNode 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_DRIVER)
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_PASSENGER)
            }
            SwitchNode.SEAT_HEAT_F_L -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_ROW_1_LEFT)
            }
            SwitchNode.SEAT_HEAT_F_R -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_ROW_1_RIGHT)
            }
            SwitchNode.SEAT_HEAT_T_L -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_ROW_2_LEFT)
            }
            SwitchNode.SEAT_HEAT_T_R -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin, VehicleAreaSeat.SEAT_ROW_2_RIGHT)
            }
            else -> false
        }
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

    }

}