package com.chinatsp.settinglib

import android.car.VehicleAreaType
import android.car.hardware.cabin.CarCabinManager
import android.os.SystemProperties
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/23 15:27
 * @desc   :
 * @version: 1.0
 */
object VcuUtils {

    val TAG: String get() = VcuUtils::class.java.simpleName

    fun isParking(manager: BaseManager = GlobalManager.instance): Boolean {
        /**
         * 挡位判断
         * 0x0=Neutral；0x1=1stgear；0x2=2ndgear；0x3=3rdgear；0x4=4tgear；0x5=5tgear；
         * 0x6=6tgear；0x7=7tgear；0x8=8tgear；0x9=Reverse；0xA=Parking；0xB=Reserved；
         * 0xC=Reserved；0xD=Reserved；0xE=Reserved；0xF=Invalid
         */
        val signal = CarCabinManager.ID_TCU_TARGETGEAR
        val areaValue = VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
        val value = manager.readIntProperty(signal, Origin.CABIN, areaValue)
        val result = value == 0x0A
        LogManager.d(TAG, "isParking invoke value:$value, result:$result")
        return result
    }

    /**
     * 设备是否支持相应功能（通过读取下线配置项）
     * @param keySerial: 下线配置项 key
     * @return 是否支持
     */
    fun isSupportFunction(keySerial: String): Boolean {
        //0 无 1有
        val value = SystemProperties.getInt(keySerial, Constant.INVALID)
        LogManager.d(TAG, "isSupportFunction keySerial: $keySerial, value: $value")
        return value == 1
    }
}