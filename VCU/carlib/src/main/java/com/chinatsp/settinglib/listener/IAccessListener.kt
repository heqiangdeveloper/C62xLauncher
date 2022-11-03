package com.chinatsp.settinglib.listener

import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/3 11:16
 * @desc   :
 * @version: 1.0
 */
interface IAccessListener: IBaseListener {

    fun onAccessChanged(@IPart part: Int, @Model model: Int, value: Int)
}