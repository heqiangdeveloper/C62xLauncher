package com.chinatsp.vehicle.controller.producer

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/18 10:17
 * @desc   :
 * @version: 1.0
 */
interface ICommandProducer {
    fun isMatch(arrays: Array<String>, serial: String?): Boolean {
        return null != serial && arrays.contains(serial)
    }

    fun isMatch(arrays: Array<String>, serial1: String?, serial2: String?): Boolean {
        return isMatch(arrays, serial1) || isMatch(arrays, serial2)
    }

    fun isLikeJson(value: String): Boolean {
        return value.startsWith("{") && value.endsWith("}")
    }
}