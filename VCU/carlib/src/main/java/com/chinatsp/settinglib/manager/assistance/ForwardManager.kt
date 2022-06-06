package com.chinatsp.settinglib.manager.assistance

import android.car.VehicleAreaSeat
import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.widget.Switch
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class ForwardManager: BaseManager() {

    companion object: ISignal {

        override val TAG: String = ForwardManager::class.java.simpleName

        val instance: ForwardManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ForwardManager()
        }

    }

    val fcwStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).apply {
            val signal = CarCabinManager.ID_FCW_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            val switchNode = SwitchNode.ADAS_FCW
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    val aebStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).apply {
            val signal = CarCabinManager.ID_AEB_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            val switchNode = SwitchNode.ADAS_AEB
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
                add(CarCabinManager.ID_FCW_STATUS)
                add(CarCabinManager.ID_AEB_STATUS)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }
    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinValueChanged(property)
            }
            else -> false
        }
        return false
    }

    private fun onCabinValueChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            CarCabinManager.ID_FCW_STATUS -> {
                onSwitchChanged(SwitchNode.ADAS_FCW, property)
            }
            CarCabinManager.ID_AEB_STATUS -> {
                onSwitchChanged(SwitchNode.ADAS_AEB, property)
            }
            else -> {}
        }
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    /**
     * 设置车道辅助系统
     * @param value
     */
    fun doAssistRadioOptions(value: Int): Boolean {
        val signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL)
    }

    /**
     *
     * @param switchNode 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            /**FCW status. 0x0:Inactive 0x1:Active 0x2:Reserved 0x3:Reserved*/
            SwitchNode.ADAS_FCW -> {
                val signal = CarCabinManager.ID_FCW_SWT
                doSetProperty(signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            SwitchNode.ADAS_FCW -> {
                var value = property.value
                if (value is Int) {
                    onSwitchChanged(switchNode, doUpdateSwitchStatus(switchNode, fcwStatus, value).get())
                }
            }
            SwitchNode.ADAS_AEB -> {
                var value = property.value
                if (value is Int) {
                    onSwitchChanged(switchNode, doUpdateSwitchStatus(switchNode, aebStatus, value).get())
                }
            }
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {

    }

    private fun doUpdateSwitchStatus(switchNode: SwitchNode, atomicBoolean: AtomicBoolean, value: Int): AtomicBoolean {
        val status = switchNode.isOn(value)
        atomicBoolean.set(status)
        return atomicBoolean
    }

}