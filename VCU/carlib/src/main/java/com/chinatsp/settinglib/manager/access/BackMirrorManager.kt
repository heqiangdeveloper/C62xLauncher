package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.access.IWindowListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class BackMirrorManager private constructor() : BaseManager(), ISwitchManager {

    private val backMirrorFold: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_BACK_MIRROR_FOLD
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin, Area.GLOBAL)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }


    companion object : ISignal {
        override val TAG: String = BackMirrorManager::class.java.simpleName
        val instance: BackMirrorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BackMirrorManager()
        }
    }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**雨天自动关窗*/
                add(CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS)
                /**锁车自动关窗*/
                add(CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: Origin
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

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AS_BACK_MIRROR_FOLD -> {
                backMirrorFold.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AS_BACK_MIRROR_FOLD -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.let {
                if (it.containsKey(serial)) it else null
            }?.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is IWindowListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            return serial
        }
        return -1
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_CLOSE_WIN_WHILE_RAIN, property)
            }
            CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_CLOSE_WIN_WHILE_LOCK, property)
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            SwitchNode.AS_BACK_MIRROR_FOLD -> {
                var value = property.value
                if (value is Int) {
                    value += 1
                    backMirrorFold.set(switchNode.isOn(value))
                    onSwitchChanged(switchNode, backMirrorFold.get())
                }
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.filterValues { null != it.get() }
                .forEach {
                    val listener = it.value.get()
                    if (null != listener && listener is ISwitchListener) {
                        listener.onSwitchOptionChanged(status, switchNode)
                    }
                }
        }
    }


}