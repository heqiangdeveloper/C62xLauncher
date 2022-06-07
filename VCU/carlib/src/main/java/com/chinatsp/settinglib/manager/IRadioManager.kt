package com.chinatsp.settinglib.manager

import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.RadioNode

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:26
 * @desc   :
 * @version: 1.0
 */
interface IRadioManager : IManager {

    /**
     * @param   radioNode 选项
     * @return 返回选中的值
     */
    fun doGetRadioOption(radioNode: RadioNode): Int

    /**
     * @param   radioNode 选项
     * @param   value 选中项的 value值
     * @return  返回接口调用是否成功
     */
    fun doSetRadioOption(radioNode: RadioNode, value: Int): Boolean
}