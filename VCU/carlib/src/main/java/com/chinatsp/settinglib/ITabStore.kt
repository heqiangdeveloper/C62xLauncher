package com.chinatsp.settinglib

import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 19:09
 * @desc   :
 * @version: 1.0
 */
interface ITabStore {

    val tabSerial: AtomicInteger

    fun getTabSerial() = tabSerial.get()

    fun setTabSerial(serial: Int) = tabSerial.set(serial)
}