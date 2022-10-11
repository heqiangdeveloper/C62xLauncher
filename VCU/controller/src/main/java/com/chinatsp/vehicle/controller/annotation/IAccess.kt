package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:23
 * @desc   :
 * @version: 1.0
 */
@IntDef(
    IAccess.HEAD,
    IAccess.LEFT_FRONT,
    IAccess.LEFT_BACK,
    IAccess.RIGHT_FRONT,
    IAccess.RIGHT_BACK,
    IAccess.TAIL,
    IAccess.SKYLIGHT
)
@Retention(RetentionPolicy.SOURCE)
annotation class IAccess {
    companion object {
        const val HEAD = 0x1 shl 0
        const val LEFT_FRONT = 0x1 shl 1
        const val LEFT_BACK = 0x1 shl 2
        const val RIGHT_FRONT = 0x1 shl 3
        const val RIGHT_BACK = 0x1 shl 4
        const val TAIL = 0x1 shl 5
        const val SKYLIGHT = 0x1 shl 6
    }

}