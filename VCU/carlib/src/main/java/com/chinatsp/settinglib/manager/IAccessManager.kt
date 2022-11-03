package com.chinatsp.settinglib.manager

import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/3 11:22
 * @desc   :
 * @version: 1.0
 */
interface IAccessManager {

    fun obtainAccessState(@IPart part: Int, @Model model: Int): Int?
}