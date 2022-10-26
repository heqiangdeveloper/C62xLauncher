package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.producer.*
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import org.json.JSONObject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 16:23
 * @desc   :
 * @version: 1.0
 */
object CarController : IController {

    private val accessProducer: AccessCommandProducer by lazy { AccessCommandProducer() }

    private val seatProducer: SeatCommandProducer by lazy { SeatCommandProducer() }

    private val ambientProducer: AmbientCommandProducer by lazy { AmbientCommandProducer() }

    private val panoramaProducer: PanoramaCommandProducer by lazy { PanoramaCommandProducer() }

    private val otherProducer: OtherCommandProducer by lazy { OtherCommandProducer() }

    override fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean {
        val slots: Slots = model.slots
        var command: CarCmd? = null
        if (null == command) {
            command = accessProducer.attemptAccessCommand(slots)
        }
        if (null == command) {
            command = seatProducer.attemptChairCommand(slots)
        }
        if (null == command) {
            command = ambientProducer.attemptAmbientCommand(slots)
        }
        if (null == command) {
            command = panoramaProducer.attemptPanoramaCommand(slots)
        }
        if (null == command) {
            command = otherProducer.attemptCommand(slots)
        }
        if (null == command) {
            command = attemptAutoParkCommand(slots)
        }
        if (null != command) {
            controller.doCarControlCommand(command, callback)
        }
        return null != command
//
//        if (isMatch(Keywords.DRIVER_WINDOW, name)
//            || isMatch(Keywords.PASSENGER_WINDOW, name)
//        ) {
//            result = open || close
//        } else if (isMatch(Keywords.OIL_SHROUDS, name)) {
//            result = open || close
//        } else if (isMatch(Keywords.SKYLIGHTS, name)) {
//
//        } else if (isMatch(Keywords.HOODS, name)) {
//            //引擎盖 操作
//            result = open || close
//        } else if (isMatch(Keywords.TRUNKS, name)) {
//            //后备厢 操作
//            result = open || close
//        } else if (isMatch(Keywords.WIPERS, name)) {
//            //前雨刮 操作
//            result = open || close
//        } else if (isMatch(Keywords.REAR_WIPERS, name)) {
//            //后雨刮 操作
//            result = open || close
//        }
////
////        else if (isMatch(Keywords.TIRE_PRESSURE_MONITORS, name)) {
////            result = open || close
////        } else if (isMatch(Keywords.SMOKES, slots.mode)) {
////            result = open || close
////        } else if (TextUtils.equals(Keywords.WIRELESS_CHARGING, name)) {
////            result = open || close
////        } else if (isMatch(Keywords.IDLE_START_AND_STOP, name)) {
////            result = open || close
////        }
//
//        else if (isMatch(Keywords.AUTO_HEAD_LIGHTS, name)) {
//            result = open || close
//        } else if (isMatch(Keywords.LIGHTS, name)) {
//            result = open || close
//        } else if (isMatch(Keywords.FOG_LIGHTS, name)) {
//            result = open || close
//        } else if (TextUtils.equals(Keywords.MODE_DRIVE, slots.mode)) {
//            result = open || close
//        } else if (isMatch(Keywords.WHEELS, slots.name)) {
//            var action = Action.VOID
//            if (open) action = Action.TURN_ON
//            if (close) action = Action.TURN_OFF
//            val command = CarCmd(action = action, model = Model.CABIN_WHEEL)
//            command.slots = slots
//            if ("方向盘加热" == slots.mode) {
//                command.car = ICar.WHEEL_HOT
//            }
//            controller.doCarControlCommand(command, callback)
//            return true
//        } else if (isMatch(Keywords.AMBIENTS, slots.name)) {
//            doAmbientCommand(slots, controller, callback)
//            return true
//        }
//        return result
    }

    private fun attemptAutoParkCommand(slots: Slots): CarCmd? {
        if ("自动泊车" == slots.name) {
            LogManager.e("------attemptAutoParkCommand--------")
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("自动停车")
            || slots.text.contains("自动倒车")
            || slots.text.contains("车位页面")
            || slots.text.contains("停车位置")
            || slots.text.contains("倒车")) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("自选车位")) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("选择") && slots.text.contains("车位")) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        return null
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
        if (isMatch(Keywords.OPT_OPENS, slots.operation)) {
            action = Action.TURN_ON
        } else if (isMatch(Keywords.OPT_CLOSES, slots.operation)) {
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
        if (!isLikeJson(value)) {
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
                LogManager.d(tag, "attemptCreateAmbientBrightnessCommand value:$value")
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
                    LogManager.d(tag, "attemptCreateAmbientBrightnessCommand consult:$consult, rule:$rule")
                }
            } else if (Keywords.REF_ZERO == consult) {
                val offset = jsonObject.getInt("offset")
                action = Action.FIXED
                command = CarCmd(action = action, model = Model.LIGHT_AMBIENT)
                command.slots = slots
                command.value = offset
                command.car = ICar.BRIGHTNESS
                LogManager.d(tag, "attemptCreateAmbientBrightnessCommand consult:$consult, offset:$offset")
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