package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.sign.SignalOrigin


interface IManager {

    /**
     * @param serial 注册时返回的序列号
     * @return
     */
    fun unRegisterVcuListener(serial: Int, callSerial: Int = 0): Boolean

    fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int

}