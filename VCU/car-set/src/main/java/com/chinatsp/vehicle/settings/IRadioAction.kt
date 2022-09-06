package com.chinatsp.vehicle.settings

import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.optios.RadioNode
import com.common.xui.widget.tabbar.TabControlView
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/27 11:44
 * @desc   :
 * @version: 1.0
 */
interface IRadioAction {

    fun findRadioByNode(node: RadioNode): TabControlView?

    fun getRadioManager(): IRadioManager

    fun onPrevSelected(node: RadioNode, value: Int) {

    }

    fun onPostSelected(node: RadioNode, value: Int) {

    }

    fun onPostSelected(tabView: TabControlView, value: Int) {

    }


    fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
    }

    fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        timely: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = findRadioByNode(node)
        doUpdateRadio(node, value, tabView, timely, isInit)
    }

    fun doUpdateRadio(
        node: RadioNode,
        value: String,
        liveData: LiveData<Int>,
        tabView: TabControlView
    ) {
        val result = isCanToInt(value) && getRadioManager().doSetRadioOption(node, value.toInt())
        Timber.tag("IRadioAction").d("doUpdateRadio value:$value, result:$result, node:$node")
        tabView.takeIf { !result }?.let {
            doUpdateRadio(it, node.obtainSelectValue(liveData.value!!))
        }
    }

    fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        tabView: TabControlView?,
        timely: Boolean = false,
        isInit: Boolean = false
    ) {
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            val result = node.obtainSelectValue(value)
            doUpdateRadio(it, result, timely)
            onPostSelected(node, result)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, timely: Boolean = false) {
        tabView.setSelection(value.toString(), true)
        onPostSelected(tabView, value)
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }

    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.set.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }
}