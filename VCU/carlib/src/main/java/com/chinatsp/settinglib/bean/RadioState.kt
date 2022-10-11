package com.chinatsp.settinglib.bean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/30 13:05
 * @desc   :
 * @version: 1.0
 */
data class RadioState(var data: Int) {

    var enable: Int = 0x1

    fun get() = data

    fun set(value: Int) {
        data = value
    }

    fun enable() = enable == 0x1

}