package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.chinatsp.vehicle.settings.widget.SoundPopup
import com.common.library.frame.base.BaseFragment
import com.king.base.util.DensityUtils.dip2px
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {

    var soundPopup: SoundPopup? = null

    val voiceManager: VoiceManager by lazy { VoiceManager.instance }

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        observeSoundVolume()
        binding.soundMeterAlarmOption.setOnTabSelectionChangedListener { title, value ->
            LogManager.d("setOnTabSelectionChangedListener title:$title, value:$value")
            voiceManager.doUpdateAlarmOption(value.toInt())
        }

        binding.soundRemixOption.setOnTabSelectionChangedListener { title, value ->
            voiceManager.doUpdateRemixOption(value.toInt())
        }

    }

    private fun observeSoundVolume() {
        viewModel.naviVolume.observe(this) {
            soundPopup?.updateVolumeValue(SoundPopup.Type.NAVI, it)
        }
        viewModel.mediaVolume.observe(this) {
            soundPopup?.updateVolumeValue(SoundPopup.Type.MEDIA, it)
        }
        viewModel.phoneVolume.observe(this) {
            soundPopup?.updateVolumeValue(SoundPopup.Type.PHONE, it)
        }
        viewModel.voiceVolume.observe(this) {
            soundPopup?.updateVolumeValue(SoundPopup.Type.VOICE, it)
        }
        viewModel.systemVolume.observe(this) {
            soundPopup?.updateVolumeValue(SoundPopup.Type.SYSTEM, it)
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundVolumeAdjustment.setOnClickListener {
            if (soundPopup == null) {
                popWindow()
            }
            soundPopup?.apply {
                width = dip2px(context, 960f)
                showPopupWindow(it)
            }
            initSoundVolume()
            initSoundListener()
        }
    }

    private fun initSoundListener() {
        soundPopup?.let {

        }
    }

    private fun initSoundVolume() {
        soundPopup?.also {
            it.updateVolumeValue(SoundPopup.Type.NAVI, viewModel.naviVolume.value)
            it.updateVolumeValue(SoundPopup.Type.VOICE, viewModel.voiceVolume.value)
            it.updateVolumeValue(SoundPopup.Type.MEDIA, viewModel.mediaVolume.value)
            it.updateVolumeValue(SoundPopup.Type.PHONE, viewModel.phoneVolume.value)
            it.updateVolumeValue(SoundPopup.Type.SYSTEM, viewModel.systemVolume.value)
        }
    }

    private fun popWindow() {
        soundPopup = SoundPopup(requireActivity())
    }

}