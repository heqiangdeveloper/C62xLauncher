package com.chinatsp.settinglib.bean

import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.bean.AirCmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/11 9:34
 * @desc   :
 * @version: 1.0
 */
class AirCmdParcel(val cmd: AirCmd, val callback: ICmdCallback?, var retryCount: Int = 1) {

    fun isRetry(): Boolean {
        return retryCount > 0
    }
}