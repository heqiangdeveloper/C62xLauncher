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
import timber.log.Timber

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

    private fun obtainChairHeatLevel(@IPart part: Int): Int {
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_FSH_STATUS_FL
            IPart.L_B -> Constant.INVALID
            IPart.R_F -> CarCabinManager.ID_FSH_STATUS_FR
            IPart.R_B -> Constant.INVALID
            else -> Constant.INVALID
        }
//        副驾座椅加热状态 0x0: OFF;0x1: Level 1; 0x2: Level 2; 0x3: Level 3
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun obtainChairVentilateLevel(@IPart part: Int): Int {
//        左前座椅通风状态 0x0: OFF; 0x1: Level 1; 0x2: Level 2; 0x3: Level 3
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_FSV_STATUS_FL
            IPart.L_B -> Constant.INVALID
            IPart.R_F -> CarCabinManager.ID_FSV_STATUS_FR
            IPart.R_B -> Constant.INVALID
            else -> Constant.INVALID
        }
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun updateChairHeatLevel(@IPart part: Int, expectLevel: Int): Boolean {
//        int类型数据 0x0:Inactive 0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val actualLevel = obtainChairHeatLevel(part)
        val result = expectLevel != (actualLevel + 1)
        if (result) {
            val signal = CarCabinManager.ID_HUM_SEAT_HEAT_POS
            val areaValue = obtainChairAreaSignal(part)
            writeProperty(signal, expectLevel, Origin.CABIN, areaValue)
        }
        return result
    }

    private fun updateChairVentilateLevel(@IPart part: Int, expectLevel: Int): Boolean {
//        int类型数据 0x0:Inactive 0x1:OFF(default)
//        0x2:Level 1; 0x3:Level 2; 0x4:Level 3
//        0x5 ………… 0x7:reserved
        val actualLevel = obtainChairVentilateLevel(part)
        val result = expectLevel != (actualLevel + 1)
//        if (result) {
        val signal = CarCabinManager.ID_HUM_SEAT_VENT_POS
        val areaValue = obtainChairAreaSignal(part)
        writeProperty(signal, expectLevel, Origin.CABIN, areaValue)
//        }
        return result
    }

    private fun updateChairKneadLevel(@IPart part: Int, expectLevel: Int): Boolean {
        val actualLevel = obtainChairKneadLevel(part)
        val result = expectLevel != (actualLevel + 1)
        if (result) {
            val setSignal = obtainChairKneadLevelSetSignal(part)
//            "驾驶员座椅按摩水平设置[0x1,-1,0x0,0xF]
//            0x0: Inactive; 0x1: No Action
//            0x2: Level1_Low; 0x3: Level2_Middle; 0x4: Level3_High; 0x5: OFF
//            0x6~0x7: Reserved"
            writeProperty(setSignal, expectLevel, Origin.CABIN)
        }
        return result
    }

    private fun obtainChairKneadLevelSetSignal(@IPart part: Int): Int {
        return when (part) {
            IPart.L_F -> CarCabinManager.ID_HUM_SEATMASSLVL_FL
            IPart.R_F -> CarCabinManager.ID_HUM_SEATMASSLVL_FR
            IPart.L_B -> CarCabinManager.ID_HUM_SEATMASSLVL_RL_SET
            IPart.R_B -> CarCabinManager.ID_HUM_SEATMASSLVL_RR_SET
            else -> Constant.INVALID
        }
    }

    private fun obtainChairKneadLevel(@IPart part: Int): Int {
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_DRV_SEAT_LVL_SET_STS1
            IPart.R_F -> CarCabinManager.ID_PASS_SEAT_LVL_SET_STS
            IPart.L_B -> CarCabinManager.ID_RLSSM_SEAT_LVL_SET_STS
            IPart.R_B -> CarCabinManager.ID_RRSSM_SEAT_LVL_SET_STS
            else -> Constant.INVALID
        }
