package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/30 13:05
 * @desc   :
 * @version: 1.0
 */
data class RadioState(var data: Int) {

    var enable: Int = Constant.VIEW_ENABLE

    fun get() = data

    fun set(value: Int) {
        data = value
    }

    fun enable() = enable == Constant.VIEW_ENABLE

    fun deepCopy(): RadioState {
        val copy = this.copy()
        copy.data = data
        copy.enable = enable
        return copy
    }

}