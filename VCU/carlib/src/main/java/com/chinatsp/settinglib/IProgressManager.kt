package com.chinatsp.settinglib

import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IManager
import com.chinatsp.settinglib.optios.Progress
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/13 13:19
 * @desc   :
 * @version: 1.0
 */
interface IProgressManager : IManager {

    fun doGetVolume(type: Progress): Volume?

    fun doSetVolume(type: Progress, position: Int): Boolean

    fun doUpdateProgress(
        volume: Volume,
        value: Int,
        status: Boolean,
        block: ((Progress, Int) -> Unit)? = null,
    ): Volume {
        val valid = volume.isValid(value)
        val notSame = value != volume.pos
        status && volume.isValid(value) && value != volume.pos
        if (status && valid && notSame) {
            volume.pos = value
            block?.let { it(volume.type, value) }
        } else {
            Timber.d("doUpdateProgress node : ${volume.type}, status:$status, valid:$valid, notSame:$notSame, value:$value")
        }
        return volume
    }

}