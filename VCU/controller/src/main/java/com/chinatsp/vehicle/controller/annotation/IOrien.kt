package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:23
 * @desc   :
 * @version: 1.0
 */
@IntDef(IOrien.VOID, IOrien.FACE, IOrien.BODY, IOrien.FOOT,
    IOrien.LEFT, IOrien.MIDDLE, IOrien.RIGHT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IOrien {
    companion object {
        const val VOID = 0X0

        const val FACE = 0x1 shl 0
        const val BODY = 0x1 shl 1
        const val FOOT = 0x1 shl 2

        const val LEFT = 0x1 shl 3
        const val MIDDLE = 0x1 shl 4
        const val RIGHT = 0x1 shl 5
    }

}