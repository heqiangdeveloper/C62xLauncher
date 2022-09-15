package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.SwitchNode
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

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
    fun doGetSwitchOption(node: SwitchNode): Boolean

    /**
     *
     * @param   node 开关选项
     * @param   status 开关期望状态
     * @return  返回接口调用是否成功
     */
    fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean

    fun onSwitchChanged(node: SwitchNode, atomic: AtomicBoolean, p: CarPropertyValue<*>) {
        val value = p.value
        if (value !is Int) {
            Timber.e("onSwitchChanged but value is not Int! node:$node, id:${p.propertyId}")
            return
        }
        Timber.d("doSwitchChanged node:$node, value:$value, status:${node.isOn(value)}")
        onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue, this::doSwitchChanged)
    }

    fun onSwitchChanged(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int,
        update: (SwitchNode, AtomicBoolean, Int, ((SwitchNode, Boolean) -> Unit)?) -> Unit,
        block: ((SwitchNode, Boolean) -> Unit)? = null
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int,
        block: ((SwitchNode, Boolean) -> Unit)? = null
    ): AtomicBoolean {
        val isValid = node.isValid(value)
        if (isValid) {
            doUpdateSwitchValue(node, atomic, node.isOn(value), block)
        } else {
            Timber.e("updateSwitchValue but isValid:$isValid, node:$node, value:$value, coreOn:${node.careOn}")
        }
        return atomic
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: AtomicBoolean,
        status: Boolean,
        block: ((SwitchNode, Boolean) -> Unit)? = null
    ): AtomicBoolean {
        val isNotEqual = atomic.get() xor status
        if (isNotEqual) {
            atomic.set(status)
            block?.let { it(node, status) }
        } else {
            Timber.e("updateSwitchValue but isNotEqual:$isNotEqual, node:$node, status:$status")
        }
        return atomic
    }
}