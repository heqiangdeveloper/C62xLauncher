package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(IAir.DEFAULT,
    IAir.AIR_TEMP,
    IAir.AIR_WIND,
    IAir.LOOP_AUTO,
    IAir.LOOP_INNER,
    IAir.LOOP_OUTER,
    IAir.MODE_HOT,
    IAir.MODE_COLD,
    IAir.AIR_PURGE,
    IAir.AIR_FLOW,
    IAir.AIR_DEFROST,
    IAir.AIR_DOUBLE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IAir {
    companion object {
        const val DEFAULT = 0x0
        /**
         * 空调温度
         */
        const val AIR_TEMP = 0x1 shl 0
        /**
         * 空调风
         */
        const val AIR_WIND = 0x1 shl 1

        //-------------空调循环模式--------------
        /**
         * 自动循环
         */
        const val LOOP_AUTO = 0x1 shl 2
        /**
         * 内循环
         */
        const val LOOP_INNER = 0x1 shl 3
        /**
         * 外循环
         */
        const val LOOP_OUTER = 0x1 shl 4
        //-------------空调循环模式--------------

        /**
         * 制热模式
         */
        const val MODE_HOT = 0x1 shl 5
        /**
         * 制冷模式
         */
        const val MODE_COLD = 0x1 shl 6

        /**
         * 空气净化模式
         */
        const val AIR_PURGE = 0x1 shl 7

        /**
         * 空气流向
         */
        const val AIR_FLOW = 0x1 shl 8

        /**
         * 除霜模式
         */
        const val AIR_DEFROST = 0x1 shl 9

        /**
         * 双区模式
         */
        const val AIR_DOUBLE = 0x1 shl 10
    }
}