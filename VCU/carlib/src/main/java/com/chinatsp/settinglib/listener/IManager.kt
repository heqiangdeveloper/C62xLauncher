package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.BuildConfig
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode


interface IManager {

//    val develop: Boolean
//        get() = BuildConfig.develop

    /**
     * @param serial 注册时返回的序列号
     * @return
     */
    fun unRegisterVcuListener(serial: Int, callSerial: Int = serial): Boolean

    fun onRegisterVcuListener(priority: Int = 0, listener: IBaseListener): Int

    fun doSwitchChanged(node: SwitchNode, status: SwitchState) {}

    fun doOptionChanged(node: RadioNode, value: RadioState) {}

    fun doProgressChanged(node: Progress, value: Int) {}

}