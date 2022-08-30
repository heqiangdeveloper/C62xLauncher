package com.chinatsp.settinglib

import android.car.VehicleAreaType
import android.car.hardware.cabin.CarCabinManager
import android.content.Context
import android.database.ContentObserver
import android.os.SystemProperties
import android.provider.Settings
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.annotation.Level
import timber.log.Timber

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
        Timber.d("isParking invoke value:$value, result:$result")
        return result
    }

    /**
     * 设备是否支持相应功能（通过读取下线配置项）
     * @param keySerial: 下线配置项 key
     * @return 是否支持
     */
    fun isSupportFunction(keySerial: String): Boolean {
        //0 无 1有
        val value = getConfigParameters(keySerial, Constant.INVALID)
        Timber.d("isSupportFunction keySerial: $keySerial, value: $value")
        return value == 1
    }

    fun isCareLevel(@Level vararg levels: Int, expect: Boolean = true): Boolean {
        val value = getLevelValue()
        Timber.d("isCareLevel value: $value")
        val actual = levels.contains(value)
        return !(actual xor expect)
    }

    fun getLevelValue(): Int {
        val value = SystemProperties.getInt(OffLine.LEVEL, Level.LEVEL3)
        Timber.d("getLevelValue value: $value")
        return value
    }
    
    fun isAmplifier(): Boolean {
        val value = getConfigParameters(OffLine.AMP_TYPE, 0)
        Timber.d("isAmplifier value: $value")
        return 0 == value
    }

    fun putInt(context: Context = BaseApp.instance, key: String, value: Int): Boolean {
        try {
            return Settings.Global.putInt(context.contentResolver, key, value)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Timber.e("putInt key:%s, value:%s, throw exception:%s", key, value, e.message)
        }
        return false
    }

    fun getInt(context: Context = BaseApp.instance, key: String, value: Int): Int {
        try {
            return Settings.Global.getInt(context.contentResolver, key, value)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Timber.e("getInt key:%s, value:%s, throw exception:%s", key, value, e.message)
        }
        return value
    }

    fun addUriObserver(uriSerial: String, observer: ContentObserver) {
        val uri = Settings.Global.getUriFor(uriSerial)
        val resolver = BaseApp.instance.contentResolver
        if (null != resolver && null != uri) {
            resolver.registerContentObserver(uri, true, observer)
        }
    }

    fun removeUriObserver(observer: ContentObserver) {
        val resolver = BaseApp.instance.contentResolver
        resolver.unregisterContentObserver(observer)
    }

    fun getConfigParameters(keySerial: String, default: Int): Int {
        val result = try {
            SystemProperties.getInt(keySerial, default)
        } catch (e: Exception) {
            Timber.d("getConfigParam key:%s, def:%s, e:%s", keySerial, default, e.message)
            default
        }
        Timber.d("getConfigParam key:%s, def:%s, result:%s", keySerial, default, result)
        return result
    }

    fun setConfigParameters(keySerial: String, value: Int): Boolean {
        try {
            SystemProperties.set(keySerial, value.toString())
            Timber.d("setConfigParameters keySerial:%s, value:%s", keySerial, value)
            return true
        } catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

}