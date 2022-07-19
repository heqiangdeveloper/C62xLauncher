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
@IntDef(Model.INVALID, Model.DOOR, Model.WINDOW)
@Retention(RetentionPolicy.SOURCE)
annotation class Model {
    companion object {
        const val INVALID = -1
        const val DOOR = 1
        const val WINDOW = 2
    }
}