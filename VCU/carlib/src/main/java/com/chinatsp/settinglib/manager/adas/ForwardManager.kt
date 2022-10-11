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
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class ForwardManager : BaseManager(), ISwitchManager {

    companion object : ISignal {
        override val TAG: String = ForwardManager::class.java.simpleName
        val instance: ForwardManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ForwardManager()
        }
    }

    private val fcwStatus: SwitchState by lazy {
        val node = SwitchNode.ADAS_FCW
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val aebStatus: SwitchState by lazy {
        val node = SwitchNode.ADAS_AEB
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
//                add(CarCabinManager.ID_FCW_STATUS)
//                add(CarCabinManager.ID_AEB_STATUS)
                add(SwitchNode.ADAS_FCW.get.signal)
                add(SwitchNode.ADAS_AEB.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ADAS_FCW.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_FCW, fcwStatus, property)
            }
            SwitchNode.ADAS_AEB.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_AEB, aebStatus, property)
            }
            else -> {}
        }
    }

//    private fun onSwitchChanged(
//        node: SwitchNode,
//        atomic: AtomicBoolean,
//        property: CarPropertyValue<*>
//    ) {
//        val value = property.value
//        if (value is Int) {
//            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue) { newNode, newValue ->
//                doSwitchChanged(newNode, newValue)
//            }
//        }
//    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.ADAS_FCW -> fcwStatus.copy()
            SwitchNode.ADAS_AEB -> aebStatus.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            /**FCW status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved*/
            SwitchNode.ADAS_FCW -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_AEB -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
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