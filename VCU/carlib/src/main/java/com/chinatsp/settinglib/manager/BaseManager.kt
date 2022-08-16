package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.listener.*
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.bean.Cmd
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 13:26
 * @desc   :
 * @version: 1.0
 */
abstract class BaseManager : IManager {

    private val signalService: SettingManager
        get() = SettingManager.instance

    protected val identity by lazy { System.identityHashCode(this) }

    protected val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    abstract val careSerials: Map<Origin, Set<Int>>

    open fun onDispatchSignal(property: CarPropertyValue<*>, origin: Origin = Origin.CABIN): Boolean {
        if (isCareSignal(property.propertyId, origin)) {
            return onHandleSignal(property, origin)
        }
        return false
    }

    open fun onHandleSignal(property: CarPropertyValue<*>, origin: Origin = Origin.CABIN): Boolean {
        when (origin) {
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

    open fun isCareSignal(signal: Int, origin: Origin = Origin.CABIN): Boolean {
        return getOriginSignal(origin).contains(signal)
    }

    open fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        synchronized(listenerStore) {
            listenerStore.takeIf { it.containsKey(serial) }?.let {
                LogManager.d(ACManager.TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
                it.remove(serial)
            }
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

    fun readIntProperty(id: Int, origin: Origin, areaValue: Int): Int {
        return signalService.readIntProperty(id, origin, areaValue)
    }


//    protected fun doUpdateSwitchValue(
//        node: SwitchNode,
//        atomic: AtomicBoolean,
//        value: Int,
//        block: ((SwitchNode, Boolean) -> Unit)? = null
//    ): AtomicBoolean {
//        if (node.isValid(value)) {
//            val status = node.isOn(value)
//            doUpdateSwitchValue(node, atomic, status, block)
//        }
//        return atomic
//    }
//
////    protected fun doUpdateSwitchValue(
////        node: SwitchNode,
////        atomic: AtomicBoolean,
////        status: Boolean,
////        block: ((Boolean) -> Unit)? = null
////    ): AtomicBoolean {
////        if (atomic.get() xor status) {
////            atomic.set(status)
////            block?.let { it(status) }
////        }
////        return atomic
////    }
//
//    protected fun doUpdateSwitchValue(
//        node: SwitchNode,
//        atomic: AtomicBoolean,
//        status: Boolean,
//        block: ((SwitchNode, Boolean) -> Unit)? = null
//    ): AtomicBoolean {
//        if (atomic.get() xor status) {
//            atomic.set(status)
//            block?.let { it(node, status) }
//        }
//        return atomic
//    }
//
////    protected fun doUpdateRadioValue(node: RadioNode, atomic: AtomicInteger, value: Int, block: ((Int) -> Unit)? = null)
////            : AtomicInteger {
////        if (node.isValid(value)) {
////            atomic.set(value)
////            block?.let { it(value) }
////        }
////        return atomic
////    }
//
//    protected fun doUpdateRadioValue(node: RadioNode, atomic: AtomicInteger, value: Int, block: ((RadioNode, Int) -> Unit)? = null)
//            : AtomicInteger {
//        if (node.isValid(value) && atomic.get() != value) {
//            atomic.set(value)
//            block?.let { it(node, value) }
//        }
//        return atomic
//    }

    override fun doSwitchChanged(node: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISwitchListener) {
                    listener.onSwitchOptionChanged(status, node)
                    LogManager.d("doSwitchChanged", "$node, status:$status, listener:${listener::class.java.simpleName}")
                }
            }
        }
    }

    override fun doRadioChanged(node: RadioNode, value: Int) {
        synchronized(listenerStore) {
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is IRadioListener) {
                    listener.onRadioOptionChanged(node, value)
                }
            }
        }
    }

    override fun doProgressChanged(node: Progress, value: Int) {
        synchronized(listenerStore) {
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is IProgressListener) {
                    listener.onProgressChanged(node, value)
                }
            }
        }
    }

    protected open fun onHvacPropertyChanged(property: CarPropertyValue<*>) {}

    protected open fun onCabinPropertyChanged(property: CarPropertyValue<*>) {}

    open fun doOuterControlCommand(cmd: Cmd, callback: ICmdCallback?) {}
}

