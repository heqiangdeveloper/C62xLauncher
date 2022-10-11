package com.chinatsp.vehicle.settings

import com.chinatsp.settinglib.Constant

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/25 16:46
 * @desc   :
 * @version: 1.0
 */
class Node(var uid: Int = Constant.INVALID, var pid: Int = Constant.INVALID) {

    var valid: Boolean = true

    var pnode: Node? = null

    var cnode: Node? = null

    override fun toString(): String {
        return "Node(uid=$uid, pid=$pid, valid=$valid)"
    }


}