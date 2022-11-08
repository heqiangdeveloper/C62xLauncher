package com.chinatsp.settinglib.manager

import com.chinatsp.settinglib.bean.CommandParcel

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/6 11:28
 * @desc   :
 * @version: 1.0
 */
interface ICmdExpress {

    fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean = true)
}