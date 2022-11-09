package com.chinatsp.settinglib

import android.car.hardware.cabin.CarCabinManager
import android.car.media.CarAudioManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 18:50
 * @desc   :
 * @version: 1.0
 */
object Constant {

    const val VehicleSignal = "VehicleSignal"

    var ENGINE_STATUS = true

    const val ANGLE_SAVE = 0x11

    const val ANGLE_ADJUST = 0x12

    const val INVALID: Int = -1

    const val DEFAULT: Int = INVALID

    const val VIEW_ENABLE = -100

    /**
     * 浅色主题（白天模式）
     */
    const val LIGHT_TOPIC: Int = 0x01

    /**
     * 深色主题（黑夜模式）
     */
    const val DARK_TOPIC: Int = 0x02

    /**
     * 隐私模式
     */
    const val PRIVACY_MODE = 0x11

    /**
     * 熄屏
     */
    const val TURN_OFF_SCREEN = 0x21

    /**
     * 导航
     */
    const val NAVIGATION = 0x31

    val ANGLE_RETURN_SIGNAL: Int
        get() = CarCabinManager.ID_R_MIRROR_MEMORY_STS

    const val CUSTOM_KEYPAD = "CUSTOM_KEYPAD"

    /**
     * 桌面搜索
     */
    const val LAUNCHER_SEARCH = "LAUNCHER_SEARCH"

    /**
     * 跳转路径
     */
    val INTENT_PATH: String
        get() = "INTENT_PATH"

    val DIALOG_SERIAL: String
        get() = "type"

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

    val STEERING_CUSTOM_KEYPAD: String
        get() = "STEERING_CUSTOM_KEYPAD"

    val STEERING_HEATING_SETTING: String
        get() = "STEERING_HEATING_SETTING"

    val DEVICE_AUDIO_VOLUME: String
        get() = "DEVICE_AUDIO_VOLUME"

    val AMBIENT_LIGHTING_MODE: String
        get() = "AMBIENT_LIGHTING_MODE"

    val AMBIENT_LIGHTING_SETTING: String
        get() = "AMBIENT_LIGHTING_SETTING"

    /**
     * 黑夜模式
     */
    val DARK_BRIGHTNESS_LEVEL: String
        get() = "DARK_BRIGHTNESS_LEVEL"
    /**
     * 白天模式
     */
    val LIGHT_BRIGHTNESS_LEVEL: String
        get() = "LIGHT_BRIGHTNESS_LEVEL"

    val VCU_GENERAL_ROUTER: String
        get() = "com.chinatsp.vcu.actions.VCU_GENERAL_ROUTER"

    val VCU_AMBIENT_LIGHTING: String
        get() = "com.chinatsp.vcu.actions.VCU_AMBIENT_LIGHTING"

    val VCU_SCREEN_BRIGHTNESS: String
        get() = "com.chinatsp.vcu.actions.VCU_SCREEN_BRIGHTNESS"

    val VCU_AUDIO_VOLUME: String
        get() = "com.chinatsp.vcu.actions.VCU_AUDIO_VOLUME"

    val VCU_CUSTOM_KEYPAD: String
        get() = "com.chinatsp.vcu.actions.VCU_CUSTOM_KEYPAD"

    val EQ_SIZE: Int
        get() {
            return if (!VcuUtils.isAmplifier) 9 else 5
        }

    val EQ_LEVELS: IntArray
        get() {
            val eqIdArray = mutableListOf(CarAudioManager.EQ_AUDIO_VOICE_LEVEL1,
                CarAudioManager.EQ_AUDIO_VOICE_LEVEL2,
                CarAudioManager.EQ_AUDIO_VOICE_LEVEL3,
                CarAudioManager.EQ_AUDIO_VOICE_LEVEL4,
                CarAudioManager.EQ_AUDIO_VOICE_LEVEL5)
            if (!VcuUtils.isAmplifier) {
                eqIdArray.add(CarAudioManager.EQ_AUDIO_VOICE_LEVEL6)
                eqIdArray.add(CarAudioManager.EQ_AUDIO_VOICE_LEVEL7)
                eqIdArray.add(CarAudioManager.EQ_AUDIO_VOICE_LEVEL8)
                eqIdArray.add(CarAudioManager.EQ_AUDIO_VOICE_LEVEL9)
            }
            return eqIdArray.toIntArray()
        }

    val LIGHT_LEVEL: IntArray by lazy {
        intArrayOf(0x19, 0x33, 0x4C, 0x66, 0x7F, 0x99, 0xB2, 0xCC, 0xE5, 0xFF)
    }

}
