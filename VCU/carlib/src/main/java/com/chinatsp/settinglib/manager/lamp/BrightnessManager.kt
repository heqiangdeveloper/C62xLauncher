package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.power.CarPowerManager
import android.os.SystemThirdScreenBA
import com.chinatsp.settinglib.*
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/13 13:09
 * @desc   :
 * @version: 1.0
 */
class BrightnessManager : BaseManager(), IProgressManager, ISwitchManager {

    //浅色模式（白天模式）
    private val isLight: Boolean
        get() {
            val signal = CarCabinManager.ID_ALC_DIMMED_RESPONSE
            //白天4  黑夜5
            val result = readIntProperty(signal, Origin.CABIN, Area.GLOBAL)
            Timber.d("get current topic mode signal：$signal， value:$result")
            return 1 != result
        }

    private val isNewHardware: Boolean get() = true

    private var manager: CarPowerManager? = null

    private var thirdScreenService: SystemThirdScreenBA? = null

    private val thirdDarkBrightness: IntArray by lazy {
        intArrayOf(1, 2, 3, 4, 5, 8, 12, 17, 23, 30)
    }

    private val thirdLightBrightness: IntArray by lazy {
        intArrayOf(30, 36, 43, 50, 58, 68, 75, 80, 86, 95)
    }

    private val hostScreenLevel: IntArray by lazy {
        intArrayOf(0x19, 0x33, 0x4C,0x66, 0x7F, 0x99, 0xB2, 0xCC, 0xE5, 0xFF)
    }

    private val thirdScreenBrightness: IntArray
        get() = if (isLight) thirdLightBrightness else thirdDarkBrightness

    companion object : ISignal {
        override val TAG: String = BrightnessManager::class.java.simpleName
        val instance: BrightnessManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BrightnessManager()
        }
    }

    fun injectManager(manager: CarPowerManager) {
        this.manager = manager
        thirdScreenService = SystemThirdScreenBA(BaseApp.instance.applicationContext)
        this.manager?.setListener(object : CarPowerManager.CarPowerStateListener {
            override fun onStateChanged(state: Int) {
                Timber.d("brightnessCallback onStateChanged state:%s", state)
            }

            override fun onBrightnessChanged(value: Int) {
                val position = (value / 10f).roundToInt()
                Timber.d("DARK_LIGHT_MODE brightnessCallback onBrightnessChanged value:$value, position:$position")
                doUpdateProgress(carVolume, position, true, instance::doProgressChanged)
            }
        }, AppExecutors.get()?.singleIO())
    }

    private val lightAutoMode: SwitchState by lazy {
        val node = SwitchNode.LIGHT_AUTO_MODE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val darkLightMode: SwitchState by lazy {
        val node = SwitchNode.DARK_LIGHT_MODE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
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
        var result: Int
        if (isNewHardware) {
            result = manager?.brightness ?: type.min
            result = (result / 10).toDouble().roundToInt()
        } else {
            result = thirdScreenService?.getThirdScreenBrightness(if (isLight) 1 else 0) ?: type.min
            result = getNearestPosition(thirdScreenBrightness, result)
        }
        val value = if (result in type.min..type.max) result else type.def
        Timber.d("initVolume isNewHardware:$isNewHardware, type:$type, result:$result,, value:$value")
        return Volume(type, type.min, type.max, value)
    }

    fun isDarkModeActive(): Boolean {
        return darkLightMode.get()
    }

    private fun getNearestPosition(array: IntArray, value: Int): Int {
        val firstMoreValue = array.firstOrNull { it >= value }
        val lastLessValue = array.lastOrNull { it <= value }
        if (null == firstMoreValue) {
            return array.size - 1
        }
        if (null == lastLessValue) {
            return 0
        }
        val moreOffset = firstMoreValue - value
        val lessOffset = value - lastLessValue
        val nearestValue = if (moreOffset >= lessOffset) firstMoreValue else lastLessValue
        return array.indexOf(nearestValue)
    }

    private fun initProgress(type: Progress): Volume {
        val result = readIntProperty(type.get.signal, type.get.origin)
        val value = if (result in type.min..type.max) result else type.def
        Timber.d("initProgress type:$type, result:$result,, value:$value")
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
        Timber.d("doSetVolume type:$type, position:$position")
        return when (type) {
            Progress.CONDITIONER_SCREEN_BRIGHTNESS -> {
                writeProperty(type.set.signal, position, type.set.origin)
            }
            Progress.HOST_SCREEN_BRIGHTNESS -> {
                var value = position
                if (value < type.min) value = type.min
                if (value > type.max) value = type.max
                manager?.brightness = value * 10
                Timber.e("====DARK_LIGHT_MODE===doSetVolume====value=$value, actual:${manager?.brightness}")
                //iBAMode:白天黑夜模式，现传1就好, value：亮度值
                thirdScreenService?.setThirdScreenBrightness(
                    if (isLight) 1 else 0,
                    thirdScreenBrightness[value - 1]
                )
                doUpdateProgress(carVolume, value, true, this::doProgressChanged)
                true
            }
            Progress.METER_SCREEN_BRIGHTNESS -> {
                writeProperty(type.set.signal, position, type.set.origin)
            }
            else -> {
                false
            }
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            Progress.METER_SCREEN_BRIGHTNESS.get.signal -> {
                doUpdateProgress(meterVolume, property.value as Int, true, this::doProgressChanged)
            }
            Progress.CONDITIONER_SCREEN_BRIGHTNESS.get.signal -> {
                doUpdateProgress(acVolume, property.value as Int, true, this::doProgressChanged)
            }
            SwitchNode.LIGHT_AUTO_MODE.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_AUTO_MODE, lightAutoMode, property)
            }
            SwitchNode.DARK_LIGHT_MODE.get.signal -> {
                val last = darkLightMode.get()
                onSwitchChanged(SwitchNode.DARK_LIGHT_MODE, darkLightMode, property)
                val actual = darkLightMode.get()
                Timber.e("====DARK_LIGHT_MODE=======last=$last, actual:$actual")
//                if (last xor actual)
                    doSwitchLightDarkMode(darkLightMode.get())
            }
            else -> {}
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(Progress.METER_SCREEN_BRIGHTNESS.get.signal)
                add(Progress.CONDITIONER_SCREEN_BRIGHTNESS.get.signal)
                add(SwitchNode.LIGHT_AUTO_MODE.get.signal)
                add(SwitchNode.DARK_LIGHT_MODE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.LIGHT_AUTO_MODE -> lightAutoMode.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.LIGHT_AUTO_MODE -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

//    private fun isDarkMode(): Boolean {
////        Status of exterior lamp switch  0x0: Off; 0x1: Auto 0x2: Park 0x3: Low Beam
//        val lightSignal = CarCabinManager.ID_EXTERIOR_LAMP_SWITCH
//    }

    private fun doSwitchLightDarkMode(dark: Boolean) {
        val progress = Progress.HOST_SCREEN_BRIGHTNESS
        val screenLevel = if (dark) {
            VcuUtils.getInt(key = Constant.DARK_BRIGHTNESS_LEVEL, value = progress.min)
        } else {
            VcuUtils.getInt(key = Constant.LIGHT_BRIGHTNESS_LEVEL, value = progress.def)
        }
        Timber.e("DARK_LIGHT_MODE doSwitchLightDarkMode dark:$dark, screenLevel:$screenLevel")
        doSetVolume(progress, screenLevel)
    }

    fun initDarkLightMode() {
        val darkActive = darkLightMode.get()
        doSwitchLightDarkMode(darkActive)
    }

}