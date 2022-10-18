package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(ICar.VOID,
    ICar.WHEEL_HOT,
    ICar.AMBIENT,
    ICar.BRIGHTNESS,
    ICar.COLOR,
    ICar.RHYTHM_MODE,
    ICar.MODE_3D_2D
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ICar {
    companion object {
        const val VOID = 0x0
        /**
         * 方向盘加热
         */
        const val WHEEL_HOT = 0x01

        /**
         * 氛围灯
         */
        const val AMBIENT = 0x02
        /**
         * 亮度
         */
        const val BRIGHTNESS = 0x03

        /**
         * 亮度
         */
        const val COLOR = 0x04

        /**
         * 亮度
         */
        const val RHYTHM_MODE = 0x05

        const val MODE_3D_2D = 0x06
    }
}