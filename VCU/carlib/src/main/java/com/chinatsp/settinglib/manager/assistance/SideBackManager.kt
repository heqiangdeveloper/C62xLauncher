package com.chinatsp.settinglib.manager.assistance

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

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val showAreaValue: AtomicInteger by lazy {
        AtomicInteger(1).apply {
            val signal = CarCabinManager.ID_LKS_SENSITIVITY
            val value = readIntProperty(signal, Origin.CABIN)
            set(value)
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
                val signal = -1
                writeProperty(signal, value, Origin.CABIN)
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
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_BSD -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_BSC -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.ADAS_GUIDES -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

}