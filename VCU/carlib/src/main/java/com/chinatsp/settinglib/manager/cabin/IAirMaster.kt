package com.chinatsp.settinglib.manager.cabin

import com.chinatsp.settinglib.bean.AirCmdParcel
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.bean.AirCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 9:54
 * @desc   :
 * @version: 1.0
 */
interface IAirMaster {

    /**
     * @return 空调是否开启
     */
    fun isAirEngine(): Boolean

    /**
     * 打开空调
     * @param hint 当空调已经打开时 是否给出相应的提示
     * @return 返回当前空调状态（是否打开）
     */
    fun doStartAirEngine(airCmdParcel: AirCmdParcel): Boolean
    /**
     * 关闭空调
     * @param isHint 当空调已经关闭时 是否给出相应的提示
     */
    fun doCeaseAirEngine(airCmdParcel: AirCmdParcel)
    /**
     * 调节空调的吹风方向（例：吹头，吹脚，吹身体）
     */
    fun doAdjustAirDirection(airCmdParcel: AirCmdParcel)
    /**
     * 调节空调风速
     */
    fun doAdjustAirWindSpeed(airCmdParcel: AirCmdParcel)
    /**
     * 调节空调温度
     */
    fun doAdjustAirTemperature(airCmdParcel: AirCmdParcel)

}