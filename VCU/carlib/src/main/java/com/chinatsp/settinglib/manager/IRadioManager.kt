package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.RadioNode
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:26
 * @desc   :
 * @version: 1.0
 */
interface IRadioManager : IManager {

    /**
     * @param   node 选项
     * @return 返回选中的值
     */
    fun doGetRadioOption(node: RadioNode): RadioState?

    /**
     * @param   node 选项
     * @param   value 选中项的 value值
     * @return  返回接口调用是否成功
     */
    fun doSetRadioOption(node: RadioNode, value: Int): Boolean

    fun onRadioChanged(node: RadioNode, atomic: RadioState, p: CarPropertyValue<*>) {
        val value = p.value
        if (value is Int) {
            Timber.d("onRadioChanged node:$node, value:$value")
            onRadioChanged(node, atomic, value, this::doUpdateRadioValue, this::doOptionChanged)
            return
        }
        Timber.e("onRadioChanged but value is not Int! node:$node, id:${p.propertyId}")
    }

    fun onRadioChanged(node: RadioNode, atomic: RadioState, value: Int) {
        onRadioChanged(node, atomic, value, this::doUpdateRadioValue, this::doOptionChanged)
    }

    fun onRadioChanged(
        node: RadioNode, atomic: RadioState, value: Int,
        update: ((RadioNode, RadioState, Int, ((RadioNode, RadioState) -> Unit)) -> Unit),
        block: ((RadioNode, RadioState) -> Unit),
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateRadioValue(
        node: RadioNode,
        atomic: RadioState,
        value: Int,
        block: ((RadioNode, RadioState) -> Unit)? = null,
    ): RadioState {
        val isValid = node.isValid(value)
        if (isValid) {
            if (value != atomic.get()) {
                atomic.set(value)
            }
            if (!atomic.enable()) {
                atomic.enable = Constant.VIEW_ENABLE
            }
            block?.let { it(node, atomic) }
            return atomic
        }
        val inactive = node.isInactive(value)
        if (inactive xor atomic.enable()) {
            atomic.enable = if (inactive) value else Constant.VIEW_ENABLE
            block?.let { it(node, atomic) }
            return atomic
        }
        Timber.e("doUpdateRadioValue node:$node, value:$value," +
                " isValid:$isValid, inactive:$inactive, oldValue:${atomic.get()}")
        return atomic
    }

}