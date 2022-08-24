package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingScreenFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.BrightnessViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.picker.VSeekBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingScreenFragment : BaseFragment<BrightnessViewModel, LightingScreenFragmentBinding>(),
    VSeekBar.OnSeekBarListener {

    private val manager: IProgressManager
        get() = BrightnessManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_screen_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSeekBar()
        setSeekBarListener(this)
        initSeekLiveData()
    }

    private fun initSeekLiveData() {
        viewModel.hostScreenVolume.observe(this) {
            updateSeekBarValue(binding.lightScreenCarSeekbar, it)
        }
        viewModel.meterScreenVolume.observe(this) {
            updateSeekBarValue(binding.lightScreenMeterSeekbar, it)
        }
        viewModel.acScreenVolume.observe(this) {
            updateSeekBarValue(binding.lightScreenAcSeekbar, it)
        }
    }

    private fun updateSeekBarValue(bar: VSeekBar, volume: Volume) {
        bar.min = volume.min
        bar.max = volume.max
        bar.setValueNoEvent(volume.pos)
    }

    private fun initSeekBar() {
        binding.lightScreenCarSeekbar.apply {
            viewModel.hostScreenVolume.value?.let {
                updateSeekBarValue(this, it)
            }
        }
        binding.lightScreenMeterSeekbar.apply {
            viewModel.meterScreenVolume.value?.let {
                updateSeekBarValue(this, it)
            }
        }
        binding.lightScreenAcSeekbar.apply {
            viewModel.acScreenVolume.value?.let {
                updateSeekBarValue(this, it)
            }
        }
    }

    private fun setSeekBarListener(listener: VSeekBar.OnSeekBarListener) {
        binding.lightScreenCarSeekbar.setOnSeekBarListener(listener)
        binding.lightScreenAcSeekbar.setOnSeekBarListener(listener)
        binding.lightScreenMeterSeekbar.setOnSeekBarListener(listener)
    }

    override fun onValueChanged(seekBar: VSeekBar?, newValue: Int) {
        seekBar?.run {
            when (this) {
                binding.lightScreenCarSeekbar -> {
                    manager.doSetVolume(Progress.HOST_SCREEN_BRIGHTNESS, newValue)
                }
                binding.lightScreenAcSeekbar -> {
                    manager.doSetVolume(Progress.CONDITIONER_SCREEN_BRIGHTNESS, newValue)
                }
                binding.lightScreenMeterSeekbar -> {
                    manager.doSetVolume(Progress.METER_SCREEN_BRIGHTNESS, newValue)
                }
                else -> {}
            }
        }
    }
}