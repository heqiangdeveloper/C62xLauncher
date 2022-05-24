package com.chinatsp.settinglib.sign

import android.car.hardware.cabin.CarCabinManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/24 10:34
 * @desc   :
 * @version: 1.0
 */
enum class Cabin(vararg signalVararg: Int) {

    /**
     * 方向盘信号
     */
//    HELM(1, 2, 4, 5),
//    CHAIR(4, 5, 8, 9),
    AIR_CONDITIONER(
        CarCabinManager.ID_ACSELFSTSDISP,
        CarCabinManager.ID_ACPREVENTNDISP,
        CarCabinManager.ID_ACC_DISTANCE_LEVEL,
        CarCabinManager.ID_AC_DIS_AC_MAX,
        CarCabinManager.ID_AC_DIS_API_INSIDE,
        CarCabinManager.ID_AC_DIS_INSIDE_PM2_5_DATA,
        CarCabinManager.ID_AC_DIS_OUTSIDE_PM2_5_DATA,
        CarCabinManager.ID_AC_DIS_IN_CAR_TEMPERATURE,
        CarCabinManager.ID_AC_DIS_AMBIENT_TEMPERATURE,
        CarCabinManager.ID_AC_REFRESH_MODE_ACT_STS,
        CarCabinManager.ID_AC_REFRESH_MODE_SET_STATUS,
        CarCabinManager.ID_AC_AUTOMATICDE_FOGGING_STATUS
    );

    val signals: IntArray = signalVararg

    fun contains(id: Int): Boolean {
        return signals.contains(id)
    }

}