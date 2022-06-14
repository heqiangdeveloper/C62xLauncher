package com.chinatsp.settinglib.manager.cabin

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class WheelManager private constructor() : BaseManager(), IOptionManager {

    private val wheelHeat: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
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
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_DRIVER
                )
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_PASSENGER
                )
            }
            SwitchNode.SEAT_HEAT_F_L -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_ROW_1_LEFT
                )
            }
            SwitchNode.SEAT_HEAT_F_R -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_ROW_1_RIGHT
                )
            }
            SwitchNode.SEAT_HEAT_T_L -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_ROW_2_LEFT
                )
            }
            SwitchNode.SEAT_HEAT_T_R -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_ROW_2_RIGHT
                )
            }
            else -> false
        }
    }


    companion object : ISignal {

        override val TAG: String = WheelManager::class.java.simpleName

        val instance: WheelManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WheelManager()
        }

    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return -1
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                wheelHeat.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

}