package com.chinatsp.settinglib.bean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 17:14
 * @desc   :
 * @version: 1.0
 */
enum class DownStatus(val value: Int) {
    //解锁主动换气使能开关
    INACTIVE(0x0),
    ENABLED(0x1),
    DISABLED(0x2),
    RESERVED(0x3)
}