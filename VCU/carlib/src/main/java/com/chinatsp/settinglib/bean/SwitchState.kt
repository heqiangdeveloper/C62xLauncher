package com.chinatsp.settinglib.bean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/30 13:05
 * @desc   :
 * @version: 1.0
 */
data class SwitchState(var data: Boolean) {

    var enable: Int = 0x1

    fun get() = data

    fun set(status: Boolean) {
        data = status
    }

    fun enable() = enable == 0x1
}