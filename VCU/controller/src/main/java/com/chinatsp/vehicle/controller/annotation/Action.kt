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
@IntDef(Action.VOID, Action.SEEK, Action.OPEN, Action.CLOSE, Action.OPTION)
@Retention(RetentionPolicy.SOURCE)
annotation class Action {
    companion object {
        const val VOID: Int = -1
        const val SEEK: Int = 1
        const val OPEN: Int = 2
        const val CLOSE: Int = 3
        const val OPTION: Int = 4
    }
}