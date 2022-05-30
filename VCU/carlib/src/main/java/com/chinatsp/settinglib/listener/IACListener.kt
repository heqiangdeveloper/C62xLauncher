package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.optios.SwitchNode

interface IACListener: IBaseListener{

    fun onACSwitchStatusChanged(status: Boolean, type: SwitchNode)

    fun onAcComfortOptionChanged(location: Int)

}