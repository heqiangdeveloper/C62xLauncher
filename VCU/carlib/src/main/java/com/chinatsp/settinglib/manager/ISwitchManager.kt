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
        if (value is Int) {
            Timber.d("doSwitchChanged node:$node, value:$value, status:${node.isOn(value)}")
            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue, this::doSwitchChanged)
            return
        }
        Timber.e("onSwitchChanged but value is not Int! node:$node, id:${p.propertyId}, value:${value.javaClass.simpleName}")
    }

    fun onSwitchChanged(
        node: SwitchNode,
        atomic: SwitchState,
        value: Int,
        update: (SwitchNode, SwitchState, Int, ((SwitchNode, SwitchState) -> Unit)?) -> Unit,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: SwitchState,
        value: Int,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val active = node.isActive(value)
        val inactive = node.isInactive(value)
        val isValid = active or inactive
        if (active or inactive) {
            if (active) {
                doUpdateSwitchValue(node, atomic, node.isOn(value), block)
            }
            if (inactive) {
                doUpdateSwitchEnable(node, atomic, Constant.INVALID, block)
            }
        } else {
            Timber.e("updateSwitchValue but isValid:$isValid, node:$node, value:$value, coreOn:${node.careOn}")
        }
        return atomic
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: SwitchState,
        status: Boolean,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isStatusChanged = atomic.get() != status
        val isEnableChanged = atomic.enable != Constant.VIEW_ENABLE
        if (isStatusChanged or isEnableChanged) {
            if (isStatusChanged) atomic.set(status)
            if (isEnableChanged) atomic.enable = Constant.VIEW_ENABLE
            block?.let { it(node, atomic) }
        } else {
            Timber.e("updateSwitchValue but isStatusChanged:$isStatusChanged, isEnableChanged:$isEnableChanged, node:$node, status:$status, oldValue:${atomic.get()}")
        }
        return atomic
    }

    fun doUpdateSwitchEnable(
        node: SwitchNode,
        atomic: SwitchState,
        value: Int,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isEnableChanged = atomic.enable != value
        if (isEnableChanged) {
            if (isEnableChanged) atomic.enable = value
            block?.let { it(node, atomic) }
        } else {
            Timber.e("updateSwitchValue but isEnableChanged:$isEnableChanged, node:$node, value:$value, old status:${atomic.get()}")
        }
        return atomic
    }
}