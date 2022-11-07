package com.chinatsp.settinglib.manager.cabin

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ShareHandler
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   : 座椅信号处理
 * @version: 1.0
 */
class SeatManager private constructor() : BaseManager(), ISoundManager, ICmdExpress {

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

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
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

    private fun obtainChairHeatLevel(@IPart part: Int, default: Int = Constant.INVALID): Int {
//        int类型数据 0x0:Inactive 0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaSignal = if (Constant.INVALID == default) obtainChairAreaSignal(part) else default
        return readIntProperty(-1, Origin.CABIN, areaSignal)
    }

    private fun obtainChairVentilateLevel(@IPart part: Int, default: Int = Constant.INVALID): Int {
//        int类型数据  0x0:Inactive  0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaSignal = if (Constant.INVALID == default) obtainChairAreaSignal(part) else default
        return readIntProperty(-1, Origin.CABIN, areaSignal)
    }

    private fun updateChairHeatLevel(@IPart part: Int, expectLevel: Int): Boolean {
//        int类型数据 0x0:Inactive 0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = obtainChairAreaSignal(part)
        val actualLevel = obtainChairHeatLevel(part, areaValue)
        val result = expectLevel != actualLevel
        if (result) {
            val signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS
            writeProperty(signal, expectLevel, Origin.CABIN, areaValue)
        }
        return result
    }


    private fun updateChairVentilateLevel(@IPart part: Int, expectLevel: Int): Boolean {
//        int类型数据 0x0:Inactive 0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val areaValue = obtainChairAreaSignal(part)
        val actualLevel = obtainChairVentilateLevel(part, areaValue)
        val result = expectLevel != actualLevel
        if (result) {
            val signal = CarCabinManager.ID_HUM_SEAT_VENT_POS
            writeProperty(signal, expectLevel, Origin.CABIN, areaValue)
        }
        return result
    }

    private fun updateChairKneadLevel(@IPart part: Int, expectLevel: Int): Boolean {
        val actualLevel = obtainChairKneadLevelGetSignal(part)
        val result = actualLevel != expectLevel
        if (result) {
            val setSignal = obtainChairKneadLevelSetSignal(part)
            writeProperty(setSignal, expectLevel, Origin.CABIN)
        }
        return result
    }

    private fun obtainChairKneadLevelSetSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> CarCabinManager.ID_HUM_SEATMASSLVL_FL
            IPart.R_F -> CarCabinManager.ID_HUM_SEATMASSLVL_FR
            IPart.L_B -> Constant.INVALID
            IPart.R_B -> Constant.INVALID
            else -> Constant.INVALID
        }
    }

    private fun obtainChairKneadLevelGetSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> Constant.INVALID
            IPart.R_F -> Constant.INVALID
            IPart.L_B -> CarCabinManager.ID_RLSSM_SEAT_LVL_SET_STS
            IPart.R_B -> CarCabinManager.ID_RRSSM_SEAT_LVL_SET_STS
            else -> Constant.INVALID
        }
    }

    private fun obtainChairKneadLevel(@IPart part: Int): Int {
        val signal = obtainChairKneadLevelGetSignal(part)
        if (Constant.INVALID != signal) {
            return readIntProperty(signal, Origin.CABIN)
        }
        return Constant.INVALID
    }

    private fun doControlChairTilt(parcel: CommandParcel) {

    }

    private fun doControlChairKnead(parcel: CommandParcel) {
        val minLevel = 0x2
        val maxLevel = 0x4
        val offLevel = 0x5
        var expectLevel = Constant.INVALID
        var lfLevel = Constant.INVALID
        var lbLevel = Constant.INVALID
        var rfLevel = Constant.INVALID
        var rbLevel = Constant.INVALID
        val callback = parcel.callback
        val command = parcel.command as CarCmd
        val lfAct = IPart.L_F == (IPart.L_F and command.part)
        val lbAct = IPart.L_B == (IPart.L_B and command.part)
        val rfAct = IPart.R_F == (IPart.R_F and command.part)
        val rbAct = IPart.R_B == (IPart.R_B and command.part)
        if (Action.TURN_ON == command.action) {
            expectLevel = maxLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.TURN_OFF == command.action) {
            expectLevel = offLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.PLUS == command.action) {
            if (lfAct) {
                val value = obtainChairKneadLevel(IPart.L_F)
                lfLevel = value + command.step
            }
            if (lbAct) {
                val value = obtainChairKneadLevel(IPart.L_B)
                lbLevel = value + command.step
            }
            if (rfAct) {
                val value = obtainChairKneadLevel(IPart.R_F)
                rfLevel = value + command.step
            }
            if (rbAct) {
                val value = obtainChairKneadLevel(IPart.R_B)
                rbLevel = value + command.step
            }
        }
        if (Action.MINUS == command.action) {
            if (lfAct) {
                val value = obtainChairKneadLevel(IPart.L_F)
                lfLevel = value - command.step
            }
            if (lbAct) {
                val value = obtainChairKneadLevel(IPart.L_B)
                lbLevel = value - command.step
            }
            if (rfAct) {
                val value = obtainChairKneadLevel(IPart.R_F)
                rfLevel = value - command.step
            }
            if (rbAct) {
                val value = obtainChairKneadLevel(IPart.R_B)
                rbLevel = value - command.step
            }
        }
        if (Action.MIN == command.action) {
            expectLevel = minLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.MAX == command.action) {
            expectLevel = maxLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.FIXED == command.action) {
            expectLevel = command.value
            if (expectLevel < minLevel) {
                expectLevel = minLevel
            } else {
                expectLevel += 1
            }
            if (expectLevel > maxLevel) {
                expectLevel = maxLevel
            }
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        val lfSend = if (lfAct) updateChairKneadLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairKneadLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairKneadLevel(IPart.R_F, rfLevel) else false
        val rbSend = if (rbAct) updateChairKneadLevel(IPart.R_B, rbLevel) else false
        val isSend = lfSend || lbSend || rfSend || rbSend
        hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
            offLevel, minLevel, maxLevel, command, isSend, "按摩")
        callback?.onCmdHandleResult(command)
    }

    private fun doControlChairVentilate(parcel: CommandParcel) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        var expectLevel = Constant.INVALID
        val offLevel = 0x1
        val minLevel = 0x2
        val maxLevel = 0x4
        var lfLevel = Constant.INVALID
        var lbLevel = Constant.INVALID
        var rfLevel = Constant.INVALID
        var rbLevel = Constant.INVALID
        val lfAct = IPart.L_F == (IPart.L_F and command.part)
        val lbAct = IPart.L_B == (IPart.L_B and command.part)
        val rfAct = IPart.R_F == (IPart.R_F and command.part)
        val rbAct = IPart.R_B == (IPart.R_B and command.part)
        if (Action.TURN_ON == command.action) {
            expectLevel = maxLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.TURN_OFF == command.action) {
            expectLevel = offLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.PLUS == command.action) {
            if (lfAct) {
                val value = obtainChairVentilateLevel(IPart.L_F)
                expectLevel = value + command.value
                lfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (lbAct) {
                val value = obtainChairVentilateLevel(IPart.L_B)
                expectLevel = value + command.value
                lbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (rfAct) {
                val value = obtainChairVentilateLevel(IPart.R_F)
                expectLevel = value + command.value
                rfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (rbAct) {
                val value = obtainChairVentilateLevel(IPart.R_B)
                expectLevel = value + command.value
                rbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
        }
        if (Action.MINUS == command.action) {
            if (lfAct) {
                val value = obtainChairVentilateLevel(IPart.L_F)
                expectLevel = value - command.value
                lfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (lbAct) {
                val value = obtainChairVentilateLevel(IPart.L_B)
                expectLevel = value - command.value
                lbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (rfAct) {
                val value = obtainChairVentilateLevel(IPart.R_F)
                expectLevel = value - command.value
                rfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
            if (rbAct) {
                val value = obtainChairVentilateLevel(IPart.R_B)
                expectLevel = value - command.value
                rbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
            }
        }
        if (Action.MIN == command.action) {
            expectLevel = minLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.MAX == command.action) {
            expectLevel = maxLevel
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        if (Action.FIXED == command.action) {
            expectLevel = calibrationValue(command.value, minLevel, maxLevel)
            if (lfAct) lfLevel = expectLevel
            if (lbAct) lbLevel = expectLevel
            if (rfAct) rfLevel = expectLevel
            if (rbAct) rbLevel = expectLevel
        }
        val lfSend = if (lfAct) updateChairVentilateLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairVentilateLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairVentilateLevel(IPart.R_F, rfLevel) else false
        val rbSend = if (rbAct) updateChairVentilateLevel(IPart.R_B, rbLevel) else false
        val isSend = lfSend || lbSend || rfSend || rbSend

        hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
            offLevel, minLevel, maxLevel, command, isSend, "通风")
        callback?.onCmdHandleResult(command)
    }

    private fun doControlChairHeat(parcel: CommandParcel) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        val offLevel = 0x1
        val minLevel = 0x2
        val maxLevel = 0x4
        var lfLevel = Constant.INVALID
        var lbLevel = Constant.INVALID
        var rfLevel = Constant.INVALID
        var rbLevel = Constant.INVALID
        val lfAct = IPart.L_F == (IPart.L_F and command.part)
        val lbAct = IPart.L_B == (IPart.L_B and command.part)
        val rfAct = IPart.R_F == (IPart.R_F and command.part)
        val rbAct = IPart.R_B == (IPart.R_B and command.part)
        if (command.status == IStatus.INIT) {
            var expectLevel: Int
            if (Action.TURN_ON == command.action) {
                expectLevel = maxLevel
                if (lfAct) lfLevel = expectLevel
                if (lbAct) lbLevel = expectLevel
                if (rfAct) rfLevel = expectLevel
                if (rbAct) rbLevel = expectLevel
            } else if (Action.TURN_OFF == command.action) {
                expectLevel = offLevel
                if (lfAct) lfLevel = expectLevel
                if (lbAct) lbLevel = expectLevel
                if (rfAct) rfLevel = expectLevel
                if (rbAct) rbLevel = expectLevel
            } else if (Action.PLUS == command.action) {
                if (lfAct) {
                    val value = obtainChairHeatLevel(IPart.L_F)
                    expectLevel = value + command.value
                    lfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (lbAct) {
                    val value = obtainChairHeatLevel(IPart.L_B)
                    expectLevel = value + command.value
                    lbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (rfAct) {
                    val value = obtainChairHeatLevel(IPart.R_F)
                    expectLevel = value + command.value
                    rfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (rbAct) {
                    val value = obtainChairHeatLevel(IPart.R_B)
                    expectLevel = value + command.value
                    rbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
            } else if (Action.MINUS == command.action) {
                if (lfAct) {
                    val value = obtainChairHeatLevel(IPart.L_F)
                    expectLevel = value - command.value
                    lfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (lbAct) {
                    val value = obtainChairHeatLevel(IPart.L_B)
                    expectLevel = value - command.value
                    lbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (rfAct) {
                    val value = obtainChairHeatLevel(IPart.R_F)
                    expectLevel = value - command.value
                    rfLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
                if (rbAct) {
                    val value = obtainChairHeatLevel(IPart.R_B)
                    expectLevel = value - command.value
                    rbLevel = calibrationValue(expectLevel, minLevel, maxLevel)
                }
            } else if (Action.MIN == command.action) {
                expectLevel = minLevel
                if (lfAct) lfLevel = expectLevel
                if (lbAct) lbLevel = expectLevel
                if (rfAct) rfLevel = expectLevel
                if (rbAct) rbLevel = expectLevel
            } else if (Action.MAX == command.action) {
                expectLevel = maxLevel
                if (lfAct) lfLevel = expectLevel
                if (lbAct) lbLevel = expectLevel
                if (rfAct) rfLevel = expectLevel
                if (rbAct) rbLevel = expectLevel
            } else if (Action.FIXED == command.action) {
                expectLevel = calibrationValue(command.value, minLevel, maxLevel)
                if (lfAct) lfLevel = expectLevel
                if (lbAct) lbLevel = expectLevel
                if (rfAct) rfLevel = expectLevel
                if (rbAct) rbLevel = expectLevel
            }
            command.lfExpect = lfLevel
            command.rfExpect = rfLevel
            command.lbExpect = lbLevel
            command.rbExpect = rbLevel
        }
        lfLevel = command.lfExpect
        rfLevel = command.rfExpect
        lbLevel = command.lbExpect
        rbLevel = command.rbExpect
        val lfSend = if (lfAct) updateChairHeatLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairHeatLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairHeatLevel(IPart.R_F, rfLevel) else false
        val rBSend = if (rbAct) updateChairHeatLevel(IPart.R_B, rbLevel) else false
        val isSend = lfSend || lbSend || rfSend || rBSend
        if (!parcel.isRetry() || !isSend) {
            hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
                offLevel, minLevel, maxLevel, command, isSend, "加热")
            callback?.onCmdHandleResult(command)
        } else {
            ShareHandler.loopParcel(parcel, delayed = ShareHandler.MID_DELAY)
        }
    }

    private fun interruptCommand(parcel: CommandParcel, coreEngine: Boolean = false): Boolean {
        val result = if (coreEngine) {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        } else {
            !VcuUtils.isPower()
        }
        if (result) {
            val command = parcel.command as CarCmd
            command.message = "操作没有成功，请先启动发动机"
            parcel.callback?.onCmdHandleResult(command)
        }
        return result
    }

    private fun hint(
        lfAct: Boolean, lrAct: Boolean, rfAct: Boolean, rbAct: Boolean,
        lfLevel: Int, rfLevel: Int, lbLevel: Int, rbLevel: Int,
        offLevel: Int, minLevel: Int, maxLevel: Int,
        command: CarCmd, isSend: Boolean, notion: String,
    ) {
        var append: String
        val option = "座椅${notion}"
        if (lfAct && lrAct && rfAct && rbAct) {
            val isFEquals = lfLevel == rfLevel
            val isBEquals = lbLevel == rbLevel
            val isLEquals = lfLevel == lbLevel
            val isREquals = rfLevel == rbLevel
            if (isFEquals && isBEquals && isLEquals && isREquals) {
                append = hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                command.message = "好的，${option}${append}"
            } else if (!isFEquals && !isBEquals && !isLEquals && !isREquals) {
                val lfAppend =
                    "前排左${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lrAppend =
                    "后排左${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rrAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lfAppend}, ${lrAppend}, ${rfAppend}, $rrAppend"
            } else if (isFEquals && isBEquals && !isLEquals) {
                val fAppend =
                    "前排${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val bAppend =
                    "后排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${fAppend}, $bAppend"
            } else if (isLEquals && isREquals && !isFEquals) {
                val lAppend =
                    "左边${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rAppend =
                    "右边${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lAppend}, $rAppend"
            } else if (isFEquals && !isBEquals) {
                val fAppend =
                    "前排${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lrAppend =
                    "后排左${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rrAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${fAppend}, ${lrAppend}, ${rrAppend}"
            } else if (isBEquals && !isFEquals) {
                val bAppend =
                    "前排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lfAppend =
                    "前排左${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${bAppend}, ${lfAppend}, ${rfAppend}"
            } else if (isLEquals && !isREquals) {
                val fAppend =
                    "左排${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rrAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${fAppend}, ${rfAppend}, ${rrAppend}"
            } else if (isREquals && !isLEquals) {
                val rAppend =
                    "右排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lfAppend =
                    "前排左${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lrAppend =
                    "后排左${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${rAppend}, ${lfAppend}, ${lrAppend}"
            }
        } else if (lrAct && rfAct && rbAct && !lfAct) {
            val isBEquals = lbLevel == rbLevel
            val isREquals = rfLevel == rbLevel
            if (isBEquals && isREquals) {
                val append =
                    "乘客${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${append}"
            } else if (isBEquals) {
                val rrAppend =
                    "后排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${rrAppend}, ${rfAppend}"
            } else if (isREquals) {
                val rAppend =
                    "右排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rbAppend =
                    "后排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${rAppend}, ${rbAppend}"
            } else {
                val lrAppend =
                    "后排左${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rrAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lrAppend}, ${rfAppend}, $rrAppend"
            }
        } else if (lfAct && rfAct && !rbAct && !lrAct) {
            val isFEquals = lfLevel == rfLevel
            if (isFEquals) {
                val fAppend =
                    "前排${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${fAppend}"
            } else {
                val lfAppend =
                    "前排左${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lfAppend}, ${rfAppend}"
            }
        } else if (lrAct && rbAct && !lfAct && !rfAct) {
            val isBEquals = lbLevel == rbLevel
            if (isBEquals) {
                val bAppend =
                    "后排${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${bAppend}"
            } else {
                val lrAppend =
                    "后排左${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rrAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lrAppend}, ${rrAppend}"
            }
        } else if (lfAct && lrAct && !rfAct && !rbAct) {
            val isLEquals = lfLevel == lbLevel
            if (isLEquals) {
                val lAppend =
                    "左排${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lAppend}"
            } else {
                val lfAppend =
                    "前排左${option}${
                        hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val lrAppend =
                    "后排右${option}${
                        hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${lfAppend}, ${lrAppend}"
            }
        } else if (rfAct && rbAct && !lfAct && !lrAct) {
            val isREquals = rfLevel == rbLevel
            if (isREquals) {
                val rAppend =
                    "右排${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${rAppend}"
            } else {
                val rfAppend =
                    "前排右${option}${
                        hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                val rbAppend =
                    "后排右${option}${
                        hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
                    }"
                command.message = "好的，${rfAppend}, ${rbAppend}"
            }
        } else if (lfAct) {
            append =
                hint(lfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
            command.message = "好的，${command.slots?.name}${notion}${append}了"
        } else if (lrAct) {
            append =
                hint(lbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
            command.message = "好的，${command.slots?.name}${notion}${append}了"
        } else if (rfAct) {
            append =
                hint(rfLevel, offLevel, minLevel, maxLevel, command.action, isSend)
            command.message = "好的，${command.slots?.name}${notion}${append}了"
        } else if (rbAct) {
            append =
                hint(rbLevel, offLevel, minLevel, maxLevel, command.action, isSend)
            command.message = "好的，${command.slots?.name}${notion}${append}了"
        }
    }

    private fun calibrationValue(value: Int, minLevel: Int, maxLevel: Int): Int {
        var level = value
        if (level < minLevel) {
            level = minLevel
        } else {
            level += 1
        }
        if (level > maxLevel) {
            level = maxLevel
        }
        return level
    }

    private fun hint(
        level: Int, offLevel: Int, minLevel: Int, maxLevel: Int, action: Int, isSend: Boolean,
    ): String {
        return when (level) {
            offLevel -> "${if (isSend) "已经" else ""}关闭了"
            minLevel -> "${if (isSend) "已经" else ""}调整到最低了"
            maxLevel -> if (Action.TURN_ON == action) "${if (isSend) "已经" else ""}打开了" else "${if (isSend) "已经" else ""}调整到最高了"
            else -> "${if (isSend) "已经" else ""}调整为${level - 1}档"
        }
    }

    private fun obtainChairAreaSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> VehicleAreaSeat.SEAT_ROW_1_LEFT
            IPart.L_B -> VehicleAreaSeat.SEAT_ROW_2_LEFT
            IPart.R_F -> VehicleAreaSeat.SEAT_ROW_1_RIGHT
            IPart.R_B -> VehicleAreaSeat.SEAT_ROW_2_RIGHT
            else -> VehicleAreaSeat.SEAT_ROW_1_LEFT
        }
    }

    private fun obtainBackrestPosition(@IPart part: Int): Int {
        val signal = if (IPart.L_F == part) {
            CarCabinManager.ID_BACKREST_POS_FB
        } else if (IPart.R_F == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_BACKREST_POS_FB
        }
//        驾驶员座椅靠背倾斜调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun obtainChairHeightPosition(@IPart part: Int): Int {
        val signal = if (IPart.L_F == part) {
            CarCabinManager.ID_SEAT_HEIGHT_POS_FB
        } else if (IPart.R_F == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_SEAT_HEIGHT_POS_FB
        }
//        驾驶员座椅高度调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun obtainChairSpacePosition(@IPart part: Int): Int {
        val signal = if (IPart.L_F == part) {
            CarCabinManager.ID_SEAT_LENGTH_POS_FB
        } else if (IPart.R_F == part) {
            Constant.INVALID
        } else {
            CarCabinManager.ID_SEAT_LENGTH_POS_FB
        }
//        驾驶员座椅长度调节位置反馈
//        0x0~0x7D0:0~100%; 0xFF:Invalid
        return readIntProperty(signal, Origin.CABIN)
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        if (ICar.CHAIR != command.car) {
            return
        }
        if (IAct.HEAT == command.act) {
            if (!interruptCommand(parcel, coreEngine = true)) {
                doControlChairHeat(parcel)
            }
        } else if (IAct.COLD == command.act) {
            if (!interruptCommand(parcel, coreEngine = true)) {
                doControlChairVentilate(parcel)
            }
        } else if (IAct.TILT == command.act) {
            if (!interruptCommand(parcel, coreEngine = true)) {
                doControlChairTilt(parcel)
            }
        } else if (IAct.KNEAD == command.act) {
            if (!interruptCommand(parcel)) {
                doControlChairKnead(parcel)
            }
        }
    }


}