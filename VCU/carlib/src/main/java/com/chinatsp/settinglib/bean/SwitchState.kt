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

    var enableStatus: Int = Constant.VIEW_ENABLE

    fun get() = data

    fun set(status: Boolean) {
        data = status
    }

    fun enable() = enableStatus == Constant.VIEW_ENABLE

    fun enable(value: Int) = value == Constant.VIEW_ENABLE

    fun isEnableChanged(value: Int) :Boolean  {
        val actual = enable()
        val expect = value == Constant.VIEW_ENABLE
        return actual xor expect
    }

}