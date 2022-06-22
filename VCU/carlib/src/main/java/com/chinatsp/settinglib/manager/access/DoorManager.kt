package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
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
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class DoorManager private constructor() : BaseManager(), IOptionManager {

    private val smartAccess: AtomicBoolean by lazy {
        val node = SwitchNode.DOOR_SMART_ENTER
        AtomicBoolean(node.isOn()).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, value)
        }
    }

    private val driveAutoLock: AtomicInteger by lazy {
        val node = RadioNode.DOOR_DRIVE_LOCK
        AtomicInteger(node.default).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, value)
        }
    }

    private val flameoutAutoUnlock: AtomicInteger by lazy {
        val node = RadioNode.DOOR_FLAMEOUT_UNLOCK
        AtomicInteger(node.default).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, value)
        }
    }

    companion object : ISignal {
        override val TAG: String = DoorManager::class.java.simpleName
        val instance: DoorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DoorManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**行车自动落锁*/
                add(RadioNode.DOOR_DRIVE_LOCK.get.signal)
                /**熄火自动解锁*/
                add(RadioNode.DOOR_FLAMEOUT_UNLOCK.get.signal)
                /**车门智能进入*/
                add(SwitchNode.DOOR_SMART_ENTER.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> {
                smartAccess.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> {
                driveAutoLock.get()
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> {
                flameoutAutoUnlock.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> {
                node.isValid(value, false) && writeProperty(node.set.signal, value, node.set.origin)
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> {
                node.isValid(value, false) && writeProperty(node.set.signal, value, node.set.origin)
            }
            else -> false
        }
    }


    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            /**熄火自动解锁*/
            SwitchNode.DOOR_SMART_ENTER.get.signal -> {
                onSwitchChanged(SwitchNode.DOOR_SMART_ENTER, smartAccess, property)
            }
            RadioNode.DOOR_DRIVE_LOCK.get.signal-> {
                onRadioChanged(RadioNode.DOOR_DRIVE_LOCK, driveAutoLock, property)
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK.get.signal-> {
                onRadioChanged(RadioNode.DOOR_FLAMEOUT_UNLOCK, flameoutAutoUnlock, property)
            }
            else -> {}
        }
    }

}