package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.semantic.CmdVoiceModel
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/14 13:34
 * @desc   :
 * @version: 1.0
 */
class CommandParser {

    val tag: String = "CommandParser"

    fun doDispatchSrAction(
        nlpVoiceModel: NlpVoiceModel,
        controller: IOuterController,
        callback: ICmdCallback,
    ): Boolean {
        var result = false
        try {
            if ("airControl" == nlpVoiceModel.service) {
                AirController.doVoiceController(controller, callback, nlpVoiceModel)
            } else if ("carControl" == nlpVoiceModel.service) {
                result = CarController.doVoiceController(controller, callback, nlpVoiceModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogManager.e(tag, " 语音解析异常 error:${e.message}")
        }

        return result
    }


    fun doDispatchCmdAction(cmdVoiceModel: CmdVoiceModel) {

    }

}