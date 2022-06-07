package com.chinatsp.settinglib.listener.cabin

import com.chinatsp.settinglib.listener.ISwitchListener

interface IACListener: ISwitchListener {

    fun onAcComfortOptionChanged(location: Int)

}