package com.chinatsp.settinglib.bean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/24 15:37
 * @desc   :
 * @version: 1.0
 */
class ValueBean {
    private var value: Int = 0

    fun setValue(input: Int, block: ((Int) -> Int)? = null) {
        value = if (null != block) block(input) else input
    }

    fun getValue(): Int {
        return value
    }
}