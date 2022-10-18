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
    IPart.DEFAULT,
    IPart.HEAD,
    IPart.LEFT_FRONT,
    IPart.LEFT_BACK,
    IPart.RIGHT_FRONT,
    IPart.RIGHT_BACK,
    IPart.TAIL,
    IPart.SKYLIGHT,
    IPart.SKYLIGHT2
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IPart {
    companion object {
        const val DEFAULT = 0X0
        const val HEAD = 0x1 shl 0
        const val LEFT_FRONT = 0x1 shl 1
        const val LEFT_BACK = 0x1 shl 2
        const val RIGHT_FRONT = 0x1 shl 3
        const val RIGHT_BACK = 0x1 shl 4
        const val TAIL = 0x1 shl 5
        const val SKYLIGHT = 0x1 shl 6
        const val SKYLIGHT2 = 0x1 shl 7
    }

}