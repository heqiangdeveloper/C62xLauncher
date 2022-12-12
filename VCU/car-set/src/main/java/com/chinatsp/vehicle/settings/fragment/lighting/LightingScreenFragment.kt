package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
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

    private val AC_BRIGHTNESS = 0x33
    private val METER_BRIGHTNESS = 0x44

    private val manager: BrightnessManager
        get() = BrightnessManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_screen_fragment
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper()
        ) {
            when (it.what) {
                METER_BRIGHTNESS -> {
                    updateSeekBarValue( binding.lightScreenMeterSeekbar, viewModel.meterScreenVolume.value!!)
                }
                AC_BRIGHTNESS -> {
                    updateSeekBarValue( binding.lightScreenAcSeekbar, viewModel.acScreenVolume.value!!)
                }
                else -> {}
            }
            return@Handler true
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSeekBar()
        setSeekBarListener(this)
        initSeekLiveData()

        initSwitchStatus()
        initSwitchStatusListener()
    }

    private fun initSwitchStatus() {
        val status = viewModel.lightAutoMode.value?.get() ?: false
        updateBrightnessEnable(!status)
    }

    private fun initSwitchStatusListener() {
        viewModel.lightAutoMode.observe(this) {
            updateBrightnessEnable(!it.get())
        }
    }

    private fun updateBrightnessEnable(enable: Boolean) {
        val alpha = if (enable) 1.0f else 0.6f
        binding.lightScreenCarSeekbar.alpha = alpha
        binding.lightScreenCarSeekbar.isEnabled = enable
        binding.lightScreenMeterSeekbar.alpha = alpha
        binding.lightScreenMeterSeekbar.isEnabled = enable
        binding.lightScreenAcSeekbar.alpha = alpha
        binding.lightScreenAcSeekbar.isEnabled = enable
    }

    private fun initSeekLiveData() {
        viewModel.hostScreenVolume.observe(this) {
            updateSeekBarValue(binding.lightScreenCarSeekbar, it)
        }
        viewModel.meterScreenVolume.observe(this) {
//            updateSeekBarValue(binding.lightScreenMeterSeekbar, it)
            handler.removeMessages(METER_BRIGHTNESS)
            handler.sendEmptyMessageDelayed(METER_BRIGHTNESS, 200)
        }
        viewModel.acScreenVolume.observe(this) {
//            updateSeekBarValue(binding.lightScreenAcSeekbar, it)
            handler.removeMessages(AC_BRIGHTNESS)
            handler.sendEmptyMessageDelayed(AC_BRIGHTNESS, 200)
        }
    }

    private fun updateSeekBarValue(bar: VSeekBar, volume: Volume, init: Boolean = false) {
        if (init) {
            bar.min = volume.min
            bar.max = volume.max
        }
        bar.setValueNoEvent(volume.pos)
    }

    private fun initSeekBar() {
        binding.lightScreenCarSeekbar.apply {
            viewModel.hostScreenVolume.value?.let {
                updateSeekBarValue(this, it, init = true)
            }
        }
        binding.lightScreenMeterSeekbar.apply {
            viewModel.meterScreenVolume.value?.let {
                updateSeekBarValue(this, it, init = true)
            }
        }
        binding.lightScreenAcSeekbar.apply {
            viewModel.acScreenVolume.value?.let {
                updateSeekBarValue(this, it, init = true)
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
                    if (isTouching) updateDarkLightLevel(newValue) else ""
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

    private fun updateDarkLightLevel(level: Int) {
        var value = level
        val type = Progress.HOST_SCREEN_BRIGHTNESS
        if (value < type.min) value = type.min
        if (value > type.max) value = type.max
        val darkActive = manager.isDarkModeActive()
        if (darkActive) {
            VcuUtils.putInt(key = Constant.DARK_BRIGHTNESS_LEVEL, value = value)
        } else {
            VcuUtils.putInt(key = Constant.LIGHT_BRIGHTNESS_LEVEL, value = value)
        }
    }

    override fun onDestroyView() {
        handler.removeMessages(AC_BRIGHTNESS)
        handler.removeMessages(METER_BRIGHTNESS)
        super.onDestroyView()
    }

}