package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IACListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class ACManager private constructor() : BaseManager(), IAcManager {

    /**
     * 【反馈】self-desiccation
     * self-desiccation 自干燥功能状态显示 0x0:ON 0x1:OFF
     * AcSelfStsDisp
     */
    private val cabinAridSignal = CarCabinManager.ID_ACSELFSTSDISP

    /**
     * 【反馈】解锁预通风功能开启状态
     * 解锁预通风功能开启状态 0x0:ON 0x1:OFF
     * VehicleArea:GLOBAL
     * AcPreVentnDisp
     */
    private val cabinWindSignal = CarCabinManager.ID_ACPREVENTNDISP

    /**
     * 【反馈】空调舒适性状态显示
     * 空调舒适性状态显示 0x0: Reserved 0x1: Gentle 0x2:
     *  Standard 0x3: Powerful 0x4~0x6: Reserved 0x7: Invalid
     * AcCmftStsDisp
     */
    private val cabinComfortSignal = CarCabinManager.ID_ACCMFTSTSDISP

    private val hvacDemistSignal = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST

    companion object : ISignal {

        override val TAG: String = ACManager::class.java.simpleName

        val instance: ACManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ACManager()
        }

    }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**空调自干燥*/
                add(CarCabinManager.ID_ACSELFSTSDISP)
                /**预通风功能*/
                add(CarCabinManager.ID_ACPREVENTNDISP)
                /**空调舒适性状态显示*/
                add(CarCabinManager.ID_ACCMFTSTSDISP)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val aridStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_AUTO_ARID
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val demistStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_AUTO_DEMIST
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val windStatus: AtomicBoolean by lazy {
        val node = SwitchNode.AC_ADVANCE_WIND
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val comfortOption: AtomicInteger by lazy {
        val node = RadioNode.AC_COMFORT
        AtomicInteger(node.default).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateRadioValue(node, this, result)
        }
    }


    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: Origin
    ): Boolean {
        when (signalOrigin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            Origin.HVAC -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }


    override fun doGetRadioOption(node: RadioNode): Int {
        return when (node) {
            RadioNode.AC_COMFORT -> {
                comfortOption.get()
            }
            else -> -1
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return if (!node.isValid(value, false)) {
            false
        } else {
            when (node) {
                RadioNode.AC_COMFORT -> {
                    writeProperty(node.set.signal, value, node.set.origin, node.area)
                }
                else -> false
            }
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, identity)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> {
                aridStatus.get()
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                windStatus.get()
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                demistStatus.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_ADVANCE_WIND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.AC_AUTO_DEMIST -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }


    /**
     * 更新空调舒适性
     * @param value (自动舒适模式开关AC Auto Comfort Switch
    0x0: Inactive
    0x1: Gentle
    0x2: Standard
    0x3: Powerful
    0x4~0x6: Reserved
    0x7: Invalid
     */
    fun doUpdateAcComfort(value: Int): Boolean {
        val isValid = listOf(0x1, 0x2, 0x3).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT
        return writeProperty(signal, value, Origin.HVAC, Area.GLOBAL)
    }

    /**
     *
     * @param node 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.AC_AUTO_ARID,
            SwitchNode.AC_AUTO_DEMIST,
            SwitchNode.AC_ADVANCE_WIND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            SwitchNode.AC_AUTO_DEMIST.get.signal -> {
                onSwitchOptionChanged(SwitchNode.AC_AUTO_DEMIST, property.value)
            }
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            cabinAridSignal -> {
                onSwitchOptionChanged(SwitchNode.AC_AUTO_ARID, property.value)
            }
            //预通风功能
            cabinWindSignal -> {
                onSwitchOptionChanged(SwitchNode.AC_ADVANCE_WIND, property.value)
            }
            //自动空调舒适性
            CarCabinManager.ID_ACCMFTSTSDISP -> {
                onRadioOptionChanged(RadioNode.AC_COMFORT, property.value)
            }
            else -> {}
        }
    }

    private fun onSwitchOptionChanged(node: SwitchNode, value: Any?) {
        if (value is Int && node.isValid(value)) {
            when (node) {
                SwitchNode.AC_AUTO_ARID -> {
                    doUpdateSwitchValue(node, aridStatus, value) {
                        notifySwitchStatus(it, node)
                    }
                }
                SwitchNode.AC_ADVANCE_WIND -> {
                    doUpdateSwitchValue(node, windStatus, value) {
                        notifySwitchStatus(it, node)
                    }
                }
                else -> {}
            }
        }
    }

    private fun onRadioOptionChanged(node: RadioNode, value: Any?) {
        if (value is Int && node.isValid(value)) {
            when (node) {
                RadioNode.AC_COMFORT -> {
                    doUpdateRadioValue(node, comfortOption, value) {
                        notifyComfortChanged(it)
                    }
                }
                else -> {}
            }
        }
    }

    private fun notifySwitchStatus(status: Boolean, type: SwitchNode) {
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is IACListener) {
                        listener.onSwitchOptionChanged(status, type)
                    }
                }
        }
    }

    private fun notifyComfortChanged(optionValue: Int) {
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is IACListener) {
                        listener.onAcComfortOptionChanged(optionValue)
                    }
                }
        }
    }

}