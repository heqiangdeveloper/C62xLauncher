package com.chinatsp.vehicle.settings.fragment.sound

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.R
import com.common.xui.utils.DensityUtils
import com.common.xui.widget.picker.VerticalSeekBar

class SoundDialogFragment(_listener: IViewListener?) : DialogFragment(),
    VerticalSeekBar.OnValuesChangeListener {
    private var recordingView: View? = null
    private lateinit var phoneVolumeVsb: VerticalSeekBar
    private lateinit var naviVolumeVsb: VerticalSeekBar
    private lateinit var voiceVolumeVsb: VerticalSeekBar
    private lateinit var mediaVolumeVsb: VerticalSeekBar
    private lateinit var systemVolumeVsb: VerticalSeekBar
    private lateinit var closeDialog: AppCompatImageView

    private lateinit var naviVolumeTxt: AppCompatTextView
    private lateinit var phoneVolumeTxt: AppCompatTextView
    private lateinit var voiceVolumeTxt: AppCompatTextView
    private lateinit var mediaVolumeTxt: AppCompatTextView
    private lateinit var systemVolumeTxt: AppCompatTextView
    private var callBack: CallBack? = null
    private var listener: IViewListener? = _listener
//    fun newInstance(): SoundDialogFragment {
//        val args = Bundle()
//        val fragment = SoundDialogFragment()
//        fragment.arguments = args
//        return fragment
//    }

    val manager: VoiceManager by lazy {
        VoiceManager.instance
    }

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

    private fun setSeekBarListener(listener: VerticalSeekBar.OnValuesChangeListener) {

        naviVolumeVsb.setOnBoxedPointsChangeListener(listener)
        voiceVolumeVsb.setOnBoxedPointsChangeListener(listener)
        mediaVolumeVsb.setOnBoxedPointsChangeListener(listener)
        phoneVolumeVsb.setOnBoxedPointsChangeListener(listener)
        systemVolumeVsb.setOnBoxedPointsChangeListener(listener)
    }

    private fun initVerticalSeekBar() {
        recordingView?.let {
            naviVolumeVsb = it.findViewById(R.id.sound_audio_navi_volume)
            voiceVolumeVsb = it.findViewById(R.id.sound_audio_voice_volume)
            mediaVolumeVsb = it.findViewById(R.id.sound_audio_media_volume)
            phoneVolumeVsb = it.findViewById(R.id.sound_audio_phone_volume)
            systemVolumeVsb = it.findViewById(R.id.sound_audio_system_volume)
            closeDialog = it.findViewById(R.id.close_dialog)
            naviVolumeTxt = it.findViewById(R.id.navi_volume_txt)
            phoneVolumeTxt = it.findViewById(R.id.phone_volume_txt)
            voiceVolumeTxt = it.findViewById(R.id.voice_volume_txt)
            mediaVolumeTxt = it.findViewById(R.id.media_volume_txt)
            systemVolumeTxt = it.findViewById(R.id.system_volume_txt)
        }
    }

    fun updateVolumeValue(volume: Volume?) {
        volume?.let {
            var seekBar: VerticalSeekBar? = null
            var textView: AppCompatTextView? = null
            when (it.type) {
                Volume.Type.NAVI -> {
                    seekBar = naviVolumeVsb
                    textView = naviVolumeTxt
                }
                Volume.Type.VOICE -> {
                    seekBar = voiceVolumeVsb
                    textView = voiceVolumeTxt
                }
                Volume.Type.MEDIA -> {
                    seekBar = mediaVolumeVsb
                    textView = mediaVolumeTxt
                }
                Volume.Type.PHONE -> {
                    seekBar = phoneVolumeVsb
                    textView = phoneVolumeTxt
                }
                Volume.Type.SYSTEM -> {
                    seekBar = systemVolumeVsb
                    textView = systemVolumeTxt
                }
            }
            seekBar?.let { bar ->
                bar.max = it.max
                bar.progress = it.pos
            }
            textView?.text = it.pos.toString()
        }
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }

    interface CallBack {
        fun onShowContent(content: String)
        fun onSendContent(content: String)
    }

    interface IViewListener {
        fun doViewCreated()
    }

    override fun onPointsChanged(view: VerticalSeekBar?, progress: Int) {

        when (view?.id) {
            R.id.sound_audio_navi_volume -> {
                manager.doSetVolume(Volume.Type.NAVI, progress)
                naviVolumeTxt.text = progress.toString()
            }
            R.id.sound_audio_voice_volume -> {
                manager.doSetVolume(Volume.Type.VOICE, progress)
                voiceVolumeTxt.text = progress.toString()
            }
            R.id.sound_audio_media_volume -> {
                manager.doSetVolume(Volume.Type.MEDIA, progress)
                mediaVolumeTxt.text = progress.toString()
            }
            R.id.sound_audio_phone_volume -> {
                manager.doSetVolume(Volume.Type.PHONE, progress)
                phoneVolumeTxt.text = progress.toString()
            }
//            R.id.sound_audio_system_volume -> {
//                manager.doSetVolume(Volume.Type.SYSTEM, progress)
//                systemVolumeTxt.text = progress.toString()
//            }
            else -> {
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: VerticalSeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: VerticalSeekBar?) {

    }

}

