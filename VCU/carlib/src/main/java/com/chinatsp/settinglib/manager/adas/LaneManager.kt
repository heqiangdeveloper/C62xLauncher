package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class LaneManager : BaseManager(), IOptionManager {

    companion object : ISignal {

        override val TAG: String = LaneManager::class.java.simpleName

        val instance: LaneManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LaneManager()
        }
    }

    private val laneAssistMode: AtomicInteger by lazy {
        val node = RadioNode.ADAS_LANE_ASSIST_MODE
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val ldwWarningSensitivity: AtomicInteger by lazy {
        val node = RadioNode.ADAS_LDW_SENSITIVITY
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val ldwWarningStyle: AtomicInteger by lazy {
        val node = RadioNode.ADAS_LDW_STYLE
//        AtomicInteger(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, value)
//        }
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val laneAssistFunction: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_LANE_ASSIST
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**车道保持灵敏度*/
//                add(CarCabinManager.ID_LKS_SENSITIVITY)
//                add(CarCabinManager.ID_ACCMFTSTSDISP)
//                add(CarCabinManager.ID_LANE_ASSIT_TYPE)
//                add(CarCabinManager.ID_ADAS_LDW_WARNING_SENSITIVITY)
                add(SwitchNode.ADAS_LANE_ASSIST.get.signal)
                add(RadioNode.ADAS_LANE_ASSIST_MODE.get.signal)
                add(RadioNode.ADAS_LDW_STYLE.get.signal)
                add(RadioNode.ADAS_LDW_SENSITIVITY.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            RadioNode.ADAS_LANE_ASSIST_MODE.get.signal -> {
                onRadioChanged(RadioNode.ADAS_LANE_ASSIST_MODE, laneAssistMode, property)
//                onRadioOptionChangedAtLaneAssist(property)
            }
            RadioNode.ADAS_LDW_SENSITIVITY.get.signal -> {
                onRadioChanged(RadioNode.ADAS_LDW_SENSITIVITY, ldwWarningSensitivity, property)
//                onRadioOptionChangedAtSensitivity(property)
            }
            RadioNode.ADAS_LDW_STYLE.get.signal -> {
                onRadioChanged(RadioNode.ADAS_LDW_STYLE, ldwWarningStyle, property)
            }
            SwitchNode.ADAS_LANE_ASSIST.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_LANE_ASSIST, laneAssistFunction, property)
            }
            else -> {}
        }
    }

    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                laneAssistMode.get()
            }
            RadioNode.ADAS_LDW_STYLE -> {
                ldwWarningStyle.get()
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                ldwWarningSensitivity.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                val result = node.isValid(value, false) && writeProperty(
                    node.set.signal,
                    value,
                    node.set.origin
                )
                takeIf { result }?.doUpdateRadioValue(node, laneAssistMode, value)
                result
            }
            RadioNode.ADAS_LDW_STYLE -> {
                val result = node.isValid(value, false) && writeProperty(
                    node.set.signal,
                    value,
                    node.set.origin
                )
                takeIf { result }?.doUpdateRadioValue(node, ldwWarningStyle, value)
                result
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                val result = node.isValid(value, false) && writeProperty(
                    node.set.signal,
                    value,
                    node.set.origin
                )
                takeIf { result }?.doUpdateRadioValue(node, ldwWarningSensitivity, value)
                result
            }
            else -> false
        }
    }

//    private fun onRadioChanged(node: RadioNode, atomic: AtomicInteger, p: CarPropertyValue<*>) {
//        val value = p.value
//        if (value is Int) {
//           onRadioChanged(node, atomic, value, this::doUpdateRadioValue) {
//               radioNode, newValue -> doRadioChanged(radioNode, newValue)
//           }
//        }
//    }
//
//    private fun onSwitchChanged(node: SwitchNode, atomic: AtomicBoolean, p: CarPropertyValue<*>) {
//        val value = p.value
//        if (value is Int) {
//            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue) { newNode, newValue ->
//                doSwitchChanged(newNode, newValue)
//            }
//        }
//    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            } finally {
                writeLock.unlock()
            }
            result = serial
        }
        return result
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                laneAssistFunction.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin, node.area)
            }
            else -> false
        }
    }


}