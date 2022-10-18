package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.CarController
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.IOuterController
import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
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
class AmbientCommandProducer {

    fun attemptAmbientCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (CarController.isMatch(Keywords.AMBIENTS, slots.name)) {
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

    private fun doAmbientCommand(
        slots: Slots,
        controller: IOuterController,
        callback: ICmdCallback,
    ): Boolean {
        var command: CarCmd? = attemptCreateAmbientRhythmCommand(slots)
        if (null == command) {
            command = attemptCreateAmbientBrightnessCommand(slots)
        }
        if (null == command) {
            command = attemptCreateAmbientColorCommand(slots)
        }
        if (null == command) {
            command = attemptCreateAmbientSwitchCommand(slots)
        }
        if (null != command) {
            controller.doCarControlCommand(command, callback)
        }
        return null != command
    }

    private fun attemptCreateAmbientRhythmCommand(slots: Slots): CarCmd? {
        var rhythm = -1
        if ("音乐律动" == slots.mode) {
            rhythm = 1
        }
        if ("车速律动" == slots.mode) {
            rhythm = 2
        }
        if ("色彩呼吸" == slots.mode) {
            rhythm = 3
        }
        if (-1 == rhythm) {
            return null
        }
        var action = Action.VOID
        if (CarController.isMatch(Keywords.OPT_OPENS, slots.operation)) {
            action = Action.TURN_ON
        } else if (CarController.isMatch(Keywords.OPT_CLOSES, slots.operation)) {
            action = Action.TURN_OFF
        }
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
        val colors = arrayOf("红色", "紫色", "冰蓝色", "橙色", "绿色", "玫红色", "果绿色", "黄色", "蓝色", "白色")
        if (colors.contains(slots.color)) {
            val command = CarCmd(action = Action.FIXED, model = Model.LIGHT_AMBIENT)
            command.car = ICar.COLOR
            command.color = slots.color
            return command
        }
        return null
    }

    private fun attemptCreateAmbientBrightnessCommand(slots: Slots): CarCmd? {
        val value = slots.nameValue?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        var action = Action.VOID
        var command: CarCmd? = null
        if (!CarController.isLikeJson(value)) {
            var step = 1
            if ((Keywords.PLUS == value) || (Keywords.PLUS_MORE == value) || (Keywords.PLUS_LITTLE == value)) {
                action = Action.PLUS
            } else if ((Keywords.MINUS == value) || (Keywords.MINUS_MORE == value) || (Keywords.MINUS_LITTLE == value)) {
                action = Action.MINUS
            } else if (Keywords.MIN == value) {
                action = Action.MIN
            } else if (Keywords.MAX == value) {
                action = Action.MAX
            }
            if (Action.VOID != action) {
                command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                command.slots = slots
                command.step = step
                command.car = ICar.BRIGHTNESS
                LogManager.d(CarController.tag, "attemptCreateAmbientBrightnessCommand value:$value")
            }
        } else {
            val jsonObject = JSONObject(value)
            val consult = jsonObject.getString("ref")
            if (Keywords.REF_CUR == consult) {
                val rule = jsonObject.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
                if (Action.VOID != action) {
                    command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                    command.slots = slots
                    command.car = ICar.BRIGHTNESS
                    LogManager.d(CarController.tag, "attemptCreateAmbientBrightnessCommand consult:$consult, rule:$rule")
                }
            } else if (Keywords.REF_ZERO == consult) {
                val offset = jsonObject.getInt("offset")
                action = Action.FIXED
                command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                command.slots = slots
                command.value = offset
                command.car = ICar.BRIGHTNESS
                LogManager.d(CarController.tag, "attemptCreateAmbientBrightnessCommand consult:$consult, offset:$offset")
            }
        }
        return command

    }

    private fun attemptCreateAmbientSwitchCommand(slots: Slots): CarCmd? {
        var action = Action.VOID
        if (Keywords.OPEN == slots.operation) {
            action = Action.TURN_ON
        } else if (Keywords.CLOSE == slots.operation) {
            action = Action.TURN_OFF
        }
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
        if (Keywords.AMBIENTS[0] == slots.name) {
            command.part = IPart.HEAD or IPart.TAIL
        }
        if (Keywords.AMBIENTS[1] == slots.name) {
            command.part = IPart.HEAD
        }
        if (Keywords.AMBIENTS[2] == slots.name) {
            command.part = IPart.TAIL
        }
        command.slots = slots
        command.car = ICar.AMBIENT
        return command
    }


}