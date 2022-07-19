package com.chinatsp.settinglib.optios

import android.car.media.CarAudioManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/11 16:41
 * @desc   :
 * @version: 1.0
 */
enum class SoundEffect(val id: Int) {

    POP(CarAudioManager.EQ_MODE_FLAT),
    FLAT(CarAudioManager.EQ_MODE_POP),
    ROCK(CarAudioManager.EQ_MODE_ROCK),
    JAZZ(CarAudioManager.EQ_MODE_JAZZ),
    VOCAL(CarAudioManager.EQ_MODE_VOCAL),
    CLASSIC(CarAudioManager.EQ_MODE_CLASSIC),
    CUSTOM(CarAudioManager.EQ_MODE_CUSTOM);

    companion object {
        fun getEffect(value: Int): SoundEffect {
            values().forEach {
                if (it.id == value) return it
            }
            return POP
        }
    }

}