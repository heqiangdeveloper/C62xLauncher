package com.chinatsp.vehicle.settings

import android.view.View
import android.view.ViewGroup

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/5 16:09
 * @desc   :
 * @version: 1.0
 */
interface IAction {

    fun updateEnable(view: View, isActive: Boolean, depend: Boolean) {
        val enable = isActive && depend
        val parent = view.parent as ViewGroup
        val alpha = if (enable) 1.0f else 0.6f
        if (parent.alpha != alpha) {
            parent.alpha = alpha
        }
        updateEnable(parent, enable)
    }

    private fun updateEnable(view: View, enable: Boolean) {
        view.isEnabled = enable
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                updateEnable(view.getChildAt(index), enable)
            }
            return
        }
    }
}