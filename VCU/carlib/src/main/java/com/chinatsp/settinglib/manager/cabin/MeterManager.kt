package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class MeterManager private constructor() : BaseManager(), IRadioManager {

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**设防提示音 开关*/
                add(RadioNode.DRIVE_METER_SYSTEM.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val meterSystemRadioOption: RadioState by lazy {
        val node = RadioNode.DRIVE_METER_SYSTEM
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }


    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }


    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //设防提示音
            RadioNode.DRIVE_METER_SYSTEM.get.signal -> {
                onRadioChanged(RadioNode.DRIVE_METER_SYSTEM, meterSystemRadioOption, property)
            }
            else -> {}
        }
    }


    companion object : ISignal {
        override val TAG: String = MeterManager::class.java.simpleName
        val instance: MeterManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MeterManager()
        }
    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> meterSystemRadioOption.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> {
                writeProperty(node, value, meterSystemRadioOption)
            }
            else -> false
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

}