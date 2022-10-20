package com.chinatsp.vehicle.controller.producer

import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import java.util.regex.Pattern

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class AccessCommandProducer: ICommandProducer {


    fun attemptAccessCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptSkylightCommand(slots)
        }
        if (null == command) {
            command = attemptWindowCommand(slots)
        }
        if (null == command) {
            command = attemptDoorCommand(slots)
        }
        return command
    }

    private fun attemptDoorCommand(slots: Slots): CarCmd? {
        if (isMatch(Keywords.HOODS, slots.name)) {//引擎盖

        } else if (isMatch(Keywords.TRUNKS, slots.name)) { //后备箱

        }
        return null
    }

    private fun attemptWindowCommand(slots: Slots): CarCmd? {
        val isDriver = isMatch(Keywords.DRIVER_WINDOW, slots.name)
        if (isDriver) {

        }
        val isPassenger = isMatch(Keywords.PASSENGER_WINDOW, slots.name)
        if (isPassenger) {

        }
        return null
    }

    private fun attemptSkylightCommand(slots: Slots): CarCmd? {
        if (isMatch(Keywords.SKYLIGHTS, slots.name)) {
            val nameValue = slots.nameValue?.toString() ?: ""
            var action = Action.VOID
            var value = -1
            if (isLikeJson(nameValue)) {

            } else {
                if ("MORE" == nameValue) {
                    action = Action.PLUS
                }
                if (Keywords.NAME_VALUE_HALF == nameValue) {
                    action = Action.FIXED
                    value = 50
                }
                if (Keywords.NAME_VALUE_QUARTER == nameValue) {
                    action = Action.FIXED
                    value = 25
                }
                if (Keywords.NAME_VALUE_ONE_THIRD == nameValue) {
                    action = Action.FIXED
                    value = 33
                }
                val pattern = Pattern.compile("^(\\d|[1-9]\\d|100)(%)\$")
                val matcher = pattern.matcher(nameValue)
                if (matcher.matches()) {
                    action = Action.FIXED
                    value = matcher.group(1)?.toInt() ?: value
                    LogManager.d("", "parse value:$value")
                }
            }
            if (Action.VOID == action) {
                if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                    action = Action.OPEN
                } else if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                    action = Action.CLOSE
                }
            }
            if (Action.VOID != action) {
                val command = CarCmd(action = action, model = Model.ACCESS_WINDOW)
                command.slots = slots
                command.value = value
                return command
            }
        }
        return null
    }


}