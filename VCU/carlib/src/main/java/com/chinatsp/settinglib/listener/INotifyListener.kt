package com.chinatsp.settinglib.listener

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/12/10 16:51
 * @desc   :
 * @version: 1.0
 */
interface INotifyListener {

    fun onNotify(signal: Int, value: Any)
}