package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundViewModel
import com.common.library.frame.base.BaseLazyFragment
import com.common.xui.utils.ViewUtils
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SoundFragment : BaseLazyFragment<SoundViewModel, SoundFragmentBinding>(), IOptionAction {

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
        initDetailsClickListener()
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            binding.soundLoudnessControl.visibility = View.GONE
            binding.line7.visibility = View.GONE
        }
    }

    private fun initDetailsClickListener() {
        binding.soundLoudnessDetails.setOnClickListener {
            showPopWindow(R.string.sound_loudness_control_content,it)
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

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AUDIO_SOUND_TONE, viewModel.toneStatus)
        initSwitchOption(SwitchNode.TOUCH_PROMPT_TONE, viewModel.touchToneStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, viewModel.loudnessStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_HUAWEI, viewModel.huaweiStatus)
        initSwitchOption(manager.volumeSpeedSwitch, viewModel.speedVolumeOffset)
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
        viewModel.touchToneStatus.observe(this) {
            Timber.d("addSwitchLiveDataListener node:${SwitchNode.TOUCH_PROMPT_TONE}, status:$it")
            doUpdateSwitch(SwitchNode.TOUCH_PROMPT_TONE, it)
        }
        viewModel.speedVolumeOffset.observe(this) {
            doUpdateSwitch(manager.volumeSpeedSwitch, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> binding.soundWarnToneSwitch
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.soundLoudnessSwitch
            SwitchNode.AUDIO_SOUND_HUAWEI -> binding.soundHuaweiSwitch
            SwitchNode.TOUCH_PROMPT_TONE -> binding.soundTouchPromptSwitch
            manager.volumeSpeedSwitch -> binding.soundSpeedOffsetSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> binding.soundMeterAlarmRadio
            RadioNode.NAVI_AUDIO_MIXING -> binding.soundNaviMixingRadio
            RadioNode.SPEED_VOLUME_OFFSET -> binding.soundSpeedOffsetRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
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
        binding.soundTouchPromptSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.TOUCH_PROMPT_TONE, buttonView, isChecked)
        }
        binding.soundSpeedOffsetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(manager.volumeSpeedSwitch, buttonView, isChecked)
        }
    }

    private fun setCheckedChangeListener() {
        binding.soundVolumeViews.apply {
            ViewUtils.expendTouchArea(this, 40)
            setOnClickListener {
                showVolumeFragment()
            }
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

    override fun onLazyLoad() {

    }

    private fun showPopWindow(id:Int, view:View){
        val popWindow = PopWindow(activity,R.layout.pop_window,activity?.let { AppCompatResources.getDrawable(it,R.drawable.popup_bg_qipao172_5) })
        var text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view,30,-160)
    }
}