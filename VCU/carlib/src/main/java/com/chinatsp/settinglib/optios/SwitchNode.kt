package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.mcu.CarMcuManager
import android.car.media.CarAudioManager
import com.chinatsp.settinglib.bean.Norm
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 10:53
 * @desc   :
 * @version: 1.0
 */
enum class SwitchNode(
    val get: Norm,
    val set: Norm,
    val default: Boolean = true,
    val careOn: Boolean = true,//当此值为true表示只有当值等于 get的on时才当为开，当此值为false表示只要值不等于get的off时就当为开
    val area: Area = Area.GLOBAL,
) {

    //-------------------座舱--开始-------------------
    /**
     * 座舱--方向盘--方向盘加热设置 [0x1,0,0x0,0x3]
     * set ->【设置】0x0: Inactive  0x1: On Press  0x2: OFF  0x3: Reserved
     * get: 0x0:not heating; 0x1:heating
     */
    DRIVE_WHEEL_AUTO_HEAT(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_SWS_AUTO_HEAT_SW),
        default = true
    ),

//    /**
//     * 座舱--安全--设防提示音/锁车提示音
//     * set -> 0x1: No sound(default)   0x2: Sound
//     * get -> 0x0: Inactive 0x1: No sound(default) 0x2: Sound
//     */
//    DRIVE_SAFE_FORTIFY_SOUND(
//        get = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE),
//        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_SET),
//        default = false
//    ),

    /**
     * 座舱--安全--视频安全模式(此开关应用自行控制，不走CAN信号)
     * 0x1: No sound(default)   0x2: Sound
     */
    DRIVE_SAFE_VIDEO_PLAYING(
        get = Norm(on = 0x01, off = 0x00),
        set = Norm(on = 0x01, off = 0x00),
        default = false
    ),

    /**
     * 座舱--空调--空调自干燥
     * set -> 自干燥使能开关 x0: Inactive；0x1: Enabled； 0x2: Disabled；0x3: Reserved
     * get -> self-desiccation 自干燥功能状态显示 0x0:OFF 0x1:ON
     */
    AC_AUTO_ARID(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ACSELFSTSDISP),
        set = Norm(on = 0x1, off = 0x2, origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT),
        default = true
    ),

    /**
     * 座舱--空调--自动除雾
     * set -> 前除霜if not set ,the value of signal is
    8        0x0(inactive) 0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
     * get -> 前除霜显示图标 0x0: Not display; 0x1: Display; 0x2: Reserved; 0x3: Error
     */
    AC_AUTO_DEMIST(
        get = Norm(on = 0x1, off = 0x0, origin = Origin.HVAC,
            signal = CarHvacManager.ID_HAVC_AC_DIS_DEFROST),
        set = Norm(on = 0x1, off = 0x2, origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST),
        default = false
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * 座舱--空调--预通风功能
     * set -> 解锁主动换气使能开关  0x0: Inactive  0x1: Enabled  0x2: Disabled 0x3: Reserved
     * get -> 解锁预通风功能开启状态 0x0:OFF 0x1:ON
     */
    AC_ADVANCE_WIND(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ACPREVENTNDISP),
        set = Norm(on = 0x1, off = 0x2, origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE),
        default = true
    ),

    /**
     * 座舱--座椅--主驾迎宾
     * set -> int类型数据 座椅迎宾开关 0x0: Inactive; 0x1: Enabled; 0x2: Disabled; 0x3: Reserved
     * get -> 0x0：Disable 0x1：Enable
     */
    MAIN_SEAT_WELCOME(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_SEAT_WELCOME_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_SEAT_WELCOME_EN),
        default = true
    ),

    /**
     * 座舱--座椅--副驾迎宾
     * get -> 0x0：Disable 0x1：Enable
     * set -> HUM_PASS_SEAT_WELCOME_ENABLE 副驾迎宾软开关  0x0: Inactive; 0x1: Enable; 0x2: Disable; 0x3: Not used
     */
    FORK_SEAT_WELCOME(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_MCU_PASS_SEAT_WELCOME_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_PASS_SEAT_WELCOME_ENABLE),
        default = true
    ),

    /**
     * 座舱--座椅--座椅加热 (座椅自动加热开关（高配HUM发送，低配HUM不发送此信号）)(no signal)
     * set -> 【设置】0x0: Inactive  0x1: On Press  0x2: OFF  0x3: Reserved
     * get -> int类型数据
     *        0x0:Inactive; 0x1:OFF(default) 0x2:Level 1;
     *        0x3:Level 2; 0x4:Level 3; 0x5:0x6:0x7:reserved
     */
    SEAT_HEAT_ALL(
        get = Norm(on = 0x2, off = 0x1, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_DSM_AUTO_HEAT_SW),
        default = true,
        careOn = false
    ),

    /**
     * 座舱--座椅--座椅加热 (前左)
     */
    SEAT_HEAT_F_L(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 座舱--座椅--座椅加热 (前右)
     */
    SEAT_HEAT_F_R(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 座舱--座椅--座椅加热 (后左)
     */
    SEAT_HEAT_T_L(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 座舱--座椅--座椅加热 (后右)
     */
    SEAT_HEAT_T_R(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 座舱--其它--拖车提醒 此开关项走的是TBox信号而非CAN信号，所以需要特殊处理
     * get -> 拖车开关：0：Inactive  1：ON  2：OFF
     * set -> 拖车开关：0：Inactive  1：ON  2：OFF
     */
    DRIVE_TRAILER_REMIND(
        get = Norm(on = 0x1, off = 0x2, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = false
    ),

    /**
     * 座舱--其它--蓄电池优化
     * set -> 整车低压能量管理开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: ON 0x2: OFF 0x3: Invalid
     * get -> 0x0:LVPM Enable; 0x1:IBS Error; 0x2:IBS dismatch; 0x3:Invalid(no ibs);
     *        0x4:LVPM Disenabled by HUM/APP; 0x05：LVPM disabled by OTA
     */
    DRIVE_BATTERY_OPTIMIZE(
        get = Norm(on = 0x0, off = 0x4, signal = CarCabinManager.ID_LOU_PWR_MNGT_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_LOU_PWR_MNG_SWT),
        default = false
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x1 || value == 0x2 || value == 0x3
        }
    },

    /**
     * 座舱--其它--无线充电
     * set -> 0x1: OFF    0x2: ON(default)
     * get -> 0x0:OFF 0x1:ON 0x2~0x3:Reserved
     */
    DRIVE_WIRELESS_CHARGING(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_WCM_SOFT_SW_STATE),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_WCM_SWT),
        default = false
    ),

    /**
     * 座舱--其它--无线充电灯
     * set -> int类型数据 氛围灯Console软开关 0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Reserved
     * get -> 氛围灯Consle软开关状态反馈 0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Reserved
     */
    DRIVE_WIRELESS_CHARGING_LAMP(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_BDC_ALC_CONSLAMPSWT_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_CONSLAMP_SWT),
        default = true
    ),
    //-------------------座舱--结束-------------------

    //----------------车辆音效 开始--------------
    /**
     * 车辆音效--声音--系统提示音
     */
    AUDIO_SOUND_TONE(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车辆音效--声音--速度音量补偿
     * set -> speed音量随速调节开关[0x1,0,0x0,0x2] 0x0: Inactive 0x1: ON(default) 0x2: OFF 0x3: Reserved
     * get -> Volume switch status with speed音量随速调节开关状态 0x0: OFF 0x1: ON
     */
    SPEED_VOLUME_OFFSET(
        get = Norm(on = 0x01, off = 0x00, signal = CarCabinManager.ID_AMP_VOL_SPEED_SW_STS),
        set = Norm(on = 0x01, off = 0x02, signal = CarCabinManager.ID_HUM_VOL_SPEED_SW),
        default = false
    ),

    SPEED_VOLUME_OFFSET_INSERT(
        get = Norm(on = 0x01, off = 0x02, origin = Origin.MCU,
            signal = CarMcuManager.ID_MCU_RET_AUDIO_INFO),
        set = Norm(on = 0x01, off = 0x02, signal = CarCabinManager.ID_SETVOLUMESPEED),
        default = false
    ),

    /**
     * 车辆音效--声音--华为音效
     */
    AUDIO_SOUND_HUAWEI(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车辆音效--声音--响度控制
     * set -> 响度开关[0x1,0,0x0,0x2] 0x0: Inactive 0x1: ON(default) 0x2: OFF 0x3: Reserved
     * get -> Loudness switch status响度开关状态 0x0: OFF 0x1: ON
     */
    AUDIO_SOUND_LOUDNESS(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_AMP_LOUD_SW_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_LOUD_SW),
        default = true
    ),

    /**
     * 车辆音效--音效--环境音效
     * set -> 环境音效开关[0x1,0,0x0,0x2] 0x0:Inactive; 0x1:ON(default); 0x2:OFF 0x3:Reserved
     * get -> 环境音效开关状态 0x0:OFF; 0x1:ON
     */
    AUDIO_ENVI_AUDIO(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_AMP_ATMOS_MOD_SW_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ATMOS_MOD_SW),
        default = true
    ),


    /**
     * 车辆音效--声音--触摸提示音
     */
    TOUCH_PROMPT_TONE(
        get = Norm(on = CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE,
            off = CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE, origin = Origin.SPECIAL),
        set = Norm(on = CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE,
            off = CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE, origin = Origin.SPECIAL),
        default = false,
        careOn = false
    ) {
        override fun isOn(value: Int): Boolean {
            return value != get.off
        }

        override fun isInvalid(value: Int): Boolean {
            return value == CarAudioManager.BEEP_VOLUME_LEVEL_LOW
                    || value == CarAudioManager.BEEP_VOLUME_LEVEL_HIGH
        }
    },

    //----------------车辆音效 结束--------------

    //----------------车门与车窗 开始--------------
    /**
     * 车门车窗--车门--车门智能进入
     * set -> 0x1: Enabled; 0x2: Disabled
     * get -> 0x0: No anthentication or failure; 0x1: Anthentication success
     */


    DOOR_SMART_ENTER(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_SMART_ENTRY_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AVN_SMART_ENTRY_EN),
        default = true
    ),

    /**
     * 车门车窗--车窗--遥控升窗/降窗
     * get -> 遥控升降窗状态反馈 0x0: Initializing  0x1: On  0x2: Off   0x3: Invalid
     * set -> 遥控升降窗软开关 0x0: Inactive; 0x1: Enabled; 0x2: Disabled; 0x3: Reserved
     */
    WIN_REMOTE_CONTROL(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_REMOTE_WINDOW_RISE_FALL_STATES),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_REMOTE_WINDOW_RISE_FALL_SW),
        default = false
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * 车门车窗--车窗--雨天自动关窗 (状态下发)
     * set -> 0x1: Disable    0x2: Enable
     * get -> 0x0: Disable    0x1: Enable
     */
    WIN_CLOSE_WHILE_RAIN(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_AVN_RAIN_WIN_CLOSE_FUN_CFG_SET),
        default = false
    ),

    /**
     * 车门车窗--车窗--锁车自动关窗
     * set -> 0x1: No action when locking（default）  0x2: Close windows when locking doors
     * get -> 0x0: No action when locking（default）  0x1: close windows when locking doors
     */
    WIN_CLOSE_FOLLOW_LOCK(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_AVN_WIN_CLOSE_FUN_CFG_SET),
        default = false
    ),


    /**
     * 车门车窗--车窗--雨刮维修
     * get -> 前雨刮维修模式状态; 0x0:Initializing; 0x1 maitenance mode; 0x2:normal mode; 0x3:Invalid
     * set -> 前雨刮维修模式开关 0x0: Invalid; 0x1: Initializing; 0x2: maitenance mode; 0x3: normal mode; 0x4: Invalid
     *
     */
    RAIN_WIPER_REPAIR(
        get = Norm(on = 0x1, off = 0x2,
            signal = CarCabinManager.ID_FRONT_WIPER_MAINTENNANCE_STATES),
        set = Norm(on = 0x2, off = 0x3, signal = CarCabinManager.ID_FRONT_WIPER_MAINTENNANCE_SW),
        default = false
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x0 || value == 0x3
        }
    },

    /**
     * 车门车窗--电动尾门--电动尾门电动功能
     * set -> 0x1: ON 0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    AS_STERN_ELECTRIC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_POWER_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_POWER_ENABLE_PTM_SET),
        default = true
    ),

//    /**
//     * 挡位
//     * set ->
//     * get -> 0x0: Initial/Interval ("Interval" is only used for CVT) 0x1: P (Park) 0x2: R (reverse)
//     *        0x3: N (Neutral) 0x4: D (Drive) 0xB: M(Manual) 0xC: L (Reserved) 0xD: S
//     */
//    GEARS(
//        get = Norm(on = 0x1, signal = CarCabinManager.ID_TCU_SELECTED_GEAR),
//        set = Norm(),
//        default = true
//    ),

    /**
     * 车门车窗--电动尾门--灯光闪烁报警
     * set -> 0x1: ON    0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    STERN_LIGHT_ALARM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_LIGHT_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_LIGHT_ENABLE_PTM_SET),
        default = true
    ),

    /**
     * 车门车窗--电动尾门--蜂鸣器报警开关
     * set -> 0x1: ON    0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    STERN_AUDIO_ALARM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_SOUND_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_SOUND_ENABLE_PTM_SET),
        default = true
    ),

    /**
     * 车门车窗--外后视镜--后视镜自动折叠
     * set -> 0x1: Enable(default)   0x2: Disable 0x3: Not used
     * get -> 0x0: Inactive 0x1: Enable(default) 0x2: Disable 0x3: Reserved
     */
    BACK_MIRROR_FOLD(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_MIRROR_FADE_IN_OUT_STATUE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_MIRROR_FADE_IN_OUT_SET),
