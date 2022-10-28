package com.chinatsp.vehicle.controller.producer

import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.HEAT
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.WHEELS
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.WIPERS

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   : 方向盘、雨刮等相关命令处理
 * @version: 1.0
 */
class OtherCommandProducer: ICommandProducer {

    fun attemptCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptWheelCommand(slots)
        }
        if (null == command) {
            command = attemptWiperCommand(slots)
        }
        return command
    }


    private fun attemptWiperCommand(slots: Slots): CarCmd? {
        if (isContains(slots.name, WIPERS)) {
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
            val action = obtainSwitchAction(slots.operation)
            if (Action.VOID != action) {
                val command = CarCmd(action = action, model = Model.ACCESS_WINDOW)
                command.part = part
                command.car = ICar.WIPER
                command.slots = slots
                return command
            }
        }
        return null
    }

    private fun attemptWheelCommand(slots: Slots): CarCmd? {
        if (isMatch(WHEELS, slots.name)) {
            val action = obtainSwitchAction(slots.operation)
            if ((Action.VOID != action) && slots.mode.contains(HEAT)) {
                val command = CarCmd(action = action, model = Model.CABIN_WHEEL)
                command.slots = slots
                command.car = ICar.WHEEL_HOT
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

    private fun isContains(value: String, array: Array<String>): Boolean {
        for (item in array) {
            if (value.contains(item)) {
                return true
            }
        }
        return false
    }


}