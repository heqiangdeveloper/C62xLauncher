package com.chinatsp.vehicle.controller.annotation

import androidx.annotation.IntDef

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc : 车型配置
 * @version: 1.0
 */
@IntDef(Level.LEVEL3, Level.LEVEL4, Level.LEVEL5)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Level {
    /**
     * 下线配置索引序号配置或车型配置
     * 1:LV3; 2:LV4; 3:LV5带蓝牙钥匙
     */
    companion object {
        const val LEVEL3 = 1
        const val LEVEL4 = 2
        const val LEVEL5 = 3
        //const val LEVEL5_2 = 4
    }
}