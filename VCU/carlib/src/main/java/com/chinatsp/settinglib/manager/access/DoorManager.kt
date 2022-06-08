package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.access.IDoorListener
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
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class DoorManager private constructor() : BaseManager(), IOptionManager {

    private val identity by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IDoorListener>>() }

    val smartEnterStatus:AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_SMART_ENTER_DOOR
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_SMART_ENTRY_STS
            val value = doGetIntProperty(signal, switchNode.origin)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    val driveLockOption: AtomicInteger by lazy {
        AtomicInteger(0).apply {
            val signal = CarCabinManager.ID_VSPEED_LOCKING_STATUE
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            set(value)
        }
    }

    val shutDownUnlockOption: AtomicInteger by lazy {
        AtomicInteger(0).apply {
            val signal = CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            set(value)
        }
    }

    companion object : ISignal {
        override val TAG: String = DoorManager::class.java.simpleName
        val instance: DoorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DoorManager()
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
                /**行车自动落锁*/
                add(CarCabinManager.ID_VSPEED_LOCKING_STATUE)
                /**熄火自动解锁*/
                add(CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE)
                /**车门智能进入*/
                add(CarCabinManager.ID_SMART_ENTRY_STS)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }



    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.let {
                if (it.containsKey(serial)) it else null
            }?.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is IDoorListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            return serial
        }
        return -1
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.AS_SMART_ENTER_DOOR -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
    }

    override fun doGetRadioOption(radioNode: RadioNode): Int {
        TODO("Not yet implemented")
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        TODO("Not yet implemented")
    }


    private fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    private fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            /**熄火自动解锁*/
            CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE -> {
                doShutDownAutoUnlockOptionChanged(property)
            }
            CarCabinManager.ID_VSPEED_LOCKING_STATUE -> {
                doDriveAutoLockOptionChanged(property)
            }
            CarCabinManager.ID_SMART_ENTRY_STS -> {
                onSwitchOptionChanged(SwitchNode.AS_SMART_ENTER_DOOR, property)
            }
            else -> {}
        }
    }

    private fun onSwitchOptionChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            val status = switchNode.isOn(value)
            if (smartEnterStatus.get() xor status) {
                smartEnterStatus.set(status)
                synchronized(listenerStore) {
                    listenerStore.filterValues { null != it.get() }.forEach {
                        it.value.get()?.onSwitchOptionChanged(status, switchNode)
                    }
                }
            }
        }
    }

    private fun doShutDownAutoUnlockOptionChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            synchronized(listenerStore) {
                listenerStore.filterValues { null != it.get() }.forEach {
                    it.value.get()?.onShutDownAutoUnlockOptionChanged(value)
                }
            }
        }
    }

    private fun doDriveAutoLockOptionChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            synchronized(listenerStore) {
                listenerStore.filterValues { null != it.get() }.forEach {
                    it.value.get()?.onDriveAutoLockOptionChanged(value)
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


}