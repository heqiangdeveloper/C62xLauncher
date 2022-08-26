package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.power.CarPowerManager
import android.os.SystemThirdScreenBA
import com.chinatsp.settinglib.AppExecutors
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
        this.manager?.setListener(object : CarPowerManager.CarPowerStateListener{
            override fun onStateChanged(state: Int) {
                Timber.d("onStateChanged state:%s", state)
            }

            override fun onBrightnessChanged(value: Int) {
                Timber.d("onBrightnessChanged value:%s", value)
                doUpdateProgress(carVolume, value, true, instance::doProgressChanged )
            }
        }, AppExecutors.get()?.singleIO())
    }

    private val acVolume: Volume by lazy {
        initProgress(Progress.CONDITIONER_SCREEN_BRIGHTNESS)
    }

    private val carVolume: Volume by lazy {
        initVolume(Progress.HOST_SCREEN_BRIGHTNESS)
    }

    private val meterVolume: Volume by lazy {
        initProgress(Progress.METER_SCREEN_BRIGHTNESS)
    }

    private fun initVolume(type: Progress): Volume {
        val pos = manager?.brightness ?: type.min
        Timber.d("initVolume type:$type, pos:$pos")
        return Volume(type, type.min, type.max, pos)
    }

    private fun initProgress(type: Progress): Volume {
        val value = readIntProperty(type.get.signal, type.get.origin)
        Timber.d("initProgress type:$type, value:$value")
        return Volume(type, type.min, type.max, value)
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
        return when (type) {
            Progress.CONDITIONER_SCREEN_BRIGHTNESS -> {
                writeProperty(type.set.signal,position, type.set.origin)
            }
            Progress.HOST_SCREEN_BRIGHTNESS -> {
                manager?.brightness = position * 10
                //iBAMode:白天黑夜模式，现传1就好, value：亮度值
                thirdScreenService?.setThirdScreenBrightness(1, position)
                doUpdateProgress(carVolume, position, true, this::doProgressChanged)
                true
            }
            Progress.METER_SCREEN_BRIGHTNESS -> {
                writeProperty(type.set.signal,position, type.set.origin)
            }
            else -> {
                false
            }
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            Progress.METER_SCREEN_BRIGHTNESS.get.signal -> {
                doUpdateProgress(meterVolume, property.value as Int, true, this::doProgressChanged )
            }
            Progress.CONDITIONER_SCREEN_BRIGHTNESS.get.signal -> {
                doUpdateProgress(acVolume, property.value as Int, true, this::doProgressChanged )
            }
            else -> {}
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(Progress.METER_SCREEN_BRIGHTNESS.get.signal)
                add(Progress.CONDITIONER_SCREEN_BRIGHTNESS.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

}