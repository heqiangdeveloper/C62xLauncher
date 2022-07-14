package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
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

class WheelManager private constructor() : BaseManager(), ISoundManager {

    private val swhFunction: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val epsMode: AtomicInteger by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        AtomicInteger(node.default).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, result)
        }
    }

    private val steeringSillTemp: Volume by lazy {
        initVolume(Volume.Type.STEERING_SILL_TEMP)
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.DRIVE_WHEEL_AUTO_HEAT.get.signal)
                add(RadioNode.DRIVE_EPS_MODE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private fun initVolume(type: Volume.Type): Volume {
        val pos = 20
        val max = 30
        return Volume(type, 10, max, pos)
    }


    companion object : ISignal {

        override val TAG: String = WheelManager::class.java.simpleName

        val instance: WheelManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WheelManager()
        }

    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> {
                epsMode.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> {
                writeProperty(node, value, epsMode)
            }
            else -> false
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                swhFunction.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                writeProperty(node, status, swhFunction)
            }
            else -> false
        }
    }

    override fun doGetVolume(type: Volume.Type): Volume? {
        return when (type) {
            Volume.Type.STEERING_SILL_TEMP -> {
                steeringSillTemp
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Volume.Type, position: Int): Boolean {
        return when (type) {
            Volume.Type.STEERING_SILL_TEMP -> {
                writeProperty(steeringSillTemp, position)
            }
            else -> false
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, swhFunction, property)
            }
            else -> {}
        }
    }

    private fun writeProperty(volume: Volume, value: Int): Boolean {
        val success = volume.isValid(value) && writeProperty(volume.type.id, value, Origin.CABIN)
        if (success && develop) {
            volume.pos = value
            doRangeChanged(volume)
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
                doRadioChanged(_node, _value)
            }
        }
        return success
    }

    private fun doRangeChanged(vararg array: Volume) {
        synchronized(listenerStore) {
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISoundListener) {
                    listener.onSoundVolumeChanged(*array)
                }
            }
        }
    }

}