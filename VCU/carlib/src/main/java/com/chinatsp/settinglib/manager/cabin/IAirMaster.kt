package com.chinatsp.settinglib.manager.cabin

import com.chinatsp.settinglib.bean.CommandParcel

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 9:54
 * @desc   :
 * @version: 1.0
 */
interface IAirMaster {
    /**
     * 打开空调
     * @param  parcel
     * @return 返回当前空调状态（是否打开）
     */
    fun doLaunchConditioner(parcel: CommandParcel): Boolean
    /**
     * 关闭空调
     * @param parcel
     */
    fun doCeaseConditioner(parcel: CommandParcel)
    /**
     * 调节空调的吹风方向（例：吹头，吹脚，吹身体）
     */
    fun doAdjustAirDirection(parcel: CommandParcel)
//    /**
//     * 调节空调风速
//     */
//    fun doAdjustAirWindSpeed(airCmdParcel: CommandParcel)
//    /**
//     * 调节空调温度
//     */
//    fun doAdjustAirTemperature(airCmdParcel: CommandParcel)

}