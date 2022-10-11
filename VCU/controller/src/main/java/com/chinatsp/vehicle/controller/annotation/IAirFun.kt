package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(IAirFun.INVALID,
    IAirFun.TURN_ON_OFF,
    IAirFun.ADJUST_WIND_SPEED,
    IAirFun.ADJUST_WIND_DIRECTION,
    IAirFun.ADJUST_TEMPERATURE,
    IAirFun.ADJUST_LOPPER_MODE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IAirFun {
    companion object {
        const val INVALID: Int = -1
        const val TURN_ON_OFF: Int = 1
        const val ADJUST_WIND_SPEED: Int = 2
        const val ADJUST_WIND_DIRECTION: Int = 3
        const val ADJUST_TEMPERATURE: Int = 4
        const val ADJUST_LOPPER_MODE: Int = 5
    }
}