//        inactive = intArrayOf(0x0),
        default = true
    ),

    /**
     * 车门车窗--外后视镜--外后视镜下翻
     * set -> int类型数据 0x0: Inactive; 0x1: Enable; 0x2: Disable; 0x3: Not used
     * get -> 0x0：Disable 0x1：Enable
     */
    BACK_MIRROR_DOWN(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_R_MIRROR_SEE_GROUND_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_MIRROR_SEE_G_ENABLE),
        default = true
    ),
    //----------------车门与车窗 结束--------------


    //----------------------驾驶辅助 开始-------------------
    /**
     * 驾驶辅助--智能巡航--智能巡航辅助 ACC
     * set -> IACC or ACC function enable switch,if not set 'IACC_FUNC_ENABLEE',
     *        the value of signal is 0x0(inactive).It is ACC function for C53F.[0x1,0,0x0,0x3]
     *        0x0: Inactive; 0x1: Enable(default); 0x2: Disable; 0x3: Reserved
     * get -> Indacating IACC or ACC function is enabled or disabled. 0x0:Disable 0x1:Enable
     */
    ADAS_IACC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_IACC_FUNC_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_IACC_FUNC_EN),
        default = true
    ),

    /**
     * 驾驶辅助--智能巡航--目标提示音
     * set -> object distingguish and disappear switch,if not set'OBJ_DETECTION',
     *        the value of signal is 0x0(inactive)[0x1,0,0x0,0x5]
     *        0x0: Inactive； 0x1: Detect warning
     *        0x2: Disappare warning； 0x3: Detect and disappear warning(default)
     *        0x4: Warning off；0x5~0x7:Reserved
     */
