package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(IAir.VOID,
    IAir.AIR_TEMP,
    IAir.AIR_WIND,
//    IAir.LOOP_AUTO,
    IAir.LOOP_MODE,
//    IAir.LOOP_OUTER,
//    IAir.MODE_HOT,
    IAir.MODE_COLD_HEAT,
    IAir.AIR_PURGE,
    IAir.AIR_FLOW,
    IAir.AIR_DEFROST,
    IAir.AIR_DOUBLE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IAir {
    companion object {

        const val VOID = 0x0
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
         * 内循环
         */
        const val LOOP_MODE = 0x1 shl 2
        //-------------空调循环模式--------------
        /**
         * 制冷/制热 模式
         */
        const val MODE_COLD_HEAT = 0x1 shl 3

        /**
         * 空气净化模式
         */
        const val AIR_PURGE = 0x1 shl 4

        /**
         * 空气流向
         */
        const val AIR_FLOW = 0x1 shl 5

        /**
         * 除霜模式
         */
        const val AIR_DEFROST = 0x1 shl 6

        /**
         * 双区模式
         */
        const val AIR_DOUBLE = 0x1 shl 7

        /**
         * 双区模式
         */
        const val AUTO_MODE = 0x1 shl 8
    }
}