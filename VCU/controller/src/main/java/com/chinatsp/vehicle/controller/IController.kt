package com.chinatsp.vehicle.controller

import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 17:43
 * @desc   :
 * @version: 1.0
 */
interface IController {

    val tag: String
        get() = this::class.java.simpleName

    fun isMatch(arrays: Array<String>, cmd: String?): Boolean {
        return arrays.contains(cmd)
    }

    fun isMatch(arrays: Array<String>, cmd1: String, cmd2: String): Boolean {
        return arrays.contains(cmd1) || arrays.contains(cmd2)
    }

    fun isLikeJson(value: String): Boolean {
        return value.startsWith("{") && value.endsWith("}")
    }

    fun doVoiceController(controller: IOuterController, callback: ICmdCallback, model: NlpVoiceModel): Boolean
}