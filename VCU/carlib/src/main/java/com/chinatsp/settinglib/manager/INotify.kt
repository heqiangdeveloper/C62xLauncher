package com.chinatsp.settinglib.manager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/12/10 16:49
 * @desc   :
 * @version: 1.0
 */
interface INotify {

    fun doNotify(signal: Int, value: Any)
}