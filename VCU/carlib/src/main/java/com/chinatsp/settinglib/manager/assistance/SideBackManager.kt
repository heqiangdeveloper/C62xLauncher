package com.chinatsp.settinglib.manager.assistance

import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
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

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    private val showAreaValue: AtomicInteger by lazy {
        AtomicInteger(1).apply {
            val signal = CarCabinManager.ID_LKS_SENSITIVITY
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            set(value)
        }
    }

    private val dowValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_DOW
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }

    private val bsdValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_BSD
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }

    private val bscValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_BSC
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }

    private val guidesValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_GUIDES
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }


    override fun doGetRadioOption(radioNode: RadioNode): Int {
        return when (radioNode) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                showAreaValue.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        return when (radioNode) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                val signal = -1
                doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL)
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

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
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

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.ADAS_DOW -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_BSD -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_BSC -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_GUIDES -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
    }

}