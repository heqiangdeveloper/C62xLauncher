package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/10 20:06
 * @desc   :
 * @version: 1.0
 */
/**
 * @param on 中间伯反馈 开关状态 开 的值
 * @param off 中间伯反馈 开关状态 关 的值
 * @param uon 设置给中间伯 开关状态 开 的值
 * @param uoff 设置给中间伯 开关状态 开关的值
 * @param def 默认开关值
 */
//data class Norm(val don: Int = 1, val doff: Int = 2, val uon: Int = 1, val uoff: Int = 2, val def: Boolean = true) {
data class Norm(val on: Int = 1, val off: Int = 2, val origin: Origin = Origin.CABIN, val signal: Int = -1)