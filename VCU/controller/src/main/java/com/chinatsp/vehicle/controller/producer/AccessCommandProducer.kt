package com.chinatsp.vehicle.controller.producer

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

    val gradeMap: HashMap<String, Int>
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


    fun attemptAccessCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptLouverCommand(slots)
            LogManager.e("attemptAccessCommand", "attemptLouverCommand : $command")
        }
        if (null == command) {
            command = attemptWindowCommand(slots)
            LogManager.e("attemptAccessCommand", "attemptWindowCommand : $command")
        }
        if (null == command) {
            command = attemptDoorCommand(slots)
            LogManager.e("attemptAccessCommand", "attemptDoorCommand : $command")
        }
        return command
    }

    private fun attemptDoorCommand(slots: Slots): CarCmd? {
        var action = Action.VOID
        var part = IPart.VOID
        if (isMatch(Keywords.HOODS, slots.name)) {//引擎盖
            part = part or IPart.HEAD
            if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                action = Action.OPEN
            } else if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                action = Action.CLOSE
            }
        }
        if (isMatch(Keywords.TRUNKS, slots.name)) { //后备箱
            part = part or IPart.TAIL
            if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
                action = Action.OPEN
            } else if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
                action = Action.CLOSE
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
        var part = IPart.VOID
        if (isMatch(Keywords.L_F_WINDOW, slots.name)) {
            part = IPart.LEFT_FRONT
        }
        if (isMatch(Keywords.R_F_WINDOW, slots.name)) {
            part = IPart.RIGHT_FRONT
        }
        if (isMatch(Keywords.L_R_WINDOW, slots.name)) {
            part = IPart.LEFT_BACK
        }
        if (isMatch(Keywords.R_R_WINDOW, slots.name)) {
            part = IPart.RIGHT_BACK
        }

        if (isMatch(Keywords.L_WINDOW, slots.name)) {
            part = IPart.LEFT_FRONT or IPart.LEFT_BACK
        }
        if (isMatch(Keywords.R_WINDOW, slots.name)) {
            part = IPart.RIGHT_FRONT or IPart.RIGHT_BACK
        }

        if (isMatch(Keywords.WINDOW_ALL, slots.name)) {
            part = IPart.LEFT_FRONT or IPart.LEFT_BACK or IPart.RIGHT_FRONT or IPart.RIGHT_BACK
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
            command.car = ICar.WINDOWS
            command.part = part
            return command
        }
        return null
    }

    private fun attemptLouverCommand(slots: Slots): CarCmd? {
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
            if ("MORE" == nameValue) {
                action = Action.PLUS
            } else if ("LITTLE" == nameValue) {
                action = Action.MINUS
            }
            else {
                val pair = obtainDegree(nameValue)
                action = pair.first
                value = pair.second
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
            command.car = ICar.LOUVER
            command.part = part
            return command
        }
        return null
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
