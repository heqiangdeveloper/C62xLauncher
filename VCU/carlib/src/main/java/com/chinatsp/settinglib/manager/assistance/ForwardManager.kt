package com.chinatsp.settinglib.manager.assistance

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
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

    private val fcwStatus: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_FCW
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_FCW_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    private val aebStatus: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_AEB
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_AEB_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(CarCabinManager.ID_FCW_STATUS)
                add(CarCabinManager.ID_AEB_STATUS)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            CarCabinManager.ID_FCW_STATUS -> {
                onSwitchChanged(SwitchNode.ADAS_FCW, property)
            }
            CarCabinManager.ID_AEB_STATUS -> {
                onSwitchChanged(SwitchNode.ADAS_AEB, property)
            }
            else -> {}
        }
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }


    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            SwitchNode.ADAS_FCW -> {
                val value = property.value
                if (value is Int) {
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchStatus(switchNode, fcwStatus, value).get()
                    )
                }
            }
            SwitchNode.ADAS_AEB -> {
                val value = property.value
                if (value is Int) {
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchStatus(switchNode, aebStatus, value).get()
                    )
                }
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.values.forEach {
                val listener = it.get()
                if (null != listener && listener is ISwitchListener) {
                    listener.onSwitchOptionChanged(status, switchNode)
                }
            }
        }
    }


    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
            SwitchNode.ADAS_FCW -> {
                fcwStatus.get()
            }
            SwitchNode.ADAS_AEB -> {
                aebStatus.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            /**FCW status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved*/
            SwitchNode.ADAS_FCW -> {
                val signal = CarCabinManager.ID_FCW_SWT
                doSetProperty(signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.ADAS_AEB -> {
                val signal = CarCabinManager.ID_AEB_SWT
                doSetProperty(signal, switchNode.obtainValue(status), switchNode.origin)
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