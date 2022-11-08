package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class LightManager private constructor() : BaseManager(), IOptionManager, IProgressManager, ICmdExpress {

    companion object : ISignal {
        override val TAG: String = LightManager::class.java.simpleName
        val instance: LightManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LightManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(Progress.SWITCH_BACKLIGHT_BRIGHTNESS.get.signal)
                add(RadioNode.LIGHT_DELAYED_OUT.get.signal)
                add(RadioNode.LIGHT_FLICKER.get.signal)
                add(RadioNode.LIGHT_CEREMONY_SENSE.get.signal)
                add(SwitchNode.LIGHT_CEREMONY_SENSE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val insideMeetLight: SwitchState by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightCeremonySenseSwitch: SwitchState by lazy {
        val node = SwitchNode.LIGHT_CEREMONY_SENSE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val outsideMeetLight: SwitchState by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightDelayOut: RadioState by lazy {
        val node = RadioNode.LIGHT_DELAYED_OUT
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val lightFlicker: RadioState by lazy {
        val node = RadioNode.LIGHT_FLICKER
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val lightCeremonySense: RadioState by lazy {
        val node = RadioNode.LIGHT_CEREMONY_SENSE
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val switchBacklight: Volume by lazy {
        initProgress(Progress.SWITCH_BACKLIGHT_BRIGHTNESS)
    }

    private fun initProgress(type: Progress): Volume {
        val value = readIntProperty(type.get.signal, type.get.origin)
        val position = findBacklightLevel(value, type)
        Timber.d("initProgress $type type:$type, value:$value, position:$position")
        return Volume(type, type.min, type.max, position)
    }

    private fun findBacklightLevel(value: Int, type: Progress): Int {
        val levels = getBacklightLevel()
        var level = Constant.INVALID
        levels.forEachIndexed { index, i ->
            if (i == value) level = index
        }
        if (Constant.INVALID == level) {
            val first = levels.indexOfFirst { it >= value }
            val last = levels.indexOfLast { it <= value }
            if (first == last) {
                level = first
            }
            when (Constant.INVALID) {
                first -> {
                    level = last
                }
                last -> {
                    level = first
                }
                else -> {
                    val offsetBefore = abs(value - levels[last])
                    val offsetAfter = abs(value - levels[first])
                    level = if (offsetBefore > offsetAfter) {
                        first
                    } else {
                        last
                    }
                }
            }
            Timber.d("findBacklightLevel first:$first, last:$last, level:$level")
        }
        if (Constant.INVALID == level) {
            level = type.def
        }
        return level + type.min
    }

    private fun getBacklightLevel(): IntArray {
        return intArrayOf(0x19, 0x33, 0x4C, 0x66, 0x7F, 0x99, 0xB2, 0xCC, 0xE5, 0xFF)
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
            RadioNode.LIGHT_DELAYED_OUT -> lightDelayOut.copy()
            RadioNode.LIGHT_FLICKER -> lightFlicker.copy()
            RadioNode.LIGHT_CEREMONY_SENSE -> lightCeremonySense.copy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> {
                writeProperty(node, value, lightDelayOut)
            }
            RadioNode.LIGHT_FLICKER -> {
                writeProperty(node, value, lightFlicker)
            }
            RadioNode.LIGHT_CEREMONY_SENSE -> {
                writeProperty(node, value, lightCeremonySense)
            }
            else -> false
        }
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                switchBacklight
            }
            else -> {
                null
            }
        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        return when (type) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                val backlightLevel = getBacklightLevel()
                val index = position - 1
                if (index in backlightLevel.indices) {
                    val value = backlightLevel[index]
                    return writeProperty(type.set.signal, value, type.set.origin)
                }
                false
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
            SwitchNode.LIGHT_INSIDE_MEET -> insideMeetLight.copy()
            SwitchNode.LIGHT_OUTSIDE_MEET -> outsideMeetLight.copy()
            SwitchNode.LIGHT_CEREMONY_SENSE -> lightCeremonySenseSwitch.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.LIGHT_INSIDE_MEET -> {
                writeProperty(node, status, insideMeetLight)
            }
            SwitchNode.LIGHT_OUTSIDE_MEET -> {
                writeProperty(node, status, outsideMeetLight)
            }
            SwitchNode.LIGHT_CEREMONY_SENSE -> {
                writeProperty(node, status, lightCeremonySenseSwitch)
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
            SwitchNode.LIGHT_OUTSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_OUTSIDE_MEET, outsideMeetLight, property)
            }
            SwitchNode.LIGHT_INSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_INSIDE_MEET, insideMeetLight, property)
            }
            SwitchNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_CEREMONY_SENSE, lightCeremonySenseSwitch, property)
            }
            RadioNode.LIGHT_DELAYED_OUT.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_DELAYED_OUT, lightDelayOut, property)
            }
            RadioNode.LIGHT_FLICKER.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_FLICKER, lightFlicker, property)
            }
            RadioNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_CEREMONY_SENSE, lightCeremonySense, property)
            }
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS.get.signal -> {
                val value = property.value as Int
                val level = findBacklightLevel(value, Progress.SWITCH_BACKLIGHT_BRIGHTNESS)
                doUpdateProgress(switchBacklight, level, true, this::doProgressChanged)
            }
            else -> {}
        }
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: RadioState): Boolean {
        val success = node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin)
        if (success && develop) {
            doUpdateRadioValue(node, atomic, value) { _node, _value ->
                doOptionChanged(_node, _value)
            }
        }
        return success
    }

    private fun writeProperty(node: SwitchNode, value: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(value), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, value) { _node, _value ->
                doSwitchChanged(_node, _value)
            }
        }
        return success
    }

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
    }

    private fun doSwitchFogLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            val isHead = IPart.HEAD == IPart.HEAD and command.part
            val isTail = IPart.TAIL == IPart.TAIL and command.part
            if (Action.TURN_ON == command.action) {
                var result: Boolean = false
                if (isHead) {
                    val isWrite = updateFogLight(true, IPart.HEAD)
                    result = result or isWrite
                }
                if (isTail) {
                    val isWrite = updateFogLight(true, IPart.TAIL)
                    result = result or isWrite
                }
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                var result: Boolean = false
                if (isHead) {
                    val isWrite = updateFogLight(false, IPart.HEAD)
                    result = result or isWrite
                }
                if (isTail) {
                    val isWrite = updateFogLight(false, IPart.TAIL)
                    result = result or isWrite
                }
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doSwitchSideLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            if (Action.TURN_ON == command.action) {
                val result = updateSideLight(true)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateSideLight(false)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doSwitchDippedLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            if (Action.TURN_ON == command.action) {
                val result = updateDippedLight(true)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateDippedLight(false)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)

    }

    private fun doSwitchDistantLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            if (Action.TURN_ON == command.action) {
                val result = updateDistantLight(true)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateDistantLight(false)
                command.message = "${command.slots?.name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun updateSideLight(expect: Boolean): Boolean {
        val actual = isSideLight()
        val result = actual != expect
        if (result) {
            val signal = CarCabinManager.ID_AVN_PARK_LIGHT
//            AVN request park light on or offreserved[0x1,0,0x0,0x3]
//            0x0: Inactive  0x1: On  0x2: Off   0x3: Not used
            val value = if (expect) 0x1 else 0x2
            writeProperty(signal, value, Origin.CABIN)
        }
        return result
    }

    private fun updateDippedLight(expect: Boolean): Boolean {
        val actual = isDippedLight()
        val result = actual != expect
        if (result) {
            val signal = CarCabinManager.ID_AVN_LOW_BEAM
//            AVN request low beam on or offreserved[0x1,0,0x0,0x3]
//            0x0: Inactive  0x1: On  0x2: Off   0x3: Not used
            val value = if (expect) 0x1 else 0x2
            writeProperty(signal, value, Origin.CABIN)
        }
        return result
    }

    private fun updateDistantLight(expect: Boolean): Boolean {
        val actual = isDistantLight()
        val result = actual != expect
        if (result) {
            val signal = CarCabinManager.ID_AVN_HIGH_BEAM
//            AVN request high beam on or offreserved[0x1,0,0x0,0x3]
//            0x0: Inactive  0x1: On  0x2: Off   0x3: Not used
            val value = if (expect) 0x1 else 0x2
            writeProperty(signal, value, Origin.CABIN)
        }
        return result
    }

    private fun updateFogLight(expect: Boolean, @IPart part: Int): Boolean {
        val actual = isFogLight(part)
        val result = actual != expect
        if (result) {
            val signal = if (IPart.HEAD == part) {
                CarCabinManager.ID_AVN_FRONT_FOG_LIGHT
            } else if (IPart.TAIL == part) {
                CarCabinManager.ID_AVN_REAR_FOG_LIGHT
            } else {
                Constant.INVALID
            }
//            AVN  follow HMA_STATUS  0x0 to set  'off', follow  HMA_STATUS 0x1-0x2
//            to set 'on'.Set state 'inactive'  when receive HMA_STATUS 0x3-0x7 or HMA_STATUS
//            signal timeout.[0x1,0,0x0,0x3]
//            0x0: Inactive  0x1: On  0x2: Off   0x3: Not used
            val value = if (expect) 0x1 else 0x2
            writeProperty(signal, value, Origin.CABIN)
        }
        return result
    }

    private fun isFogLight(@IPart part: Int): Boolean {
        val signal = if (IPart.HEAD == part) {
            CarCabinManager.ID_TELLTALE_FRONT_FOG_LIGHT
        } else if (IPart.TAIL == part) {
            CarCabinManager.ID_TELLTALE_REAR_FOG_LIGHT
        } else {
            Constant.INVALID
        }
//        val signal = CarCabinManager.ID_FOG_LAMP_SWITCH
//        Status of front fog light; 0x0: Not active; 0x1: Active
        val value = readIntProperty(signal, Origin.CABIN)
        Timber.d("isFogLight signal:$signal value:$value")
//        val statusValue = if (IPart.HEAD == part) 0x2 else 0x1
//            Status of fog lamp switch. Status of rear fog lamp SW is same as status of rear fog lamp.
//        0x0: Inactive/Default
//        0x1: Rear fog lamp switch active
//        0x2: Front fog lamp switch active
        return value == 0x1
    }

    private fun isSideLight(): Boolean {
        val signalA = CarCabinManager.ID_PARK_LIGHT_A_INDICATION
        val signalB = CarCabinManager.ID_PARK_LIGHT_B_INDICATION
        val valueA = readIntProperty(signalA, Origin.CABIN)
        val valueB = readIntProperty(signalB, Origin.CABIN)
        val result = valueA ==0x1 && valueB == 0x1
        Timber.d("isSideLight signalA:$signalA valueA:$valueA, signalA:$signalA valueA:$valueA, result:$result")
//        Status of park light B
//        C51/C53/C62:B=RIGHT
//        other projects unless specified: REAR
//        0x0: Off
//        0x1: On
        return result
    }

    private fun isDippedLight(): Boolean {
        val signal = CarCabinManager.ID_LOW_BEAM_INDICATOR
//        Low beam indicator 0x0: Off; 0x1: On
        val value = readIntProperty(signal, Origin.CABIN)
        Timber.d("isDippedLight signal:$signal value:$value")
        return value == 0x1
    }

    private fun isDistantLight(): Boolean {
        val signal = CarCabinManager.ID_HIGH_BEAM_INDICATOR
//        High beam indicator 0x0: Off; 0x1: On
        val value = readIntProperty(signal, Origin.CABIN)
        Timber.d("isDistantLight signal:$signal value:$value")
        return value == 0x1
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        if ((Model.LIGHT_COMMON == command.model) && (ICar.LAMPS == command.car)) {
            val callback = parcel.callback
            if (IAct.DISTANT_LIGHT == command.act) {
                doSwitchDistantLight(parcel)
            } else if (IAct.DIPPED_LIGHT == command.act) {
                doSwitchDippedLight(parcel)
            } else if (IAct.SIDE_LIGHT == command.act) {
                doSwitchSideLight(parcel)
            } else if (IAct.FOG_LIGHT == command.act) {
                doSwitchFogLight(parcel)
            }
        }
    }

}