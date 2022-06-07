package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IRadioManager
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
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class MeterManager private constructor() : BaseManager(), IRadioManager {

    private val fortifySoundSignal = CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE

    private val identity by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }


    val fortifySoundStatus: AtomicBoolean by lazy {
        val switchNode = SwitchNode.DRIVE_SAFE_FORTIFY_SOUND
        AtomicBoolean(switchNode.isOn()).apply {
            val value = doGetIntProperty(fortifySoundSignal, switchNode.origin, switchNode.area)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**设防提示音 开关*/
                add(CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }


    override fun onHandleConcernedSignal(property: CarPropertyValue<*>, signalOrigin: SignalOrigin):
            Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            else -> {}
        }
        return false
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }


    fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //设防提示音
            CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE -> {
                onSwitchChanged(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, property)
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> {
                val value = property.value
                if (value is Int) {
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchStatus(switchNode, fortifySoundStatus, value).get()
                    )
                }
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.filterValues { null != it.get() }.forEach { (_, u) ->
                val listener = u.get()
                if (listener is ISwitchListener) {
                    listener.onSwitchOptionChanged(status, switchNode)
                }
            }
        }
    }

    private fun doUpdateSwitchStatus(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int
    ): AtomicBoolean {
        if (node.isValidValue(value)) {
            val status = node.isOn(value)
            if (atomic.get() xor status) {
                atomic.set(status)
            }
        }
        return atomic
    }

    companion object : ISignal {
        override val TAG: String = MeterManager::class.java.simpleName
        val instance: MeterManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MeterManager()
        }
    }

    override fun doGetRadioOption(radioNode: RadioNode): Int {
        TODO("Not yet implemented")
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        return when (radioNode) {
            RadioNode.DRIVE_METER_SYSTEM -> {
                doSetProperty(-1, value, SignalOrigin.CABIN_SIGNAL)
            }
            else -> false
        }
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        TODO("Not yet implemented")
    }

}