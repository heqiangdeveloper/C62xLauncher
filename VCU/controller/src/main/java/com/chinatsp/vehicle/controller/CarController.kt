package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.producer.*
import com.chinatsp.vehicle.controller.semantic.VoiceModel
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords

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

    private val lampsProducer: LampCommandProducer get() = LampCommandProducer()

    override fun doVoiceVehicleQuery(
        controller: IOuterController,
        callback: ICmdCallback,
        model: VoiceModel,
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
        model: VoiceModel,
    ): Boolean {
        val slots: Slots = model.slots
        var command: CarCmd? = accessProducer.attemptCreateCommand(slots)
        if (null == command) {
            command = seatProducer.attemptCreateCommand(slots)
        }
        if (null == command) {
            command = ambientProducer.attemptCreateCommand(slots)
        }
        if (null == command) {
            command = otherProducer.attemptCreateCommand(slots)
        }
        if (null == command) {
            command = lampsProducer.attemptCreateCommand(slots)
        }
        if (null == command) {
            command = panoramaProducer.attemptCreateCommand(slots)
        }
//        if (null == command) {
//            command = attemptAutoParkCommand(slots)
//        }
        if (null != command) {
            controller.doCarControlCommand(command, callback)
        } else {
            val service = if (isApaHandle(slots)) Keywords.APA_SERVICE else Keywords.SCENE_SERVICE
            controller.doTransmitSemantic(service, slots.json)
        }
        return true
    }

    private fun isApaHandle(slots: Slots): Boolean {
        var result = false
        do {
            if ("????????????" == slots.name) {
                result = true
                break
            }
            if (slots.text.contains("????????????") || slots.text.contains("????????????")
                || slots.text.contains("????????????") || slots.text.contains("????????????")
                || slots.text.contains("??????")) {
                result = true
                break
            }
            if (slots.text.contains("????????????")) {
                result = true
                break
            }
            if (slots.text.contains("??????") && slots.text.contains("??????")) {
                result = true
                break
            }
        } while (false)
        return result
    }

    private fun attemptAutoParkCommand(slots: Slots): CarCmd? {
        if ("????????????" == slots.name) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("????????????")
            || slots.text.contains("????????????")
            || slots.text.contains("????????????")
            || slots.text.contains("????????????")
            || slots.text.contains("??????")
        ) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("????????????")) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        if (slots.text.contains("??????") && slots.text.contains("??????")) {
            val command = CarCmd(action = Action.VOID, model = Model.AUTO_PARK)
            command.slots = slots
            return command
        }
        return null
    }

}