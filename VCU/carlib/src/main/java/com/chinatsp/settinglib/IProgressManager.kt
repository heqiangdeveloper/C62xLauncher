package com.chinatsp.settinglib

import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.optios.RadioNode
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/13 13:19
 * @desc   :
 * @version: 1.0
 */
interface IProgressManager {

    fun doGetVolume(type: Volume.Type): Volume?

    fun doSetVolume(type: Volume.Type, position: Int): Boolean

    fun doUpdateProgress(volume: Volume, value: Int, status: Boolean, block: ((Int) -> Unit)? = null): Volume {
        if (status && value != volume.pos) {
            volume.pos = value
            block?.let { it(value) }
        }
        return volume
    }

}