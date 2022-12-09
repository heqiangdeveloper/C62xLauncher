package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.CarController
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import com.chinatsp.vehicle.controller.utils.Keywords.MAX
import com.chinatsp.vehicle.controller.utils.Keywords.MIN
import com.chinatsp.vehicle.controller.utils.Keywords.MINUS
import com.chinatsp.vehicle.controller.utils.Keywords.MINUS_LITTLE
import com.chinatsp.vehicle.controller.utils.Keywords.MINUS_MORE
import com.chinatsp.vehicle.controller.utils.Keywords.PLUS
import com.chinatsp.vehicle.controller.utils.Keywords.PLUS_LITTLE
import com.chinatsp.vehicle.controller.utils.Keywords.PLUS_MORE
import org.json.JSONObject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class AmbientCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (!TextUtils.isEmpty(slots.name) && slots.name.contains(Keywords.LAMP)) {
            if (null == command) {
                command = attemptCreateAmbientRhythmCommand(slots)
            }
            if (null == command) {
                command = attemptCreateAmbientBrightnessCommand(slots)
            }
            if (null == command) {
                command = attemptCreateAmbientColorCommand(slots)
            }
            if (null == command) {
                command = attemptCreateAmbientSwitchCommand(slots)
            }
        }
        return command
    }

    private fun attemptCreateAmbientRhythmCommand(slots: Slots): CarCmd? {
        if (TextUtils.isEmpty(slots.mode)) {
            return null
        }
        val rhythm = when (slots.mode) {
            "音乐律动" -> 1
            "车速律动" -> 2
            "色彩呼吸" -> 3
            else -> -1
        }
        if (-1 == rhythm) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
        command.value = rhythm
        command.slots = slots
        command.car = ICar.RHYTHM_MODE
        return command
    }

    private fun attemptCreateAmbientColorCommand(slots: Slots): CarCmd? {
//        val colors = arrayOf("红色", "紫色", "冰蓝色", "橙色", "绿色", "玫红色", "果绿色", "黄色", "蓝色", "白色")
//        if (colors.contains(slots.color)) {
        if (!TextUtils.isEmpty(slots.color)) {
            val command = CarCmd(action = Action.FIXED, model = Model.LIGHT_AMBIENT)
            command.car = ICar.COLOR
            command.color = slots.color
            command.slots = slots
            return command
        }
        return null
    }

    private fun attemptCreateAmbientBrightnessCommand(slots: Slots): CarCmd? {
        val value = slots.nameValue?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        val action: Int
        var command: CarCmd? = null
        if (!CarController.isLikeJson(value)) {
            val pair = obtainActionPair(value)
            action = pair.first
            if (Action.VOID != action) {
                command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                command.step = pair.second
            }
        } else {
            val jsonObject = JSONObject(value)
            val consult = jsonObject.getString("ref")
            if (Keywords.REF_CUR == consult) {
                val offset = jsonObject.getInt("offset")
                val rule = jsonObject.getString("direct")
                action = when (rule) {
                    "+" -> Action.PLUS
                    "-" -> Action.MINUS
                    else -> Action.VOID
                }
                if (Action.VOID != action) {
                    command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                    command.step = offset
                }
            } else if (Keywords.REF_ZERO == consult) {
                val offset = jsonObject.getInt("offset")
                action = Action.FIXED
                command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                command.value = offset
            }
        }
        if (null != command) {
            command.slots = slots
            command.car = ICar.BRIGHTNESS
        }
        return command
    }

    private fun attemptCreateAmbientSwitchCommand(slots: Slots): CarCmd? {
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
        command.part = checkoutPart(slots)
        command.slots = slots
        command.car = ICar.AMBIENT
        return command
    }

    private fun checkoutPart(slots: Slots): Int {
        val part = if (Keywords.LAMP == slots.name) {
            IPart.HEAD or IPart.TAIL
        } else {
            if (isContains(slots.name, Keywords.F_R)) {
                IPart.HEAD
            } else if (isContains(slots.name, Keywords.B_R)) {
                IPart.TAIL
            } else {
                IPart.HEAD or IPart.TAIL
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

    private fun obtainActionPair(value: String): Pair<Int, Int> {
        var step = 1
        var action = Action.VOID
        do {
            if (MIN == value) {
                action = Action.MIN
                break
            }
            if (MAX == value) {
                action = Action.MAX
                break
            }
            if (PLUS_MORE == value) {
                action = Action.PLUS
                step = 2
                break
            }
            if (MINUS_MORE == value) {
                action = Action.MINUS
                step = 2
                break
            }
            if ((PLUS == value) || (PLUS_LITTLE == value)) {
                action = Action.PLUS
                break
            }
            if ((MINUS == value) || (MINUS_LITTLE == value)) {
                action = Action.MINUS
                break
            }
        } while (false)
        return Pair(action, step)
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

}