package com.chinatsp.settinglib

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 18:50
 * @desc   :
 * @version: 1.0
 */
object Constant {

    var ENGINE_STATUS = true

    const val INVALID: Int = -1

    const val DEFAULT: Int = INVALID

    /**
     * 浅色主题（白天模式）
     */
    const val LIGHT_TOPIC: Int = 0x01

    /**
     * 深色主题（黑夜模式）
     */
    const val DARK_TOPIC: Int = 0x02


    val ROUTE_SERIAL: String
        get() = "ROUTE_SERIAL"

    /**
     * 视频开关
     */
    val DRIVE_VIDEO_PLAYING: String
        get() = "DRIVE_VIDEO_PLAYING"

    /**
     * 辅助线
     */
    val AUXILIARY_LINE: String
        get() = "AUXILIARY_LINE"

    val SHOW_AREA: String
        get() = "SHOW_AREA"

    /**
     * 座椅加热开关
     */
    val SEAT_HEAT_SWITCH: String
        get() = "SEAT_HEAT_SWITCH"

    /**
     * 座椅加热开始温度
     */
    val SEAT_HEAT_TEMP: String
        get() = "SEAT_HEAT_TEMP"


    /**
     * 方向盘加热开关
     */
    val STEERING_HEAT_SWITCH: String
        get() = "STEERING_HEAT_SWITCH"

    /**
     * 方向盘加热开始温度
     */
    val STEERING_HEAT_TEMP: String
        get() = "STEERING_HEAT_TEMP"

}