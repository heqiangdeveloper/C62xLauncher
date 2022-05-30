package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.sign.SignalOrigin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 13:26
 * @desc   :
 * @version: 1.0
 */
abstract class BaseManager {

    val signalService: SettingManager
        get() = SettingManager.getInstance()

    fun onDispatchSignal(signal: Int, property: CarPropertyValue<*>, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean {
        if (isConcernedSignal(signal, signalOrigin)) {
            return onHandleConcernedSignal(property, signalOrigin)
        }
        return false
    }

    abstract fun onHandleConcernedSignal(property: CarPropertyValue<*>, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean

    abstract fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean

    abstract fun getConcernedSignal(signalOrigin: SignalOrigin):Set<Int>?

    fun doSetProperty(id: Int, value: Int, origin: SignalOrigin, area: Area = Area.GLOBAL): Boolean {
        return signalService.doSetProperty(id, value, origin, area)
    }

    fun doGetIntProperty(id: Int, origin: SignalOrigin, area: Area = Area.GLOBAL): Int {
        return signalService.doGetIntProperty(id, origin, area)
    }
}