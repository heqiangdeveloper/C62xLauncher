package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISignalListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IAccessManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class DoorManager private constructor() : BaseManager(), IOptionManager, IAccessManager {

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
                add(RadioNode.DOOR_DRIVE_LOCK.get.signal)
                /**行车自动落锁*/
                add(RadioNode.DOOR_FLAMEOUT_UNLOCK.get.signal)
                /**熄火自动解锁*/
                add(SwitchNode.DOOR_SMART_ENTER.get.signal)
                /**车门智能进入*/

                add(CarCabinManager.ID_DR_DOOR_OPEN)
                add(CarCabinManager.ID_PA_DOOR_OPEN)
                add(CarCabinManager.ID_REAR_LEFT_DOOR_OPEN)
                add(CarCabinManager.ID_REAR_RIGHT_DOOR_OPEN)
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
            listenerStore[serial] = WeakReference(listener)
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> smartAccess.deepCopy()
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
                driveAutoLock.deepCopy()
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> {
                flameoutAutoUnlock.deepCopy()
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

    override fun onCabinPropertyChanged(p: CarPropertyValue<*>) {
        when (p.propertyId) {
            /**熄火自动解锁*/
            SwitchNode.DOOR_SMART_ENTER.get.signal -> {
                onSwitchChanged(SwitchNode.DOOR_SMART_ENTER, smartAccess, p)
            }
            RadioNode.DOOR_DRIVE_LOCK.get.signal -> {
                onRadioChanged(RadioNode.DOOR_DRIVE_LOCK, driveAutoLock, p)
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK.get.signal -> {
                onRadioChanged(RadioNode.DOOR_FLAMEOUT_UNLOCK, flameoutAutoUnlock, p)
            }
            CarCabinManager.ID_DR_DOOR_OPEN -> {
                onSignalChanged(IPart.L_F, Model.ACCESS_DOOR, p.propertyId, p.value as Int)
            }
            CarCabinManager.ID_PA_DOOR_OPEN -> {
                onSignalChanged(IPart.R_F, Model.ACCESS_DOOR, p.propertyId, p.value as Int)
            }
            CarCabinManager.ID_REAR_LEFT_DOOR_OPEN -> {
                onSignalChanged(IPart.L_B, Model.ACCESS_DOOR, p.propertyId, p.value as Int)
            }
            CarCabinManager.ID_REAR_RIGHT_DOOR_OPEN -> {
                onSignalChanged(IPart.R_B, Model.ACCESS_DOOR, p.propertyId, p.value as Int)
            }
            else -> {}
        }
    }

    private fun onSignalChanged(@IPart part: Int, @Model model: Int, signal: Int, value: Int) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISignalListener) {
                    listener.onSignalChanged(part, model, signal, value)
                }
            }
        } finally {
            readLock.unlock()
        }
    }

    private fun doControlDoors(command: CarCmd, callback: ICmdCallback?) {
        if (IPart.HEAD == command.part) {
            if (Action.OPEN == command.action) {
                doSwitchHood(command, callback, true)
                return
            }
            if (Action.CLOSE == command.action) {
                doSwitchHood(command, callback, false)
                return
            }
        }
        if (IPart.TAIL == command.part) {
            if (Action.OPEN == command.action) {
                doSwitchTrunks(command, callback, true)
                return
            }
            if (Action.CLOSE == command.action) {
                doSwitchTrunks(command, callback, false)
                return
            }
        }
    }

    private fun doSwitchHood(command: CarCmd, callback: ICmdCallback?, expect: Boolean) {
        val onOff = if (expect) "打开" else "关闭"
        command.message = "好的，${command.slots?.name}${onOff}了"
        callback?.onCmdHandleResult(command)
    }

    private fun doSwitchTrunks(command: CarCmd, callback: ICmdCallback?, expect: Boolean) {
        val onOff = if (expect) "打开" else "关闭"
        command.message = "好的，${command.slots?.name}${onOff}了"
//        AVN request trunk release.
//        0x0: Inactive   0x1: Not released   0x2: Released   0x3: Not used
        val value = if (expect) 0x2 else 0x1
        writeProperty(CarCabinManager.ID_AVN_TRUNK_RELEASE, value, Origin.CABIN)
        callback?.onCmdHandleResult(command)
    }

    override fun obtainAccessState(part: Int, model: Int): Int? {
        if (Model.ACCESS_DOOR != model){
            return null
        }
        return when (part) {
            IPart.L_F -> readIntProperty(CarCabinManager.ID_DR_DOOR_OPEN, Origin.CABIN)
            IPart.R_F -> readIntProperty(CarCabinManager.ID_PA_DOOR_OPEN, Origin.CABIN)
            IPart.L_B -> readIntProperty(CarCabinManager.ID_REAR_LEFT_DOOR_OPEN, Origin.CABIN)
            IPart.R_B -> readIntProperty(CarCabinManager.ID_REAR_RIGHT_DOOR_OPEN, Origin.CABIN)
            else -> null
        }
    }

}