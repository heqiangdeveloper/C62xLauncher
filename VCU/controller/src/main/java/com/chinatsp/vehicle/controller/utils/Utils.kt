package com.chinatsp.vehicle.controller.utils

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/21 14:53
 * @desc :
 * @version: 1.0
 */
class Utils {

    companion object {

        @JvmStatic
        fun toFullBinary(x: Int): String? {
            val buffer = IntArray(Integer.SIZE)
            for (i in Integer.SIZE - 1 downTo 0) {
                buffer[i] = x shr i and 1
            }
            val builder = StringBuilder()
            for (j in Integer.SIZE - 1 downTo 0) {
                builder.append(buffer[j])
            }
            return builder.toString()
        }
    }

}