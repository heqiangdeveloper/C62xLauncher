package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/9/30 13:05
 * @desc   :
 * @version: 1.0
 */
data class SwitchState(var data: Boolean) {

    var enable: Int = Constant.VIEW_ENABLE

    fun get() = data

    fun set(status: Boolean) {
        data = status
    }

    fun enable() = enable == Constant.VIEW_ENABLE

    fun enable(value: Int) = value == Constant.VIEW_ENABLE

    fun setEnable(value: Int): Boolean {
        val result = value != enable
        if (result) {
            enable = value
        }
        return result
    }

    fun deepCopy(): SwitchState {
        return this
//        val copy = this.copy()
//        copy.data = data
//        copy.enable = enable
//        return copy
    }

}

