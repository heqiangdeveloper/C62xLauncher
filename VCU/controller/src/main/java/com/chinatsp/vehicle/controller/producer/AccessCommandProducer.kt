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
import java.util.*
import java.util.regex.Pattern

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class AccessCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
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
        if (IPart.VOID == part) {
            LogManager.e("attemptDoorCommand================text:${slots.text}")
            if (isContains(slots.text, Keywords.HOODS)) {
                part = part or IPart.HEAD
                action = obtainSwitchAction(slots.operation)
            } else if (isContains(slots.text, Keywords.TRUNKS)) {
                part = part or IPart.TAIL
                action = obtainSwitchAction(slots.operation)
            }
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
            part = IPart.L_F or IPart.L_B or IPart.R_F or IPart.R_B
        }
        if (IPart.VOID == part) {
            part = checkoutPart(slots.name, flag = false)
        }
        if (IPart.VOID == part) {
            part = checkoutPart(slots.name, flag = true)
        }
        if (IPart.VOID == part) {
            return null
        }
        var value = -1
        var action = Action.VOID
        val nameValue = slots.nameValue?.toString() ?: ""
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
            part = IPart.TOP
        }
        if (Keywords.ABAT_VENT == slots.name) {
            part = IPart.BOTTOM
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


    private fun checkoutPart(name: String, flag: Boolean = false): Int {
        var part = IPart.VOID
        if (flag) {
            if (isMatch(Keywords.L_WINDOW, name)) {
                part = IPart.L_F or IPart.L_B
            } else if (isMatch(Keywords.R_WINDOW, name)) {
                part = IPart.R_F or IPart.R_B
            } else if (isMatch(Keywords.F_WINDOW, name)) {
                part = IPart.L_F or IPart.R_F
            } else if (isMatch(Keywords.B_WINDOW, name)) {
                part = IPart.L_B or IPart.R_B
            }
        } else {
            if (isContains(name, Keywords.L_F)) {
                part = part or IPart.L_F
            } else if (isContains(name, Keywords.L_R)) {
                part = part or IPart.L_B
            } else if (isContains(name, Keywords.R_F)) {
                part = part or IPart.R_F
            } else if (isContains(name, Keywords.R_R)) {
                part = part or IPart.R_B
            } else if (isContains(name, Keywords.L_C)) {
                part = part or IPart.L_F or IPart.L_B
            } else if (isContains(name, Keywords.R_C)) {
                part = part or IPart.R_F or IPart.R_B
            } else if (isContains(name, Keywords.F_R)) {
                part = part or IPart.L_F or IPart.R_F
            } else if (isContains(name, Keywords.B_R)) {
                part = part or IPart.L_B or IPart.R_B
            }
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
