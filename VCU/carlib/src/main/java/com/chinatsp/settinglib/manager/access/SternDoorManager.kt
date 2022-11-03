package com.chinatsp.settinglib.manager.access

import android.car.VehicleAreaType
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.AppExecutors
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.listener.IAccessListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class SternDoorManager private constructor() : BaseManager(), IOptionManager, IProgressManager {

    companion object : ISignal {
        override val TAG: String = SternDoorManager::class.java.simpleName
        val instance: SternDoorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SternDoorManager()
        }
    }

    private val electricSwitchState: SwitchState by lazy {
        val node = SwitchNode.AS_STERN_ELECTRIC
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightAlarmSwitchState: SwitchState by lazy {
        val node = SwitchNode.STERN_LIGHT_ALARM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val audioAlarmSwitchState: SwitchState by lazy {
        val node = SwitchNode.STERN_AUDIO_ALARM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val sternSmartEnter: RadioState by lazy {
        val node = RadioNode.STERN_SMART_ENTER
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val stopPosition: Volume by lazy {
        val node = Progress.TRUNK_STOP_POSITION
        Volume(node, node.min, node.max, node.min).apply {
            AppExecutors.get()?.singleIO()?.execute {
                val value = readIntProperty(node.get.signal, node.get.origin)
                doUpdateProgress(stopPosition, value, true, instance::doProgressChanged)
            }
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.AS_STERN_ELECTRIC.get.signal)
                add(SwitchNode.STERN_LIGHT_ALARM.get.signal)
                add(SwitchNode.STERN_AUDIO_ALARM.get.signal)
                add(RadioNode.STERN_SMART_ENTER.get.signal)
                add(Progress.TRUNK_STOP_POSITION.get.signal)
                add(CarCabinManager.ID_HOOD_LID_OPEN)
                add(CarCabinManager.ID_TRUNK_LID_OPEN)
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

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> {
                sternSmartEnter.copy()
            }
            else -> null
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

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.TRUNK_STOP_POSITION -> {
                stopPosition
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        return when (type) {
            Progress.TRUNK_STOP_POSITION -> {
                writeProperty(type.set.signal, position, type.set.origin)
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
            SwitchNode.AS_STERN_ELECTRIC -> electricSwitchState.copy()
            SwitchNode.STERN_LIGHT_ALARM -> lightAlarmSwitchState.copy()
            SwitchNode.STERN_AUDIO_ALARM -> audioAlarmSwitchState.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        val result = when (node) {
            SwitchNode.STERN_AUDIO_ALARM -> audioAlarmSwitchState
            SwitchNode.STERN_LIGHT_ALARM -> lightAlarmSwitchState
            SwitchNode.AS_STERN_ELECTRIC -> electricSwitchState
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
                onSwitchChanged(SwitchNode.AS_STERN_ELECTRIC, electricSwitchState, property)
            }
            SwitchNode.STERN_LIGHT_ALARM.get.signal -> {
                onSwitchChanged(SwitchNode.STERN_LIGHT_ALARM, lightAlarmSwitchState, property)
            }
            SwitchNode.STERN_AUDIO_ALARM.get.signal -> {
                onSwitchChanged(SwitchNode.STERN_AUDIO_ALARM, audioAlarmSwitchState, property)
            }
            RadioNode.STERN_SMART_ENTER.get.signal -> {
                onRadioChanged(RadioNode.STERN_SMART_ENTER, sternSmartEnter, property)
            }
            Progress.TRUNK_STOP_POSITION.get.signal -> {
                doUpdateProgress(stopPosition, property.value as Int, true, this::doProgressChanged)
            }

            CarCabinManager.ID_HOOD_LID_OPEN -> {
                onDoorStatusChanged(IPart.HEAD, Model.ACCESS_STERN, property.value)
            }
            CarCabinManager.ID_TRUNK_LID_OPEN -> {
                onDoorStatusChanged(IPart.TAIL, Model.ACCESS_STERN, property.value)
            }
            else -> {}
        }
    }

    private fun onDoorStatusChanged(@IPart part: Int, @Model model: Int, value: Any?) {
        if (value is Int) {
            val readLock = readWriteLock.readLock()
            try {
                readLock.lock()
                listenerStore.forEach { (_, ref) ->
                    val listener = ref.get()
                    if (null != listener && listener is IAccessListener) {
                        listener.onAccessChanged(part, model, value)
                        Timber.d("onDoorStatusChanged part:$part, model:$model, value:$value, listener:${listener::class.java.simpleName}")
                    }
                }
            } finally {
                readLock.unlock()
            }
        }
    }

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?) {
        if (Model.ACCESS_STERN != command.model) {
            return
        }
        Timber.d("doOuterControlCommand $command")
        if (ICar.DOORS == command.car) {
            if (IPart.HEAD == command.part) {
                doControlHood(command, callback)
                return
            }
            if (IPart.TAIL == command.part) {
                doControlTrunk(command, callback)
                return
            }
        }
    }

    private fun doControlHood(command: CarCmd, callback: ICmdCallback?) {
//        if (!VcuUtils.isSupportFunction(OffLine.ETRUNK)) {
//            command.message = "您的爱车不支持此功能！"
//            return
//        }
        if (Action.OPEN == command.action) {
            doTrunkAction(isTrunkOpened(), isTrunkOpening(), 1)
            command.message = "${command.slots?.name}已打开"
            callback?.onCmdHandleResult(command)
        } else if (Action.CLOSE == command.action) {
            doTrunkAction(isTrunkClosed(), isTrunkClosing(), 0)
            command.message = "${command.slots?.name}已关闭"
            callback?.onCmdHandleResult(command)
        }
    }

    private fun doControlTrunk(command: CarCmd, callback: ICmdCallback?) {
//        if (!VcuUtils.isSupportFunction(OffLine.ETRUNK)) {
//            command.message = "您的爱车不支持此功能！"
//            return
//        }
        val signal = CarCabinManager.ID_AVN_TRUNK_RELEASE
        if (Action.OPEN == command.action) {
//            doTrunkAction(isTrunkOpened(), isTrunkOpening(), 1)
//        AVN request trunk release.
//        0x0: Inactive   0x1: Not released   0x2: Released   0x3: Not used
            val value = 0x2
            writeProperty(signal, value, Origin.CABIN)
            command.message = "${command.slots?.name}已打开"
            callback?.onCmdHandleResult(command)
        } else if (Action.CLOSE == command.action) {
//            doTrunkAction(isTrunkClosed(), isTrunkClosing(), 0)
            val value = 0x1
            writeProperty(signal, value, Origin.CABIN)
            command.message = "${command.slots?.name}已关闭"
            callback?.onCmdHandleResult(command)
        }
    }

    private fun doTrunkAction(complete: Boolean, running: Boolean, value: Int) {
        if (!complete && !running) {
            //1表示press,发起打开请求
            writeProperty(
                CarCabinManager.ID_HU_BACKDOORSWITCH,
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
    private fun isTrunkOpened(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x01 == value
    }

    /**
     * 尾门是否正在打开中
     * @return true 表示打开中
     */
    private fun isTrunkOpening(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x03 == value
    }

    /**
     * 尾门是否已经关闭
     * @return true 表示已经关闭
     */
    private fun isTrunkClosed(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x02 == value
    }

    /**
     * 尾门是否正在关闭中
     * @return true 表示关闭中
     */
    private fun isTrunkClosing(): Boolean {
        val value = getTrunkStatusValue()
        return isValidValue(value, 0x00, 0x06, 0x07) && 0x04 == value
    }

    private fun isValidValue(value: Int, vararg arrays: Int): Boolean {
        return !arrays.contains(value)
    }

}