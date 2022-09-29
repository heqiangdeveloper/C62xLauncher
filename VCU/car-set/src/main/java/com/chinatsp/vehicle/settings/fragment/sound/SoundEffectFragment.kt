package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.EqualizerDialogFragment
import com.chinatsp.vehicle.settings.fragment.doors.dialog.VolumeDialogFragment
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundEffectFragment : BaseFragment<SoundEffectViewModel, SoundEffectFragmentBinding>(),
    IOptionAction {

    override fun getLayoutId(): Int {
        return R.layout.sound_effect_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initViewsDisplay()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
        initDetailsClickListener()
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, Level.LEVEL5_2, expect = true)) {
            binding.soundLoudnessControlCompensation.visibility = View.VISIBLE
            binding.line3.visibility = View.VISIBLE
        }
    }

    private fun initDetailsClickListener() {
        binding.soundLoudnessDetails.setOnClickListener {
            showPopWindow(R.string.sound_loudness_control_content, it)
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.AUDIO_ENVI_AUDIO, viewModel.effectOption)
    }

    private fun addRadioLiveDataListener() {
        viewModel.effectOption.observe(this) {
            doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, it, false)
        }
        viewModel.currentEffect.observe(this) {
            val array = resources.getStringArray(R.array.sound_equalizer_option)
            binding.soundEffectHint.text = array[it]
        }
    }

    private fun setRadioListener() {
        binding.soundEnvironmentalTab.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, value, viewModel.effectOption, it)
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AUDIO_ENVI_AUDIO, viewModel.effectStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, viewModel.audioLoudness)
        binding.soundEnvironmentalSw.run {
            checkDisableOtherDiv(this, this.isChecked)
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.effectStatus.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_ENVI_AUDIO, it)
            binding.soundEnvironmentalSw.run {
                checkDisableOtherDiv(this, this.isChecked)
            }
        }
        viewModel.audioLoudness.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_SOUND_LOUDNESS, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalSw
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.audioEffectLoudnessSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return EffectManager.instance
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalTab
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return EffectManager.instance
    }

    private fun setSwitchListener() {
        binding.soundEnvironmentalSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_ENVI_AUDIO, buttonView, isChecked)
            checkDisableOtherDiv(binding.soundEnvironmentalSw, buttonView.isChecked)
        }
        binding.audioEffectLoudnessSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, buttonView, isChecked)
        }
    }

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.soundEnvironmentalSw) {
            val child = binding.soundEnvironmentalTab
            child.alpha = if (status) 1.0f else 0.6f
            binding.soundEnvironmentalTab.updateEnable(status)
            val childCount = binding.layoutContent.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.layoutContent.getChildAt(it)
                if (null != childAt && childAt != binding.soundEnvironmentalCompensation) {
                    childAt.alpha = if (status) 0.7f else 1.0f
                    updateViewEnable(childAt, status, filterView = swb)
                }
            }
        }
    }

    private fun updateViewEnable(view: View?, status: Boolean, filterView: View? = null) {
        if (null == view) {
            return
        }
        if (view is SwitchButton && view != filterView) {
            view.isEnabled = !status
            return
        }
        if (view is TabControlView) {
            view.updateEnable(status)
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status, filterView) }
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

    private fun showPopWindow(id: Int, view: View) {
        val popWindow = PopWindow(activity,
            R.layout.pop_window,
            activity?.let { AppCompatResources.getDrawable(it, R.drawable.popup_bg_qipao172_5) })
        var text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view, 30, -160)
    }
}