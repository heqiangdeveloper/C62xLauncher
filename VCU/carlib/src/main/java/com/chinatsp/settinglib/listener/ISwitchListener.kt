package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.optios.SwitchNode

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/6 19:51
 * @desc   :
 * @version: 1.0
 */
interface ISwitchListener : IBaseListener {

    fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode)

}