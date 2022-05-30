package com.chinatsp.settinglib.optios

import android.hardware.automotive.vehicle.V2_0.VehicleArea

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 13:26
 * @desc   :
 * @version: 1.0
 */
enum class Area(val id: Int) {
    GLOBAL(VehicleArea.GLOBAL),
    WINDOW(VehicleArea.WINDOW),
    MIRROR(VehicleArea.MIRROR),
    SEAT(VehicleArea.SEAT),
    DOOR(VehicleArea.DOOR),
    WHEEL(VehicleArea.WHEEL),
    IN_OUT_CAR(VehicleArea.IN_OUT_CAR),
    MASK(VehicleArea.MASK)
}