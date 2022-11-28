package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.bean.BaseCmd
import com.chinatsp.vehicle.controller.producer.ICommandProducer
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import com.chinatsp.vehicle.controller.utils.Keywords

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 17:43
 * @desc   :
 * @version: 1.0
 */
interface IController : ICommandProducer, Keywords {

    val tag: String
        get() = this::class.java.simpleName

    fun doHandleUnknownHint(callback: ICmdCallback?) {
        callback?.run {
            val command = BaseCmd()
            command.message = "我还不会这个操作"
            onCmdHandleResult(command)
        }
    }

    fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean

    fun doVoiceVehicleQuery(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean {
        return true
    }

}