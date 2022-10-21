package com.chinatsp.vehicle.controller.producer

import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords

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
        val isFront = isMatch(Keywords.WIPERS, slots.name)
        if (isFront) {//前雨刮

            return null
        }
        val isRear = isMatch(Keywords.REAR_WIPERS, slots.name)
        if (isRear) {//后雨刮

            return null
        }
        return null
    }

    private fun attemptWheelCommand(slots: Slots): CarCmd? {
        if (isMatch(Keywords.WHEELS, slots.name)) {
            var action = Action.VOID
            if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                action = Action.TURN_ON
            } else if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                action = Action.TURN_OFF
            }
            if ((Action.VOID != action) && ("方向盘加热" == slots.mode)) {
                val command = CarCmd(action = action, model = Model.CABIN_WHEEL)
                command.slots = slots
                command.car = ICar.WHEEL_HOT
                return command
            }
        }
        return null
    }


}