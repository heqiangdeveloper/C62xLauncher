package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.EqualizerDialogFragment
import com.chinatsp.vehicle.settings.fragment.doors.dialog.VolumeDialogFragment
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
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
        setSwitchListener()
        initViewsDisplay()
    }

    private fun initViewsDisplay() {
        if (!VcuUtils.isCareLevel(Level.LEVEL5)) {
            binding.soundLoudnessControlCompensation.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
    }

    private fun setSwitchListener() {
        binding.soundEnvironmentalSw.setOnCheckedChangeListener { _, isChecked ->
            checkDisableOtherDiv(binding.soundEnvironmentalSw, isChecked)
        }
    }

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.soundEnvironmentalSw) {
            val child = binding.soundEnvironmentalTab
            child.alpha = if (status) 1.0f else 0.7f
            binding.soundEnvironmentalTab.updateEnable(status)
            val childCount = binding.layoutContent.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.layoutContent.getChildAt(it)
                if (null != childAt && childAt != binding.soundEnvironmentalCompensation) {
                    childAt.alpha = if (status) 0.7f else 1.0f
                    updateViewEnable(childAt, status)
                }

            }
        }
    }

    private fun updateViewEnable(view: View?, status: Boolean) {
        if (null == view) {
            return
        }
        if (view is SwitchButton) {
            view.isEnabled = status
            return
        }
        if (view is TabControlView) {
            view.updateEnable(status)
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundEqualizerCompensation.setOnClickListener {
            if (!binding.soundEnvironmentalSw.isChecked) {
                val fragment = EqualizerDialogFragment()
                activity?.supportFragmentManager?.let {
                    fragment.show(it, fragment.javaClass.simpleName)
                }
            }
        }
        binding.soundVolumeBalanceCompensation.setOnClickListener {
            if (!binding.soundEnvironmentalSw.isChecked) {
                val fragment = VolumeDialogFragment()
                activity?.supportFragmentManager?.let {
                    fragment.show(it, fragment.javaClass.simpleName)
                }
            }
        }
    }

}