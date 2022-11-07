package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:23
 * @desc   :
 * @version: 1.0
 */
@IntDef(
    IPart.VOID,
    IPart.HEAD,
    IPart.L_F,
    IPart.L_B,
    IPart.R_F,
    IPart.R_B,
    IPart.TAIL,
    IPart.TOP,
    IPart.BOTTOM
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IPart {
    companion object {
        /**
         * 无效值
         */
        const val VOID = 0x0
        /**
         * 方向-前
         */
        const val HEAD = 0x1 shl 0
        /**
         * 方向-后
         */
        const val TAIL = 0x1 shl 1
        /**
         * 方向-左前
         */
        const val L_F = 0x1 shl 2
        /**
         * 方向-左后
         */
        const val L_B = 0x1 shl 3
        /**
         * 方向-右前
         */
        const val R_F = 0x1 shl 4
        /**
         * 方向-右后
         */
        const val R_B = 0x1 shl 5
        /**
         * 方向-顶 (天窗玻璃的方向)
         */
        const val TOP = 0x1 shl 6
        /**
         * 方向-底 (遮阳帘的方向)
         */
        const val BOTTOM = 0x1 shl 7
    }
}