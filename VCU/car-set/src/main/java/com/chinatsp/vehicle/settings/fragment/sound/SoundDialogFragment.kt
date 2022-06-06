package com.chinatsp.vehicle.settings.fragment.sound

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.manager.SoundManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.bean.Volume
import com.common.xui.utils.DensityUtils
import com.common.xui.widget.picker.VerticalSeekBar

class SoundDialogFragment(_listener: IViewListener?) : DialogFragment(), VerticalSeekBar.OnChangeListener {
    private var recordingView: View? = null
    private lateinit var phoneVolumeVsb: VerticalSeekBar
    private lateinit var naviVolumeVsb: VerticalSeekBar
    private lateinit var voiceVolumeVsb: VerticalSeekBar
    private lateinit var mediaVolumeVsb: VerticalSeekBar
    private lateinit var systemVolumeVsb: VerticalSeekBar
    private lateinit var closeDialog: AppCompatImageView
    private var callBack: CallBack? = null
    private var listener: IViewListener? = _listener
//    fun newInstance(): SoundDialogFragment {
//        val args = Bundle()
//        val fragment = SoundDialogFragment()
//        fragment.arguments = args
//        return fragment
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        recordingView = inflater.inflate(R.layout.volume_adjustment_popupwindow, null, false);
        initVerticalSeekBar()
        setSeekBarListener(this)
        setCheckedChangeListener();
        LogManager.e("SoundDialogFragment", "onCreateView")
        return recordingView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener?.doViewCreated()
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(DensityUtils.dp2px(960f), DensityUtils.dp2px(680f));
    }

    private fun setCheckedChangeListener() {
        closeDialog.setOnClickListener {
            dismiss()
        }
    }

    private fun setSeekBarListener(listener: VerticalSeekBar.OnChangeListener) {
        naviVolumeVsb.setOnChangeListener(listener)
        voiceVolumeVsb.setOnChangeListener(listener)
        mediaVolumeVsb.setOnChangeListener(listener)
        phoneVolumeVsb.setOnChangeListener(listener)
        systemVolumeVsb.setOnChangeListener(listener)
    }

    private fun initVerticalSeekBar() {
        recordingView?.let {
            naviVolumeVsb = it.findViewById(R.id.sound_audio_navi_volume)
            voiceVolumeVsb = it.findViewById(R.id.sound_audio_voice_volume)
            mediaVolumeVsb = it.findViewById(R.id.sound_audio_media_volume)
            phoneVolumeVsb = it.findViewById(R.id.sound_audio_phone_volume)
            systemVolumeVsb = it.findViewById(R.id.sound_audio_system_volume)
            closeDialog = it.findViewById(R.id.close_dialog)
        }
    }

    fun updateVolumeValue(type: Type, volume: Volume?) {
        val seekBar = when (type) {
            Type.NAVI -> naviVolumeVsb
            Type.VOICE -> voiceVolumeVsb
            Type.MEDIA -> mediaVolumeVsb
            Type.PHONE -> phoneVolumeVsb
            Type.SYSTEM -> systemVolumeVsb
        }
        volume?.let {
            seekBar.setMaxValue(it.max)
            seekBar.setMinValue(it.min)
            seekBar.currentValue = it.pos
            LogManager.d("SoundDialogFragment", "updateVolumeValue type:$type, volume:$volume, seekbar:$seekBar")
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
        LogManager.e("SoundDialogFragment", "setSeekBarMaxValue type:$type, value:$value")
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
        LogManager.e("SoundDialogFragment", "updateSeekBarValue type:$type, value:$value")
        seekBar.currentValue = value
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
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
        when (view?.id) {
            R.id.sound_audio_navi_volume -> {
                volumeService.naviVolume = currentValue
            }
            R.id.sound_audio_voice_volume -> {
                volumeService.cruiseVolume = currentValue
            }
            R.id.sound_audio_media_volume -> {
                volumeService.mediaVolume = currentValue
            }
            R.id.sound_audio_phone_volume -> {
                volumeService.phoneVolume = currentValue
            }
            R.id.sound_audio_system_volume -> {
                volumeService.systemVolume = currentValue
            }
            else -> {
            }
        }
    }

    interface IViewListener {
        fun doViewCreated()
    }

}