//    ADAS_TARGET_PROMPT(
//        get = Norm(on = 0x1, off = 0x0, signal = -1),
//        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_OBJ_DETECTION_SWT),
//        default = true
//    ),

    /**
     * 驾驶辅助--智能巡航--前车驶离提示
     * get -> Response for FRONT_VEHICLE_DRIVE_AWAY
     *        0x0: Inactive
     *        0x1: Warning on(default)
     *        0x2: Warning off
     *        0x3: Reserved
     * set -> front vehicle drive away switch,if not set'FRONT_VEHICLE_DRIVE_AWAY',
     *        the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        0x0: Inactive; 0x1: Warning on(default); 0x2: Warning off; 0x3: Reserved
     */
    ADAS_LIMBER_LEAVE(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_FRONT_VEHICLE_DRIVE_AWAY_RES),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_FRONT_VEHICLE_DRIVE_AWAY_SWT),
//        inactive = intArrayOf(0x0),
        default = true
    ),

    /**
     * 驾驶辅助--前身辅助--前车碰撞预警
     * set -> not used in C40D to switch on or off FCW warning function.The value of signal is 0x1 when every Igon .
     *        if not set 'FCW_SWITCH' ,the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        0x0: Inactive; 0x1: On(default); 0x2: Off; 0x3: Reserved
     * get -> FCW status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved
     */
    ADAS_FCW(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_FCW_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_FCW_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--前身辅助--自动紧急制动
     * set -> 0x0: Inactive[0x1,0,0x0,0x3] 0x1: On(default); 0x2: Off; 0x3: Reserved
     * get -> AEB status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved
     */
    ADAS_AEB(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_AEB_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AEB_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--车道辅助--车道辅助系统
     * get -> Operation mode of LDW/RDP/LKS. The default value is 0x1 LDW in C53F, 0x3 LKS in C62X.
     *        0x0:Initial 0x1:LDW 0x2:RDP 0x3:LKS
     * set -> LDW/RDP/LKS function enable switch,if not set 'LDW_RDP_LKS_FUNC_ENABLE',the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        C53F send the signal 0x0 all the time
     *        0x0: Inactive
     *        0x1: LDW Enable
     *        0x2: RDP Enable
     *        0x3: LKS Enable（C62 default）
     *
     */
    ADAS_LANE_ASSIST( //无此开关项
//        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_LANE_ASSIT_TYPE),
        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_LDW_RDP_LKS_STATUS),
        set = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN),
        default = true,
    ){
        //LDW/RDP/LKS status. MPC will save the status, while the AVN will not. 0x0:Off 0x1:Standby 0x2:Active 0x3:Temporary failure 0x4:Camera blocked 0x5:Permanent failure 0x6:Reserved 0x7:Reserved
        override fun isOn(value: Int): Boolean {
            return value == 0x1 || value == 0x2 || value == 0x3
        }

        override fun isValid(value: Int): Boolean {
            return value == 0x0 || value == 0x1 || value == 0x2 || value == 0x3 || value == 0x4 || value == 0x5
        }
//        override fun isOn(value: Int): Boolean {
//            return value == 0x1 || value == 0x2 || value == 0x3
//        }
//
//        override fun isValid(value: Int): Boolean {
//            return value == 0x0 || value == 0x1 || value == 0x2 || value == 0x3
//        }
     },

    /**
     * 驾驶辅助--交通标志--速度限制提醒 SLA
     * set -> not used in C40D/C53F TSR function switch signal.if not set 'TSR_SWITCH' ,
     *        the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        0x0: Inacitve；0x1: UI warning；0x2: UI and SPEAKER warning(default)；0x3: OFF
     * get -> Operation status of traffic sign functions.
     *        0x0:Off
     *        0x1:Operating Fusion mode(reserved) ON
     *        0x2:Operating Vision only mode ON
     *        0x3:Operating Navigation onlymode(reserved) ON
     *        0x4:Temporary failure
     *        0x5:Camera blocked
     *        0x6:Permanent failure
     *        0x7:TSR not configured
     */
    ADAS_TSR(
        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_TSR_OPERATING_STATUS),
        set = Norm(on = 0x2, off = 0x3, signal = CarCabinManager.ID_TSR_SWT),
        default = true,
        careOn = false
    ) {
        override fun isOn(value: Int): Boolean {
            return value == 0x1 || value == 0x2 || value == 0x3
        }

        override fun isInvalid(value: Int): Boolean {
            return value == 0x4 || value == 0x5 || value == 0x6 || value == 0x7
        }
    },

    /**
     * 驾驶辅助--灯光辅助--智能远光灯辅助 HMA
     * get -> HMA status
     *        0x0:HMA OFF; 0x1:HMA passive; 0x2:HMA active; 0x3:Temporary failure
     *        0x4:Camera blocked; 0x5:Permanent failure; 0x6: Reserved; 0x7: HMA not configured
     * set -> not used in C40D/C53F HMA funtion switch signal,if not set 'HMAOnOffReq' ,
     *        the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Reserved
     * 一个是HMA_ON_OFF_REQ，一个是AVN_HMA_ON_OFF_STS，
     */
    ADAS_HMA(
        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_HMA_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HMA_ON_OFF_SWT),
        default = false,
        careOn = false
    ) {

        override fun isOn(value: Int): Boolean {
            return value == 0x1 || value == 0x2
        }

        override fun isPopWindow(value: Int): Boolean {
            return 0x3 == value
        }

        override fun isInvalid(value: Int): Boolean {
            return value == 0x3 || value == 0x4 || value == 0x5 || value == 0x6 || value == 0x7
        }
    },

    /**
     * 驾驶辅助--侧后辅助--开门预警 DOW
     * get -> 开门预警功能状态信号(高配的APA发送该信号，低配的AVM不发送)
     *        0x0: Not available(缺省); 0x1: DOW off（关闭）
     *        0x2: DOW standby（待机）; 0x3: DOW active（开启）;0x4: DOW failed（错误）
     *        0x5: CAMERA blocked（遮挡）;0x6~0x7: reserved（预留）
     *        set -> DOW开关 0x0: Inactive; 0x1: ON; 0x2: OFF;0x3: Invalid
     * set -> DOW开关  0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Invalid
     */
    ADAS_DOW(
        get = Norm(on = 0x3, off = 0x1, signal = CarCabinManager.ID_AVM_DOW_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_AVM_DOW_SWT),
        default = true,
        careOn = false
    ) {
        override fun isOn(value: Int): Boolean {
            return 0x2 == value || 0x3 == value
        }

        override fun isValid(value: Int): Boolean {
            return 0x1 == value || 0x2 == value || 0x3 == value
        }

        override fun isInvalid(value: Int): Boolean {
            return value == 0x0 || value == 0x4 || value == 0x5 || value == 0x6
        }

        override fun isPopWindow(value: Int): Boolean {
            return 0x4 == value
        }
    },

    /**
     * 驾驶辅助--侧后辅助--盲区监测 BSD
     * set -> BSD Switch. BsdSwt
     *        0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Invalid
     * get -> Set the BSD switch of AVM,used for international car.
     *        0x0: Inactive
     *        0x1: On
     *        0x2: Off
     *        0x3: Invalid
     */
    ADAS_BSD(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AVM_BSD_SWT_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_BSD_SWT),
        default = true
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * 驾驶辅助--侧后辅助--盲区摄像头
     * get -> BSD Display status; 0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Invalid
     * set -> 盲区单侧影像显示开关 0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Invalid
     */
    ADAS_BSC(
//        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AVM_BSD_DISP_STS),
//        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_BSD_DISP_SWT),
        get = Norm(on = 0x1, off = 0x2, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * get -> MEB switch status 对应HUMMEB功能开关信号
     *        0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Invalid
     * set -》MEB功能开关   0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Invalid
     */
    ADAS_MEB(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_MEB_SWT_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_MEB_SWT),
        default = true
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * 驾驶辅助--侧后辅助--辅助线
     */
    ADAS_GUIDES(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x0, signal = -1),
        default = true
    ),
    //----------------------驾驶辅助 结束-------------------

    //-------------------灯光设置--开始-------------------
    /**
     * 灯光设置--灯光--车内迎宾灯
     */
    LIGHT_INSIDE_MEET(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 灯光设置--灯光--车外迎宾灯
     */
    LIGHT_OUTSIDE_MEET(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 灯光设置--灯光--外部灯光仪式感
     * set -> 外部灯光仪式感设置：车机发0x4 high表示开启，0x1表示关闭
     *        0x1: Disable; 0x2: low Varient(reserved);
     *        0x3: Middle Varient(reserved); 0x4: Hign Varient
     * get -> bdc回 0x0表示关，0x3表示开
     *        0x0:Disabled; 0x1:low Varient(reserved); 0x2:Middle Varient(reserved); 0x3:Hign Varient
     */
    LIGHT_CEREMONY_SENSE(
        get = Norm(on = 0x3, off = 0x0, signal = CarCabinManager.ID_BCM_EL_CERE_SENSE_STATUS),
        set = Norm(on = 0x4, off = 0x1, signal = CarCabinManager.ID_HMI_EL_CERE_SENSE_TYPE_SET),
//        inactive = intArrayOf(0x1, 0x2),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--前排氛围灯
     * get -> 前排氛围灯开关状态 0x0: Off; 0x1: On
     * set -> 前排氛围灯软开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: ON 0x2: OFF 0x3: Reserved
     */
    FRONT_AMBIENT_LIGHTING(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_FRONT_PART_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_FRONT_PART_SW),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--后排氛围灯
     * get -> 后排氛围灯开关状态 0x0: Off;  0x1: On
     * set -> 后排氛围灯软开关[0x1,0,0x0,0x3] x0: Inactive;  0x1: ON; 0x2: OFF;  0x3: Reserved
     */
    BACK_AMBIENT_LIGHTING(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_REAR_PART_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_REAR_PART_SW),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--开关门提醒
     * set -> 开关门提醒开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: Enabled 0x2: Disabled 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_DOOR_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_DR_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_DOOR_REMIND_ENABLE),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--开关锁提醒
     * set -> 开关锁提醒开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: Enabled 0x2: Disabled 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_LOCK_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_LUCK_SW_RESPONSE),
        set = Norm(
            on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_SW_LOCK_REMIND_ENABLE),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--亮度呼吸
     * set -> 亮度呼吸模式开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: On 0x2: OFF 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_BREATHE_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_BRIG_BREAT_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_BRIGHT_BREA_SW),
        default = true
    ),

    /**
     * 灯光设置--氛围灯--开门后方来车提醒
     * set -> int类型数据 0x0: Inactive 0x1: Enabled 0x2: Disabled 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_COMING_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_BSD_SW_REPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_BSD_REMIND_EN),
        default = true
    ),

    /**
     * 灯光设置--氛围灯--关联主题
     * set -> int类型数据 氛围灯关联主题开关[0x1,0,0x0,0x3] 0x0: Inactive 0x1: Enabled 0x2: Disabled 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_RELATED_TOPICS(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_THEME_SW_REPONS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_THEME_SW_EN),
        default = true
    ),

    /**
     * 灯光设置--氛围灯--智能模式
     * set -> 智能模式开关[0x1,0,0x0,0x3]  0x0: Inactive 0x1: On 0x2: OFF 0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_SMART_MODE(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_INTE_MODE_SW_RESPONSE),
        set = Norm(
            on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_INTELLIGENT_MODE_SW),
        area = Area.AREA_TYPE_GLOBAL,
        default = true
    ),

    /**
     * 灯光设置--氛围灯--色彩呼吸
     * set -> 色彩呼吸模式开关[0x1,0,0x0,0x3] 0x0: Inactive; 0x1: On; 0x2: OFF; 0x3: Reserved
     * get -> 0x0:OFF; 0x1:ON
     */
    COLOUR_BREATHE(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_COLOUR_BREAT_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_COLOUR_BREAT_SW),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--音乐律动
     * set -> 音乐律动模式开关[0x1,0,0x0,0x3] 0x0: Inactive; 0x1: On; 0x2: OFF; 0x3: Reserved
     * get -> 0x0:OFF; 0x1:ON
     */
    MUSIC_RHYTHM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_MUSIC_RHY_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_MUSIC_RHY_SW),
        default = false
    ),

    /**
     * 灯光设置--氛围灯--车速律动
     * set -> 音乐律动模式开关[0x1,0,0x0,0x3] 0x0: Inactive; 0x1: On; 0x2: OFF; 0x3: Reserved
     * get -> 0x0:OFF; 0x1:ON
     */
    SPEED_RHYTHM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_VEH_SPD_RHY_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_VEHICLE_SP_RHY_SW),
        default = false
    ),
    //-------------------灯光设置--结束-------------------

    /**
     * 倒车后视镜下翻开关
     * set -> int类型数据; 0x0: Inactive; 0x1: Enable; 0x2: Disable
     * get -> 0x0：Disable 0x1：Enable
     */
    UNDER_REARVIEW_MIRROR(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_R_MIRROR_SEE_GROUND_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_MIRROR_SEE_G_ENABLE),
        default = false
    ),

    /**
     * 照地位置设置
     * set -> int类型数据 照地位置设置[0x1,-1,0x0,0x3] 0x0: Invalid; 0x1: Inactive; 0x2: Active
     */
    ACCORDING_POSITION(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_HUM_MIRROR_SEE_G_SET),
        default = false
    ),

    /**
     * 灯光自动模式
     * get -> auto模式反馈 0x0: Off; 0x1: On
     * set -> screen brightness auto mode 0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Not used
     */
    LIGHT_AUTO_MODE(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ICM_SCR_AUTO_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_MIRROR_SEE_G_SET),
        default = false
    ),

    /**
     * 前灯打开该位发出信号，指示在自动灯光模式下打开和关闭外部灯的命令，如果RLS_status状态不允许，请勿关闭前照灯。
    0x0: off
    0x1: on
     */
    HEAD_LINES(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_RLS_HEADLINES_ON),
        set = Norm(on = 0x1, off = 0x0, signal = -1),
        default = false
    ),

    /**
     * 白天黑夜模式
     * get -> Status of exterior lamp switch  0x0: Off; 0x1: Auto; 0x2: Park; 0x3: Low Beam
     */
