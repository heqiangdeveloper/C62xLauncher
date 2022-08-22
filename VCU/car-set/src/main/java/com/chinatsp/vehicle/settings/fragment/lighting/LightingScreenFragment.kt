package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.settinglib.IProgressManager
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
    VSeekBar.OnSeekBarListener{

    private val manager: IProgressManager
        get() = BrightnessManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_screen_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSeekBar()
        setSeekBarListener(this)
    }

    private fun initSeekBar() {
        binding.lightScreenCarSeekbar.let { seekBar ->
            viewModel.carScreenVolume.value?.let {
                seekBar.min = it.min
                seekBar.max = it.max
                seekBar.setValueNoEvent(it.pos)
            }
        }
    }

    private fun setSeekBarListener(listener: VSeekBar.OnSeekBarListener) {
        binding.lightScreenCarSeekbar.setOnSeekBarListener(listener)
    }

    override fun onValueChanged(seekBar: VSeekBar?, newValue: Int) {
        manager.doSetVolume(Progress.HOST_SCREEN_BRIGHTNESS, newValue)
    }
}