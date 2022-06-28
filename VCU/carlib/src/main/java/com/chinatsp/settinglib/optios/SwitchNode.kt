package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.bean.Norm
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 10:53
 * @desc   :
 * @version: 1.0
 */
//enum class SwitchNode(val on: Int, val off: Int, val def: Int, val origin: SignalOrigin = SignalOrigin.CABIN_SIGNAL, val set: Int = -1, val get: Int = -1, val area: Area = Area.GLOBAL) {
enum class SwitchNode(
    val get: Norm,
    val set: Norm,
    val default: Boolean = true,
    val careOn: Boolean = true,//当此值为true表示只有当值等于 get的on时才当为开，当此值为false表示只要值不等于get的off时就当为开
    val area: Area = Area.GLOBAL
) {


    /**
     * 方向盘加热设置 [0x1,0,0x0,0x3]
     * set: 0x0: Inactive; 0x1: On; 0x2: Off; 0x3: Reserved
     * get: 0x0:not heating; 0x1:heating
     */
//    DRIVE_WHEEL_AUTO_HEAT(Norm(on = 1, off = 0, uon = 1, uoff = 2), set = CarCabinManager.ID_SWS_HEAT_SWT, get = CarCabinManager.ID_SWH_STATUS),
    DRIVE_WHEEL_AUTO_HEAT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_SWH_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_SWS_HEAT_SWT),
        default = true
    ),

    /**
     * 行车--安全--设防提示音 开关
     * set -> 0x1: No sound(default)   0x2: Sound
     * get -> 0x0: Inactive 0x1: No sound(default) 0x2: Sound
     */
    DRIVE_SAFE_FORTIFY_SOUND(
        get = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_SET),
        default = false
    ),

    /**
     * 视频安全模式 开关
     * 0x1: No sound(default)   0x2: Sound
     */
