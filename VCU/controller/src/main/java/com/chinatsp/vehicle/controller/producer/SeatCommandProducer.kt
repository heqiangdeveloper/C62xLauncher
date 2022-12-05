package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import org.json.JSONObject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class SeatCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptHeatCommand(slots)
        }
        if (null == command) {
            command = attemptVentilateCommand(slots)
        }
        if (null == command) {
            command = attemptKneadCommand(slots)
        }
        if (null == command) {
            command = attemptMoveCommand(slots)
        }
        if (null == command) {
            command = attemptBackrestCommand(slots)
        }
        return command
    }

    private fun attemptBackrestCommand(slots: Slots): CarCmd? {

        return null
    }

    private fun attemptMoveCommand(slots: Slots): CarCmd? {

        return null
    }

    private fun attemptKneadCommand(slots: Slots): CarCmd? {
        if (!isValidSlots(slots)) {
            return null
        }
        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (!slots.mode.contains(Keywords.KNEAD)) {
            return null
        }
        var value = -1
        var action = Action.VOID
        val pair = attemptCreatePair(slots.nameValue?.toString())
        if (null != pair) {
            action = pair.first
            value = pair.second
        }
        if (Action.VOID == action) {
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.CABIN_SEAT)
        command.slots = slots
        command.value = value
        command.car = ICar.CHAIR
        command.act = IAct.KNEAD
        command.step = if (-1 != value) value else 1
        command.part = checkoutPart(slots)
        command.soundDirection = obtainDirection(slots.user)
        return command
    }

    /**
     * 座椅通风指令
     */
    private fun attemptVentilateCommand(slots: Slots): CarCmd? {
        if (!isValidSlots(slots)) {
            return null
        }
        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (!slots.mode.contains(Keywords.VENTILATE)) {
            return null
        }
        var value = -1
        var action = Action.VOID
        var pair = attemptCreatePair(slots.nameValue?.toString())
        if (null == pair) {
            pair = attemptCreatePair(slots.fanSpeed?.toString())
        }
        if (null != pair) {
            action = pair.first
            value = pair.second
        }
        if (Action.VOID == action) {
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.CABIN_SEAT)
        command.slots = slots
        command.value = value
        command.act = IAct.COLD
        command.car = ICar.CHAIR
        command.step = if (-1 != value) value else 1
        command.part = checkoutPart(slots)
        command.soundDirection = obtainDirection(slots.user)
        return command
    }

    private fun obtainSwitchAction(operation: String): Int {
        if (isMatch(Keywords.OPT_OPENS, operation)) {
            return Action.TURN_ON
        }
        if (isMatch(Keywords.OPT_CLOSES, operation)) {
            return Action.TURN_OFF
        }
        return Action.VOID
    }

    /**
     * 座椅加热指令
     */
    private fun attemptHeatCommand(slots: Slots): CarCmd? {
        //{"slots":{"mode":"座椅加热","name":"座椅","temperature":"PLUS_LITTLE"}}
        //{"slots":{"name":"座椅","nameValue":{"direct":"+","offset":"20","ref":"ZERO","type":"SPOT"}}}
        //{"slots":{"name":"座椅","nameValue":{"direct":"+","offset":"10","ref":"ZERO","type":"SPOT"}}}
        //{"slots":{"mode":"座椅位置","modeValue":"UP_LITTLE","name":"主驾座椅"}}
//        var part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        if (!isValidSlots(slots)) {
            return null
        }
        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (!slots.mode.contains(Keywords.HEAT)) {
            return null
        }
        var value = -1
        var action = Action.VOID
        var pair = attemptCreatePair(slots.nameValue?.toString())
        if (null == pair) {
            pair = attemptCreatePair(slots.temperature?.toString())
        }
        if (null != pair) {
            action = pair.first
            value = pair.second
        }
        if (Action.VOID == action) {
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.CABIN_SEAT)
        command.slots = slots
        command.value = value
        command.act = IAct.HEAT
        command.car = ICar.CHAIR
        command.step = if (-1 != value) value else 1
        command.part = checkoutPart(slots)
        command.soundDirection = obtainDirection(slots.user)
        return command
    }

    private fun attemptCreatePair(nameValue: String?): Pair<Int, Int>? {
        val value = nameValue ?: ""
        var pair: Pair<Int, Int>? = null
        if (TextUtils.isEmpty(value)) {
            return pair
        }
        var offset = 1
        var action = Action.VOID
        if (isLikeJson(value)) {
            val jsonObject = JSONObject(value)
            offset = jsonObject.getInt("offset")
            val consult = jsonObject.getString("ref")
            if (Keywords.REF_ZERO == consult) {
                action = Action.FIXED
            } else if (Keywords.REF_CUR == consult) {
                val rule = jsonObject.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                } else if ("-" == rule) {
                    action = Action.MINUS
                }
            }
            pair = Pair(action, offset)
        } else {
            if (Keywords.PLUS == value) {
                action = Action.PLUS
            } else if (Keywords.PLUS_MORE == value) {
                action = Action.PLUS
                offset = 2
            } else if (Keywords.PLUS_LITTLE == value) {
                action = Action.PLUS
            } else if ((Keywords.MINUS == value)) {
                action = Action.MINUS
            } else if (Keywords.MINUS_MORE == value) {
                action = Action.MINUS
                offset = 2
            } else if (Keywords.MINUS_LITTLE == value) {
                action = Action.MINUS
            } else if (Keywords.MIN == value) {
                action = Action.MIN
            } else if (Keywords.MAX == value) {
                action = Action.MAX
            }
            pair = Pair(action, offset)
        }
        return pair
    }

    private fun isValidSlots(slots: Slots) =
        !TextUtils.isEmpty(slots.name) && !TextUtils.isEmpty(slots.mode)

    private fun isContains(value: String, array: Array<String>): Boolean {
        for (item in array) {
            if (value.contains(item)) {
                return true
            }
        }
        return false
    }

    private fun obtainDirection(value: String?): Int {
        if ("left" == value) {
            return IPart.L_F
        }
        if ("right" == value) {
            return IPart.R_F
        }
        return IPart.VOID
    }

    private fun checkoutPart(slots: Slots): Int {
        var part = IPart.VOID
        if (slots.name.contains(Keywords.ALL)) {
            part = IPart.L_F or IPart.L_B or IPart.R_F or IPart.R_B
        } else if (slots.name.contains(Keywords.PASSENGER)) {
            part = IPart.L_B or IPart.R_B
        } else if (isContains(slots.name, Keywords.L_F)) {
            part = part or IPart.L_F
        } else if (isContains(slots.name, Keywords.L_R)) {
            part = part or IPart.L_B
        } else if (isContains(slots.name, Keywords.R_F)) {
            part = part or IPart.R_F
        } else if (isContains(slots.name, Keywords.R_R)) {
            part = part or IPart.R_B
        } else if (isContains(slots.name, Keywords.L_C)) {
            part = part or IPart.L_F or IPart.L_B
        } else if (isContains(slots.name, Keywords.R_C)) {
            part = part or IPart.R_F or IPart.R_B
        } else if (isContains(slots.name, Keywords.F_R)) {
            part = part or IPart.L_F or IPart.R_F
        } else if (isContains(slots.name, Keywords.B_R)) {
            part = part or IPart.L_B or IPart.R_B
        } else {
            part = IPart.L_F or IPart.L_B or IPart.R_F or IPart.R_B or IPart.VAGUE
        }
        return part
    }

}
