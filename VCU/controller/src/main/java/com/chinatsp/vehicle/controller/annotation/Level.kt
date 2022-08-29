package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(Level.LEVEL3, Level.LEVEL4, Level.LEVEL5)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Level {
    companion object {
        const val LEVEL3 = 0
        const val LEVEL4 = 1
        const val LEVEL5 = 2
    }
}