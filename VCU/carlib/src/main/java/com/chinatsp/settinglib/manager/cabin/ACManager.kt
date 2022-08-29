package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class ACManager private constructor() : BaseManager(), IAcManager {

    companion object : ISignal {
        override val TAG: String = ACManager::class.java.simpleName
        val instance: ACManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ACManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**空调自干燥*/
                add(SwitchNode.AC_AUTO_ARID.get.signal)
                /**预通风功能*/
                add(SwitchNode.AC_ADVANCE_WIND.get.signal)
                add(SwitchNode.AC_AUTO_DEMIST.get.signal)
                /**空调舒适性状态显示*/
                add(RadioNode.AC_COMFORT.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val aridStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_AUTO_ARID
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val demistStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_AUTO_DEMIST
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val windStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_ADVANCE_WIND
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val comfortOption: AtomicInteger by lazy {
        val node = RadioNode.AC_COMFORT
//        AtomicInteger(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, result)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.AC_COMFORT -> {
                comfortOption.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.AC_COMFORT -> {
                node.isValid(value, false) && writeProperty(
                    node.set.signal,
                    value,
                    node.set.origin,
                    node.area
                )
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        val writeLock = readWriteLock.writeLock()
        try {
            writeLock.lock()
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> {
                aridStatus.get()
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                windStatus.get()
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                demistStatus.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            SwitchNode.AC_AUTO_DEMIST.get.signal -> {
                onSwitchChanged(SwitchNode.AC_AUTO_DEMIST, demistStatus, property)
//                onSwitchOptionChanged(SwitchNode.AC_AUTO_DEMIST, property.value)
            }
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            SwitchNode.AC_AUTO_ARID.get.signal -> {
                onSwitchChanged(SwitchNode.AC_AUTO_ARID, aridStatus, property)
            }
            //预通风功能
            SwitchNode.AC_ADVANCE_WIND.get.signal -> {
                onSwitchChanged(SwitchNode.AC_ADVANCE_WIND, windStatus, property)
            }
            //自动空调舒适性
            RadioNode.AC_COMFORT.get.signal -> {
                onRadioChanged(RadioNode.AC_COMFORT, comfortOption, property)
            }
            else -> {}
        }
    }

//    private fun onRadioChanged(node: RadioNode, atomic: AtomicInteger, property: CarPropertyValue<*>) {
//        val value = property.value
//        if (value is Int) {
//            onRadioChanged(node, atomic, value, this::doUpdateRadioValue) {
//                    newNode, newValue -> doRadioChanged(newNode, newValue)
//            }
//        }
//    }
//
//    private fun onSwitchChanged(node: SwitchNode, atomic: AtomicBoolean, property: CarPropertyValue<*>) {
//        val value = property.value
//        if (value is Int) {
//            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue) {
//                    newNode, newValue -> doSwitchChanged(newNode, newValue)
//            }
//        }
//    }


}