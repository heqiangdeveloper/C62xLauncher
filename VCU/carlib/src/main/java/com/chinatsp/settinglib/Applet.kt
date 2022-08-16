package com.chinatsp.settinglib

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/9 11:33
 * @desc   :
 * @version: 1.0
 */
object Applet {

    var speed: Float = 0.0f

    fun isCanSwitchEps(consult: Float): Boolean {
        return speed < consult
    }
}