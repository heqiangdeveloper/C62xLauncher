package com.chinatsp.settinglib.listener

import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/6 19:51
 * @desc   :
 * @version: 1.0
 */
interface IProgressListener: IBaseListener {

    fun onProgressChanged(node: Progress, value: Int)
}