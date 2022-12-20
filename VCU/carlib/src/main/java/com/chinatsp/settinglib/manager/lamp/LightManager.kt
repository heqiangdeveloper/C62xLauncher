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
import com.chinatsp.settinglib.listener.ISignalListener
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


class LightManager private constructor() : BaseManager(), IOptionManager, IProgressManager,
    ICmdExpress {

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
                add(CarCabinManager.ID_HIGH_BEAM_INDICATOR)
                add(CarCabinManager.ID_LOW_BEAM_INDICATOR)
                add(CarCabinManager.ID_TELLTALE_REAR_FOG_LIGHT)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val insideMeetLight: SwitchState by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val lightCeremonySenseSwitch: SwitchState by lazy {
        val node = SwitchNode.LIGHT_CEREMONY_SENSE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val outsideMeetLight: SwitchState by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
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

    private fun initProgress(progress: Progress): Volume {
        val value = readIntProperty(progress.get.signal, progress.get.origin)
        val position = findBacklightLevel(value, progress)
        Timber.d("initProgress $progress progress:$progress, value:$value, position:$position")
        return Volume(progress, progress.min, progress.max, position)
    }

    private fun findBacklightLevel(value: Int, type: Progress): Int {
        val levels = Constant.LIGHT_LEVEL
        var level = Constant.INVALID
        levels.forEachIndexed { index, i ->
            if (i == value) level = index
        }
        if (Constant.INVALID == level) {
            val first = levels.indexOfFirst { it >= value }
            val last = levels.indexOfLast { it <= value }
            val isFindLast = Constant.INVALID != last
            val isFindFirst = Constant.INVALID != first
            if (isFindFirst && !isFindLast) {
                level = first
            } else if (!isFindFirst && isFindLast) {
                level = last
            } else if (isFindFirst && isFindLast) {
                level = if (first == last) {
                    first
                } else {
                    val offsetBefore = abs(value - levels[last])
                    val offsetAfter = abs(value - levels[first])
                    if (offsetBefore > offsetAfter) first else last
                }
            }
            Timber.d("findBacklightLevel first:$first, last:$last, level:$level")
        }
        if (Constant.INVALID == level) {
            level = type.def
        }
        return level + type.min
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
            RadioNode.LIGHT_DELAYED_OUT -> lightDelayOut.deepCopy()
            RadioNode.LIGHT_FLICKER -> lightFlicker.deepCopy()
            RadioNode.LIGHT_CEREMONY_SENSE -> lightCeremonySense.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> {
                node.isValid(value, isGet = false) && writeProperty(node, value, lightDelayOut)
            }
            RadioNode.LIGHT_FLICKER -> {
                node.isValid(value, isGet = false) && writeProperty(node, value, lightFlicker)
            }
            RadioNode.LIGHT_CEREMONY_SENSE -> {
                node.isValid(value, isGet = false) && writeProperty(node, value, lightCeremonySense)
            }
            else -> false
        }
    }

    override fun doGetVolume(progress: Progress): Volume? {
        return when (progress) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                switchBacklight
            }
            else -> {
                null
            }
        }
    }

    override fun doSetVolume(progress: Progress, position: Int): Boolean {
        return when (progress) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                val backlightLevel = Constant.LIGHT_LEVEL
                val index = position - 1
                if (index in backlightLevel.indices) {
                    val value = backlightLevel[index]
                    return writeProperty(progress.set.signal, value, progress.set.origin)
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
            SwitchNode.LIGHT_INSIDE_MEET -> insideMeetLight.deepCopy()
            SwitchNode.LIGHT_OUTSIDE_MEET -> outsideMeetLight.deepCopy()
            SwitchNode.LIGHT_CEREMONY_SENSE -> lightCeremonySenseSwitch.deepCopy()
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

    override fun onCabinPropertyChanged(p: CarPropertyValue<*>) {
        when (p.propertyId) {
            SwitchNode.LIGHT_OUTSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_OUTSIDE_MEET, outsideMeetLight, p)
            }
            SwitchNode.LIGHT_INSIDE_MEET.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_INSIDE_MEET, insideMeetLight, p)
            }
            SwitchNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onSwitchChanged(SwitchNode.LIGHT_CEREMONY_SENSE, lightCeremonySenseSwitch, p)
            }
            RadioNode.LIGHT_DELAYED_OUT.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_DELAYED_OUT, lightDelayOut, p)
            }
            RadioNode.LIGHT_FLICKER.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_FLICKER, lightFlicker, p)
            }
            RadioNode.LIGHT_CEREMONY_SENSE.get.signal -> {
                onRadioChanged(RadioNode.LIGHT_CEREMONY_SENSE, lightCeremonySense, p)
            }
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS.get.signal -> {
                val value = p.value as Int
                val level = findBacklightLevel(value, Progress.SWITCH_BACKLIGHT_BRIGHTNESS)
                doUpdateProgress(switchBacklight, level, true, this::doProgressChanged)
            }
            CarCabinManager.ID_HIGH_BEAM_INDICATOR -> {
                onSignalChanged(IPart.HEAD, Model.LIGHT_COMMON, Constant.HIGH_LAMP, p.value as Int)
            }
            CarCabinManager.ID_LOW_BEAM_INDICATOR -> {
                onSignalChanged(IPart.HEAD, Model.LIGHT_COMMON, Constant.LOW_LAMP, p.value as Int)
            }
            CarCabinManager.ID_TELLTALE_REAR_FOG_LIGHT -> {
                onSignalChanged(IPart.HEAD, Model.LIGHT_COMMON, Constant.B_FOG_LAMP, p.value as Int)
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
            doUpdateSwitch(node, atomic, value) { _node, _value ->
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

            val name = command.slots?.name ?: if (isHead && !isTail) {
                "前雾灯"
            } else if (!isHead && isTail) {
                "后雾灯"
            } else {
                "雾灯"
            }
            if (Action.TURN_ON == command.action) {
                var result = false
                if (isHead) {
                    val isWrite = updateFogLight(true, IPart.HEAD)
                    result = result or isWrite
                }
                if (isTail) {
                    val isWrite = updateFogLight(true, IPart.TAIL)
                    result = result or isWrite
                }
                command.message = "${name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                var result = false
                if (isHead) {
                    val isWrite = updateFogLight(false, IPart.HEAD)
                    result = result or isWrite
                }
                if (isTail) {
                    val isWrite = updateFogLight(false, IPart.TAIL)
                    result = result or isWrite
                }
                command.message = "${name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doSwitchSideLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            val name = command.slots?.name ?: "位置灯"
            if (Action.TURN_ON == command.action) {
                val result = updateSideLight(true)
                command.message = "${name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateSideLight(false)
                command.message = "${name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun doSwitchDippedLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            val name = command.slots?.name ?: "近光灯"
            if (Action.TURN_ON == command.action) {
                val result = updateDippedLight(true)
                command.message = "${name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateDippedLight(false)
                command.message = "${name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)

    }

    private fun doSwitchDistantLight(parcel: CommandParcel) {
        val command = parcel.command
        do {
            val name = command.slots?.name ?: "远光灯"
            if (Action.TURN_ON == command.action) {
                val result = updateDistantLight(true)
                command.message = "${name}${if (!result) "已经" else ""}打开了"
                break
            }
            if (Action.TURN_OFF == command.action) {
                val result = updateDistantLight(false)
                command.message = "${name}${if (!result) "已经" else ""}关闭了"
                break
            }
        } while (false)
        parcel.callback?.onCmdHandleResult(command)
    }

    private fun updateSideLight(expect: Boolean): Boolean {
        val actual = isSideLight(expect)
        val result = actual != expect
        if (result) {
            val signal = CarCabinManager.ID_AVN_PARK_LIGHT
//            AVN request park light on or off reserved[0x1,0,0x0,0x3]
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
//            AVN request low beam on or off reserved[0x1,0,0x0,0x3]
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
//            AVN request high beam on or off reserved[0x1,0,0x0,0x3]
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

    private fun isSideLight(expect: Boolean): Boolean {
        val signalA = CarCabinManager.ID_PARK_LIGHT_A_INDICATION
        val signalB = CarCabinManager.ID_PARK_LIGHT_B_INDICATION
        val valueA = readIntProperty(signalA, Origin.CABIN)
        val valueB = readIntProperty(signalB, Origin.CABIN)
        val result =
            if (expect) (valueA == 0x1 && valueB == 0x1) else (valueA == 0x1 || valueB == 0x1)
        Timber.d("isSideLight signalA:$signalA valueA:$valueA, signalA:$signalA valueA:$valueA, result:$result")
//        Status of park light B; C51/C53/C62:B=RIGHT
//        other projects unless specified: REAR; 0x0: Off;  0x1: On
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

    private fun isExteriorLampOff(): Boolean {
        val signal = CarCabinManager.ID_EXTERIOR_LAMP_SWITCH
//        Status of exterior lamp switch
//        0x0: Off; 0x1: Auto; 0x2: Park; 0x3: Low Beam
        val value = readIntProperty(signal, Origin.CABIN)
        Timber.d("isDistantLight signal:$signal value:$value")
        return value == 0x0
    }

    fun obtainLightState(model: Int, serial: Int): Int? {
        if (Model.LIGHT_COMMON != model) {
            return null
        }
        return when (serial) {
            Constant.LOW_LAMP -> readIntProperty(CarCabinManager.ID_LOW_BEAM_INDICATOR,
                Origin.CABIN)
            Constant.HIGH_LAMP -> readIntProperty(CarCabinManager.ID_HIGH_BEAM_AND_FLASH_SWITCH,
                Origin.CABIN)
            Constant.B_FOG_LAMP -> readIntProperty(CarCabinManager.ID_TELLTALE_REAR_FOG_LIGHT,
                Origin.CABIN)
            else -> null
        }
    }


    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        if ((Model.LIGHT_COMMON == command.model) && (ICar.LAMPS == command.car)) {
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