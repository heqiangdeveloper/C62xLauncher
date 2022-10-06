package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class DoorManager private constructor() : BaseManager(), IOptionManager {

    private val smartAccess: SwitchState by lazy {
        val node = SwitchNode.DOOR_SMART_ENTER
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val driveAutoLock: RadioState by lazy {
        val node = RadioNode.DOOR_DRIVE_LOCK
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val flameoutAutoUnlock: RadioState by lazy {
        val node = RadioNode.DOOR_FLAMEOUT_UNLOCK
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
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

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> smartAccess.copy()
            else -> null
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

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> {
                driveAutoLock.copy()
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> {
                flameoutAutoUnlock.copy()
            }
            else -> null
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
            RadioNode.DOOR_DRIVE_LOCK.get.signal -> {
                onRadioChanged(RadioNode.DOOR_DRIVE_LOCK, driveAutoLock, property)
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK.get.signal -> {
                onRadioChanged(RadioNode.DOOR_FLAMEOUT_UNLOCK, flameoutAutoUnlock, property)
            }
            else -> {}
        }
    }

}