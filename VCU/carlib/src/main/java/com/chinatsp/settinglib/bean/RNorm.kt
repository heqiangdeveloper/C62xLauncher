package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/11 13:47
 * @desc   :
 * @version: 1.0
 */
data class RNorm(
    val values: IntArray = IntArray(3),
    val origin: Origin = Origin.CABIN,
    val signal: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RNorm

        if (!values.contentEquals(other.values)) return false
        if (origin != other.origin) return false
        if (signal != other.signal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.contentHashCode()
        result = 31 * result + origin.hashCode()
        result = 31 * result + signal
        return result
    }

    fun isValid(value: Int): Boolean {
        return values.contains(value)
    }
}
