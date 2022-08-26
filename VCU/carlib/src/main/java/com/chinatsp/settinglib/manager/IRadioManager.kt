package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
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
        if (value is Int) {
            onRadioChanged(node, atomic, value, this::doUpdateRadioValue) { radioNode, newValue ->
                doRadioChanged(radioNode, newValue)
            }
        }
    }

    fun onRadioChanged(
        node: RadioNode, atomic: AtomicInteger, value: Int,
        update: ((RadioNode, AtomicInteger, Int, ((RadioNode, Int) -> Unit)) -> Unit),
        block: ((RadioNode, Int) -> Unit)
    ) {
        update(node, atomic, value, block)
    }

    fun doUpdateRadioValue(
        node: RadioNode,
        atomic: AtomicInteger,
        value: Int,
        block: ((RadioNode, Int) -> Unit)? = null
    ): AtomicInteger {
        if (node.isValid(value) && atomic.get() != value) {
            atomic.set(value)
            block?.let { it(node, value) }
        }
        return atomic
    }

    fun doUpdateProgress(
        node: Progress,
        atomic: AtomicInteger,
        value: Int,
        block: ((Progress, Int) -> Unit)? = null
    ): AtomicInteger {
        if (node.isValid(value) && atomic.get() != value) {
            atomic.set(value)
            block?.let { it(node, value) }
        }
        return atomic
    }

}