package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.HEAT
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.WHEELS

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   : 方向盘、雨刮等相关命令处理
 * @version: 1.0
 */
class OtherCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptWheelCommand(slots)
        }
        if (null == command) {
            command = attemptWiperCommand(slots)
        }
        return command
    }

    fun attemptVehicleInfoCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (TextUtils.isEmpty(slots.name)) {
            return null
        }
        if (Keywords.QUERY != slots.operation) {
            return null
        }
        if (null == command) {
            command = attemptEnduranceCommand(slots)
        }
        if (null == command) {
            command = attemptMaintainCommand(slots)
        }
        if (null == command) {
            command = attemptFuelConsumptionCommand(slots)
        }
        if (null == command) {
            command = attemptTirePressureCommand(slots)
        }
        if (null == command) {
            command = attemptRemainingCommand(slots)
        }
        return command
    }

    /**
     * 剩余油量
     */
    private fun attemptRemainingCommand(slots: Slots): CarCmd? {
        if (Keywords.REMAINING == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            command.act = IAct.REMAINING
            return command
        }
        return null
    }

    /**
     * 胎压
     */
    private fun attemptTirePressureCommand(slots: Slots): CarCmd? {
        if (Keywords.TIRE_PRESSURE == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            command.act = IAct.TIRE_PRESSURE
            return command
        }
        return null
    }

    /**
     * 油耗
     */
    private fun attemptFuelConsumptionCommand(slots: Slots): CarCmd? {
        if (Keywords.AVERAGE_FUEL_CONSUMPTION == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            command.act = IAct.AVERAGE_FUEL_CONSUMPTION
            return command
        }
        if (Keywords.FUEL_CONSUMPTION == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            command.act = IAct.INSTANTANEOUS_FUEL_CONSUMPTION
            return command
        }
        return null
    }

    /**
     * 保养里程
     */
    private fun attemptMaintainCommand(slots: Slots): CarCmd? {
        if (Keywords.MAINTAIN_MILEAGE == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            command.act = IAct.MAINTAIN_MILEAGE
            return command
        }
        return null
    }

    /**
     * 续航里程
     */
    private fun attemptEnduranceCommand(slots: Slots): CarCmd? {
        if (Keywords.ENDURANCE_MILEAGE == slots.name) {
            val command = CarCmd(action = Action.QUERY_INFO, model = Model.GLOBAL)
            command.car = ICar.WIPER
            command.slots = slots
            if (Keywords.KM == slots.text.substring(slots.text.length - 2)) {//公里
                command.act = IAct.ENDURANCE_MILEAGE_KM
            } else {
                command.act = IAct.ENDURANCE_MILEAGE
            }
            return command
        }
        return null
    }

    private fun attemptWiperCommand(slots: Slots): CarCmd? {
        var car = ICar.VOID
        var part = IPart.VOID
        var action = Action.VOID
        if (isContains(slots.name, Keywords.WIPERS)) {
            car = ICar.WIPER
            part = analysisPart(slots)
            action = obtainSwitchAction(slots.operation)

        } else if (isContains(slots.name, Keywords.WASHING)) {
            car = ICar.WASHING
            part = analysisPart(slots)
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.ACCESS_WINDOW)
        command.car = car
        command.part = part
        command.slots = slots
        return command
    }

    private fun attemptWheelCommand(slots: Slots): CarCmd? {
        if (isMatch(WHEELS, slots.name)) {
            val action = obtainSwitchAction(slots.operation)
            if ((Action.VOID != action) && slots.mode.contains(HEAT)) {
                val command = CarCmd(action = action, model = Model.CABIN_WHEEL)
                command.slots = slots
                command.car = ICar.WHEEL
                return command
            }
        }
        return null
    }

    private fun obtainSwitchAction(value: String): Int {
        if (isMatch(Keywords.OPT_OPENS, value)) {
            return Action.TURN_ON
        }
        if (isMatch(Keywords.OPT_CLOSES, value)) {
            return Action.TURN_OFF
        }
        return Action.VOID
    }

    private fun analysisPart(slots: Slots): Int {
        var part = IPart.VOID
        if (slots.name.contains("前")) {
            part = part or IPart.HEAD
        }
        if (slots.name.contains("后")) {
            part = part or IPart.TAIL
        }
        if (IPart.VOID == part) {
            part = IPart.HEAD or IPart.TAIL
        }
        return part
    }

    private fun isContains(value: String, array: Array<String>): Boolean {
        for (item in array) {
            if (value.contains(item)) {
                return true
            }
        }
        return false
    }


}