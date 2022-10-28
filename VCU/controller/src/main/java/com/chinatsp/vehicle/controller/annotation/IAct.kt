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
    IAct.KNEAD
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
    }
}