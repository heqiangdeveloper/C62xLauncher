package com.chinatsp.settinglib

import android.content.Context
import android.provider.Settings
import java.lang.Exception

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date : 2022/6/21 15:53
 * @desc :
 * @version: 1.0
 */
object SettingUtils {

    val AUXILIARY_LINE: String
        get() = "AUXILIARY_LINE"

    val SHOW_AREA: String
        get() = "SHOW_AREA"

    fun putInt(context: Context, key: String, value: Int): Boolean {
        try {
            return Settings.Global.putInt(context.contentResolver, key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getInt(context: Context, key: String, value: Int): Int {
        try {
            return Settings.Global.getInt(context.contentResolver, key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }
}