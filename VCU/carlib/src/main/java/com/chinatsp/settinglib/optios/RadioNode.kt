package com.chinatsp.settinglib.optios

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:19
 * @desc   :
 * @version: 1.0
 */
enum class RadioNode {

    ACCESS_DOOR_DRIVE_LOCK,
    ACCESS_DOOR_ACC_OFF_UNLOCK,

    /**
     * 车门与车窗--电动尾门--电动尾门智能进入
     */
    ACCESS_STERN_SMART_ENTER,

    /**
     * 驾驶辅助-智能巡航-前车驶离提示
     */
    ADAS_LIMBER_LEAVE,

    /**
     * 驾驶辅助-车道辅助-车道辅助系统
     */
    ADAS_LANE_ASSIST_MODE,
    /**
     * 驾驶辅助-车道辅助-报警方式
     */
    ADAS_LDW_STYLE,
    /**
     * 驾驶辅助-车道辅助-灵敏度
     */
    ADAS_LDW_SENSITIVITY,


    /**
     * 驾驶辅助-侧后辅助-显示区域
     */
    ADAS_SIDE_BACK_SHOW_AREA,

    /**
     * 行车--仪表--制式
     */
    DRIVE_METER_SYSTEM

}