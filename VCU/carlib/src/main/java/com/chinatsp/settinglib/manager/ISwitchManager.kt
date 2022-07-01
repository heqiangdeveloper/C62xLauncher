package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.SwitchNode
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
        if (value is Int) {
            LogManager.d("doSwitchChanged", "$node, value:$value, isON:${node.isOn(value)}")
            onSwitchChanged(node, atomic, value, this::doUpdateSwitchValue) { newNode, newValue ->
                doSwitchChanged(newNode, newValue)
            }
        }
    }

    fun onSwitchChanged(
        node: SwitchNode,
        atomic: AtomicBoolean,
        value: Int,
        update: (SwitchNode, AtomicBoolean, Int, b: ((SwitchNode, Boolean) -> Unit)?) -> Unit,
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
//        if (node.isValid(value)) {
            val status = node.isOn(value)
            doUpdateSwitchValue(node, atomic, status, block)
//        }
        return atomic
    }

    fun doUpdateSwitchValue(
        node: SwitchNode,
        atomic: AtomicBoolean,
        status: Boolean,
        block: ((SwitchNode, Boolean) -> Unit)? = null
    ): AtomicBoolean {
        if (atomic.get() xor status) {
            atomic.set(status)
            block?.let { it(node, status) }
        }
        return atomic
    }


}