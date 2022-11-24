package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatTextView
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AudioSoundVolumeDialogBinding
import com.chinatsp.vehicle.settings.vm.sound.VolumeViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VolumeDialogFragment : BaseDialogFragment<VolumeViewModel, AudioSoundVolumeDialogBinding>(),
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private var index = 0
    val manager: VoiceManager by lazy {
        VoiceManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.audio_sound_volume_dialog
    }

    private fun setSeekBarListener(listener: SeekBar.OnSeekBarChangeListener) {
        binding.soundAudioNaviVoVolume.setOnSeekBarChangeListener(listener)
        binding.soundAudioVoiceVolume.setOnSeekBarChangeListener(listener)
        binding.soundAudioMediaVolume.setOnSeekBarChangeListener(listener)
        binding.soundAudioPhoneVolume.setOnSeekBarChangeListener(listener)
        binding.soundAudioSystemVolume.setOnSeekBarChangeListener(listener)
        binding.reset.setOnClickListener(this)
    }

    private fun updateVolumeValue(volume: Volume?) {
        volume?.let {
            val seekBar: SeekBar?
            val textView: AppCompatTextView?
            when (it.type) {
                Progress.NAVI -> {
                    seekBar = binding.soundAudioNaviVoVolume
                    textView = binding.naviVolumeTxt
                    seekBar.max = it.max
                    seekBar.min = it.min
                    textView.text = it.pos.toString()
                    seekBar.progress = it.pos
                }
                Progress.VOICE -> {
                    seekBar = binding.soundAudioVoiceVolume
                    textView = binding.voiceVolumeTxt
                    seekBar.max = it.max
                    seekBar.min = it.min
                    textView.text = it.pos.toString()
                    seekBar.progress = it.pos
                }
                Progress.MEDIA -> {
                    seekBar = binding.soundAudioMediaVolume
                    textView = binding.mediaVolumeTxt
                    seekBar.max = it.max
                    seekBar.min = it.min
                    textView.text = it.pos.toString()
                    seekBar.progress = it.pos
                }
                Progress.PHONE -> {
                    seekBar = binding.soundAudioPhoneVolume
                    textView = binding.phoneVolumeTxt
                    if(index == 0){
                        index++
                        seekBar.max = 25
                        seekBar.min = 0
                        textView.text = it.pos.toString()
                        seekBar.progress = it.pos
                    }
                }
                Progress.SYSTEM -> {
                    seekBar = binding.soundAudioSystemVolume
                    textView = binding.systemVolumeTxt
                    seekBar.max = it.max
                    seekBar.min = it.min
                    textView.text = it.pos.toString()
                    seekBar.progress = it.pos
                }
                else -> {
                }
            }

        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        updateVolumeValue(viewModel.naviVolume.value)
        updateVolumeValue(viewModel.voiceVolume.value)
        updateVolumeValue(viewModel.mediaVolume.value)
        updateVolumeValue(viewModel.phoneVolume.value)
        updateVolumeValue(viewModel.systemVolume.value)
        setSeekBarListener(this)
        observeSoundVolume()
        binding.closeDialog.setOnClickListener {
            dismiss()
        }
        binding.reset.setOnClickListener { onClick(binding.reset) }
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

    override fun onClick(v: View?) {
        if (v == binding.reset) {
            viewModel.resetDeviceVolume()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
        when (seekBar?.id) {
            binding.soundAudioNaviVoVolume.id -> {
                binding.naviVolumeTxt.text = progress.toString()
                manager.doSetVolume(Progress.NAVI, progress)
            }
            binding.soundAudioVoiceVolume.id -> {
                binding.voiceVolumeTxt.text = progress.toString()
                manager.doSetVolume(Progress.VOICE, progress)
            }
            binding.soundAudioMediaVolume.id -> {
                binding.mediaVolumeTxt.text = progress.toString()
                manager.doSetVolume(Progress.MEDIA, progress)
            }
            binding.soundAudioPhoneVolume.id -> {
                val value = progress + 5
                binding.phoneVolumeTxt.text = value.toString()
                manager.doSetVolume(Progress.PHONE, value)
            }
            binding.soundAudioSystemVolume.id -> {
                binding.systemVolumeTxt.text = progress.toString()
                manager.doSetVolume(Progress.SYSTEM, progress)
            }
            else -> {
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        when (seekBar?.id) {
            binding.soundAudioNaviVoVolume.id -> {
                binding.ivPointVolume.startAnimation(
                    R.drawable.animation_volume_point,
                    2000,
                    R.drawable.animation_volume_point39
                )
                binding.ivPoint1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom,
                    1250,
                    R.drawable.animation_volume_point_bottom24
                )
            }
            binding.soundAudioVoiceVolume.id -> {
                binding.ivVoiceVolume.startAnimation(
                    R.drawable.animation_volume_point,
                    2000,
                    R.drawable.animation_volume_point39
                )
                binding.ivVoice1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom,
                    1250,
                    R.drawable.animation_volume_point_bottom24
                )
            }
            binding.soundAudioMediaVolume.id -> {
                binding.ivMediaVolume.startAnimation(
                    R.drawable.animation_volume_point,
                    2000,
                    R.drawable.animation_volume_point39
                )
                binding.ivMedia1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom,
                    1250,
                    R.drawable.animation_volume_point_bottom24
                )
            }
            binding.soundAudioPhoneVolume.id -> {
                binding.ivPhoneVolume.startAnimation(
                    R.drawable.animation_volume_point,
                    2000,
                    R.drawable.animation_volume_point39
                )
                binding.ivPhone1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom,
                    1250,
                    R.drawable.animation_volume_point_bottom24
                )
            }
            binding.soundAudioSystemVolume.id -> {
                binding.ivSystemVolume.startAnimation(
                    R.drawable.animation_volume_point,
                    2000,
                    R.drawable.animation_volume_point39
                )
                binding.ivSystem1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom,
                    1250,
                    R.drawable.animation_volume_point_bottom24
                )
            }
            else -> {
            }
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        when (seekBar?.id) {
            binding.soundAudioNaviVoVolume.id -> {
                binding.ivPointVolume.startAnimation(
                    R.drawable.animation_volume_point_hide,
                    300,
                    R.drawable.animation_volume_point_hide5
                )
                binding.ivPoint1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom_hide,
                    1500,
                    R.drawable.animation_volume_point_bottom_hide29
                )
            }
            binding.soundAudioVoiceVolume.id -> {
                binding.ivVoiceVolume.startAnimation(
                    R.drawable.animation_volume_point_hide,
                    300,
                    R.drawable.animation_volume_point_hide5
                )
                binding.ivVoice1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom_hide,
                    1500,
                    R.drawable.animation_volume_point_bottom_hide29
                )
            }
            binding.soundAudioMediaVolume.id -> {
                binding.ivMediaVolume.startAnimation(
                    R.drawable.animation_volume_point_hide,
                    300,
                    R.drawable.animation_volume_point_hide5
                )
                binding.ivMedia1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom_hide,
                    1500,
                    R.drawable.animation_volume_point_bottom_hide29
                )
            }
            binding.soundAudioPhoneVolume.id -> {
                binding.ivPhoneVolume.startAnimation(
                    R.drawable.animation_volume_point_hide,
                    300,
                    R.drawable.animation_volume_point_hide5
                )
                binding.ivPhone1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom_hide,
                    1500,
                    R.drawable.animation_volume_point_bottom_hide29
                )
            }
            binding.soundAudioSystemVolume.id -> {
                binding.ivSystemVolume.startAnimation(
                    R.drawable.animation_volume_point_hide,
                    300,
                    R.drawable.animation_volume_point_hide5
                )
                binding.ivSystem1Volume.startAnimation(
                    R.drawable.animation_volume_point_bottom_hide,
                    1500,
                    R.drawable.animation_volume_point_bottom_hide29
                )
            }
            else -> {
            }
        }
    }

}

