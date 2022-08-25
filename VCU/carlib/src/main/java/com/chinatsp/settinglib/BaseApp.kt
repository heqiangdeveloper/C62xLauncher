package com.chinatsp.settinglib

import android.app.Application
import android.os.Handler
import kotlin.properties.Delegates

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/21 16:03
 * @desc   :
 * @version: 1.0
 */
open class BaseApp: Application() {

    val mainHandler: Handler by lazy {
        Handler(mainLooper)
    }

    companion object {
        var instance: BaseApp by Delegates.notNull()
    }

}