//    DRIVE_SAFE_VIDEO_PLAYING(Norm(), set = -1, get = -1),
    DRIVE_SAFE_VIDEO_PLAYING(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 空调自干燥
     * set ->
        自干燥使能开关Self-desiccation Switch
        0x0: Inactive
        0x1: Enabled
        0x2: Disabled
        0x3: Reserved
     * get -> self-desiccation 自干燥功能状态显示 0x0:ON 0x1:OFF
     */
//    AC_AUTO_ARID(Norm(), SignalOrigin.HVAC_SIGNAL, set = CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT),
    AC_AUTO_ARID(
        get = Norm(
            on = 0x0,
            off = 0x1,
            origin = Origin.CABIN,
            signal = CarCabinManager.ID_ACSELFSTSDISP
        ),
        set = Norm(
            on = 0x1, off = 0x2,
            origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_SELF_DESICAA_SWT
        )
    ),

    /**
     * 自动除雾
     * set -> 前除霜if not set ,the value of signal is 0x0(inactive)
        0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
     * get -> 前除霜显示图标 0x0: Not display; 0x1: Display; 0x2: Reserved; 0x3: Error
     */
//    AC_AUTO_DEMIST(Norm(on = 1, off = 0, uon = 1, uoff = 2, def = false), SignalOrigin.HVAC_SIGNAL, set = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST, get = CarHvacManager.ID_HAVC_AC_DIS_DEFROST),
    AC_AUTO_DEMIST(
        get = Norm(
            on = 0x1,
            off = 0x0,
            origin = Origin.HVAC,
            signal = CarHvacManager.ID_HAVC_AC_DIS_DEFROST
        ),
        set = Norm(
            on = 0x1,
            off = 0x2,
            origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST
        ),
        default = false
    ),

    /**
     * 预通风功能
     * set -> 解锁主动换气使能开关
        0x0: Inactive
        0x1: Enabled
        0x2: Disabled
        0x3: Reserved
     * get -> 解锁预通风功能开启状态 0x0:ON 0x1:OFF
     */
    AC_ADVANCE_WIND(
        get = Norm(
            on = 0x0,
            off = 0x1,
            origin = Origin.CABIN,
            signal = CarCabinManager.ID_ACPREVENTNDISP
        ),
        set = Norm(
            on = 0x1,
            off = 0x2,
            origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE
        ),
        default = false
    ),

    //----------------车辆音效 开始--------------
    /**
     * 车辆音效-声音-系统提示音
     */
    AUDIO_SOUND_TONE(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车辆音效-声音-速度音量补偿
     */
    SPEED_VOLUME_OFFSET(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车辆音效-声音-华为音效
     */
    AUDIO_SOUND_HUAWEI(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车辆音效-声音-响度控制
     * set -> Loudness switch响度开关[0x1,0,0x0,0x2] 0x0: Inactive 0x1: ON(default) 0x2: OFF 0x3: Reserved
     * get -> Loudness switch status响度开关状态 0x0: OFF 0x1: ON
     */
    AUDIO_SOUND_LOUDNESS(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_AMP_LOUD_SW_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_LOUD_SW),
        default = true
    ),


    //----------------车辆音效 结束--------------

    //----------------车门与车窗 开始--------------
    /**
     * 车门车窗-车门-车门智能进入
     * set -> 0x1: Enabled    0x2: Disabled
     * get -> 0x0: No anthentication or failure 0x1: Anthentication success
     */
    DOOR_SMART_ENTER(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_SMART_ENTRY_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AVN_SMART_ENTRY_EN),
        default = true
    ),

    /**
     * 车门车窗-车窗-雨天自动关窗 (状态下发)
     * set -> 0x1: Disable    0x2: Enable
     * get -> 0x0: Disable    0x1: Enable
     */
    WIN_CLOSE_WHILE_RAIN(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_AVN_RAIN_WIN_CLOSE_FUN_CFG_SET),
        default = false
    ),

    /**
     * 车门车窗-车窗-锁车自动关窗
     * set -> 0x1: No action when locking（default）  0x2: Close windows when locking doors
     * get -> 0x0: No action when locking（default）  0x1: close windows when locking doors
     */
    WIN_CLOSE_FOLLOW_LOCK(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_AVN_WIN_CLOSE_FUN_CFG_SET),
        default = false
    ),

    /**
     * 车门车窗-车窗-遥控升窗/降窗 (no signal)
     *
     */
    WIN_REMOTE_CONTROL(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车门车窗-车窗-雨刮维修 (no signal)
     *
     */
    RAIN_WIPER_REPAIR(
        get = Norm(),
        set = Norm(),
        default = false
    ),

    /**
     * 车门车窗-电动尾门-蜂鸣报警开关
     * set -> 0x1: ON    0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    STERN_AUDIO_ALARM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_SOUND_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_SOUND_ENABLE_PTM_SET),
        default = true
    ),

    /**
     * 车门车窗-电动尾门-灯光闪烁报警
     * set -> 0x1: ON    0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    STERN_LIGHT_ALARM(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_LIGHT_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_LIGHT_ENABLE_PTM_SET),
        default = true
    ),

    /**
     * 车门车窗-电动尾门-电动尾门电动功能
     * set -> 0x1: ON 0x2: OFF
     * get -> 0x0: Disable 0x1: Enable
     */
    AS_STERN_ELECTRIC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_PTM_POWER_ENABLE_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_POWER_ENABLE_PTM_SET),
        default = true
    ),

    /**
     * 车门车窗-外后视镜-后视镜自动折叠
     * set -> 0x1: Enable(default)   0x2: Disable 0x3: Not used
     * get -> 0x0: 0x0: Inactive 0x1: Enable(default) 0x2: Disable 0x3: Reserved
     */
    BACK_MIRROR_FOLD(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_MIRROR_FADE_IN_OUT_STATUE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_MIRROR_FADE_IN_OUT_SET),
        default = true
    ),
    //----------------车门与车窗 结束--------------


    /**
     * 行车-座椅-主驾迎宾
     * set ->
            int类型数据
            座椅迎宾开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: Enabled
            0x2: Disabled
            0x3: Reserved
     * get -> 0x0：Disable 0x1：Enable
     */
    SEAT_MAIN_DRIVE_MEET(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_SEAT_WELCOME_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_SEAT_WELCOME_EN),
        default = true
    ),

    /**
     * 行车-座椅-副驾迎宾
     */
    SEAT_FORK_DRIVE_MEET(
        get = Norm(),
        set = Norm(),
        default = true
    ),

    /**
     * 行车-座椅-座椅加热
     */
    SEAT_HEAT_ALL(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(on = 0x1, off = 0x2),
        default = true
    ),

    /**
     * 行车-座椅-座椅加热 (前左)
     */
    SEAT_HEAT_F_L(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 行车-座椅-座椅加热 (前右)
     */
    SEAT_HEAT_F_R(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 行车-座椅-座椅加热 (后左)
     */
    SEAT_HEAT_T_L(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    /**
     * 行车-座椅-座椅加热 (后右)
     */
    SEAT_HEAT_T_R(
        get = Norm(on = 0x1, off = 0x2),
        set = Norm(signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS),
        default = true
    ),

    //----------------------驾驶辅助 开始-------------------
    /**
     * 驾驶辅助--智能巡航--智能巡航辅助 ACC
     * set -> IACC or ACC function enable switch,if not set 'IACC_FUNC_ENABLEE',
     * the value of signal is 0x0(inactive).It is ACC function for C53F.[0x1,0,0x0,0x3]
    0x0: Inactive; 0x1: Enable(default); 0x2: Disable; 0x3: Reserved
     * get -> Indacating IACC or ACC function is enabled or disabled. 0x0:Disable 0x1:Enable
     */
    ADAS_IACC(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_IACC_FUNC_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_IACC_FUNC_EN),
        default = true
    ),

    /**
     * 驾驶辅助--智能巡航--目标提示音
     * set ->
            object distingguish and disappear switch,if not set'OBJ_DETECTION',the value of signal is 0x0(inactive)[0x1,0,0x0,0x5]
            0x0: Inactive
            0x1: Detect warning
            0x2: Disappare warning
            0x3: Detect and disappear warning(default)
            0x4: Warning off
            0x5~0x7:Reserved
     */
    ADAS_TARGET_PROMPT(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_OBJ_DETECTION_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--智能巡航--前车驶离提示
     * set -> front vehicle drive away switch,if not set'FRONT_VEHICLE_DRIVE_AWAY',
     * the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
        0x0: Inactive
        0x1: Warning on(default)
        0x2: Warning off
        0x3: Reserved
     */
    ADAS_LIMBER_LEAVE(
        //get 暂时没有找到中间件信号
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_FRONT_VEHICLE_DRIVE_AWAY_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--前身辅助-前车碰撞预警
     * set ->
            not used in C40D to switch on or off FCW warning function.The value of signal is 0x1 when every Igon .
            if not set 'FCW_SWITCH' ,the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: On(default)
            0x2: Off
            0x3: Reserved
     * get ->
            FCW status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved
     */
    ADAS_FCW(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_FCW_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_FCW_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--前身辅助-自动紧急制动
     * set ->
            0x0: Inactive[0x1,0,0x0,0x3]
            0x1: On(default)
            0x2: Off
            0x3: Reserved
     * get ->
            AEB status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved
     */
    ADAS_AEB(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_AEB_STATUS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AEB_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--车道辅助-车道辅助系统
     * get -> LDW/RDP/LKS status. MPC will save the status, while the AVN will not. 0x0:Off 0x1:Standby 0x2:Active 0x3:Temporary failure 0x4:Camera blocked 0x5:Permanent failure 0x6:Reserved 0x7:Reserved
     * set -> LDW/RDP/LKS function enable switch,if not set 'LDW_RDP_LKS_FUNC_ENABLE',the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
            C53F send the signal 0x0 all the time
            0x0: Inactive
            0x1: LDW Enable
            0x2: RDP Enable
            0x3: LKS Enable（C62 default）
     *
     */
    ADAS_LANE_ASSIST(
//        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_LDW_RDP_LKS_STATUS),
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_LANE_ASSIT_TYPE),
        set = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN),
        default = true,
        careOn = false
    ),


    /**
     * 驾驶辅助--交通标志--速度限制提醒 SLA
     * set ->
            not used in C40D/C53F
            TSR function switch signal.if not set 'TSR_SWITCH' ,the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
            0x0: Inacitve
            0x1: UI warning
            0x2: UI and SPEAKER warning(default)
            0x3: OFF
     * get ->
            Operation status of traffic sign functions.
            0x0:Off
            0x1:Operating Fusion mode(reserved)
            0x2:Operating Vision only mode
            0x3:Operating Navigation onlymode(reserved)
            0x4:Temporary failure
            0x5:Camera blocked
            0x6:Permanent failure
            0x7:TSR not configured
     */
    ADAS_TSR(
        get = Norm(on = 0x2, off = 0x0, signal = CarCabinManager.ID_TSR_OPERATING_STATUS),
        set = Norm(on = 0x2, off = 0x3, signal = CarCabinManager.ID_TSR_SWT),
        default = true,
        careOn = false
    ),
    /**
     * 驾驶辅助--灯光辅助--智能远光灯辅助 HMA
     *  not used in C40D/C53F
     *  HMA funtion switch signal,if not set 'HMAOnOffReq' ,the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *  0x0: Inactive
     *  0x1: On
     *  0x2: Off
     *  0x3: Reserved
     */
    ADAS_HMA(
        get = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_HMA_STATUS),
        set = Norm(on = 0x2, off = 0x1, signal = CarCabinManager.ID_HMA_ON_OFF_SWT),
        default = false
    ),

    /**
     * 驾驶辅助--侧后辅助--开门预警 DOW
     * get -> 开门预警功能状态信号(高配的APA发送该信号，低配的AVM不发送)
            0x0: Not available(缺省)
            0x1: DOW off（关闭）
            0x2: DOW standby（待机）
            0x3: DOW active（开启）
            0x4: DOW failed（错误）
            0x5: CAMERA blocked（遮挡）
            0x6~0x7: reserved（预留）
     * set -> DOW开关
            0x0: Inactive
            0x1: ON
            0x2: OFF
            0x3: Invalid
     */
    ADAS_DOW(
        get = Norm(on = 0x3, off = 0x1, signal = CarCabinManager.ID_AVM_DOW_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_AVM_DOW_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--侧后辅助--盲区监测 BSD
     * set:int类型数据; 0x0: Inactive; 0x1: Enabled; 0x2: Disabled; 0x3: Reserved
     * get :0x0: OFF 0x1: ON
     */
    ADAS_BSD(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_BSD_SW_REPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_BSD_REMIND_EN),
        default = true
    ),

    /**
     * 驾驶辅助--侧后辅助--盲区摄像头
     * get ->
            BSD Display status.
            0x0: Inactive
            0x1: On
            0x2: Off
            0x3: Invalid
     * set ->
            盲区单侧影像显示开关
            0x0: Inactive
            0x1: ON
            0x2: OFF
            0x3: Invalid
     */
    ADAS_BSC(
        get = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_AVM_BSD_DISP_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_APA_BSD_DISP_SWT),
        default = true
    ),

    /**
     * 驾驶辅助--侧后辅助--辅助线
     */
    ADAS_GUIDES(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x0, signal = -1),
        default = true
    ),
    //----------------------驾驶辅助 结束-------------------

    //----------------行车 start ---------------------------------
    /**
     * 行车--其它--拖车提醒
     */
    DRIVE_TRAILER_REMIND(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 行车--其它--蓄电池优化
     * set -> 整车低压能量管理开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: ON
            0x2: OFF
            0x3: Invalid
     * get -> 0x0:LVPM Enable
            0x1:IBS Error
            0x2:IBS dismatch
            0x3:Invald(no ibs)
            0x4:LVPM Disenabled by HUM/APP
            0x05：LVPM disabled by OTA
     */
    DRIVE_BATTERY_OPTIMIZE(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_LOU_PWR_MNGT_STS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_LOU_PWR_MNG_SWT),
        default = true
    ),

    /**
     * 行车--其它--无线充电
     * set -> 0x1: OFF    0x2: ON(default)
     * get -> 0x0:OFF 0x1:ON 0x2~0x3:Reserved
     */
    DRIVE_WIRELESS_CHARGING(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_WCM_SOFT_SW_STATE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_WCM_SWT),
        default = true
    ),

    /**
     * 行车--其它--无线充电灯
     */
    DRIVE_WIRELESS_CHARGING_LAMP(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),
    //----------------行车 end ---------------------------------
    //----------------灯光 start ---------------------------------
    /**
     * 灯光--灯光--车内迎宾灯
     */
    LIGHT_INSIDE_MEET(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 灯光--灯光--车外迎宾灯
     */
    LIGHT_OUTSIDE_MEET(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 灯光--氛围灯--前排氛围灯
     * get -> 0x0:ALT OFF 0x1:ALT ON 0x2~0x3:Reserved
     */
    FRONT_AMBIENT_LIGHTING(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_AL_RESPONSE_SW),
        set = Norm(on = 0x1, off = 0x0, signal = -1),
        default = true
    ),

    /**
     * 灯光--氛围灯--后排氛围灯
     */
    BACK_AMBIENT_LIGHTING(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    ),

    /**
     * 灯光--氛围灯--开关门提醒
     * set ->
            开关门提醒开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: Enabled
            0x2: Disabled
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_DOOR_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_DR_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_DIRECT_BREAM_SW),
        default = true
    ),

    /**
     * 灯光--氛围灯--开关锁提醒
     * set ->
            开关锁提醒开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: Enabled
            0x2: Disabled
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_LOCK_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_LUCK_SW_RESPONSE),
        set = Norm(
            on = 0x1,
            off = 0x2,
            signal = CarCabinManager.ID_ALC_HUM_ALC_SW_LOCK_REMIND_ENABLE
        ),
        default = true
    ),

    /**
     * 灯光--氛围灯--亮度呼吸
     * set ->
            亮度呼吸模式开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: On
            0x2: OFF
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_BREATHE_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_BRIG_BREAT_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_BRIGHT_BREA_SW),
        default = true
    ),

    /**
     * 灯光--氛围灯--开门后方来车提醒
     * set ->
            int类型数据
            0x0: Inactive
            0x1: Enabled
            0x2: Disabled
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_COMING_HINT(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_BSD_SW_REPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_BSD_REMIND_EN),
        default = true
    ),

    /**
     * 灯光--氛围灯--关联主题
     * set ->
            int类型数据
            氛围灯关联主题开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: Enabled
            0x2: Disabled
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_RELATED_TOPICS(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_HUM_THEME_SW_REPONS),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_HUM_ALC_THEME_SW_EN),
        default = true
    ),

    /**
     * 灯光--氛围灯--智能模式
     * set -> 智能模式开关[0x1,0,0x0,0x3]
            0x0: Inactive
            0x1: On
            0x2: OFF
            0x3: Reserved
     * get -> 0x0: OFF 0x1: ON
     */
    ALC_SMART_MODE(
        get = Norm(on = 0x1, off = 0x0, signal = CarCabinManager.ID_ALC_INTE_MODE_SW_RESPONSE),
        set = Norm(on = 0x1, off = 0x2, signal = CarCabinManager.ID_ALC_HUM_ALC_INTELLIGENT_MODE_SW),
        default = true
    ),

    //----------------灯光 end ---------------------------------

    INVALID(
        get = Norm(on = 0x1, off = 0x0, signal = -1),
        set = Norm(on = 0x1, off = 0x2, signal = -1),
        default = true
    );

    fun value(status: Boolean): Int {
        return if (status) set.on else set.off
    }

    fun isValid(value: Int) = (get.on == value) or (get.off == value)

    fun isOn(value: Int) = if (careOn) get.on == value else get.off != value

    fun isOn() = default
}