package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.producer.*
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import com.chinatsp.vehicle.controller.semantic.Slots

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

    override fun doVoiceVehicleQuery(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel
    ): Boolean {
        val slots: Slots = model.slots
        val command: CarCmd? = otherProducer.attemptVehicleInfoCommand(slots)
        if (null != command) {
            controller.doCarControlCommand(command, callback)
        }
        return null != command
    }

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
}