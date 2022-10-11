package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/9 16:55
 * @desc   :
 * @version: 1.0
 */
class CombineManager : BaseManager(), ISwitchManager {

    companion object : ISignal {
        override val TAG: String = CombineManager::class.java.simpleName
        val instance: CombineManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CombineManager()
        }
    }

    private val slaValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_TSR
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val hmaValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_HMA
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.ADAS_HMA.get.signal)
                add(SwitchNode.ADAS_TSR.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ADAS_HMA.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_HMA, hmaValue, property)
            }
            SwitchNode.ADAS_TSR.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_TSR, slaValue, property)
            }
            else -> {}
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.ADAS_HMA -> hmaValue.copy()
            SwitchNode.ADAS_TSR -> slaValue.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            SwitchNode.ADAS_TSR -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            else -> false
        }
    }


    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is ISwitchListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            } finally {
                writeLock.unlock()
            }
            result = serial
        }
        return result
    }

}