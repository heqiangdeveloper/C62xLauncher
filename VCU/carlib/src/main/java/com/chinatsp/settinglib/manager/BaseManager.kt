package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.AppExecutors
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.listener.*
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.bean.Cmd
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

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

    protected val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()

    protected val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    abstract val careSerials: Map<Origin, Set<Int>>

    fun createAtomicBoolean(
        node: SwitchNode,
        block: ((AtomicBoolean, Int) -> Unit),
    ): AtomicBoolean {
        val result = AtomicBoolean(node.default)
        readProperty(node.get.signal, node.get.origin) {
            block(result, it)
        }
        return result
    }

    fun createAtomicBoolean(
        node: SwitchNode,
        key: String,
        block: ((AtomicBoolean, Int) -> Unit),
    ): AtomicBoolean {
        val result = AtomicBoolean(node.default)
        AppExecutors.get()?.singleIO()?.execute {
            val resultValue = VcuUtils.getInt(key = key, value = node.value(node.default))
            block(result, resultValue)
        }
        return result
    }

    fun createAtomicInteger(node: RadioNode, block: ((AtomicInteger, Int) -> Unit)): AtomicInteger {
        val result = AtomicInteger(node.default)
        readProperty(node.get.signal, node.get.origin) {
            block(result, it)
        }
        return result
    }

    open fun onDispatchSignal(
        property: CarPropertyValue<*>,
        origin: Origin = Origin.CABIN,
    ): Boolean {
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
            Origin.MCU -> {
                onMcuPropertyChanged(property)
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
        val writeLock = readWriteLock.writeLock()
        try {
            writeLock.lock()
            unRegisterVcuListener(serial, identity)
            listenerStore[serial] = WeakReference(listener)
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        val writeLock = readWriteLock.writeLock()
        try {
            writeLock.lock()
            listenerStore.takeIf { it.containsKey(serial) }?.let {
                Timber.d("unRegisterVcuListener serial:$serial, callSerial:$callSerial")
                it.remove(serial)
            }
        } finally {
            writeLock.unlock()
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

    private fun readProperty(
        id: Int,
        origin: Origin,
        area: Area = Area.GLOBAL,
        block: ((Int) -> Unit),
    ) {
        signalService.readProperty(id, origin, area, block)
    }

//    fun readProperty(id: Int, origin: Origin, areaValue: Int, block:((Int)->Unit)) {
//        signalService.readProperty(id, origin, areaValue, block)
//    }

    override fun doSwitchChanged(node: SwitchNode, status: Boolean) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISwitchListener) {
                    listener.onSwitchOptionChanged(status, node)
                    Timber.d("$node, status:$status, listener:${listener::class.java.simpleName}")
                }
            }
        } finally {
            readLock.unlock()
        }
    }

    override fun doOptionChanged(node: RadioNode, value: Int) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is IRadioListener) {
                    listener.onRadioOptionChanged(node, value)
                }
            }
        } finally {
            readLock.unlock()
        }
    }

    override fun doProgressChanged(node: Progress, value: Int) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is IProgressListener) {
                    listener.onProgressChanged(node, value)
                }
            }
        } finally {
            readLock.unlock()
        }
    }

    protected open fun onHvacPropertyChanged(property: CarPropertyValue<*>) {}

    protected open fun onCabinPropertyChanged(property: CarPropertyValue<*>) {}

    protected open fun onMcuPropertyChanged(property: CarPropertyValue<*>) {}

    open fun doOuterControlCommand(cmd: Cmd, callback: ICmdCallback?) {}
}

