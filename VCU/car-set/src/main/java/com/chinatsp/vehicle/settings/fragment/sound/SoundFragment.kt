package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.chinatsp.vehicle.settings.widget.SoundPopup
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.picker.VerticalSeekBar
import com.king.base.util.DensityUtils.dip2px
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {

    var soundPopup: SoundPopup? = null

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
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
        }
    }

    private fun initSoundVolume() {
        soundPopup?.also {
            it.setSeekBarMaxValue(SoundPopup.Type.NAVI, viewModel.naviMaxVolume)
            it.setSeekBarMaxValue(SoundPopup.Type.VOICE, viewModel.voiceMaxVolume)
            it.setSeekBarMaxValue(SoundPopup.Type.MEDIA, viewModel.mediaMaxVolume)
            it.setSeekBarMaxValue(SoundPopup.Type.PHONE, viewModel.phoneMaxVolume)
            it.setSeekBarMaxValue(SoundPopup.Type.SYSTEM, viewModel.systemMaxVolume)

            it.updateSeekBarValue(SoundPopup.Type.NAVI, viewModel.naviSoundVolume.value!!)
            it.updateSeekBarValue(SoundPopup.Type.VOICE, viewModel.voiceSoundVolume.value!!)
            it.updateSeekBarValue(SoundPopup.Type.MEDIA, viewModel.mediaSoundVolume.value!!)
            it.updateSeekBarValue(SoundPopup.Type.PHONE, viewModel.phoneSoundVolume.value!!)
            it.updateSeekBarValue(SoundPopup.Type.SYSTEM, viewModel.systemSoundVolume.value!!)
        }
    }

    private fun popWindow() {
        soundPopup = SoundPopup(requireActivity())
    }

}