//        乘客座椅按摩水平设置状态
//        0x1: Level1_Low; 0x2: Level2_Middle; 0x3: Level3_High
//        0x4: OFF; 0x5~0x7: Reserved
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun doControlChairTilt(parcel: CommandParcel) {

    }

    private fun obtainKneadLevel (
        @IPart part: Int, minLevel: Int, maxLevel: Int, offLevel: Int, step: Int): Int {
        val value = obtainChairKneadLevel(part) + 1
        val expect = if (offLevel == value) {
            minLevel
        } else {
            value + step
        }
        Timber.e("obtainKneadLevel--value:$value, step:${step}, expect:$expect")
        return calibrationValue(expect, minLevel, maxLevel)
    }

    private fun doControlChairKnead(parcel: CommandParcel) {
        val minLevel = 0x2
        val maxLevel = 0x4
        val offLevel = 0x5
        var expectLevel: Int
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
        if (IStatus.INIT == command.status) {
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
                val step = command.step
                if (lfAct) {
                    lfLevel = obtainKneadLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainKneadLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainKneadLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainKneadLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
                }
            }
            if (Action.MINUS == command.action) {
                val step = command.step * -1
                if (lfAct) {
                    lfLevel = obtainKneadLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainKneadLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainKneadLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainKneadLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
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
            command.lfExpect = lfLevel
            command.rfExpect = rfLevel
            command.lbExpect = lbLevel
            command.rbExpect = rbLevel
        }
        lfLevel = command.lfExpect
        rfLevel = command.rfExpect
        lbLevel = command.lbExpect
        rbLevel = command.rbExpect
        val lfSend = if (lfAct) updateChairKneadLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairKneadLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairKneadLevel(IPart.R_F, rfLevel) else false
        val rbSend = if (rbAct) updateChairKneadLevel(IPart.R_B, rbLevel) else false
        command.status = IStatus.RUNNING
        val isSend = lfSend || lbSend || rfSend || rbSend
        if (!parcel.isRetry() || !isSend) {
            hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
                offLevel, minLevel, maxLevel, command, isSend, "按摩")
            callback?.onCmdHandleResult(command)
        } else {
            ShareHandler.loopParcel(parcel, delayed = ShareHandler.MID_DELAY)
        }
    }

