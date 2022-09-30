package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.media.AudioAttributes
import com.chinatsp.settinglib.bean.CanLocate
import com.chinatsp.settinglib.sign.Origin

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
    val def: Int = min,
    val get: CanLocate,
    val set: CanLocate = get,
) {
    /**
     * 氛围灯亮度
     * get -> 0x00:OFF; 0x01:grade1,20%; 0x02:grade2,40%; 0x03:grade3,60%; 0x04:grade4,80%; 0x05:grade5,100%; 0x06~0xF：Reserved
     * set -> 0x00:OFF; 0x01:grade1,20%; 0x02:grade2,40%; 0x03:grade3,60%; 0x04:grade4,80%; 0x05:grade5,100%; 0x06~0xF：Reserved
     */
    AMBIENT_LIGHT_BRIGHTNESS(
        min = 0x01,
        max = 0x05,
        def = 0x03,
        get = CanLocate(signal = CarCabinManager.ID_ALC_AL_RESPONSE_BRIGHTNESS),
        set = CanLocate(signal = CarCabinManager.ID_ALC_HUM_ALC_BRIGHTNESS_GRADE)
    ),

    /**
     * 氛围灯颜色
     * get -> 全车氛围灯颜色反馈 0x0: Inactive; 0x1~0x40: Color 1~64; 0x41~0x7F: Reserved
     * set -> 全车氛围灯颜色设置 0x0: Inactive; 0x1~0x40: Color 1~64; 0x41~0x7F: Reserved
     */
    AMBIENT_LIGHT_COLOR(
        min = 0x01,
        max = 0x40,
        def = 0x10,
        get = CanLocate(signal = CarCabinManager.ID_ALC_AL_RESPONSE_COLOR),
        set = CanLocate(signal = CarCabinManager.ID_ALC_HUM_ALC_COLOR_ADJUST)
    ),

    /**
     * 主机屏亮度
     */
    HOST_SCREEN_BRIGHTNESS(
        min = 0x01,
        max = 0x0A,
        def = 0x05,
        get = CanLocate(signal = -1),
        set = CanLocate(signal = -1)
    ),

    /**
     * 仪表屏亮度
     * set -> 仪表背光等级设置[0x1,0,0x0,0xB]
     *        0x0: Inactive; 0x1: Level 1; 0x2: Level 2; 0x3: Level 3; 0x4: Level 4; 0x5: Level 5
     *        0x6: Level 6; 0x7: Level 7; 0x8: Level 8; 0x9: Level 9; 0xA: Level 10; 0xB~0xF: Reserved
     * get -> 0x0: Inactive; 0x1: Level 1; 0x2: Level 2; 0x3: Level 3; 0x4: Level 4; 0x5: Level 5
     *        0x6: Level 6; 0x7: Level 7; 0x8: Level 8; 0x9: Level 9; 0xA: Level 10; 0xB~0xF: Reserved
     */
    METER_SCREEN_BRIGHTNESS(
        min = 0x01,
        max = 0x0A,
        def = 0x05,
        get = CanLocate(signal = CarCabinManager.ID_ICM_SCR_BRI_LEVEL_STS),
        set = CanLocate(signal = CarCabinManager.ID_ALC_ICM_SCR_BRI_LEVEL)
    ),

    /**
     * 空调屏亮度
     * set -> VCS背光等级设置[0x1,0,0x0,0xB]
     *        0x0: Inactive; 0x1: Level 1; 0x2: Level 2; 0x3: Level 3; 0x4: Level 4; 0x5: Level 5
     *        0x6: Level 6; 0x7: Level 7; 0x8: Level 8; 0x9: Level 9; 0xA: Level 10; 0xB~0xF: Reserved
     * get -> 0x0: Inactive; 0x1: Level 1; 0x2: Level 2; 0x3: Level 3; 0x4: Level 4; 0x5: Level 5
     *        0x6: Level 6; 0x7: Level 7; 0x8: Level 8; 0x9: Level 9; 0xA: Level 10; 0xB~0xF: Reserved
     */
    CONDITIONER_SCREEN_BRIGHTNESS(
        min = 0x01,
        max = 0x0A,
        def = 0x05,
        get = CanLocate(signal = CarCabinManager.ID_VCS_SCR_BRI_LEVEL_STS),
        set = CanLocate(signal = CarCabinManager.ID_ALC_VCS_SCR_BRI_LEVEL)
    ),

    /**
     * 软开关背光亮度
     * 整车开关背光等级设置[0x1,0,0x0,0xFF]
     * set -> 0x0: Inactive 0x19: Level 1 0x33: Level 2 0x4C: Level 3 0x66: Level 4 0x7F: Level 5 0x99: Level 6 0xB2: Level 7 0xCC: Level 8 0xE5: Level 9 0xFF: Level 10
     * get -> 0x0: Dark 0x19: Level 1 0x33: Level 2 0x4C: Level 3 0x66: Level 4 0x7F: Level 5 0x99: Level 6 0xB2: Level 7 0xCC: Level 8 0xE5: Level 9 0xFF: Level 10
     */
    SWITCH_BACKLIGHT_BRIGHTNESS(
        min = 0x01,
        max = 0x0A,
        def = 0x05,
        get = CanLocate(signal = CarCabinManager.ID_BCM_BACKLIGHT_LEVEL_STATUS),
        set = CanLocate(signal = CarCabinManager.ID_ALC_HUM_BACKLIGHT_LEVEL)
    ),

    /**
     * 座椅自动加热起始温度 (座椅自动加热温度（高配HUM发送，低配HUM不发送此信号）)
     * set -> 【设置】0x0~0xA: 0~10℃  0xB~0xF: Reserved
     */
    SEAT_ONSET_TEMPERATURE(
        min = 0x00,
        max = 0x0A,
        def = 0x05,
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
        def = 0x05,
        get = CanLocate(signal = -1),
        set = CanLocate(signal = CarCabinManager.ID_SWS_AUTO_HEAT_TEMP)
    ),

    NAVI(
        min = 0x00,
        max = 0x1E,
        def = 0x0C,
        get = CanLocate(
            origin = Origin.ATTR,
            signal = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE
        )
    ),

    VOICE(
        min = 0x00,
        max = 0x1E,
        def = 0x0C,
        get = CanLocate(origin = Origin.ATTR, signal = AudioAttributes.USAGE_ASSISTANT)
    ),
    MEDIA(
        min = 0x00,
        max = 0x1E,
        def = 0x0C,
        get = CanLocate(origin = Origin.ATTR, signal = AudioAttributes.USAGE_MEDIA)
    ),
    PHONE(
        min = 0x00,
        max = 0x1E,
        def = 0x0C,
        get = CanLocate(origin = Origin.ATTR, signal = AudioAttributes.USAGE_VOICE_COMMUNICATION)
    ),
    SYSTEM(
        min = 0x00,
        max = 0x1E,
        def = 0x0C,
        get = CanLocate(origin = Origin.ATTR, signal = AudioAttributes.USAGE_NOTIFICATION)
    ),

    /**
     * 电动尾门依靠位置 from 0 to 100
     */
    TRUNK_STOP_POSITION(
        min = 50,
        max = 100,
        def = 75,
        get = CanLocate(signal = CarCabinManager.ID_PTM_POSITION_STATUS),
        set = CanLocate(signal = CarCabinManager.ID_PTM_STP_POSN_SET)
    );


    fun isValid(value: Int): Boolean = value in min..max

}