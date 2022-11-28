package com.chinatsp.settinglib.bean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/24 14:53
 * @desc   :
 * @version: 1.0
 */
data class AppState(
    val activeStatus: String,
    val `data`: Data = Data(),
    val default: String = "360",
    val scene: String = "carControl",
    val sceneStatus: String = "default",
    val service: String = "carControl",
)

class Data