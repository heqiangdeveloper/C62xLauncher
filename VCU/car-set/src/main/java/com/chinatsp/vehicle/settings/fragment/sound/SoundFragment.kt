package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {

    private val manager: VoiceManager by lazy { VoiceManager.instance }

    private val volumeControl: String
        get() = "volumeControl"

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initRouteListener()

        initViewsDisplay()
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5)) {
            binding.soundLoudnessControl.visibility = View.GONE
            binding.line7.visibility = View.GONE
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ICM_VOLUME_LEVEL, viewModel.volumeLevel)
        initRadioOption(RadioNode.NAVI_AUDIO_MIXING, viewModel.audioMixing)
        initRadioOption(RadioNode.SPEED_VOLUME_OFFSET, viewModel.volumeOffset)
    }

    private fun addRadioLiveDataListener() {
        viewModel.volumeLevel.observe(this) {
            doUpdateRadio(RadioNode.ICM_VOLUME_LEVEL, it, false)
        }
        viewModel.audioMixing.observe(this) {
            doUpdateRadio(RadioNode.NAVI_AUDIO_MIXING, it, false)
        }
        viewModel.volumeOffset.observe(this) {
            doUpdateRadio(RadioNode.SPEED_VOLUME_OFFSET, it, false)
        }
    }

    private fun setRadioListener() {
        binding.soundMeterAlarmRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ICM_VOLUME_LEVEL, value, viewModel.volumeLevel, it)
            }
        }
        binding.soundNaviMixingRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.NAVI_AUDIO_MIXING, value, viewModel.audioMixing, it)
            }
        }
        binding.soundSpeedOffsetRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.SPEED_VOLUME_OFFSET, value, viewModel.volumeOffset, it)
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
        val result = isCanToInt(value) && manager.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.setSelection(liveData.value.toString(), true)
    }

    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false, isInit: Boolean = false) {
        val tabView = when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> binding.soundMeterAlarmRadio
            RadioNode.NAVI_AUDIO_MIXING -> binding.soundNaviMixingRadio
            RadioNode.SPEED_VOLUME_OFFSET -> binding.soundSpeedOffsetRadio
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
        initSwitchOption(SwitchNode.AUDIO_SOUND_TONE, viewModel.toneStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, viewModel.loudnessStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_HUAWEI, viewModel.huaweiStatus)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.toneStatus.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_SOUND_TONE, it)
        }
        viewModel.loudnessStatus.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_SOUND_LOUDNESS, it)
        }
        viewModel.huaweiStatus.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_SOUND_HUAWEI, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> binding.soundWarnToneSwitch
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.soundLoudnessSwitch
            SwitchNode.AUDIO_SOUND_HUAWEI -> binding.soundHuaweiSwitch
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
        binding.soundWarnToneSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_SOUND_TONE, buttonView, isChecked)
        }
        binding.soundLoudnessSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, buttonView, isChecked)
        }
        binding.soundHuaweiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_SOUND_HUAWEI, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }


    private fun setCheckedChangeListener() {
        binding.soundVolumeAdjustment.setOnClickListener {
            showVolumeFragment()
        }
    }

    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            iroute.cleanPopupLiveDate(serial)
        }
    }

    private fun initRouteListener() {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            val liveData = iroute.obtainPopupLiveData()
            liveData.observe(this) {
                if (it.equals(volumeControl)) {
                    showVolumeFragment()
                }
            }
        }
    }

    private fun showVolumeFragment() {
        val fragment = VolumeDialogFragment()
        activity?.supportFragmentManager?.let { it ->
            fragment.show(it, fragment.javaClass.simpleName)
        }
        cleanPopupSerial(volumeControl)
    }

}