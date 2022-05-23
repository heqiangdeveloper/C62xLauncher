package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.ACManager


interface IACListener: IBaseListener{

    fun onACSwitchStatusChanged(status: Boolean, type: ACManager.SwitchNape)

}