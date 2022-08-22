package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
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
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = false)) {
            binding.soundLoudnessControlCompensation.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.AUDIO_ENVI_AUDIO, viewModel.effectOption)
    }

    private fun addRadioLiveDataListener() {
        viewModel.effectOption.observe(this) {
            doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, it, false)
        }
    }

    private fun setRadioListener() {
        binding.soundEnvironmentalTab.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, value, viewModel.effectOption, it)
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: String,
        liveData: LiveData<Int>,
        tabView: TabControlView
    ) {
        val result =
            isCanToInt(value) && EffectManager.instance.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.setSelection(liveData.value.toString(), true)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalTab
            else -> null
        }
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            doUpdateRadio(it, value, immediately)
        }
    }


    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.get.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
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

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalSw
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.audioEffectLoudnessSwitch
            else -> null
        }
        takeIf { null != swb }?.doUpdateSwitch(swb!!, status, immediately)
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
    }

    private fun setSwitchListener() {
        binding.soundEnvironmentalSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_ENVI_AUDIO, buttonView, isChecked)
            checkDisableOtherDiv(binding.soundEnvironmentalSw, buttonView.isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = EffectManager.instance.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
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