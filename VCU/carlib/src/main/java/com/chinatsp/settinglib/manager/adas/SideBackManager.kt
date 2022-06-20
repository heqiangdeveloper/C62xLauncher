package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
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
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class SideBackManager : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = SideBackManager::class.java.simpleName
        val instance: SideBackManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SideBackManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.ADAS_DOW.get.signal)
                add(SwitchNode.ADAS_BSC.get.signal)
                add(SwitchNode.ADAS_BSD.get.signal)
                add(SwitchNode.ADAS_GUIDES.get.signal)
                add(RadioNode.ADAS_SIDE_BACK_SHOW_AREA.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val showAreaValue: AtomicInteger by lazy {
        val node = RadioNode.ADAS_SIDE_BACK_SHOW_AREA
        AtomicInteger(node.default).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val dowValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_DOW
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val bsdValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_BSD
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val bscValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_BSC
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val guidesValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_GUIDES
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ADAS_DOW.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_DOW, dowValue, property)
            }
            SwitchNode.ADAS_BSC.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_BSC, bscValue, property)
            }
            SwitchNode.ADAS_BSD.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_BSD, bsdValue, property)
            }
            SwitchNode.ADAS_GUIDES.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_GUIDES, guidesValue, property)
            }
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA.get.signal -> {
                onRadioChanged(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, showAreaValue, property)
            }
            else -> {}
        }

    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                showAreaValue.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                node.isValid(value, false) && writeProperty(node.set.signal, value, node.set.origin)
            }
            else -> false
        }
    }


    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            result = serial
        }
        return result
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_DOW -> {
                dowValue.get()
            }
            SwitchNode.ADAS_BSD -> {
                bsdValue.get()
            }
            SwitchNode.ADAS_BSC -> {
                bscValue.get()
            }
            SwitchNode.ADAS_GUIDES -> {
                guidesValue.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_DOW -> {
//                writeProperty(node.set.signal, node.value(status), node.set.origin)
                doSetSwitchOption(node, status, dowValue)
            }
            SwitchNode.ADAS_BSD -> {
//                writeProperty(node.set.signal, node.value(status), node.set.origin)
                doSetSwitchOption(node, status, bsdValue)
            }
            SwitchNode.ADAS_BSC -> {
//                writeProperty(node.set.signal, node.value(status), node.set.origin)
                doSetSwitchOption(node, status, bscValue)
            }
            SwitchNode.ADAS_GUIDES -> {
                doSetSwitchOption(node, status, guidesValue)
            }
            else -> false
        }
    }

    fun doSetSwitchOption(node: SwitchNode, status: Boolean, atomic: AtomicBoolean): Boolean {
        val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, status) {_node, _status ->
                doSwitchChanged(_node, _status)
            }
        }
        return success
    }

}