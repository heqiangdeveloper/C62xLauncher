package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.manager.ACManager

interface IACListener: IBaseListener{

    fun onACSwitchStatusChanged(status: Boolean, type: ACManager.SwitchNape)

}