package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.power.CarPowerManager
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/13 13:09
 * @desc   :
 * @version: 1.0
 */
class BrightnessManager : BaseManager(), IProgressManager {

    var manager: CarPowerManager? = null

    companion object : ISignal {

        override val TAG: String = BrightnessManager::class.java.simpleName

        val instance: BrightnessManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BrightnessManager()
        }

    }

    fun injectManager(manager: CarPowerManager) {
        this.manager = manager
    }

    private val acVolume: Volume by lazy {
        initVolume(Volume.Type.AC_SCREEN)
    }

    private val carVolume: Volume by lazy {
        initVolume(Volume.Type.CAR_SCREEN)
    }

    private val meterVolume: Volume by lazy {
        initVolume(Volume.Type.METER_SCREEN)
    }

    private fun initVolume(type: Volume.Type): Volume {
        val max = 10
        var pos = manager?.brightness ?: 0
        LogManager.d(TAG, "initVolume pos:$pos")
        pos /= 10
        return Volume(type, 0, max, pos)
    }

    override fun doGetVolume(type: Volume.Type): Volume? {
        return when (type) {
            Volume.Type.AC_SCREEN -> {
                acVolume
            }
            Volume.Type.CAR_SCREEN -> {
                carVolume
            }
            Volume.Type.METER_SCREEN -> {
                meterVolume
            }
            else -> null

        }
    }

    override fun doSetVolume(type: Volume.Type, position: Int): Boolean {
        LogManager.d(TAG, "doSetVolume position:$position")
        return when (type) {
            Volume.Type.AC_SCREEN -> {
                manager?.brightness = position * 10
                true
            }
            Volume.Type.CAR_SCREEN -> {
                manager?.brightness = position * 10
                doUpdateProgress(carVolume, position, true)
                true
            }
            Volume.Type.METER_SCREEN -> {
                manager?.brightness = position * 10
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