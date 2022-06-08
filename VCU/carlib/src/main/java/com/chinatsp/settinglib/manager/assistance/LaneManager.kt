package com.chinatsp.settinglib.manager.assistance

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
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

    val keepLaneSensitivity: AtomicInteger by lazy {
        AtomicInteger(1).apply {
            val signal = CarCabinManager.ID_LKS_SENSITIVITY
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            set(value)
        }
    }

    val laneAssistType: AtomicInteger by lazy {
        AtomicInteger(1).apply {
            val signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            set(value)
        }
    }

    val fcwStatus: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_FCW
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_FCW_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    val aebStatus: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_AEB
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_AEB_STATUS
            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
            doUpdateSwitchStatus(switchNode, this, value)
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(CarCabinManager.ID_LDW_RDP_LKS_STATUS)
                /**车道辅助类型*/
                add(CarCabinManager.ID_LANE_ASSIT_TYPE)
                /**车道保持灵敏度*/
                add(CarCabinManager.ID_LKS_SENSITIVITY)
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
            CarCabinManager.ID_LANE_ASSIT_TYPE -> {
                onRadioOptionChangedAtLaneAssist(property)
            }
            CarCabinManager.ID_LKS_SENSITIVITY -> {
                onRadioOptionChangedAtSensitivity(property)
            }
            else -> {}
        }
    }

    private fun onRadioOptionChangedAtLaneAssist(property: CarPropertyValue<*>) {
        //Operation mode of LDW/RDP/LKS. The default value is 0x1 LDW in C53F, 0x3 LKS in C62X. 0x0:Initial 0x1:LDW 0x2:RDP 0x3:LKS
        val value = property.value
        if (value is Int) {
            if ((laneAssistType.get() != value) and setOf(0x01, 0x02, 0x03).contains(value)) {
                laneAssistType.set(value)
            }
        }
    }

    private fun onRadioOptionChangedAtSensitivity(property: CarPropertyValue<*>) {
        //LKS sensitivity车道保持的灵敏度 0x0:lowSensitivity 0x1:highSensitivity 0x2: Initial 0x3:reserved
        val value = property.value
        if (value is Int) {
            if ((keepLaneSensitivity.get() != value) and setOf(0x01, 0x02).contains(value)) {
                keepLaneSensitivity.set(value)
            }
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
     * 设置车道辅助灵敏度
     * @param value
     * @return 接口执行结果
     */
    fun doTryCutRadioOptionAtSensitivity(value: Int): Boolean {
        if (!setOf(0x01, 0x02).contains(value)) {
            return false
        }
        val signal = CarCabinManager.ID_LDW_LKS_SENSITIVITY_SWT
        //LDW/LKS sensitivity switch,
        // if not set 'LDW_LKS_SENSITIVITY_SWITCH ',the value of signal
        // is 0x0(inactive)[0x1,0,0x0,0x3]
        //0x0: Inacitve
        //0x1: Low Sensitivity
        //0x2: High Sensitivity(default)
        //0x3: Reserved
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL)
    }

    /**
     * 设置车道辅助系统
     * @param value
     * @return 接口执行结果
     */
    fun doTryCutRadioOptionAtLaneAssist(value: Int): Boolean {
        if (!setOf(0x01, 0x02, 0x03).contains(value)) {
            return false
        }
        val signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN
//        LDW/RDP/LKS function enable switch,if not set 'LDW_RDP_LKS_FUNC_ENABLE',the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
//        C53F send the signal 0x0 all the time
//                0x0: Inactive
//        0x1: LDW Enable
//        0x2: RDP Enable
//        0x3: LKS Enable（C62 default）
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL)
    }

    /**
     *
     * @param switchNode 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return false
    }


    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {

    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {

    }

    private fun doUpdateSwitchStatus(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int
    ): AtomicBoolean {
        if (node.isValidValue(value)) {
            val status = node.isOn(value)
            if (atomic.get() xor status) {
                atomic.set(status)
            }
        }
        return atomic
    }

    override fun doGetRadioOption(radioNode: RadioNode): Int {
        TODO("Not yet implemented")
    }

    override fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        TODO("Not yet implemented")
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        TODO("Not yet implemented")
    }

}