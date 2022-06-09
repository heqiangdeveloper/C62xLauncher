package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 13:26
 * @desc   :
 * @version: 1.0
 */
abstract class BaseManager: IManager {

    val signalService: SettingManager
        get() = SettingManager.getInstance()

    protected val identity by lazy { System.identityHashCode(this) }

    protected val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    abstract val concernedSerials: Map<SignalOrigin, Set<Int>>

    fun onDispatchSignal(signal: Int, property: CarPropertyValue<*>, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean {
        if (isConcernedSignal(signal, signalOrigin)) {
            return onHandleConcernedSignal(property, signalOrigin)
        }
        return false
    }

    protected open fun onHandleConcernedSignal(property: CarPropertyValue<*>, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    open fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin = SignalOrigin.CABIN_SIGNAL):Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    open fun getConcernedSignal(signalOrigin: SignalOrigin):Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        return -1
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(ACManager.TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.takeIf { it.containsKey(serial) }?.remove(serial)
        }
        return true
    }

    fun doSetProperty(id: Int, value: Int, origin: SignalOrigin, area: Area = Area.GLOBAL): Boolean {
        return signalService.doSetProperty(id, value, origin, area)
    }

    fun doSetProperty(id: Int, value: Int, origin: SignalOrigin, areaValue: Int): Boolean {
        return signalService.doSetProperty(id, value, origin, areaValue)
    }

    fun doGetIntProperty(id: Int, origin: SignalOrigin, area: Area = Area.GLOBAL): Int {
        return signalService.doGetIntProperty(id, origin, area)
    }

    protected fun doUpdateSwitchStatus(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int
    ): AtomicBoolean {
        if (node.isValidValue(value)) {
            val status = node.isOn(value)
            if (atomic.get() xor status) {
                atomic.set(status)
            }
        }
        return atomic
    }

    protected open fun onHvacPropertyChanged(property: CarPropertyValue<*>) {

    }

    protected open fun onCabinPropertyChanged(property: CarPropertyValue<*>) {

    }
}