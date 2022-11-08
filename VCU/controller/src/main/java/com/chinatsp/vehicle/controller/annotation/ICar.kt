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
    ICar.WHEEL,
    ICar.AMBIENT,
    ICar.BRIGHTNESS,
    ICar.COLOR,
    ICar.RHYTHM_MODE,
    ICar.WINDOWS,
    ICar.LOUVER,
    ICar.DOORS,
    ICar.CHAIR,
    ICar.WIPER,
    ICar.LAMPS
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ICar {
    companion object {
        const val VOID = 0x0
        /**
         * 方向盘
         */
        const val WHEEL = 0x01

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
        /**
         * 车窗
         */
        const val WINDOWS = 0x08
        /**
         * 天窗（包括遮阳帘）
         */
        const val LOUVER = 0x09
        /**
         * 车门
         */
        const val DOORS = 0x0A
        /**
         * 座椅
         */
        const val CHAIR = 0x0B
        /**
         * 雨刮
         */
        const val WIPER = 0x0C
        /**
         * 灯光
         */
        const val LAMPS = 0x0D
    }
}