//        驾驶员座椅按摩模式设置[0x1,-1,0x0,0xF] 0x0: Inactive; 0x1: No Action
//        0x2: Wave Mode; 0x3: Serpentine Mode; 0x4: Catwalk Mode;  0x5~0xF: Reserved
    private fun updateKneadMode(@IPart part: Int, expect: Int): Boolean {
        val actual = obtainKneadMode(part)
        val result = actual != expect
        if (result) {
            val signal = when (part) {
                IPart.L_F -> CarCabinManager.ID_HUM_SEATMASSMOD_FL_SET
                IPart.L_B -> CarCabinManager.ID_HUM_SEATMASSMOD_RL_SET
                IPart.R_F -> CarCabinManager.ID_HUM_SEATMASSMOD_FR_SET
                IPart.R_B -> CarCabinManager.ID_HUM_SEATMASSMOD_RR_SET
                else -> Constant.INVALID
            }
            writeProperty(signal, expect, Origin.CABIN)
        }
        return result
    }

    private fun obtainKneadMode(@IPart part: Int): Int{
        val signal = when (part) {
            IPart.L_F -> CarCabinManager.ID_DRV_SEAT_MODE_STS1
            IPart.L_B -> CarCabinManager.ID_RLSSM_SEAT_MODE_STS
            IPart.R_F -> CarCabinManager.ID_PASS_SEAT_MODE_STS
            IPart.R_B -> CarCabinManager.ID_RRSSM_SEAT_MODE_STS
            else -> Constant.INVALID
        }
//        右后座椅按摩模式状态 0x0: No Action; 0x1: Wave Mode;
//        0x2: Serpentine Mode; 0x3: Catwalk Mode; 0x4~0xF: Reserved
        return readIntProperty(signal, Origin.CABIN)
    }

    private fun doControlKneadMode(parcel: CommandParcel) {
        val minLevel = 0x2
        val maxLevel = 0x4
        val offLevel = 0x5
        var expectLevel: Int
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
        if (IStatus.INIT == command.status) {
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
                val step = command.step
                if (lfAct) {
                    lfLevel = obtainKneadLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainKneadLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainKneadLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainKneadLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
                }
            }
            if (Action.MINUS == command.action) {
                val step = command.step * -1
                if (lfAct) {
                    lfLevel = obtainKneadLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainKneadLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainKneadLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainKneadLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
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
            command.lfExpect = lfLevel
            command.rfExpect = rfLevel
            command.lbExpect = lbLevel
            command.rbExpect = rbLevel
        }
        lfLevel = command.lfExpect
        rfLevel = command.rfExpect
        lbLevel = command.lbExpect
        rbLevel = command.rbExpect
        val lfSend = if (lfAct) updateChairKneadLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairKneadLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairKneadLevel(IPart.R_F, rfLevel) else false
        val rbSend = if (rbAct) updateChairKneadLevel(IPart.R_B, rbLevel) else false
        command.status = IStatus.RUNNING
        val isSend = lfSend || lbSend || rfSend || rbSend
        if (!parcel.isRetry() || !isSend) {
            hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
                offLevel, minLevel, maxLevel, command, isSend, "按摩")
            callback?.onCmdHandleResult(command)
        } else {
            ShareHandler.loopParcel(parcel, delayed = ShareHandler.MID_DELAY)
        }
    }

    private fun obtainVentilateLevel (
        @IPart part: Int, minLevel: Int, maxLevel: Int, offLevel: Int, step: Int): Int {
        val value = obtainChairVentilateLevel(part) + 0x1
        val expect = if (offLevel == value) {
            minLevel
        } else {
            value + step
        }
        Timber.e("obtainVentilateLevel--value:$value, step:${step}, expect:$expect")
        return calibrationValue(expect, minLevel, maxLevel)
    }

    private fun doControlChairVentilate(parcel: CommandParcel) {
        val offLevel = 0x1
        val minLevel = 0x2
        val maxLevel = 0x4
        val expectLevel: Int
        var lfLevel = Constant.INVALID
        var lbLevel = Constant.INVALID
        var rfLevel = Constant.INVALID
        var rbLevel = Constant.INVALID
        val callback = parcel.callback
        val command = parcel.command as CarCmd
        val lfAct = IPart.L_F == (IPart.L_F and command.part)
        val lbAct = false //IPart.L_B == (IPart.L_B and command.part)
        val rfAct = IPart.R_F == (IPart.R_F and command.part)
        val rbAct = false//IPart.R_B == (IPart.R_B and command.part)
        if (IStatus.INIT == command.status) {
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
                val step = command.step
                if (lfAct) {
                    lfLevel = obtainVentilateLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainVentilateLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainVentilateLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainVentilateLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
                }

            } else if (Action.MINUS == command.action) {
                val step = command.step * -1
                if (lfAct) {
                    lfLevel = obtainVentilateLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainVentilateLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainVentilateLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainVentilateLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
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
                expectLevel = calibrationValue(command.value + 1, minLevel, maxLevel)
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
        val lfSend = if (lfAct) updateChairVentilateLevel(IPart.L_F, lfLevel) else false
        val lbSend = if (lbAct) updateChairVentilateLevel(IPart.L_B, lbLevel) else false
        val rfSend = if (rfAct) updateChairVentilateLevel(IPart.R_F, rfLevel) else false
        val rbSend = if (rbAct) updateChairVentilateLevel(IPart.R_B, rbLevel) else false
        command.status = IStatus.RUNNING

        val isSend = lfSend || lbSend || rfSend || rbSend
        if (!parcel.isRetry() || !isSend) {
            hint(lfAct, lbAct, rfAct, rbAct, lfLevel, rfLevel, lbLevel, rbLevel,
                offLevel, minLevel, maxLevel, command, isSend, "通风")
            callback?.onCmdHandleResult(command)
        } else {
            ShareHandler.loopParcel(parcel, delayed = ShareHandler.MID_DELAY)
        }
    }

    private fun obtainHeatLevel (
        @IPart part: Int, minLevel: Int, maxLevel: Int, offLevel: Int, step: Int): Int {
        val value = obtainChairHeatLevel(part) + 0x1
        val expect = if (offLevel == value) {
            minLevel
        } else {
            value + step
        }
        Timber.e("obtainHeatLevel--value:$value, step:${step}, expect:$expect")
        return calibrationValue(expect, minLevel, maxLevel)
    }

    private fun doControlChairHeat(parcel: CommandParcel) {
        val offLevel = 0x1
        val minLevel = 0x2
        val maxLevel = 0x4
        var lfLevel = Constant.INVALID
        var lbLevel = Constant.INVALID
        var rfLevel = Constant.INVALID
        var rbLevel = Constant.INVALID
        val callback = parcel.callback
        val command = parcel.command as CarCmd
        val lfAct = IPart.L_F == (IPart.L_F and command.part)
        val lbAct = false//IPart.L_B == (IPart.L_B and command.part)
        val rfAct = IPart.R_F == (IPart.R_F and command.part)
        val rbAct = false//IPart.R_B == (IPart.R_B and command.part)
        if (command.status == IStatus.INIT) {
            val expectLevel: Int
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
                val step = command.step
                if (lfAct) {
                    lfLevel = obtainHeatLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainHeatLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainHeatLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainHeatLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
                }
            } else if (Action.MINUS == command.action) {
                val step = command.step * -1
                if (lfAct) {
                    lfLevel = obtainHeatLevel(IPart.L_F, minLevel, maxLevel, offLevel, step)
                }
                if (lbAct) {
                    lbLevel = obtainHeatLevel(IPart.L_B, minLevel, maxLevel, offLevel, step)
                }
                if (rfAct) {
                    rfLevel = obtainHeatLevel(IPart.R_F, minLevel, maxLevel, offLevel, step)
                }
                if (rbAct) {
                    rbLevel = obtainHeatLevel(IPart.R_B, minLevel, maxLevel, offLevel, step)
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
                expectLevel = calibrationValue(command.value + 1, minLevel, maxLevel)
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
        val rbSend = if (rbAct) updateChairHeatLevel(IPart.R_B, rbLevel) else false
        command.status = IStatus.RUNNING
        val isSend = lfSend || lbSend || rfSend || rbSend
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