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
            if (isNeedLog(node)) {
                Timber.d("doSwitchChanged node:$node, value:$value, status:${node.isOn(value)}")
            }
            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue, this::doSwitchChanged)
            return
        }
        if (isNeedLog(node)) {
            Timber.e("onSwitchChanged but value is not Int! node:$node, id:${p.propertyId}, value:${value.javaClass.simpleName}")
        }
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
        if (isNeedLog(node)) {
            Timber.d("updateSwitchValue active:$active, inactive:$inactive, node:$node, value:$value, coreOn:${node.careOn}")
        }
        if (active || inactive) {
            if (active) {
                doUpdateSwitchValue(node, atomic, node.isOn(value), block)
            }
            if (inactive) {
                doUpdateSwitchEnable(node, atomic, value, block)
            }
        } else {
            Timber.e("updateSwitchValue but isValid:$isValid, node:$node, value:$value, coreOn:${node.careOn}")
        }
        return atomic
    }

    fun isNeedLog(node: SwitchNode): Boolean {

        return SwitchNode.ADAS_AEB != node && SwitchNode.ADAS_FCW != node
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: SwitchState,
        status: Boolean,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isStatusChanged = atomic.get() xor status
        val isEnableChanged = !atomic.enable()
        if (isStatusChanged or isEnableChanged) {
            if (isStatusChanged) atomic.set(status)
            if (isEnableChanged) atomic.enableStatus = Constant.VIEW_ENABLE
            block?.let { it(node, atomic) }
        } else {
            if (isNeedLog(node)) {
                Timber.e("updateSwitchValue 111 but isStatusChanged:$isStatusChanged, isEnableChanged:$isEnableChanged, node:$node, status:$status, oldValue:${atomic.get()}")
            }
        }
        return atomic
    }

    fun doUpdateSwitchEnable(
        node: SwitchNode,
        atomic: SwitchState,
        value: Int,
        block: ((SwitchNode, SwitchState) -> Unit)? = null,
    ): SwitchState {
        val isEnableChanged = atomic.isEnableChanged(value)
//        Timber.e("updateSwitchValue -------- isEnableChanged:$isEnableChanged, node:$node oEnable:${atomic.enableStatus}, value:$value, oldEnable:${atomic.enable()}")
//        if (isEnableChanged) {
        atomic.enableStatus = value
        block?.let { it(node, atomic) }
//        } else {
//            Timber.e("updateSwitchValue 222 but isEnableChanged:$isEnableChanged, node:$node oEnable:${atomic.enableStatus}, value:$value, oldEnable:${atomic.enable()}")
//        }
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