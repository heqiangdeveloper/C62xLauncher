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
    IPart.LEFT_FRONT,
    IPart.LEFT_BACK,
    IPart.RIGHT_FRONT,
    IPart.RIGHT_BACK,
    IPart.TAIL,
    IPart.SKYLIGHT,
    IPart.LOVE_LUCY
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IPart {
    companion object {
        const val VOID = 0x0
        const val HEAD = 0x1 shl 0
        const val LEFT_FRONT = 0x1 shl 1
        const val LEFT_BACK = 0x1 shl 2
        const val RIGHT_FRONT = 0x1 shl 3
        const val RIGHT_BACK = 0x1 shl 4
        const val TAIL = 0x1 shl 5
        const val SKYLIGHT = 0x1 shl 6
        const val LOVE_LUCY = 0x1 shl 7
        const val LEFT = 0x1 shl 8
        const val RIGHT = 0x1 shl 9
    }

}