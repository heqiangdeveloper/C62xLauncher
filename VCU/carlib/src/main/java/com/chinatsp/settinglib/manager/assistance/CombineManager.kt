package com.chinatsp.settinglib.manager.assistance

import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
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
        val node = SwitchNode.ADAS_SLA
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }

    private val hmaValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_HMA
        AtomicBoolean(node.isOn()).apply {
            val signal = -1
            val value = doGetIntProperty(signal, node.origin, node.area)
            doUpdateSwitchStatus(node, this, value)
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                hmaValue.get()
            }
            SwitchNode.ADAS_SLA -> {
                slaValue.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                doSetProperty(node.signal, node.obtainValue(status), node.origin, node.area)
            }
            SwitchNode.ADAS_SLA -> {
                doSetProperty(node.signal, node.obtainValue(status), node.origin, node.area)
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