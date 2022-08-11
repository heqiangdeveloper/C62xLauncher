package com.chinatsp.vehicle.settings

import android.content.Context
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/8 13:15
 * @desc   :
 * @version: 1.0
 */
object HintHold {

    private var title: Int? = null

    private var content: Int? = null

    fun setTitle(title: Int) {
        this.title = title
    }

    fun getTitle(context: Context): String {
        try {
            if (null != title) {
                return context.resources.getString(title!!)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return ""
    }

    fun setContent(content: Int) {
        this.content = content
    }

    fun getContent(context: Context): String {
        try {
            if (null != content) {
                return context.resources.getString(content!!)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return ""
    }

}