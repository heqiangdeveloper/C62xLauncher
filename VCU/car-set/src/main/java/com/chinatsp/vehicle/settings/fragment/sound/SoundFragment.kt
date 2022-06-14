package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.os.Handler
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {

    var soundDialog: SoundDialogFragment? = null

    private val voiceManager: VoiceManager by lazy { VoiceManager.instance }

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
//        observeSoundVolume()
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
            soundDialog?.updateVolumeValue(it)
        }
        viewModel.mediaVolume.observe(this) {
            soundDialog?.updateVolumeValue(it)
        }
        viewModel.phoneVolume.observe(this) {
            soundDialog?.updateVolumeValue(it)
        }
        viewModel.voiceVolume.observe(this) {
            soundDialog?.updateVolumeValue(it)
        }
        viewModel.systemVolume.observe(this) {
            soundDialog?.updateVolumeValue(it)
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundVolumeAdjustment.setOnClickListener {
            val volumeDialog = VolumeDialogFragment()
            activity?.supportFragmentManager?.let { it ->
                volumeDialog.show(it, volumeDialog.javaClass.simpleName)
            }

//            soundDialog = SoundDialogFragment(object : SoundDialogFragment.IViewListener {
//                override fun doViewCreated() {
//                    initSoundVolume()
//                    initSoundListener()
//                    observeSoundVolume()
//                }
//
//            })
//            activity?.let { it1 ->
//                soundDialog!!.show(
//                    it1.supportFragmentManager,
//                    "soundDialog"
//                )
//            }
        }
    }

    private fun initSoundListener() {
        soundDialog?.let {

        }
    }

    private fun initSoundVolume() {
        soundDialog?.also {
            it.updateVolumeValue(viewModel.naviVolume.value)
            it.updateVolumeValue(viewModel.voiceVolume.value)
            it.updateVolumeValue(viewModel.mediaVolume.value)
            it.updateVolumeValue(viewModel.phoneVolume.value)
            it.updateVolumeValue(viewModel.systemVolume.value)
        }
    }


}