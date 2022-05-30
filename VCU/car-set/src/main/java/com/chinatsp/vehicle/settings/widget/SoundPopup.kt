package com.chinatsp.vehicle.settings.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.manager.SoundManager
import com.chinatsp.vehicle.settings.R
import com.common.xui.widget.picker.VerticalSeekBar
import com.king.base.util.DensityUtils

class SoundPopup : PopupWindow, VerticalSeekBar.OnChangeListener {

    private var recordingView: View? = null

    private lateinit var phoneVolumeVsb: VerticalSeekBar
    private lateinit var naviVolumeVsb: VerticalSeekBar
    private lateinit var voiceVolumeVsb: VerticalSeekBar
    private lateinit var mediaVolumeVsb: VerticalSeekBar
    private lateinit var systemVolumeVsb: VerticalSeekBar

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
        initVerticalSeekBar()
        setSeekBarListener(this)
    }

    private fun setSeekBarListener(listener: VerticalSeekBar.OnChangeListener) {
        naviVolumeVsb?.setOnChangeListener(listener)
        voiceVolumeVsb?.setOnChangeListener(listener)
        mediaVolumeVsb?.setOnChangeListener(listener)
        phoneVolumeVsb?.setOnChangeListener(listener)
        systemVolumeVsb?.setOnChangeListener(listener)
    }

    private fun initVerticalSeekBar() {
        recordingView?.let {
            naviVolumeVsb = it.findViewById(R.id.sound_audio_navi_volume)
            voiceVolumeVsb = it.findViewById(R.id.sound_audio_voice_volume)
            mediaVolumeVsb = it.findViewById(R.id.sound_audio_media_volume)
            phoneVolumeVsb = it.findViewById(R.id.sound_audio_phone_volume)
            systemVolumeVsb = it.findViewById(R.id.sound_audio_system_volume)
        }
    }

    fun setSeekBarMaxValue(type: Type, value: Int) {
        val seekBar = when (type) {
            Type.NAVI -> naviVolumeVsb
            Type.VOICE -> voiceVolumeVsb
            Type.MEDIA -> mediaVolumeVsb
            Type.PHONE -> phoneVolumeVsb
            Type.SYSTEM -> systemVolumeVsb
        }
        LogManager.d("SoundPopup", "setSeekBarMaxValue type:$type, value:$value")
        seekBar.setMaxValue(value)
    }

    fun updateSeekBarValue(type: Type, value: Int) {
        val seekBar = when (type) {
            Type.NAVI -> naviVolumeVsb
            Type.VOICE -> voiceVolumeVsb
            Type.MEDIA -> mediaVolumeVsb
            Type.PHONE -> phoneVolumeVsb
            Type.SYSTEM -> systemVolumeVsb
        }
        LogManager.d("SoundPopup", "updateSeekBarValue type:$type, value:$value")
        seekBar.currentValue = value
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    fun showPopupWindow(parent: View) {
        if (!this.isShowing) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 300)
        }
    }


    interface CallBack {
        fun onShowContent(content: String)
        fun onSendContent(content: String)
    }

    enum class Type {
        NAVI,
        VOICE,
        MEDIA,
        PHONE,
        SYSTEM
    }

    override fun onChange(view: View?, currentValue: Int) {
        val volumeService = SoundManager.getInstance()
        when (view!!.id) {
            R.id.sound_audio_navi_volume -> {volumeService.naviVolume = currentValue}
            R.id.sound_audio_voice_volume -> {volumeService.cruiseVolume = currentValue}
            R.id.sound_audio_media_volume -> {volumeService.mediaVolume = currentValue}
            R.id.sound_audio_phone_volume -> {volumeService.phoneVolume = currentValue}
            R.id.sound_audio_system_volume -> {volumeService.systemVolume = currentValue}
            else -> {}
        }
    }

}