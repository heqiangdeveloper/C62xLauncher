package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class SeatCommandProducer : ICommandProducer {

    private val gradeMap: HashMap<String, Int>
        get() {
            val map = HashMap<String, Int>()
            map["一"] = 1
            map["二"] = 2
            map["三"] = 3
            map["四"] = 4
            map["五"] = 5
            map["六"] = 6
            map["七"] = 7
            map["八"] = 8
            map["九"] = 9
            map["十"] = 10
            return map
        }


    fun attemptChairCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptHeatCommand(slots)
            LogManager.e("attemptChairCommand", "attemptHeatCommand : $command")
        }
        if (null == command) {
            command = attemptVentilateCommand(slots)
            LogManager.e("attemptChairCommand", "attemptVentilateCommand : $command")
        }
        if (null == command) {
            command = attemptKneadCommand(slots)
            LogManager.e("attemptChairCommand", "attemptDoorCommand : $command")
        }
        return command
    }

    private fun attemptKneadCommand(slots: Slots): CarCmd? {
        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (!slots.mode.contains(Keywords.KNEAD)) {
            return null
        }

        var action = Action.VOID
        var value = -1
        val pair = attemptCreateHeatPair(slots)
        if (null != pair) {
            action = pair.first
            value = pair.second
        }
        val car = ICar.CHAIR
        val act = IAct.KNEAD
        if (Action.VOID == action) {
            if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                action = Action.TURN_ON
            }
            if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                action = Action.TURN_OFF
            }
        }
        if (Action.VOID != action) {
            val command = CarCmd(action = action, model = Model.CABIN_SEAT)
            command.slots = slots
            command.value = value
            command.car = car
            command.act = act
            command.part = checkoutPart(slots)
            return command
        }
        return null
    }

    /**
     * 座椅通风指令
     */
    private fun attemptVentilateCommand(slots: Slots): CarCmd? {
        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (slots.mode.contains(Keywords.VENTILATE)) {
            var action = Action.VOID
            var value = -1
            val pair = attemptCreateHeatPair(slots)
            if (null != pair) {
                action = pair.first
                value = pair.second
            }
            val car = ICar.CHAIR
            val act = IAct.COLD
            if (Action.VOID == action) {
                if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                    action = Action.TURN_ON
                }
                if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                    action = Action.TURN_OFF
                }
            }
            if (Action.VOID != action) {
                val command = CarCmd(action = action, model = Model.CABIN_SEAT)
                command.slots = slots
                command.value = value
                command.car = car
                command.act = act
                command.part = checkoutPart(slots)
                return command
            }
        }
        return null
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

        if (!slots.name.contains(Keywords.CHAIR)) {
            return null
        }
        if (slots.mode.contains(Keywords.HEAT)) {
            var action = Action.VOID
            var value = -1
            val pair = attemptCreateHeatPair(slots)
            if (null != pair) {
                action = pair.first
                value = pair.second
            }
            LogManager.e("attemptHeatCommand------------action:$action")
            val car = ICar.CHAIR
            val act = IAct.HEAT
            if (Action.VOID == action) {
                if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                    action = Action.TURN_ON
                }
                if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                    action = Action.TURN_OFF
                }
            }
            if (Action.VOID != action) {
                val command = CarCmd(action = action, model = Model.CABIN_SEAT)
                command.slots = slots
                command.value = value
                command.car = car
                command.act = act
                command.part = checkoutPart(slots)
                return command
            }
        }
        return null
    }

    private fun attemptCreateHeatPair(slots: Slots): Pair<Int, Int>? {
        val value = slots.nameValue?.toString() ?: ""
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

    private fun obtainDegree(nameValue: String): Pair<Int, Int> {
        var action = Action.VOID
        var value = -1
        do {
            val pattern = Pattern.compile("^(\\d|[1-9]\\d|100)(%)\$")
            val matcher = pattern.matcher(nameValue)
            if (matcher.matches()) {
                action = Action.FIXED
                value = matcher.group(1)?.toInt() ?: value
                break
            }
            val pattern2 = Pattern.compile("^([一|二|三|四|五|六|七|八|九|十])+分之+(([一|二|三|四|五|六|七|八|九|十]))")
            val matcher2 = pattern2.matcher(nameValue)
            if (matcher2.matches()) {
                action = Action.FIXED
                val dKey = matcher2.group(1)
                val mKey = matcher2.group(2)
                val molecule = gradeMap[mKey]!!
                val denominator = gradeMap[dKey]!!
                LogManager.d("", "parse molecule:$molecule, denominator:$denominator")
                value = (molecule * 100) / denominator
                break
            }
            val pattern3 = Pattern.compile("^(\\d|[1-9]\\d|100)\\/((\\d|[1-9]\\d|100))\$")
            val matcher3 = pattern3.matcher(nameValue)
            if (matcher3.matches()) {
                action = Action.FIXED
                val molecule = matcher3.group(1)?.toInt() ?: value
                val denominator = matcher3.group(2)?.toInt() ?: value
                LogManager.d("", "parse molecule:$molecule, denominator:$denominator")
                value = (molecule * 100) / denominator
            }
        } while (false)
        return Pair(action, value)
    }
}
