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
    Action.OPTION)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Action {
    companion object {
        /**
         * 无效Action
         */
        const val VOID: Int = -1

        /**
         * 打开
         */
        const val OPEN: Int = 1

        /**
         * 关闭
         */
        const val CLOSE: Int = 2

        /**
         * 增加（如：风速增大、温度调高，车窗开大）
         */
        const val PLUS: Int = 3
        /**
         * 减少（如：风速减小、温度调低，车窗开小）
         */
        const val MINUS: Int = 4

        /**
         * 最小值（如：风速最小等）
         */
        const val MIN: Int = 5
        /**
         * 最大值（如：风速最大等）
         */
        const val MAX: Int = 6
        /**
         * 固定值（如：温度调到20等）
         */
        const val FIXED: Int = 7
        /**
         * 选项值（如：空调吹头，空调吹脚 等）
         */
        const val OPTION: Int = 8
    }
}