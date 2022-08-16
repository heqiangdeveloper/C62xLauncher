package com.chinatsp.settinglib.bean

import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/9 20:47
 * @desc   :
 * @version: 1.0
 */
data class CanLocate(val origin: Origin = Origin.CABIN, val signal: Int = -1)
