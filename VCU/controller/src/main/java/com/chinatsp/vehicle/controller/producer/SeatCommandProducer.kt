package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.LogManager
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

    fun attemptChairCommand(slots: Slots): CarCmd? {
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
        if (TextUtils.isEmpty(slots.name) || TextUtils.isEmpty(slots.mode)) {
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
        command.part = checkoutPart(slots)
        return command
    }

    /**
     * 座椅通风指令
     */
    private fun attemptVentilateCommand(slots: Slots): CarCmd? {
        if (TextUtils.isEmpty(slots.name) || TextUtils.isEmpty(slots.mode)) {
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
        command.act = IAct.COLD
        command.part = checkoutPart(slots)
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

    private fun checkoutPart(slots: Slots): Int {
        var part = IPart.VOID
        if (isContains(slots.name, Keywords.L_F)) {
            part = part or IPart.LEFT_FRONT
        } else if (isContains(slots.name, Keywords.L_R)) {
            part = part or IPart.LEFT_BACK
        } else if (isContains(slots.name, Keywords.R_F)) {
            part = part or IPart.RIGHT_FRONT
        } else if (isContains(slots.name, Keywords.R_R)) {
            part = part or IPart.RIGHT_BACK
        } else if (isContains(slots.name, Keywords.L_C)) {
            part = part or IPart.LEFT_FRONT or IPart.LEFT_BACK
        } else if (isContains(slots.name, Keywords.R_C)) {
            part = part or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        } else if (isContains(slots.name, Keywords.F_R)) {
            part = part or IPart.LEFT_FRONT or IPart.RIGHT_FRONT
        } else if (isContains(slots.name, Keywords.B_R)) {
            part = part or IPart.LEFT_BACK or IPart.RIGHT_BACK
        } else {
            part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
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

    /**
     * 座椅加热指令
     */
    private fun attemptHeatCommand(slots: Slots): CarCmd? {
        //{"slots":{"mode":"座椅加热","name":"座椅","temperature":"PLUS_LITTLE"}}
        //{"slots":{"name":"座椅","nameValue":{"direct":"+","offset":"20","ref":"ZERO","type":"SPOT"}}}
        //{"slots":{"name":"座椅","nameValue":{"direct":"+","offset":"10","ref":"ZERO","type":"SPOT"}}}
        //{"slots":{"mode":"座椅位置","modeValue":"UP_LITTLE","name":"主驾座椅"}}
//        var part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        if (TextUtils.isEmpty(slots.name) || TextUtils.isEmpty(slots.mode)) {
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
        command.act = IAct.HEAT
        command.part = checkoutPart(slots)
        return command
    }

    private fun attemptCreatePair(nameValue: String?): Pair<Int, Int>? {
        val value = nameValue ?: ""
        var pair: Pair<Int, Int>? = null
        if (TextUtils.isEmpty(value)) {
            return pair
        }
        var action = Action.VOID
        if (!isLikeJson(value)) {
            if ((Keywords.PLUS == value) || (Keywords.PLUS_MORE == value) || (Keywords.PLUS_LITTLE == value)) {
                action = Action.PLUS
            } else if ((Keywords.MINUS == value) || (Keywords.MINUS_MORE == value) || (Keywords.MINUS_LITTLE == value)) {
                action = Action.MINUS
            } else if (Keywords.MIN == value) {
                action = Action.MIN
            } else if (Keywords.MAX == value) {
                action = Action.MAX
            }
            pair = Pair(action, -1)
        } else {
            val json = JSONObject(value)
            val consult = json.getString("ref")
            val offset = json.getInt("offset")
            if (Keywords.REF_ZERO == consult) {
                action = Action.FIXED
            } else if (Keywords.REF_CUR == consult) {
                val rule = json.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
            }
            pair = Pair(action, offset)
        }
        return pair
    }
}
