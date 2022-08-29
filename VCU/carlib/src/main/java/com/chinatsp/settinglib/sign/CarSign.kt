package com.chinatsp.settinglib.sign

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/26 13:30
 * @desc   :
 * @version: 1.0
 */
class CarSign(val type: Origin) {

    val signals: MutableSet<Int> by lazy { HashSet() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CarSign
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

}