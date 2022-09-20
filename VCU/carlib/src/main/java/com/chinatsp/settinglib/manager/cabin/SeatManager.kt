package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

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

    private val mainMeetFunction: AtomicBoolean by lazy {
        val node = SwitchNode.SEAT_MAIN_DRIVE_MEET
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val forkMeetFunction: AtomicBoolean by lazy {
        val node = SwitchNode.SEAT_FORK_DRIVE_MEET
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val seatHeatFunction: AtomicBoolean by lazy {
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
        val pos = 0x05
        val result = VcuUtils.getInt(key = Constant.SEAT_HEAT_TEMP, value = pos)
        val volume = Volume(type, type.min, type.max, result)
//        AppExecutors.get()?.singleIO()?.execute {
//            val result = VcuUtils.getInt(key = Constant.SEAT_HEAT_TEMP, value = pos)
//            doUpdateProgress(volume, result, true, instance::doProgressChanged)
//        }
        return volume
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


    override fun doGetRadioOption(node: RadioNode): Int {
        return -1
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> {
                mainMeetFunction.get()
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                forkMeetFunction.get()
            }
            SwitchNode.SEAT_HEAT_ALL -> {
                seatHeatFunction.get()
            }
            else -> false
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
                }
                return true
            }
            else -> false
        }
    }

    private fun writeProperty(volume: Volume, value: Int): Boolean {
        val success =
            volume.isValid(value) && writeProperty(volume.type.get.signal, value, Origin.CABIN)
        if (success && develop) {
            volume.pos = value
//            doRangeChanged(volume)
        }
        return success
    }

    private fun writeProperty(node: SwitchNode, status: Boolean, atomic: AtomicBoolean): Boolean {
        val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, status) { _node, _status ->
                doSwitchChanged(_node, _status)
            }
        }
        return success
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: AtomicInteger): Boolean {
        val success = node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin)
        if (success && develop) {
            doUpdateRadioValue(node, atomic, value) { _node, _value ->
                doOptionChanged(_node, _value)
            }
        }
        return success
    }

    private fun doRangeChanged(vararg array: Volume) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISoundListener) {
                    listener.onSoundVolumeChanged(*array)
                }
            }
        } finally {
            readLock.unlock()
        }
    }

}