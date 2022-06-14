package com.chinatsp.settinglib.manager.cabin

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.cabin.IACListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
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

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: Origin
    ): Boolean {

        return false
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    /**
     *
     * @param node 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_DRIVER)
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_PASSENGER)
            }
            SwitchNode.SEAT_HEAT_F_L -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_ROW_1_LEFT)
            }
            SwitchNode.SEAT_HEAT_F_R -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_ROW_1_RIGHT)
            }
            SwitchNode.SEAT_HEAT_T_L -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_ROW_2_LEFT)
            }
            SwitchNode.SEAT_HEAT_T_R -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, VehicleAreaSeat.SEAT_ROW_2_RIGHT)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {

    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {

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