package com.chinatsp.settinglib.navigation

import android.annotation.IntRange
import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/6/25 10:40
 * @desc :
 * @version: 1.0
 */
object RouterSerial {
    private const val SHIFT_UNIT = 8
    private const val LEVEL1_SHIFT = 3 * SHIFT_UNIT
    private const val LEVEL2_SHIFT = 2 * SHIFT_UNIT
    private const val LEVEL3_SHIFT = SHIFT_UNIT
    private const val LEVEL1_MASK = 0xf shl LEVEL1_SHIFT
    private const val LEVEL2_MASK = 0xf shl LEVEL2_SHIFT
    private const val LEVEL3_MASK = 0xf shl LEVEL3_SHIFT
    private const val LEVEL4_MASK = 0xf
    const val UNIVERSAL = 0x1
    const val ACCESS = 0x2
    const val LIGHTING = 0x2
    fun makeRouteSerial(
        @IntRange(from = 0, to = 0xf) level1: Int,
        @IntRange(from = 0, to = 0xf) level2: Int,
        @IntRange(from = 0, to = 0xf) level3: Int,
    ): Int {
        return level1 shl LEVEL1_SHIFT or (level2 shl LEVEL2_SHIFT) or (level3 shl LEVEL3_SHIFT)
    }

    fun getLevel(routeSerial: Int, @IntRange(from = 1, to = 3) hierarchy: Int): Int {
        return when (hierarchy) {
            1 -> routeSerial and LEVEL1_MASK shr LEVEL1_SHIFT
            2 -> routeSerial and LEVEL2_MASK shr LEVEL2_SHIFT
            3 -> routeSerial and LEVEL3_MASK shr LEVEL3_SHIFT
            else -> Constant.INVALID
        }
    }
}