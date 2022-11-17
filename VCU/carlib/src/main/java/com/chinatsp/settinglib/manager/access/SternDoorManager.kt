package com.chinatsp.settinglib.manager.access

import android.car.VehicleAreaType
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.AppExecutors
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IAccessListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.*
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class SternDoorManager private constructor() : BaseManager(), IOptionManager, IProgressManager, ICmdExpress {

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

    private val gearsState: RadioState by lazy {
        val node = RadioNode.GEARS
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
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
                add(RadioNode.GEARS.get.signal)
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
            RadioNode.STERN_SMART_ENTER -> sternSmartEnter.copy()
            RadioNode.GEARS -> gearsState.copy()
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
            RadioNode.GEARS.get.signal -> {
                onRadioChanged(RadioNode.GEARS, gearsState, property)
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

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
    }

    private fun doControlHood(parcel: CommandParcel) {
        val command = parcel.command
        if (Action.OPEN == command.action) {
            doTrunkAction(isTrunkOpened(), isTrunkOpening(), 1)
            command.message = "${command.slots?.name}已打开"
            parcel.callback?.onCmdHandleResult(command)
        } else if (Action.CLOSE == command.action) {
            doTrunkAction(isTrunkClosed(), isTrunkClosing(), 0)
            command.message = "${command.slots?.name}已关闭"
            parcel.callback?.onCmdHandleResult(command)
        }
    }

    private fun doControlTrunk(parcel: CommandParcel) {
//        0x1: ON;  0x2: OFF;  0x3: Stop
        val signal = CarCabinManager.ID_PTM_OPER_CMD
        val command = parcel.command
        val state = obtainTrunkState()
        val name = command.slots?.name ?: "电动尾门"
        val isRetry = parcel.isRetry()
        if (IStatus.INIT == command.status) {
            command.lfCount = 1
            parcel.retryCount = 40
        }
        if (Action.OPEN == command.action) {
            val opening = isStandard(state, 0x3)
            if (opening) {
                command.message = "${name}开启中"
                if (IStatus.INIT == command.status) {
                    parcel.callback?.onCmdHandleResult(command)
                    return
                } else {
                    ShareHandler.loopParcel(parcel, ShareHandler.HIG_DELAY)
                    return
                }
            }
            val opened = isStandard(state, 0x1)
            if (opened) {
                command.message = "${name}已打开"
                parcel.callback?.onCmdHandleResult(command)
                return
            }
//            val stopped = isStandard(state, 0x0)
//            val closing = isStandard(state, 0x4)
            if (!command.isSent()) {
                command.status = IStatus.RUNNING
                writeProperty(signal, 0x1, Origin.CABIN)
                command.sent()
            }
            if (isRetry) {
                ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
            } else {
                command.message = Keywords.COMMAND_FAILED
                parcel.callback?.onCmdHandleResult(command)
            }
        } else if (Action.CLOSE == command.action) {
            val value = 0x1
            val closing = isStandard(state, 0x4)
            if (closing) {
                command.message = "${name}关闭中"
                if (IStatus.INIT == command.status) {
                    parcel.callback?.onCmdHandleResult(command)
                    return
                } else {
                    ShareHandler.loopParcel(parcel, ShareHandler.HIG_DELAY)
                    return
                }
            }
            val closed = isStandard(state, 0x2)
            if (closed) {
                command.message = "${name}已关闭"
                parcel.callback?.onCmdHandleResult(command)
                return
            }
//            val stopped = isStandard(state, 0x0)
//            val opening = isStandard(state, 0x3)
            if (!command.isSent()) {
                command.status = IStatus.RUNNING
                writeProperty(signal, 0x2, Origin.CABIN)
                command.sent()
            }
            if (isRetry) {
                ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
            } else {
                command.message = Keywords.COMMAND_FAILED
                parcel.callback?.onCmdHandleResult(command)
            }
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

    private fun obtainTrunkState(): Int {
        val signal = CarCabinManager.ID_PTM_OPERATE_STATUS
//        0x0: Stop; 0x1: Open; 0x2: close; 0x3: opening; 0x4: Closing; 0x5~0x7:Reserved
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun isStandard(actual: Int, expect: Int): Boolean = actual == expect

    /**
     * 获取挡位信息
     *
     * @return 0x0=Unkonw；
     */
    fun getGearsValue(node: SwitchNode): SwitchState? {
        val signal = CarCabinManager.ID_TCU_TARGETGEAR
        // return when (node) {
        //            SwitchNode.AS_STERN_ELECTRIC -> electricSwitchState.copy()
        //            SwitchNode.STERN_LIGHT_ALARM -> lightAlarmSwitchState.copy()
        //            SwitchNode.STERN_AUDIO_ALARM -> audioAlarmSwitchState.copy()
        //            else -> null
        //        }
        //return readIntProperty(signal, Origin.CABIN, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)
        return when (node) {
//            SwitchNode.GEARS -> electricSwitchState.copy()
            SwitchNode.STERN_LIGHT_ALARM -> lightAlarmSwitchState.copy()
            SwitchNode.STERN_AUDIO_ALARM -> audioAlarmSwitchState.copy()
            else -> null
        }
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

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        if (ICar.DOORS == command.car) {
            val callback = parcel.callback
            if (IPart.HEAD == command.part) {
                doControlHood(parcel)
                return
            }
            if (IPart.TAIL == command.part) {
                doControlTrunk(parcel)
                return
            }
        }
    }

}