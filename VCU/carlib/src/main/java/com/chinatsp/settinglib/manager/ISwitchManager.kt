package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.SwitchNode
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:26
 * @desc   :
 * @version: 1.0
 */
interface ISwitchManager : IManager {

    /**
     *
     * @param   node 开关选项
     * @return  返回开关状态
     */
    fun doGetSwitchOption(node: SwitchNode): SwitchState?

    /**
     *
     * @param   node 开关选项
     * @param   status 开关期望状态
     * @return  返回接口调用是否成功
     */
    fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean

    fun onSwitchChanged(node: SwitchNode, atomic: SwitchState, p: CarPropertyValue<*>) {
        val value = p.value
        if (value !is Int) {
            val append = "node:$node, id:${p.propertyId}, value:${value.javaClass.simpleName}"
            Timber.e("onSwitchChanged but value is not Int! $append")
            return
        }
        Timber.d("doSwitchChanged node:$node, value:$value, status:${node.isOn(value)}")
        onSwitchChanged(node, atomic, value, this::doUpdateSwitch, this::doSwitchChanged)
    }

    fun onSwitchChanged(
        node: SwitchNode, atomic: SwitchState, value: Int,
        update: (SwitchNode, SwitchState, Int, ((SwitchNode, SwitchState) -> Unit)?) -> Unit,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateSwitch(
        node: SwitchNode, atomic: SwitchState, value: Int,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isValid = node.isValid(value)
        val invalid = node.isInvalid(value)
        val isCoreValue = isValid || invalid
        if (!isCoreValue) {
            Timber.e("doUpdateSwitch but isValid:$isValid, invalid:$invalid, node:$node, value:$value")
        } else {
            if (isValid) {
                doUpdateSwitch(node, atomic, node.isOn(value), block)
            }
            if (invalid) {
                doUpdateSwitchEnable(node, atomic, value, block)
            }
        }
        return atomic
    }


    fun doUpdateSwitch(
        node: SwitchNode,
        atomic: SwitchState,
        status: Boolean,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isStatusChanged = atomic.get() xor status
        val isEnableChanged = !atomic.enable()
        val isChanged = isStatusChanged or isEnableChanged
        if (!isChanged) {
            Timber.e("doUpdateSwitch but isStatusChanged:$isStatusChanged, isEnableChanged:$isEnableChanged, node:$node, status:$status, oldValue:${atomic.get()}")
        } else {
            if (isStatusChanged) atomic.set(status)
            if (isEnableChanged) atomic.enable = Constant.VIEW_ENABLE
            block?.let { it(node, atomic) }
        }
        return atomic
    }

    private fun doUpdateSwitchEnable(
        node: SwitchNode, atomic: SwitchState, value: Int,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val enableSuccess = atomic.setEnable(value)
        if (enableSuccess) {
            block?.let { it(node, atomic) }
        } else {
            Timber.e("doUpdateSwitchEnable enableSuccess:$enableSuccess, node:$node, oEnable:${atomic.enable}, value:$value}")
        }
        return atomic
    }

    fun convert(
        property: CarPropertyValue<*>,
        target: Int,
        vararg filter: Int,
    ): CarPropertyValue<*>? {
        val value = property.value
        if (value is Int) {
            if (filter.contains(value)) {
                return CarPropertyValue(property.propertyId, property.areaId, target)
            }
        }
        return null
    }

}