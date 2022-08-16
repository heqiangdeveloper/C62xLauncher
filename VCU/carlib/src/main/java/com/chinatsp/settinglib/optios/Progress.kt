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
enum class Progress (
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
        min = 0x01,
        max = 0x06,
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
    );

    fun isValid(value: Int): Boolean = value in min..max

}