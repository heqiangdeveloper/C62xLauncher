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
            result = when (nlpVoiceModel.service) {
                "airControl" -> {
                    AirController.doVoiceController(controller, callback, nlpVoiceModel)
                }
                "carControl" -> {
                    CarController.doVoiceController(controller, callback, nlpVoiceModel)
                }
                "app", "cmd", "radio", "video", "musicX" -> {
                    true
                }
                else -> {
                    CarController.doVoiceController(controller, callback, nlpVoiceModel)
                    true
                }
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