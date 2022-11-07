package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.BaseCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/11 9:34
 * @desc   :
 * @version: 1.0
 */
class CommandParcel(
    val command: BaseCmd,
    val callback: ICmdCallback?,
    val receiver: ICmdExpress,
    var retryCount: Int = 1,
) {

    fun isRetry(): Boolean {
        return retryCount > 0
    }
}