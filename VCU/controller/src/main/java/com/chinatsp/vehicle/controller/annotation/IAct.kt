package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(IAct.VOID,
    IAct.HEAT,
    IAct.COLD,
    IAct.KNEAD,
    IAct.TILT,
    IAct.ENDURANCE_MILEAGE,
    IAct.MAINTAIN_MILEAGE,
    IAct.AVERAGE_FUEL_CONSUMPTION,
    IAct.INSTANTANEOUS_FUEL_CONSUMPTION,
    IAct.DIPPED_LIGHT,
    IAct.DISTANT_LIGHT,
    IAct.FOG_LIGHT,
    IAct.SIDE_LIGHT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IAct {
    companion object {
        /**
         * 无效值
         */
        const val VOID = 0x0

        /**
         * 加热
         */
        const val HEAT = 0x01

        /**
         * 制冷
         */
        const val COLD = 0x02

        /**
         * 按摩
         */
        const val KNEAD = 0x03

        /**
         * 倾斜度
         */
        const val TILT = 0x04

        /**
         * 续航里程
         */
        const val ENDURANCE_MILEAGE = 0x05

        /**
         * 保养里程
         */
        const val MAINTAIN_MILEAGE = 0x06

        /**
         * 平均油耗
         */
        const val AVERAGE_FUEL_CONSUMPTION = 0x07

        /**
         * 瞬时油耗
         */
        const val INSTANTANEOUS_FUEL_CONSUMPTION = 0x08

        /**
         * 近光灯/小灯
         */
        const val DIPPED_LIGHT = 0x09
        /**
         * 远光灯/大灯
         */
        const val DISTANT_LIGHT = 0x0A

        /**
         * 雾灯
         */
        const val FOG_LIGHT = 0x0B
        /**
         * 位置灯
         */
        const val SIDE_LIGHT = 0x0C
    }
}