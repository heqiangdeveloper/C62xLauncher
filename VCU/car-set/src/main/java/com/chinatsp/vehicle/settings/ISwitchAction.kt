package com.chinatsp.vehicle.settings

import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.common.xui.widget.button.switchbutton.SwitchButton
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/27 13:37
 * @desc   :
 * @version: 1.0
 */
interface ISwitchAction : IAction {

    fun findSwitchByNode(node: SwitchNode): SwitchButton?

    fun obtainActiveByNode(node: SwitchNode): Boolean {
        return true
    }

    fun obtainDependByNode(node: SwitchNode): Boolean {
        return true
    }

    fun getSwitchManager(): ISwitchManager

    fun onPrevChecked(button: SwitchButton, status: Boolean) {

    }

    fun onPostChecked(button: SwitchButton, status: Boolean) {

    }

    fun initSwitchOption(node: SwitchNode, liveData: LiveData<SwitchState>) {
//        val status = liveData.value ?: false
        liveData.value?.let {
            doUpdateSwitch(node, it, true)
        }
    }

    fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = getSwitchManager().doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            recoverSwitch(button, !status, timely = true)
        }
    }

    fun doUpdateSwitch(node: SwitchNode, status: SwitchState, timely: Boolean = false) {
        val button = findSwitchByNode(node)
        button?.let {
            doUpdateSwitch(it, status, timely)
        }
    }

    fun recoverSwitch(node: SwitchNode, status: Boolean, timely: Boolean = false) {
        val button = findSwitchByNode(node)
        button?.let {
            recoverSwitch(it, status, timely)
        }
    }

    fun doUpdateSwitch(swb: SwitchButton, status: SwitchState, timely: Boolean = false) {
        if (!timely) {
            swb.setCheckedNoEvent(status.get())
        } else {
            swb.setCheckedImmediatelyNoEvent(status.get())
        }
        onPostChecked(swb, status.get())
    }

    fun recoverSwitch(swb: SwitchButton, status: Boolean, timely: Boolean = false) {
        if (!timely) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
        onPostChecked(swb, status)
    }

    fun updateSwitchEnable(node: SwitchNode) {
        findSwitchByNode(node)?.let {
            val selfActive = obtainActiveByNode(node)
            val dependActive = obtainDependByNode(node)
            Timber.d("updateSwitchEnable $node, selfActive:$selfActive, dependActive:$dependActive")
            updateEnable(it, obtainActiveByNode(node), obtainDependByNode(node))
        }
    }

}