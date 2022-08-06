package com.chinatsp.vehicle.settings.fragment.sound

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.EqualizerDialogFragment
import com.chinatsp.vehicle.settings.fragment.doors.dialog.VolumeDialogFragment
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundEffectFragment : BaseFragment<SoundEffectViewModel, SoundEffectFragmentBinding>() {
    private val xValueTop: List<String>
        get() = listOf("4dB", "-2dB", "4dB", "2dB", "4dB")

    override fun getLayoutId(): Int {
        return R.layout.sound_effect_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()

        initViewsDisplay()
    }

    private fun initViewsDisplay() {
        if (!VcuUtils.isCareLevel(Level.LEVEL5)) {
            binding.soundLoudnessControlCompensation.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundEqualizerCompensation.setOnClickListener {
            val fragment = EqualizerDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
        binding.soundVolumeBalanceCompensation.setOnClickListener {
            val fragment = VolumeDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

}