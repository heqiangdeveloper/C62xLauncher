package com.chinatsp.settinglib.bean

import android.media.AudioAttributes

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/9 20:40
 * @desc   :
 * @version: 1.0
 */
data class Volume(val type: Type, var min: Int, var max: Int, var pos:Int): Comparable<Volume> {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    enum class Type(val id: Int) {
        NAVI(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE),
//        VOICE(AudioAttributes.USAGE_NOTIFICATION_EVENT),
        VOICE(AudioAttributes.USAGE_ASSISTANT),
        MEDIA(AudioAttributes.USAGE_MEDIA),
        PHONE(AudioAttributes.USAGE_VOICE_COMMUNICATION),
        SYSTEM(AudioAttributes.USAGE_ASSISTANT),
        CAR_SCREEN(-1),
        METER_SCREEN(-1),
        AC_SCREEN(-1)
    }

    override fun compareTo(other: Volume): Int {
        return if (type == other.type && pos == other.pos && min == other.min && max == other.max) 0 else 1
    }

}