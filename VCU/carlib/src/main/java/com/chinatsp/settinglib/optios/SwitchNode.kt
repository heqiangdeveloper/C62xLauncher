package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 10:53
 * @desc   :
 * @version: 1.0
 */
enum class SwitchNode(val on: Int, val off: Int, val def: Int, val origin: SignalOrigin, val signal: Int, val area: Area = Area.GLOBAL) {

    /**
     * 方向盘加热设置[0x1,0,0x0,0x3]
     * 0x0: Inactive
     * 0x1: On
     * 0x2: Off
     * 0x3: Reserved
     */
    DRIVE_WHEEL_AUTO_HEAT(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_SWS_HEAT_SWT),

    /**
     * 设防提示音 开关
     * 0x1: No sound(default)   0x2: Sound
     */
    DRIVE_SAFE_FORTIFY_SOUND(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_LOCK_SUCCESS_SOUND_SET),

    /**
     * 视频安全模式 开关
     * 0x1: No sound(default)   0x2: Sound
     */
    DRIVE_SAFE_VIDEO_PLAYING(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 空调自干燥
     */
    AC_AUTO_ARID(0x01, 0x02, 0x01, SignalOrigin.HVAC_SIGNAL, CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT),

    /**
     * 自动除雾
     */
    AC_AUTO_DEMIST(0x01, 0x02, 0x01, SignalOrigin.HVAC_SIGNAL, CarHvacManager.ID_HVAC_AVN_KEY_DEFROST),

    /**
     * 预通风功能
     */
    AC_ADVANCE_WIND(0x01, 0x02, 0x01, SignalOrigin.HVAC_SIGNAL, CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE),

    //----------------车辆音效 开始--------------
    /**
     * 车辆音效-声音-系统提示音
     */
    AUDIO_SOUND_TONE(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    /**
     * 车辆音效-声音-速度音量补偿
     */
    AUDIO_SOUND_OFFSET(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    /**
     * 车辆音效-声音-华为音效
     */
    AUDIO_SOUND_HUAWEI(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    /**
     * 车辆音效-声音-响度控制
     */
    AUDIO_SOUND_LOUDNESS(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_LOUD_SW),


    //----------------车辆音效 结束--------------

    //----------------车门与车窗 开始--------------
    /**
     * 车门车窗-车门-车门智能进入
     */
    AS_SMART_ENTER_DOOR(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AVN_SMART_ENTRY_EN),

    /**
     * 车门车窗-车窗-雨天自动关窗 (状态下发)
     * 0x0: Inactive
     * 0x1: Disable
     * 0x2: Enable
     * 0x3: Invalid
     */
    AS_AUTO_CLOSE_WIN_IN_RAIN(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AVN_RAIN_WIN_CLOSE_FUN_CFG_SET),

    /**
     * 车门车窗-车窗-锁车自动关窗
     * 0x0: Inactive
     * 0x1: No action when locking
     * 0x2: Close windows when locking doors
     * 0x3: Invalid
     */
    AS_AUTO_CLOSE_WIN_AT_LOCK(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AVN_WIN_CLOSE_FUN_CFG_SET),

    /**
     * 车门车窗-车窗-遥控升窗/降窗 (状态下发)
     * 0x0: Inactive
     * 0x1: Disable
     * 0x2: Enable
     * 0x3: Invalid
     */
    AS_REMOTE_RISE_AND_FALL(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AVN_RAIN_WIN_CLOSE_FUN_CFG_SET),
    /**
     * 车门车窗-车窗-雨刮维修 (状态下发)
     * 0x0: Inactive
     * 0x1: Disable
     * 0x2: Enable
     * 0x3: Invalid
     */
    AS_RAIN_WIPER_REPAIR(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AVN_RAIN_WIN_CLOSE_FUN_CFG_SET),

    /**
     * 车门车窗-电动尾门-蜂鸣报警开关
     */
    AS_STERN_AUDIO_ALARM(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_SOUND_ENABLE_PTM_SET),

    /**
     * 车门车窗-电动尾门-灯光闪烁报警
     */
    AS_STERN_LIGHT_ALARM(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_SOUND_ENABLE_PTM_SET),
    /**
     * 车门车窗-电动尾门-电动尾门电动功能
     */
    AS_STERN_ELECTRIC(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_POWER_ENABLE_PTM_SET),

    /**
     * 车门车窗-外后视镜-后视镜自动折叠
     */
    AS_BACK_MIRROR_FOLD(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    //----------------车门与车窗 结束--------------


    /**
     * 行车-座椅-主驾迎宾
     */
    SEAT_MAIN_DRIVE_MEET(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_RESMOD_SW),
    /**
     * 行车-座椅-副驾迎宾
     */
    SEAT_FORK_DRIVE_MEET(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_RESMOD_SW),
    /**
     * 行车-座椅-座椅加热 (前左)
     */
    SEAT_HEAT_F_L(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_SEAT_HEAT_POS),

    /**
     * 行车-座椅-座椅加热 (前右)
     */
    SEAT_HEAT_F_R(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_SEAT_HEAT_POS),

    /**
     * 行车-座椅-座椅加热 (后左)
     */
    SEAT_HEAT_T_L(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_SEAT_HEAT_POS),

    /**
     * 行车-座椅-座椅加热 (后右)
     */
    SEAT_HEAT_T_R(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_SEAT_HEAT_POS),

    //----------------------驾驶辅助 开始-------------------
    /**
     * 驾驶辅助--智能巡航--智能巡航辅助 ACC
     */
    ADAS_CRUISE_ASSIST(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 驾驶辅助--智能巡航--目标提示音
     */
    ADAS_TARGET_PROMPT(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 驾驶辅助--智能巡航--前车驶离提示
     */
    ADAS_LIMBER_LEAVE(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 驾驶辅助--前身辅助-前车碰撞预警
     */
    ADAS_FCW(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_FCW_SWT),
    /**
     * 驾驶辅助--前身辅助-自动紧急制动
     */
    ADAS_AEB(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_AEB_SWT),

    /**
     * 驾驶辅助--车道辅助-车道辅助系统
     */
    ADAS_LANE_ASSIST(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, -1),


    /**
     * 驾驶辅助--交通标志--速度限制提醒 SLA
     * [0x1,-1,0x0,0x4] 0x0: Invalid 0x1: NOT overspeed 0x2: Overspeed
     */
    ADAS_SLA(0x02, 0x01, 0x02, SignalOrigin.CABIN_SIGNAL, CarCabinManager.ID_HUM_ICM_SPEEDLIMIT_STATS),
    /**
     * 驾驶辅助--灯光辅助--智能远光灯辅助 HMA
     *
     */
    ADAS_HMA(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 驾驶辅助--侧后辅助--开门预警 DOW
     */
    ADAS_DOW(0x02, 0x01, 0x02, SignalOrigin.CABIN_SIGNAL, -1),
    /**
     * 驾驶辅助--侧后辅助--盲区监测 BSD
     */
    ADAS_BSD(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 驾驶辅助--侧后辅助--盲区摄像头
     */
    ADAS_BSC(0x02, 0x01, 0x02, SignalOrigin.CABIN_SIGNAL, -1),
    /**
     * 驾驶辅助--侧后辅助--辅助线
     */
    ADAS_GUIDES(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    //----------------------驾驶辅助 结束-------------------

    //----------------行车 start ---------------------------------
    /**
     * 行车--其它--蓄电池优化
     */
    DRIVE_BATTERY_OPTIMIZE(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 行车--其它--无线充电
     */
    DRIVE_WIRELESS_CHARGING(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),

    /**
     * 行车--其它--无线充电灯
     */
    DRIVE_WIRELESS_CHARGING_LAMP(0x02, 0x01, 0x01, SignalOrigin.CABIN_SIGNAL, -1),
    //----------------行车 end ---------------------------------



    INVALID(0x01, 0x02, 0x01, SignalOrigin.CABIN_SIGNAL, CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT);

    fun obtainValue(status: Boolean): Int {
        return if (status) on else off
    }

    fun isValidValue(value: Int) = (on == value) or (off == value)

    fun isOn(value: Int) = on == value

    fun isOn() = on == def
}