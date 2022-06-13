package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 13:26
 * @desc   :
 * @version: 1.0
 */
abstract class BaseManager : IManager {

    private val signalService: SettingManager
        get() = SettingManager.getInstance()

    protected val identity by lazy { System.identityHashCode(this) }

    protected val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    abstract val concernedSerials: Map<Origin, Set<Int>>

    fun onDispatchSignal(
        signal: Int,
        property: CarPropertyValue<*>,
        signalOrigin: Origin = Origin.CABIN
    ): Boolean {
        if (isConcernedSignal(signal, signalOrigin)) {
            return onHandleConcernedSignal(property, signalOrigin)
        }
        return false
    }

    protected open fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: Origin = Origin.CABIN
    ): Boolean {
        when (signalOrigin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            Origin.HVAC -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    open fun isConcernedSignal(signal: Int, signalOrigin: Origin = Origin.CABIN): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    open fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
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

    fun writeProperty(id: Int, value: Int, origin: Origin, area: Area = Area.GLOBAL): Boolean {
        return signalService.doSetProperty(id, value, origin, area)
    }

    fun writeProperty(id: Int, value: Int, origin: Origin, areaValue: Int): Boolean {
        return signalService.doSetProperty(id, value, origin, areaValue)
    }

    fun readIntProperty(id: Int, origin: Origin, area: Area = Area.GLOBAL): Int {
        return signalService.readIntProperty(id, origin, area)
    }

    protected fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int,
        block: ((Boolean) -> Unit)? = null
    ): AtomicBoolean {
        if (node.isValid(value)) {
            val status = node.isOn(value)
            doUpdateSwitchValue(node, atomic, status, block)
        }
        return atomic
    }

    protected fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: AtomicBoolean,
        status: Boolean,
        block: ((Boolean) -> Unit)? = null
    ): AtomicBoolean {
        if (atomic.get() xor status) {
            atomic.set(status)
            block?.let { it(status) }
        }
        return atomic
    }

    protected fun doUpdateRadioValue(node: RadioNode, atomic: AtomicInteger, value: Int, block: ((Int) -> Unit)? = null)
            : AtomicInteger {
        if (node.isValid(value)) {
            atomic.set(value)
            block?.let { it(value) }
        }
        return atomic
    }

    protected open fun onHvacPropertyChanged(property: CarPropertyValue<*>) {

    }

    protected open fun onCabinPropertyChanged(property: CarPropertyValue<*>) {

    }
}