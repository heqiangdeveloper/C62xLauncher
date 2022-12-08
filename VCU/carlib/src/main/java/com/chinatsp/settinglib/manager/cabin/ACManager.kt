package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.bean.AirCmd
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class ACManager private constructor() : BaseManager(), IOptionManager {

    private val airSupplier: AirSupplier by lazy { AirSupplier(this) }

    companion object : ISignal {
        override val TAG: String = ACManager::class.java.simpleName
        val instance: ACManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ACManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**空调自干燥*/
                add(SwitchNode.AC_AUTO_ARID.get.signal)
                /**预通风功能*/
                add(SwitchNode.AC_ADVANCE_WIND.get.signal)
                add(SwitchNode.AC_AUTO_DEMIST.get.signal)
                /**空调舒适性状态显示*/
                add(RadioNode.AC_COMFORT.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val aridStatus: SwitchState by lazy {
        val node = SwitchNode.AC_AUTO_ARID
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val demistStatus: SwitchState by lazy {
        val node = SwitchNode.AC_AUTO_DEMIST
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val windStatus: SwitchState by lazy {
        val node = SwitchNode.AC_ADVANCE_WIND
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val comfortOption: RadioState by lazy {
        val node = RadioNode.AC_COMFORT
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.AC_COMFORT -> comfortOption.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.AC_COMFORT -> {
                node.isValid(value, false)
                        && writeProperty(node.set.signal, value, node.set.origin, node.area)
            }
            else -> false
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
            SwitchNode.AC_AUTO_ARID -> aridStatus.deepCopy()
            SwitchNode.AC_ADVANCE_WIND -> windStatus.deepCopy()
            SwitchNode.AC_AUTO_DEMIST -> demistStatus.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            SwitchNode.AC_AUTO_DEMIST.get.signal -> {
                onSwitchChanged(SwitchNode.AC_AUTO_DEMIST, demistStatus, property)
//                onSwitchOptionChanged(SwitchNode.AC_AUTO_DEMIST, property.value)
            }
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            SwitchNode.AC_AUTO_ARID.get.signal -> {
                onSwitchChanged(SwitchNode.AC_AUTO_ARID, aridStatus, property)
            }
            //预通风功能
            SwitchNode.AC_ADVANCE_WIND.get.signal -> {
                onSwitchChanged(SwitchNode.AC_ADVANCE_WIND, windStatus, property)
            }
            //自动空调舒适性
            RadioNode.AC_COMFORT.get.signal -> {
                onRadioChanged(RadioNode.AC_COMFORT, comfortOption, property)
            }
            else -> {}
        }
    }

    override fun doAirControlCommand(command: AirCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val airCmdParcel = CommandParcel(command, callback, receiver = airSupplier)
        val mask = Action.OPEN
        if ((mask != command.action) && mask == (mask and command.action)) {
            airCmdParcel.retryCount = 2
        }
        airSupplier.doCommandExpress(airCmdParcel)
    }


}