//    DARK_LIGHT_MODE(
//        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_EXTERIOR_LAMP_SWITCH),
//        set = Norm(on = 0x1, off = 0x2, signal = -1),
//        default = false
//    ) {
//        override fun isValid(value: Int): Boolean {
//            return value == 0x0 || value == 0x2 || value == 0x3
//        }
//
//        override fun isOn(value: Int): Boolean {
//
//            return 0x2 == value || 0x3 == value
//        }
//    },

    /**
     * get -> 0x0: Not forbidden; 0x1: Forbidden
     * set -> 【设置】NFC读卡器禁用设置（高配HUM发送，低配HUM不发送此信号） [0x1,-1,0x0,0x4]
     * 0x0: Invalid; 0x1: defaule; 0x2: Forbidden; 0x3: Active; 0x4: Reverse
     */
    INNER_NFC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_INNER_NFC_READER_FORBIDDEN),
        set = Norm(on = 0x2, off = 0x3, signal = CarCabinManager.ID_HUM_NFC_FORBIDDEN_CMD),
        default = false
    ),

    /**
     * get -> 0x0: Not forbidden; 0x1: Forbidden
     * set -> 【设置】NFC读卡器禁用设置（高配HUM发送，低配HUM不发送此信号） [0x1,-1,0x0,0x4]
     * 0x0: Invalid; 0x1: defaule; 0x2: Forbidden; 0x3: Active; 0x4: Reverse
     */
    OUTER_NFC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_EXT_NFC_READER_FORBIDDEN),
        set = Norm(on = 0x2, off = 0x3, signal = CarCabinManager.ID_HUM_NFC_FORBIDDEN_CMD),
        default = false
    ),

    /**
     * 锁车成功提示音
     * set -> 0x1: No sound(default)   0x2: Sound
     * get -> 0x0: Inactive 0x1: No sound(default) 0x2: Sound; 0x3:invalid
     */
    LOCK_SUCCESS_AUDIO_HINT(
        get = Norm(on = 0x2, off = 0x1,
            signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE),//LOCK_SUCCESS_SOUND_STATUE
        set = Norm(on = 0x2, off = 0x1,
            signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_SET),//LOCK_SUCCESS_SOUND
        default = true
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    /**
     * 锁车失败提示音
     * set -> 0x1: No sound(default)   0x2: Sound
     * get -> 0x0: Inactive 0x1: No sound(default) 0x2: Sound; 0x3:invalid
     */
    LOCK_FAILED_AUDIO_HINT(
        get = Norm(on = 0x2, off = 0x1,
            signal = CarCabinManager.ID_LOCK_FAILED_SOUND_STATUE),//LOCK_FAILED_SOUND_STATUE
        set = Norm(on = 0x2, off = 0x1,
            signal = CarCabinManager.ID_LOCK_FAILED_SOUND_SET),//LOCK_FAILED_SOUND
        default = true
    ) {
        override fun isInvalid(value: Int): Boolean {
            return value == 0x3
        }
    },

    INVALID(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    );

    fun value(status: Boolean, isGet: Boolean = false): Int {
        return if (isGet) {
            if (status) get.on else get.off
        } else {
            if (status) set.on else set.off
        }
    }

//    fun isValid(value: Int) = isActive(value) or isInactive(value)

    open fun isValid(value: Int) = (get.on == value) or (get.off == value)

    /**
     * 判断传入值是否为需要置灰功能项
     */
//    open fun isInvalid(value: Int) = inactive?.contains(value) ?: false
    open fun isInvalid(value: Int) = false

    open fun isOn(value: Int) = if (careOn) get.on == value else get.off != value

    open fun isPopWindow(value: Int) = false

}