package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {

    var soundDialog: SoundDialogFragment? = null

    val voiceManager: VoiceManager by lazy { VoiceManager.instance }

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSoundVolume()
        initSoundListener()
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
            soundDialog?.updateVolumeValue(SoundDialogFragment.Type.NAVI, it)
        }
        viewModel.mediaVolume.observe(this) {
            soundDialog?.updateVolumeValue(SoundDialogFragment.Type.MEDIA, it)
        }
        viewModel.phoneVolume.observe(this) {
            soundDialog?.updateVolumeValue(SoundDialogFragment.Type.PHONE, it)
        }
        viewModel.voiceVolume.observe(this) {
            soundDialog?.updateVolumeValue(SoundDialogFragment.Type.VOICE, it)
        }
        viewModel.systemVolume.observe(this) {
            soundDialog?.updateVolumeValue(SoundDialogFragment.Type.SYSTEM, it)
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundVolumeAdjustment.setOnClickListener {
            soundDialog = SoundDialogFragment()
            activity?.let { it1 ->
                soundDialog!!.show(
                    it1.supportFragmentManager,
                    "soundDialog"
                )
            }

        }
    }

    private fun initSoundListener() {
        soundDialog?.let {

        }
    }

    private fun initSoundVolume() {
        soundDialog?.also {
            it.updateVolumeValue(SoundDialogFragment.Type.NAVI, viewModel.naviVolume.value)
            it.updateVolumeValue(SoundDialogFragment.Type.VOICE, viewModel.voiceVolume.value)
            it.updateVolumeValue(SoundDialogFragment.Type.MEDIA, viewModel.mediaVolume.value)
            it.updateVolumeValue(SoundDialogFragment.Type.PHONE, viewModel.phoneVolume.value)
            it.updateVolumeValue(SoundDialogFragment.Type.SYSTEM, viewModel.systemVolume.value)
        }
    }

}