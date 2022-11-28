package com.chinatsp.settinglib

import android.car.VehicleAreaType
import android.car.hardware.cabin.CarCabinManager
import android.content.Context
import android.content.pm.PackageManager
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

    val V_N: String by lazy {
        val context = BaseApp.instance.applicationContext
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA) //P
        info.versionName
    }

    fun isEngineRunning(manager: BaseManager = GlobalManager.instance): Boolean {
//        0x0: Engine NOT running 0x1: Cranking 0x2: Engine running 0x3: Fault
        val signal = CarCabinManager.ID_ENGINE_RUNNING
        val value = manager.readIntProperty(signal, Origin.CABIN)
        val result = value == 0x2
        Timber.d("isEngineRunning invoke value:$value, result:$result")
        return result
    }

    fun isPower(manager: BaseManager = GlobalManager.instance): Boolean {
        val result = isPowerValid(manager) && isPowerLaunch(manager)
        Timber.d("isPower invoke power status result:$result")
        return result
    }

    private fun isPowerLaunch(manager: BaseManager = GlobalManager.instance): Boolean {
//        0x0: OFF 0x1: ACC 0x2: IGN ON 0x3: CRANK
        val signal = CarCabinManager.ID_POWER_MODE_BCM
        val value = manager.readIntProperty(signal, Origin.CABIN)
        val result = value == Constant.POWER_ON
        Timber.d("isPowerLaunch invoke value:$value, result:$result")
        return result
    }

    fun isPowerValid(manager: BaseManager = GlobalManager.instance): Boolean {
//        0x0:  reserved  0x1:  Invalid  0x2:  Valid  0x3:  reserved
        val signal = CarCabinManager.ID_POWER_MODE_VALID_BCM
        val value = manager.readIntProperty(signal, Origin.CABIN)
//        Timber.d("isPowerValid invoke value:$value, result:$result")
        return value == 0x2
    }

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
    fun <T> isSupport(keySerial: String, expect: T): Boolean {
        //0 无 1有
        val value = getConfigParameters(keySerial, Constant.INVALID)
        Timber.d("isSupport keySerial: $keySerial, expect: $expect, value: $value")
        return value == expect
    }

    fun isCareLevel(@Level vararg levels: Int, expect: Boolean = true): Boolean {
        val value = VEHICLE_LEVEL
        val actual = levels.contains(value)
        val result = !(actual xor expect)
        Timber.d("isCareLevel value: $value, actual:$actual, result:$result")
        return result
    }

    val VEHICLE_LEVEL: Int by lazy {
        val value = SystemProperties.getInt(OffLine.LEVEL, Level.LEVEL3)
        Timber.d("getLevelValue value: $value")
        return@lazy value
    }

    val isAmplifier: Boolean by lazy {
        val value = getConfigParameters(OffLine.AMP_TYPE, 0)
        Timber.d("isAmplifier value: $value")
        0 == value
    }

    fun putInt(
        context: Context = BaseApp.instance,
        key: String,
        value: Int,
        system: Boolean = false,
    ): Boolean {
        try {
            Timber.d("putInt key:%s, value:%s, system:%s", key, value, system)
            return if (system) {
                Settings.System.putInt(context.contentResolver, key, value)
            } else {
                Settings.Global.putInt(context.contentResolver, key, value)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Timber.e("putInt key:%s, value:%s, throw exception:%s", key, value, e.message)
        }
        return false
    }

    fun getInt(
        context: Context = BaseApp.instance,
        key: String,
        value: Int,
        system: Boolean = false,
    ): Int {
        try {
            return if (system) {
                Settings.System.getInt(context.contentResolver, key, value)
            } else {
                Settings.Global.getInt(context.contentResolver, key, value)
            }
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

    fun setConfigParameters(keySerial: String, value: String): Boolean {
        try {
            SystemProperties.set(keySerial, value)
            Timber.d("setConfigParameters keySerial:%s, value:%s", keySerial, value)
            return true
        } catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

    fun getConfigParameters(keySerial: String, default: String): String {
        val result = try {
            SystemProperties.get(keySerial, default)
        } catch (e: Exception) {
            Timber.d("getConfigParam key:%s, def:%s, e:%s", keySerial, default, e.message)
            default
        }
        Timber.d("getConfigParam key:%s, def:%s, result:%s", keySerial, default, result)
        return result
    }

    fun isAvmEngine(value: Int): Boolean {
//        Indicate the current display requirement of AVM
//        0x0: Initial
//        0x1: Request to display normal view
//        0x2: Request to display off view
//        0x3: Request to display error view
//        0x4: Request to display EVM view
//        0x5: Request to display EVM view for fault and reduction of speed  reminding  请求开启EVM故障降速提示界面
//        0x6: Request to display single  Left side view 请求单独显示左侧影像界面
//        0x7: Request to display single Right side view 请求单独显示右侧影像界面
//        return value == 0x1 || value == 0x2 || value == 0x3 || value == 0x4 || value == 0x5 || value == 0x6 || value == 0x7
        return value == 0x1 || value == 0x4 || value == 0x5 || value == 0x6 || value == 0x7
    }

//    ID_TCU_SELECTED_GEAR 挡位
//    ID_POWER_MODE_BCM 电源

}