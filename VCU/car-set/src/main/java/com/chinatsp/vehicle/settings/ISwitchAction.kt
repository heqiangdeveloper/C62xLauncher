package com.chinatsp.vehicle.settings

import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.common.xui.widget.button.switchbutton.SwitchButton

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/27 13:37
 * @desc   :
 * @version: 1.0
 */
interface ISwitchAction {

    fun findSwitchByNode(node: SwitchNode): SwitchButton?

    fun getSwitchManager(): ISwitchManager

    fun onPrevChecked(button: SwitchButton, status: Boolean) {

    }

    fun onPostChecked(button: SwitchButton, status: Boolean) {

    }

    fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = getSwitchManager().doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            doUpdateSwitch(button, !status, timely = true)
        }
    }

    fun doUpdateSwitch(node: SwitchNode, status: Boolean, timely: Boolean = false) {
        val button = findSwitchByNode(node)
        button?.let {
            doUpdateSwitch(it, status, timely)
        }
    }

    fun doUpdateSwitch(swb: SwitchButton, status: Boolean, timely: Boolean = false) {
        if (!timely) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
        onPostChecked(swb, status)
    }
}