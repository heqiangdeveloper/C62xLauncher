package com.chinatsp.settinglib.listener.sound

import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/30 14:25
 * @desc   :
 * @version: 1.0
 */
interface ISoundListener: IBaseListener {

    fun onSoundVolumeChanged(vararg array: Volume)
}