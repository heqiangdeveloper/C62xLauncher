package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(Level.LEVEL3, Level.LEVEL4, Level.LEVEL5)
@Retention(RetentionPolicy.SOURCE)
annotation class Level {
    companion object {
        const val LEVEL3 = 0
        const val LEVEL4 = 1
        const val LEVEL5 = 2
    }
}