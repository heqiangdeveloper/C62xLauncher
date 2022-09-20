package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.RadioNode
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

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
    fun doGetRadioOption(node: RadioNode): Int

    /**
     * @param   node 选项
     * @param   value 选中项的 value值
     * @return  返回接口调用是否成功
     */
    fun doSetRadioOption(node: RadioNode, value: Int): Boolean

    fun onRadioChanged(node: RadioNode, atomic: AtomicInteger, p: CarPropertyValue<*>) {
        val value = p.value
        if (value !is Int) {
            Timber.e("onRadioChanged but value is not Int! node:$node, id:${p.propertyId}")
            return
        }
        Timber.d("onRadioChanged node:$node, value:$value")
        onRadioChanged(node, atomic, value, this::doUpdateRadioValue, this::doOptionChanged)
    }

    fun onRadioChanged(
        node: RadioNode, atomic: AtomicInteger, value: Int,
        update: ((RadioNode, AtomicInteger, Int, ((RadioNode, Int) -> Unit)) -> Unit),
        block: ((RadioNode, Int) -> Unit),
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateRadioValue(
        node: RadioNode,
        atomic: AtomicInteger,
        value: Int,
        block: ((RadioNode, Int) -> Unit)? = null,
    ): AtomicInteger {
        val isValid = node.isValid(value)
        val isEqual = value == atomic.get()
        if (isValid && !isEqual) {
            atomic.set(value)
            block?.let { it(node, value) }
        } else {
            Timber.e("doUpdateRadioValue node:$node, value:$value, isValid:$isValid, isEqual:$isEqual")
        }
        return atomic
    }

}