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
interface IController: ICommandProducer, Keywords {

    val tag: String
        get() = this::class.java.simpleName

//    fun isMatch(arrays: Array<String>, serial: String?): Boolean {
//        return null != serial && arrays.contains(serial)
//    }
//
//    fun isMatch(arrays: Array<String>, serial1: String?, serial2: String?): Boolean {
//        return isMatch(arrays, serial1) || isMatch(arrays, serial2)
//    }
//
//    fun isLikeJson(value: String): Boolean {
//        return value.startsWith("{") && value.endsWith("}")
//    }

    fun doHandleUnknownHint(callback: ICmdCallback?) {
        callback?.run {
            val command = BaseCmd()
            command.message = "我还不会这个操作"
            onCmdHandleResult(command)
        }
    }

    fun doVoiceController(controller: IOuterController, callback: ICmdCallback, model: NlpVoiceModel): Boolean
}