package com.chinatsp.settinglib.manager

import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:26
 * @desc   :
 * @version: 1.0
 */
interface ISwitchManager : IManager {

    /**
     *
     * @param   switchNode 开关选项
     * @return  返回开关状态
     */
    fun doGetSwitchOption(switchNode: SwitchNode): Boolean

    /**
     *
     * @param   switchNode 开关选项
     * @param   status 开关期望状态
     * @return  返回接口调用是否成功
     */
    fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean

}