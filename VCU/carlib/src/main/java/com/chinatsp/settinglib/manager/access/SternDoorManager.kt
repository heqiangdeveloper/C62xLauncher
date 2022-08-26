package com.chinatsp.settinglib.manager.access

import android.car.VehicleAreaType
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.bean.Cmd
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


class SternDoorManager private constructor() : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = SternDoorManager::class.java.simpleName
        val instance: SternDoorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SternDoorManager()
        }
    }

    private val _electricFunction: AtomicBoolean by lazy {
        val node = SwitchNode.AS_STERN_ELECTRIC
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) {result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val _lightAlarmFunction: AtomicBoolean by lazy {
        val node = SwitchNode.STERN_LIGHT_ALARM
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) {result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val _audioAlarmFunction: AtomicBoolean by lazy {
        val node = SwitchNode.STERN_AUDIO_ALARM
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) {result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val sternSmartEnter: AtomicInteger by lazy {
        val node = RadioNode.STERN_SMART_ENTER
//        AtomicInteger(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, result)
//        }
        return@lazy createAtomicInteger(node) {result, value ->
            doUpdateRadioValue(node, result, value, this::doRadioChanged)
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.AS_STERN_ELECTRIC.get.signal)
                add(SwitchNode.STERN_LIGHT_ALARM.get.signal)
                add(SwitchNode.STERN_AUDIO_ALARM.get.signal)
                add(RadioNode.STERN_SMART_ENTER.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> {
                sternSmartEnter.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> {
                node.isValid(value, false) && writeProperty(node.set.signal, value, node.set.origin)
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
            listenerStore.put(serial, WeakReference(listener))
        } finally {
            writeLock.unlock()
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                _electricFunction.get()
            }
            SwitchNode.STERN_LIGHT_ALARM -> {
                _lightAlarmFunction.get()
            }
            SwitchNode.STERN_AUDIO_ALARM -> {
                _audioAlarmFunction.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        val result = when (node) {
            SwitchNode.STERN_AUDIO_ALARM -> _audioAlarmFunction
            SwitchNode.STERN_LIGHT_ALARM -> _lightAlarmFunction
            SwitchNode.AS_STERN_ELECTRIC -> _electricFunction
            else -> null
        }
        return result?.let {
            val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
            if (success) {
                doUpdateSwitchValue(node, result, status)
            }
            return@let success
        } ?: false
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.AS_STERN_ELECTRIC.get.signal -> {
                onSwitchChanged(SwitchNode.AS_STERN_ELECTRIC, _electricFunction, property)
            }
            SwitchNode.STERN_LIGHT_ALARM.get.signal -> {
                onSwitchChanged(SwitchNode.STERN_LIGHT_ALARM, _lightAlarmFunction, property)
            }
            SwitchNode.STERN_AUDIO_ALARM.get.signal -> {
                onSwitchChanged(SwitchNode.STERN_AUDIO_ALARM, _audioAlarmFunction, property)
            }
            RadioNode.STERN_SMART_ENTER.get.signal -> {
                onRadioChanged(RadioNode.STERN_SMART_ENTER, sternSmartEnter, property)
            }
            else -> {}
        }
    }

    override fun doOuterControlCommand(cmd: Cmd, callback: ICmdCallback?) {
        if (Action.OPEN == cmd.action) {
            doControlTrunk(cmd, callback, true)
            callback?.onCmdHandleResult(cmd)
        } else if (Action.CLOSE == cmd.action) {
            doControlTrunk(cmd, callback, false)
            callback?.onCmdHandleResult(cmd)
        }
    }

    private fun doControlTrunk(cmd: Cmd, callback: ICmdCallback?, open: Boolean) {
        if (!VcuUtils.isSupportFunction(OffLine.ETRUNK)) {
            cmd.message = "您的爱车不支持此功能！"
            return
        }
        if (open) {
            doTrunkAction(isTrunkOpened(), isTrunkOpening(), 1)
            cmd.message = "电动尾门已打开"
        } else {
            doTrunkAction(isTrunkClosed(), isTrunkClosing(), 0)
            cmd.message = "电动尾门已关闭"
        }
    }

    private fun doTrunkAction(complete: Boolean, running: Boolean, value: Int) {
        if (!complete && !running) {
            //1表示press,发起打开请求
            writeProperty(CarCabinManager.ID_HU_BACKDOORSWITCH,
                value, Origin.CABIN, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)
        }
    }

    /**
     * 获取电动尾门 打开的位置
     */
    fun getTrunkOpenLevel(): Int {
        val signal = CarCabinManager.ID_BODY_DOOR_TRUNK_DOOR_POS;
        //VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
        return readIntProperty(signal, Origin.CABIN, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)
    }

    /**
     * 获取尾门状态
     *
     * @return 0x0=Unkonw；
     * 0x1=FullyOpened；
     * 0x2=FullyClosed；
     * 0x3=Opening；
     * 0x4=Closing；
     * 0x5=Stop；
     * 0x6=Reserved；
     * 0x7=Reserved
     */
    private fun getTrunkStatusValue(): Int {
        val signal = CarCabinManager.ID_BODY_DOOR_TRUNK_DOOR_STATE;
        //VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
        return readIntProperty(signal, Origin.CABIN, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)
    }

    /**
     * 尾门是否已经打开
     * @return true 表示已经打开
     */
    fun isTrunkOpened(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x01 == value
    }
    /**
     * 尾门是否正在打开中
     * @return true 表示打开中
     */
    fun isTrunkOpening(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x03 == value
    }

    /**
     * 尾门是否已经关闭
     * @return true 表示已经关闭
     */
    fun isTrunkClosed(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x02 == value
    }
    /**
     * 尾门是否正在关闭中
     * @return true 表示关闭中
     */
    fun isTrunkClosing(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x04 == value
    }

    private fun isValidValue(value: Int, vararg arrays: Int): Boolean {
        return !arrays.contains(value)
    }

}