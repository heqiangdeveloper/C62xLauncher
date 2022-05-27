package com.chinatsp.vehicle.settings.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.chinatsp.vehicle.settings.R
import com.king.base.util.DensityUtils

class SoundPopup : PopupWindow {
    private var recordingView: View? = null

    constructor(ctx: Context) : super(ctx) {
        init(ctx, text = "")
    }

    private var callBack: CallBack? = null

    private fun init(context: Context, text: String) {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        recordingView = inflater.inflate(R.layout.volume_adjustment_popupwindow, null)
        this.contentView = recordingView
        this.width = DensityUtils.dip2px(context, 960f)
        this.height = DensityUtils.dip2px(context, 580f)
        this.isTouchable = true
        isFocusable = true
        isOutsideTouchable = true
        this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_sound_popup_20))
        this.animationStyle = android.R.style.Animation_Dialog
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public fun showPopupWindow(parent: View) {
        if (!this.isShowing) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 300)
        }
    }


    interface CallBack {
        fun onShowContent(content: String)
        fun onSendContent(content: String)
    }
}