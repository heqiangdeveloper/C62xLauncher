package com.chinatsp.vehicle.controller.annotation

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/7/18 15:17
 * @desc :
 * @version: 1.0
 */
@SuppressLint("UniqueConstants")
@IntDef(
    Model.VOID, Model.GLOBAL,
    Model.ACCESS_DOOR, Model.ACCESS_WINDOW, Model.ACCESS_STERN, Model.ACCESS_MIRROR,
    Model.LIGHT_COMMON, Model.LIGHT_AMBIENT, Model.LIGHT_SCREEN,
    Model.AUDIO_SOUND, Model.AUDIO_EFFECT,
    Model.CABIN_AIR, Model.CABIN_SEAT, Model.CABIN_SAFE, Model.CABIN_OTHER,
    Model.ADAS_SMART, Model.ADAS_AHEAD, Model.ADAS_LANE, Model.ADAS_REAR, Model.ADAS_OTHER
)
@Retention(RetentionPolicy.SOURCE)
annotation class Model {
    companion object {
        private const val MASK = 0xF shl 28
        const val GLOBAL = 0x1 shl 28
        const val ACCESS = 0x2 shl 28
        const val LIGHT = 0x3 shl 28
        const val AUDIO = 0x4 shl 28
        const val CABIN = 0x5 shl 28
        const val ADAS = 0x6 shl 28
        const val PANORAMA = 0x7 shl 28
        const val AUTO_PARK = 0x8 shl 28

        const val VOID = 0

        const val ACCESS_DOOR = 0x01 or ACCESS
        const val ACCESS_WINDOW = 0x02 or ACCESS
        const val ACCESS_STERN = 0x03 or ACCESS
        const val ACCESS_MIRROR = 0x04 or ACCESS

        const val LIGHT_COMMON = 0x01 or LIGHT
        const val LIGHT_AMBIENT = 0x02 or LIGHT
        const val LIGHT_SCREEN = 0x03 or LIGHT

        const val AUDIO_SOUND = 0x01 or AUDIO
        const val AUDIO_EFFECT = 0x02 or AUDIO

        const val CABIN_AIR = 0x01 or CABIN
        const val CABIN_SEAT = 0x02 or CABIN
        const val CABIN_SAFE = 0x03 or CABIN
        const val CABIN_OTHER = 0x04 or CABIN
        const val CABIN_WHEEL = 0x05 or CABIN

        const val ADAS_SMART = 0x01 or ADAS //智能巡航
        const val ADAS_AHEAD = 0x02 or ADAS //前向辅助
        const val ADAS_LANE = 0x03 or ADAS //车道辅助
        const val ADAS_REAR = 0x04 or ADAS //侧后辅助
        const val ADAS_OTHER = 0x05 or ADAS //灯光辅助 交通标志

        fun obtainEchelon(@Model serial: Int): Int {
            return serial and MASK
        }

    }
}