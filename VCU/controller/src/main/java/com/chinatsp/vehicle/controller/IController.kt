package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.producer.ICommandProducer
import com.chinatsp.vehicle.controller.semantic.VoiceModel

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 17:43
 * @desc   :
 * @version: 1.0
 */
interface IController : ICommandProducer {

    val tag: String
        get() = this::class.java.simpleName

    fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: VoiceModel,
    ): Boolean

    fun doVoiceVehicleQuery(
        controller: IOuterController,
        callback: ICmdCallback,
        model: VoiceModel,
    ): Boolean {
        return true
    }

}