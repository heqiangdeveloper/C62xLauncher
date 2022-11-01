package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
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
class AccessCommandProducer : ICommandProducer {

//    private val gradeMap: HashMap<String, Int>
//        get() {
//            val map = HashMap<String, Int>()
//            map["一"] = 1
//            map["二"] = 2
//            map["三"] = 3
//            map["四"] = 4
//            map["五"] = 5
//            map["六"] = 6
//            map["七"] = 7
//            map["八"] = 8
//            map["九"] = 9
//            map["十"] = 10
//            return map
//        }


    fun attemptAccessCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptLouverCommand(slots)
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
        if (TextUtils.isEmpty(slots.name)) {
            return null
        }
        var action = Action.VOID
        var part = IPart.VOID
        if (isMatch(Keywords.HOODS, slots.name)) {//引擎盖
            part = part or IPart.HEAD
            action = obtainSwitchAction(slots.operation)
        }
        if (isMatch(Keywords.TRUNKS, slots.name)) { //后备箱
            part = part or IPart.TAIL
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID != action) {
            val command = CarCmd(action = action, model = Model.ACCESS_STERN)
            command.slots = slots
            command.car = ICar.DOORS
            command.part = part
            return command
        }
        return null
    }

    private fun attemptWindowCommand(slots: Slots): CarCmd? {
        if (TextUtils.isEmpty(slots.name)) {
            return null
        }
        if (!isContains(slots.name, Keywords.WINDOWS)) {
            return null
        }
        var part = IPart.VOID
        if (isMatch(Keywords.WINDOW_ALL, slots.name)) {
            part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        }
        if (IPart.VOID == part) {
            part = checkoutPart(slots.name)
        }
        if (IPart.VOID == part) {
            if (isMatch(Keywords.L_WINDOW, slots.name)) {
                part = IPart.LEFT_FRONT or IPart.LEFT_BACK
            } else if (isMatch(Keywords.R_WINDOW, slots.name)) {
                part = IPart.RIGHT_FRONT or IPart.RIGHT_BACK
            } else if (isMatch(Keywords.F_WINDOW, slots.name)) {
                part = IPart.LEFT_FRONT or IPart.RIGHT_FRONT
            } else if (isMatch(Keywords.B_WINDOW, slots.name)) {
                part = IPart.LEFT_BACK or IPart.RIGHT_BACK
            }
        }
        if (Action.VOID == part) {
            return null
        }
        val nameValue = slots.nameValue?.toString() ?: ""
        var action = Action.VOID
        var value = -1
        if (isLikeJson(nameValue)) {

        } else {
            if ("MORE" == nameValue) {
                action = Action.PLUS
            } else if ("LITTLE" == nameValue) {
                action = Action.MINUS
            } else {
                val pair = obtainDegree(nameValue)
                action = pair.first
                value = pair.second
            }
        }
        if (Action.VOID == action) {
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID != action) {
            val command = CarCmd(action = action, model = Model.ACCESS_WINDOW)
            command.slots = slots
            command.value = value
            command.car = ICar.WINDOWS
            command.part = part
            return command
        }
        return null
    }

    private fun attemptLouverCommand(slots: Slots): CarCmd? {
        if (TextUtils.isEmpty(slots.name)) {
            return null
        }
        var part = IPart.VOID
        if (isMatch(Keywords.SKYLIGHTS, slots.name)) {
            part = IPart.SKYLIGHT
        }
        if (Keywords.ABAT_VENT == slots.name) {
            part = IPart.LOVE_LUCY
        }
        if (Action.VOID == part) {
            return null
        }
        val nameValue = slots.nameValue?.toString() ?: ""
        var action = Action.VOID
        var value = -1
        if (isLikeJson(nameValue)) {

        } else {
            when (nameValue) {
                "MORE" -> action = Action.PLUS
                "LITTLE" -> action = Action.MINUS
                else -> {
                    val pair = obtainDegree(nameValue)
                    action = pair.first
                    value = pair.second
                }
            }
        }
        if (Action.VOID == action) {
            action = obtainSwitchAction(slots.operation)
        }
        if (Action.VOID != action) {
            val command = CarCmd(action = action, model = Model.ACCESS_WINDOW)
            command.slots = slots
            command.value = value
            command.car = ICar.LOUVER
            command.part = part
            return command
        }
        return null
    }

    private fun obtainSwitchAction(operation: String): Int {
        if (isMatch(Keywords.OPT_OPENS, operation)) {
            return Action.OPEN
        }
        if (isMatch(Keywords.OPT_CLOSES, operation)) {
            return Action.CLOSE
        }
        return Action.VOID
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
                val molecule = convertToNumber(mKey)
                val denominator = convertToNumber(dKey)
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


    private fun checkoutPart(name: String): Int {
        var part = IPart.VOID
        if (isContains(name, Keywords.L_F)) {
            part = part or IPart.LEFT_FRONT
        } else if (isContains(name, Keywords.L_R)) {
            part = part or IPart.LEFT_BACK
        } else if (isContains(name, Keywords.R_F)) {
            part = part or IPart.RIGHT_FRONT
        } else if (isContains(name, Keywords.R_R)) {
            part = part or IPart.RIGHT_BACK
        } else if (isContains(name, Keywords.L_C)) {
            part = part or IPart.LEFT_FRONT or IPart.LEFT_BACK
        } else if (isContains(name, Keywords.R_C)) {
            part = part or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        } else if (isContains(name, Keywords.F_R)) {
            part = part or IPart.LEFT_FRONT or IPart.RIGHT_FRONT
        } else if (isContains(name, Keywords.B_R)) {
            part = part or IPart.LEFT_BACK or IPart.RIGHT_BACK
        }
//        else {
//            part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
//        }
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

    private fun convertToNumber(value: String): Int {
        return when (value) {
            "一" -> 1
            "二" -> 2
            "三" -> 3
            "四" -> 4
            "五" -> 5
            "六" -> 6
            "七" -> 7
            "八" -> 8
            "九" -> 9
            "十" -> 10
            else -> -1
        }
    }
}
