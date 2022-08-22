package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.power.CarPowerManager
import android.os.SystemThirdScreenBA
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/13 13:09
 * @desc   :
 * @version: 1.0
 */
class BrightnessManager : BaseManager(), IProgressManager {

    private var manager: CarPowerManager? = null
    private var thirdScreenService: SystemThirdScreenBA? = null

    private val topicNode: Int
        get() {
            try {
                return readIntProperty(
                    CarCabinManager.ID_VENDOR_LIGHT_NIGHT_AUTOMODE_REPORT, Origin.CABIN, Area.GLOBAL
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Constant.LIGHT_TOPIC
        }

    companion object : ISignal {

        override val TAG: String = BrightnessManager::class.java.simpleName

        val instance: BrightnessManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BrightnessManager()
        }

    }

    fun injectManager(manager: CarPowerManager) {
        this.manager = manager
        thirdScreenService = SystemThirdScreenBA(BaseApp.instance.applicationContext)
    }

    private val acVolume: Volume by lazy {
        initVolume(Progress.CONDITIONER_SCREEN_BRIGHTNESS)
    }

    private val carVolume: Volume by lazy {
        initVolume(Progress.HOST_SCREEN_BRIGHTNESS)
    }

    private val meterVolume: Volume by lazy {
        initVolume(Progress.METER_SCREEN_BRIGHTNESS)
    }

    private fun initVolume(type: Progress): Volume {
        val max = 10
        var pos = manager?.brightness ?: 0
        pos /= 10
        Timber.d("initVolume type:$type, pos:$pos")
        return Volume(type, 0, max, pos)
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.CONDITIONER_SCREEN_BRIGHTNESS -> {
                acVolume
            }
            Progress.HOST_SCREEN_BRIGHTNESS -> {
                carVolume
            }
            Progress.METER_SCREEN_BRIGHTNESS -> {
                meterVolume
            }
            else -> null

        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        Timber.d("doSetVolume position:$position")
        val value = position * 10
        return when (type) {
            Progress.CONDITIONER_SCREEN_BRIGHTNESS -> {
                manager?.brightness = value
                true
            }
            Progress.HOST_SCREEN_BRIGHTNESS -> {
//                manager?.brightness = value
                //iBAMode:白天黑夜模式，现传1就好, value：亮度值
                thirdScreenService?.setThirdScreenBrightness(1, position)
                doUpdateProgress(carVolume, position, true)
                true
            }
            Progress.METER_SCREEN_BRIGHTNESS -> {
                manager?.brightness = value
                true
            }
            else -> {
                false
            }
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val keySet = LampManager.managers.flatMap {
                it.careSerials.keys
            }.toSet()
            keySet.forEach { key ->
                val hashSet = HashSet<Int>()
                LampManager.managers.forEach { manager ->
                    hashSet.addAll(manager.getOriginSignal(key))
                }
                put(key, hashSet)
            }
        }
    }

}