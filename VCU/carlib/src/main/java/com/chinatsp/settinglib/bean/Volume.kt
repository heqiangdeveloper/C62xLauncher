package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.optios.Progress

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/9 20:40
 * @desc   :
 * @version: 1.0
 */
data class Volume(val type: Progress, var min: Int, var max: Int, var pos: Int) : Comparable<Volume> {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun isValid(value: Int): Boolean {

        return value in min..max
    }

    override fun compareTo(other: Volume): Int {
        return if (type == other.type && pos == other.pos && min == other.min && max == other.max) 0 else 1
    }

    fun deepCopy(): Volume {
        return copy(type = type, min = min, max = max, pos = pos)
    }

}