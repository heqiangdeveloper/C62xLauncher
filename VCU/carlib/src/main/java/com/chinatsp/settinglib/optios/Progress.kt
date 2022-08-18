package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.bean.CanLocate

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/10 10:50
 * @desc   :
 * @version: 1.0
 */
enum class Progress(
    var min: Int = 0,
    var max: Int = 10,
    val step: Int = 1,
    val get: CanLocate,
    val set: CanLocate
) {
    /**
     * 氛围灯亮度
     */
    AMBIENT_LIGHT_BRIGHTNESS(
        min = 0x00,
        max = 0x05,
        get = CanLocate(signal = CarCabinManager.ID_ALC_AL_RESPONSE_BRIGHTNESS),
        set = CanLocate(signal = CarCabinManager.ID_ALC_HUM_ALC_BRIGHTNESS_GRADE)
    ),

    /**
     * 氛围灯颜色
     */
    AMBIENT_LIGHT_COLOR(
        min = 0x01,
        max = 0x40,
        get = CanLocate(signal = CarCabinManager.ID_ALC_AL_RESPONSE_COLOR),
        set = CanLocate(signal = CarCabinManager.ID_ALC_HUM_ALC_COLOR_ADJUST)
    ),


    /**
     * 仪表屏亮度
     */
    METER_SCREEN_BRIGHTNESS(
        get = CanLocate(signal = -1),
        set = CanLocate(signal = -1)
    ),

    /**
     * 空调屏亮度
     */
    CONDITIONER_SCREEN_BRIGHTNESS(
        get = CanLocate(signal = -1),
        set = CanLocate(signal = -1)
    ),

    /**
     * 座椅自动加热起始温度 (座椅自动加热温度（高配HUM发送，低配HUM不发送此信号）)
     * set -> 【设置】0x0~0xA: 0~10℃  0xB~0xF: Reserved
     */
    SEAT_ONSET_TEMPERATURE(
        min = 0x00,
        max = 0x0A,
        get = CanLocate(signal = -1),
        set = CanLocate(signal = CarCabinManager.ID_DSM_AUTO_HEAT_TEMP)
    ),

    /**
     * 方向盘自动加热起始温度 (方向盘自动加热温度（高配HUM发送，低配HUM不发送此信号）)
     * set -> 【设置】0x0~0xA: 0~10℃  0xB~0xF: Reserved
     */
    STEERING_ONSET_TEMPERATURE(
        min = 0x00,
        max = 0x0A,
        get = CanLocate(signal = -1),
        set = CanLocate(signal = CarCabinManager.ID_SWS_AUTO_HEAT_TEMP)
    )
    ;

    fun isValid(value: Int): Boolean = value in min..max

}