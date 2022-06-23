package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/9 16:55
 * @desc   :
 * @version: 1.0
 */
class CombineManager : BaseManager(), ISwitchManager {

    companion object: ISignal {
        override val TAG: String = CombineManager::class.java.simpleName
        val instance: CombineManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CombineManager()
        }
    }

    private val slaValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_TSR
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val hmaValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_HMA
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
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


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                hmaValue.get()
            }
            SwitchNode.ADAS_TSR -> {
                slaValue.get()
            }
            else -> false
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
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            result = serial
        }
        return result
    }

}