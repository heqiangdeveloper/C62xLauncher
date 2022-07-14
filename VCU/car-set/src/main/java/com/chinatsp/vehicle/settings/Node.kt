package com.chinatsp.vehicle.settings

import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/25 16:46
 * @desc   :
 * @version: 1.0
 */
class Node(var id: Int = Constant.INVALID, var presentId: Int = Constant.INVALID) {
    var valid: Boolean = false

    override fun toString(): String {
        return "Node(id=$id, presentId=$presentId, valid=$valid)"
    }


}