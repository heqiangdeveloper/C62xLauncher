package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AudioSoundVolumeDialogBinding
import com.chinatsp.vehicle.settings.vm.sound.VolumeViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.picker.VerticalSeekBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VolumeDialogFragment : BaseDialogFragment<VolumeViewModel, AudioSoundVolumeDialogBinding>(),
    VerticalSeekBar.OnValuesChangeListener {

    val manager: VoiceManager by lazy {
        VoiceManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.audio_sound_volume_dialog
    }

    private fun setSeekBarListener(listener: VerticalSeekBar.OnValuesChangeListener) {
        binding.soundAudioNaviVolume.setOnBoxedPointsChangeListener(listener)
        binding.soundAudioVoiceVolume.setOnBoxedPointsChangeListener(listener)
        binding.soundAudioMediaVolume.setOnBoxedPointsChangeListener(listener)
        binding.soundAudioPhoneVolume.setOnBoxedPointsChangeListener(listener)
        binding.soundAudioSystemVolume.setOnBoxedPointsChangeListener(listener)
    }

    private fun updateVolumeValue(volume: Volume?) {
        volume?.let {
            var seekBar: VerticalSeekBar? = null
            var textView: AppCompatTextView? = null
            when (it.type) {
                Volume.Type.NAVI -> {
                    seekBar = binding.soundAudioNaviVolume
                    textView = binding.naviVolumeTxt
                }
                Volume.Type.VOICE -> {
                    seekBar = binding.soundAudioVoiceVolume
                    textView = binding.voiceVolumeTxt
                }
                Volume.Type.MEDIA -> {
                    seekBar = binding.soundAudioMediaVolume
                    textView = binding.mediaVolumeTxt
                }
                Volume.Type.PHONE -> {
                    seekBar = binding.soundAudioPhoneVolume
                    textView = binding.phoneVolumeTxt
                }
                Volume.Type.SYSTEM -> {
                    seekBar = binding.soundAudioSystemVolume
                    textView = binding.systemVolumeTxt
                }
                else -> {
                }
            }
            seekBar?.max = it.max
            seekBar?.progress = it.pos
            textView?.text = it.pos.toString()
        }
    }



    override fun onPointsChanged(view: VerticalSeekBar?, progress: Int) {
        when (view?.id) {
            R.id.sound_audio_navi_volume -> {
                manager.doSetVolume(Volume.Type.NAVI, progress)
            }
            R.id.sound_audio_voice_volume -> {
                manager.doSetVolume(Volume.Type.VOICE, progress)
            }
            R.id.sound_audio_media_volume -> {
                manager.doSetVolume(Volume.Type.MEDIA, progress)
            }
            R.id.sound_audio_phone_volume -> {
                manager.doSetVolume(Volume.Type.PHONE, progress)
            }
//            R.id.sound_audio_system_volume -> {
//                manager.doSetVolume(Volume.Type.SYSTEM, progress)
//            }
            else -> {
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: VerticalSeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: VerticalSeekBar?) {

    }


    override fun initData(savedInstanceState: Bundle?) {
        setSeekBarListener(this)
        updateVolumeValue(viewModel.naviVolume.value)
        updateVolumeValue(viewModel.voiceVolume.value)
        updateVolumeValue(viewModel.mediaVolume.value)
        updateVolumeValue(viewModel.phoneVolume.value)
        updateVolumeValue(viewModel.systemVolume.value)
        observeSoundVolume()
        binding.closeDialog.setOnClickListener {
            dismiss()
        }
    }

    private fun observeSoundVolume() {
        viewModel.naviVolume.observe(this) {
            updateVolumeValue(it)
        }
        viewModel.mediaVolume.observe(this) {
            updateVolumeValue(it)
        }
        viewModel.phoneVolume.observe(this) {
            updateVolumeValue(it)
        }
        viewModel.voiceVolume.observe(this) {
            updateVolumeValue(it)
        }
        viewModel.systemVolume.observe(this) {
            updateVolumeValue(it)
        }
    }

}

