package com.chinatsp.settinglib.manager.assistance

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
        AtomicInteger(1).apply {
            val signal = CarCabinManager.ID_LKS_SENSITIVITY
            val value = readIntProperty(signal, Origin.CABIN)
            set(value)
        }
    }

    private val ldwWarningSensitivity: AtomicInteger by lazy {
        AtomicInteger(1).apply {
//            val signal = CarCabinManager.ID_LKS_SENSITIVITY
//            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
//            set(value)
        }
    }

    private val ldwWarningStyle: AtomicInteger by lazy {
        AtomicInteger(2).apply {
//            val signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN
//            val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL)
//            set(value)
        }
    }

    private val laneAssistFunction: AtomicBoolean by lazy {
        val switchNode = SwitchNode.ADAS_LANE_ASSIST
        AtomicBoolean(switchNode.isOn()).apply {
            val result = readIntProperty(switchNode.get.signal, switchNode.get.origin)
            doUpdateSwitchValue(switchNode, this, result)
        }
    }


    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**车道保持灵敏度*/
                add(CarCabinManager.ID_ADAS_LDW_WARNING_SENSITIVITY)
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
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
            if ((ldwWarningStyle.get() != value) and setOf(0x01, 0x02, 0x03).contains(value)) {
                ldwWarningStyle.set(value)
            }
        }
    }

    private fun onRadioOptionChangedAtSensitivity(property: CarPropertyValue<*>) {
        //LKS sensitivity车道保持的灵敏度 0x0:lowSensitivity 0x1:highSensitivity 0x2: Initial 0x3:reserved
        val value = property.value
        if (value is Int) {
            if ((ldwWarningSensitivity.get() != value) && setOf(0x01, 0x02, 0x03).contains(value)
            ) {
                ldwWarningSensitivity.set(value)
            }
        }
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
        return writeProperty(signal, value, Origin.CABIN)
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
        return writeProperty(signal, value, Origin.CABIN)
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
                val signal = -1
                writeProperty(signal, value, Origin.CABIN)
            }
            RadioNode.ADAS_LDW_STYLE -> {
                val signal = -1
                writeProperty(signal, value, Origin.CABIN)
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                val signal = CarCabinManager.ID_LDW_LKS_SENSITIVITY_SWT
                writeProperty(signal, value, Origin.CABIN)
            }
            else -> false
        }
    }


    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
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