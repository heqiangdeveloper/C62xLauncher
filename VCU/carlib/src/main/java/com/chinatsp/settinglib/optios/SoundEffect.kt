package com.chinatsp.settinglib.optios

import android.car.media.CarAudioManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/11 16:41
 * @desc   :
 * @version: 1.0
 */
enum class SoundEffect(val id: Int, description: String) {

//    BEGIN(CarAudioManager.EQ_MODE_AMP_BEGIN, "默认"),
//    CLASSIC(CarAudioManager.EQ_MODE_AMP_CLASSIC, "典型的"),
//    POP(CarAudioManager.EQ_MODE_AMP_POP, "流行"),
//    JAZZ(CarAudioManager.EQ_MODE_AMP_JAZZ, "爵士"),
//    BEATS(CarAudioManager.EQ_MODE_AMP_BEATS, "打击"),
//    ROCK(CarAudioManager.EQ_MODE_AMP_ROCK, "摇滚"),
//    CUSTOM(CarAudioManager.EQ_MODE_AMP_CUSTOM, "自定义");


    BEGIN(CarAudioManager.EQ_MODE_FLAT, "默认"),
    CLASSIC(CarAudioManager.EQ_MODE_CLASSIC, "典型的"),
    POP(CarAudioManager.EQ_MODE_POP, "流行"),
    JAZZ(CarAudioManager.EQ_MODE_JAZZ, "爵士"),
    BEATS(CarAudioManager.EQ_MODE_BEATS, "打击"),
    ROCK(CarAudioManager.EQ_MODE_ROCK, "摇滚"),
    CUSTOM(CarAudioManager.EQ_MODE_CUSTOM, "自定义");

    companion object {
        fun getEffect(value: Int): SoundEffect {
            values().forEach {
                if (it.id == value) return it
            }
            return POP
        }

        fun idArray(): IntArray {
            return values().map { it.id }.toIntArray()
        }
    }

}