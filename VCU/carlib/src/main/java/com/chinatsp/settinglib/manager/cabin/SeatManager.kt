package com.chinatsp.settinglib.manager.cabin

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAct
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.CarCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class SeatManager private constructor() : BaseManager(), ISoundManager {

    companion object : ISignal {
        override val TAG: String = SeatManager::class.java.simpleName
        val instance: SeatManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SeatManager()
        }
    }

    private val mainMeetFunction: SwitchState by lazy {
        val node = SwitchNode.SEAT_MAIN_DRIVE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val forkMeetFunction: SwitchState by lazy {
        val node = SwitchNode.SEAT_FORK_DRIVE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val seatHeatFunction: SwitchState by lazy {
        val node = SwitchNode.SEAT_HEAT_ALL
        return@lazy createAtomicBoolean(node, Constant.SEAT_HEAT_SWITCH) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val seatHeatStartTemp: Volume by lazy {
        initVolume(Progress.SEAT_ONSET_TEMPERATURE)
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.SEAT_MAIN_DRIVE_MEET.get.signal)
                add(SwitchNode.SEAT_FORK_DRIVE_MEET.get.signal)
                add(SwitchNode.SEAT_HEAT_ALL.get.signal)
                add(Progress.SEAT_ONSET_TEMPERATURE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private fun initVolume(type: Progress): Volume {
        val pos = type.def
        val result = VcuUtils.getInt(key = Constant.SEAT_HEAT_TEMP, value = pos)
//        AppExecutors.get()?.singleIO()?.execute {
//            val result = VcuUtils.getInt(key = Constant.SEAT_HEAT_TEMP, value = pos)
//            doUpdateProgress(volume, result, true, instance::doProgressChanged)
//        }
        return Volume(type, type.min, type.max, result)
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.SEAT_MAIN_DRIVE_MEET, mainMeetFunction, property)
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.SEAT_FORK_DRIVE_MEET, forkMeetFunction, property)
            }
            SwitchNode.SEAT_HEAT_ALL.get.signal -> {
//                onSwitchChanged(SwitchNode.SEAT_HEAT_ALL, seatHeatFunction, property)
            }
            else -> {}
        }
    }


    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return null
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }


    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> mainMeetFunction.copy()
            SwitchNode.SEAT_FORK_DRIVE_MEET -> forkMeetFunction.copy()
            SwitchNode.SEAT_HEAT_ALL -> seatHeatFunction.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> {
                writeProperty(node, status, mainMeetFunction)
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                writeProperty(node, status, forkMeetFunction)
            }
            SwitchNode.SEAT_HEAT_ALL -> {
                val result = writeProperty(node, status, seatHeatFunction)
                if (result) {
                    val value = node.value(status, isGet = true)
                    VcuUtils.putInt(key = Constant.SEAT_HEAT_SWITCH, value = value)
                    doUpdateSwitchValue(node, seatHeatFunction, status, this::doSwitchChanged)
                }
                return result
            }
            else -> false
        }
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.SEAT_ONSET_TEMPERATURE -> {
                seatHeatStartTemp
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        return when (type) {
            Progress.SEAT_ONSET_TEMPERATURE -> {
                val result = writeProperty(seatHeatStartTemp, position)
                if (result) {
                    VcuUtils.putInt(key = Constant.SEAT_HEAT_TEMP, value = position)
                    seatHeatStartTemp.pos = position
                }
                return true
            }
            else -> false
        }
    }

    private fun writeProperty(volume: Volume, value: Int): Boolean {
        val success =
            volume.isValid(value) && writeProperty(volume.type.set.signal, value, Origin.CABIN)
        if (success && develop) {
            volume.pos = value
//            doRangeChanged(volume)
        }
        return success
    }

    private fun writeProperty(node: SwitchNode, status: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, status) { _node, _status ->
                doSwitchChanged(_node, _status)
            }
        }
        return success
    }

    private fun obtainHeatLevel(@IPart part: Int, defArea: Int = Constant.INVALID): Int {
//        int类型数据
//        0x0:Inactive
//        0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = if (Constant.INVALID == defArea) obtainChairArea(part) else defArea
        return readIntProperty(CarCabinManager.ID_HUM_SEAT_HEAT_POS, Origin.CABIN, areaValue)
    }

    private fun updateHeatLevel(@IPart part: Int, level: Int): Boolean {
//        int类型数据
//        0x0:Inactive
//        0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = obtainChairArea(part)
        val actual = -1//obtainHeatLevel(part, areaValue)
        val result = level != actual
        if (result) {
            writeProperty(CarCabinManager.ID_HUM_SEAT_HEAT_POS, level, Origin.CABIN, areaValue)
        }
        return result
    }

    private fun obtainVentilateLevel(@IPart part: Int, defArea: Int = Constant.INVALID): Int {
//        int类型数据
//        0x0:Inactive
//        0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = if (Constant.INVALID == defArea) obtainChairArea(part) else defArea
        return readIntProperty(CarCabinManager.ID_HUM_SEAT_HEAT_POS, Origin.CABIN, areaValue)
    }

    private fun updateVentilateLevel(@IPart part: Int, level: Int): Boolean {
//        int类型数据
//        0x0:Inactive
//        0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = obtainChairArea(part)
        val actual = -1//obtainVentilateLevel(part, areaValue)
        val result = level != actual
        if (result) {
            writeProperty(CarCabinManager.ID_HUM_SEAT_VENT_POS, level, Origin.CABIN, areaValue)
        }
        return result
    }

    private fun obtainChairArea(@IPart part: Int): Int {
        return when (part) {
            IPart.LEFT_FRONT -> VehicleAreaSeat.SEAT_ROW_1_LEFT
            IPart.LEFT_BACK -> VehicleAreaSeat.SEAT_ROW_2_LEFT
            IPart.RIGHT_FRONT -> VehicleAreaSeat.SEAT_ROW_1_RIGHT
            IPart.RIGHT_BACK -> VehicleAreaSeat.SEAT_ROW_2_RIGHT
            else -> VehicleAreaSeat.SEAT_ROW_1_LEFT
        }
    }

    private fun updateKneadLevel(@IPart part: Int, level: Int): Boolean {
        val setSignal = obtainChairSetSignal(part)
        if (Constant.INVALID != setSignal) {
            val actual = obtainChairGetSignal(part)
            val result = actual != level
            if (result) {
                writeProperty(setSignal, level, Origin.CABIN)
            }
            return result
        }
        return false
    }

    private fun obtainChairSetSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.LEFT_FRONT -> CarCabinManager.ID_HUM_SEATMASSLVL_FL
            IPart.RIGHT_FRONT -> CarCabinManager.ID_HUM_SEATMASSLVL_FR
            IPart.LEFT_BACK -> -1
            IPart.RIGHT_BACK -> -1
            else -> Constant.INVALID
        }
    }

    private fun obtainChairGetSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.LEFT_FRONT -> -1
            IPart.RIGHT_FRONT -> -1
            IPart.LEFT_BACK -> CarCabinManager.ID_RLSSM_SEAT_LVL_SET_STS
            IPart.RIGHT_BACK -> CarCabinManager.ID_RRSSM_SEAT_LVL_SET_STS
            else -> Constant.INVALID
        }
    }

    private fun obtainChairKneadLevel(@IPart part: Int): Int {
        val signal = obtainChairGetSignal(part)
        if (Constant.INVALID != signal) {
            return readIntProperty(signal, Origin.CABIN)
        }
        return Constant.INVALID
    }


    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?) {
        if (ICar.CHAIR == command.car) {
            if (IAct.HEAT == command.act) {
                doControlChairHeat(command, callback)
                return
            }
            if (IAct.COLD == command.act) {
                doControlChairVentilate(command, callback)
                return
            }
            if (IAct.KNEAD == command.act) {
                doControlChairKnead(command, callback)
                return
            }
            if (IAct.TILT == command.act) {
                doControlChairTilt(command, callback)
                return
            }
        }
    }

    private fun doControlChairTilt(command: CarCmd, callback: ICmdCallback?) {


    }

    private fun doControlChairKnead(command: CarCmd, callback: ICmdCallback?) {
        var level = Constant.INVALID
        val min = 0x2
        val max = 0x5
        var append = ""
        if (Action.TURN_ON == command.action) {
            level = 0x4
            append = "打开"
        }
        if (Action.TURN_OFF == command.action) {
            level = max
            append = "关闭"
        }
        if (Action.FIXED == command.action) {
            level = command.value
            if (level <= 0) {
                level = max
            } else {
                level += 0x1
                if (level > 0x4) {
                    level = 0x4
                }
            }
            append = when (level) {
                max -> "关闭"
                0x4 -> "调整到最高"
                else -> "调整到${level - 0x1}级"
            }
        }
        var mask = IPart.LEFT_FRONT
        val isLF = if (mask == (mask and command.part)) updateKneadLevel(mask, level) else false
        mask = IPart.LEFT_BACK
        val isLB = if (mask == (mask and command.part)) updateKneadLevel(mask, level) else false
        mask = IPart.RIGHT_FRONT
        val isRF = if (mask == (mask and command.part)) updateKneadLevel(mask, level) else false
        mask = IPart.RIGHT_BACK
        val isRB = if (mask == (mask and command.part)) updateKneadLevel(mask, level) else false
        if (isLF || isLB || isRF || isRB) {
            command.message = "好的，${command.slots?.mode}${append}了"
        } else {
            command.message = "好的，${command.slots?.mode}已经${append}了"
        }
        callback?.onCmdHandleResult(command)
    }

    private fun doControlChairVentilate(command: CarCmd, callback: ICmdCallback?) {
        var level = Constant.INVALID
        val min = 0x1
        val max = 0x4
        var append = ""
        if (Action.TURN_ON == command.action) {
            level = max
            append = "打开"
        }
        if (Action.TURN_OFF == command.action) {
            level = min
            append = "关闭"
        }
        if (Action.FIXED == command.action) {
            level = command.value + min
            if (level < min) level = min
            if (level > max) level = max
            append = when (level) {
                min -> "关闭"
                max -> "调整到最高"
                else -> "调整到${level - 1}级"
            }
        }
        var mask = IPart.LEFT_FRONT
        val isLF = if (mask == (mask and command.part)) updateVentilateLevel(mask, level) else false
        mask = IPart.LEFT_BACK
        val isLB = if (mask == (mask and command.part)) updateVentilateLevel(mask, level) else false
        mask = IPart.RIGHT_FRONT
        val isRF = if (mask == (mask and command.part)) updateVentilateLevel(mask, level) else false
        mask = IPart.RIGHT_BACK
        val isRB = if (mask == (mask and command.part)) updateVentilateLevel(mask, level) else false
        if (isLF || isLB || isRF || isRB) {
            command.message = "好的，${command.slots?.name}通风${append}了"
        } else {
            command.message = "好的，${command.slots?.name}通风已经${append}了"
        }
        callback?.onCmdHandleResult(command)
    }

    private fun doControlChairHeat(command: CarCmd, callback: ICmdCallback?) {
        var level = Constant.INVALID
        val min = 0x1
        val max = 0x4
        var append = ""
        if (Action.TURN_ON == command.action) {
            level = 0x3
            append = "打开"
        }
        if (Action.TURN_OFF == command.action) {
            level = 0x1
            append = "关闭"
        }
        if (Action.FIXED == command.action) {
            level = command.value + min
            if (level < min) level = min
            if (level > max) level = max
            append = when (level) {
                min -> "关闭"
                max -> "调整到最高"
                else -> "调整到${level - 1}级"
            }
        }
        var mask = IPart.LEFT_FRONT
        val isLF = if (mask == (mask and command.part)) updateHeatLevel(mask, level) else false
        mask = IPart.LEFT_BACK
        val isLB = if (mask == (mask and command.part)) updateHeatLevel(mask, level) else false
        mask = IPart.RIGHT_FRONT
        val isRF = if (mask == (mask and command.part)) updateHeatLevel(mask, level) else false
        mask = IPart.RIGHT_BACK
        val isRB = if (mask == (mask and command.part)) updateHeatLevel(mask, level) else false
        if (isLF || isLB || isRF || isRB) {
            command.message = "好的，${command.slots?.name}加热${append}了"
        } else {
            command.message = "好的，${command.slots?.name}加热已经${append}了"
        }
        callback?.onCmdHandleResult(command)
    }

    private fun obtainBackrestPosition(@IPart part: Int): Int {
        val signal = if (IPart.LEFT_FRONT == part) {
            CarCabinManager.ID_BACKREST_POS_FB
        } else if (IPart.RIGHT_FRONT == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_BACKREST_POS_FB
        }
//        驾驶员座椅靠背倾斜调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun obtainChairHeightPosition(@IPart part: Int): Int {
        val signal = if (IPart.LEFT_FRONT == part) {
            CarCabinManager.ID_SEAT_HEIGHT_POS_FB
        } else if (IPart.RIGHT_FRONT == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_SEAT_HEIGHT_POS_FB
        }
//        驾驶员座椅高度调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun obtainChairSpacePosition(@IPart part: Int): Int {
        val signal = if (IPart.LEFT_FRONT == part) {
            CarCabinManager.ID_SEAT_LENGTH_POS_FB
        } else if (IPart.RIGHT_FRONT == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_SEAT_LENGTH_POS_FB
        }
//        驾驶员座椅长度调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }



}