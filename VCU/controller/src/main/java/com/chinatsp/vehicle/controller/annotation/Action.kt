package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@IntDef(Action.VOID,
    Action.OPEN,
    Action.CLOSE,
    Action.PLUS,
    Action.MINUS,
    Action.MIN,
    Action.MAX,
    Action.FIXED,
    Action.OPTION,
    Action.TURN_ON,
    Action.TURN_OFF
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Action {
    companion object {
        /**
         * 无效Action
         */
        const val VOID = 0x0

        /**
         * 打开
         */
        const val OPEN = 0x1 shl 1

        /**
         * 关闭
         */
        const val CLOSE = 0x1 shl 2

        /**
         * 增加（如：风速增大、温度调高，车窗开大）
         */
        const val PLUS = (0x1 shl 3) //xor OPEN

        /**
         * 减少（如：风速减小、温度调低，车窗开小）
         */
        const val MINUS = (0x1 shl 4) //xor OPEN

        /**
         * 最小值（如：风速最小等）
         */
        const val MIN = (0x1 shl 5) //xor OPEN

        /**
         * 最大值（如：风速最大等）
         */
        const val MAX = (0x1 shl 6) //xor OPEN

        /**
         * 固定值（如：温度调到20等）
         */
        const val FIXED = (0x1 shl 7) //xor OPEN

        /**
         * 选项值（如：空调吹头，空调吹脚 等）
         */
        const val OPTION = (0x1 shl 8) //xor OPEN

        /**
         * 打开 压缩机 （空调）
         */
        const val TURN_ON = (0x1 shl 9) //xor OPEN
        /**
         * 关闭 压缩机 （空调）
         */
        const val TURN_OFF = 0x1 shl 10
    }
}