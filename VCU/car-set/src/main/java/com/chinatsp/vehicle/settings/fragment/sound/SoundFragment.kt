package com.chinatsp.vehicle.settings.fragment.sound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.sound.EffectManager
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
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SoundFragment : BaseLazyFragment<SoundViewModel, SoundFragmentBinding>(), IOptionAction {

    private val manager: VoiceManager by lazy { VoiceManager.instance }

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initClickView()

        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initViewsDisplay()
        initDetailsClickListener()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.soundVolumeAdjustment
        map[2] = binding.soundLoudnessDetails
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                it.takeIf { it.valid && it.uid == pid }?.let { level1 ->
                    level1.cnode?.takeIf { child -> child.valid && child.uid == uid }
                        .let { level2 ->
                            level2?.cnode?.let { lv3Node ->
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.soundVolumeAdjustment -> showVolumeFragment()
            binding.soundLoudnessDetails -> showPopWindow(R.string.sound_loudness_control_content,
                it)
        }
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            binding.soundLoudnessControl.visibility = View.GONE
            binding.line7.visibility = View.GONE
        }
    }

    private fun initDetailsClickListener() {
        binding.soundLoudnessDetails.setOnClickListener(this::onViewClick)
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
            doUpdateSwitch(SwitchNode.TOUCH_PROMPT_TONE, it)
        }
        viewModel.speedVolumeOffset.observe(this) {
            doUpdateSwitch(manager.volumeSpeedSwitch, it)
        }
        viewModel.node645.observe(this) {
            updateSwitchEnable(SwitchNode.AUDIO_SOUND_LOUDNESS)
            updateSwitchEnable(SwitchNode.SPEED_VOLUME_OFFSET)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> binding.soundWarnToneSwitch
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.soundLoudnessSwitch
            SwitchNode.AUDIO_SOUND_HUAWEI -> binding.soundHuaweiSwitch
            SwitchNode.TOUCH_PROMPT_TONE -> binding.soundTouchPromptSwitch
            SwitchNode.SPEED_VOLUME_OFFSET_INSERT, SwitchNode.SPEED_VOLUME_OFFSET -> binding.soundSpeedOffsetSwitch
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
    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_LOUDNESS -> viewModel.node645.value?.get() ?: true
            SwitchNode.SPEED_VOLUME_OFFSET -> viewModel.node645.value?.get() ?: true
            else -> super.obtainActiveByNode(node)
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
        binding.soundVolumeAdjustment.setOnClickListener(this::onViewClick)
    }

    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val route = activity as IRoute
            route.cleanPopupLiveDate(serial)
        }
    }

//    private fun initRouteListener() {
//        if (activity is IRoute) {
//            val route = activity as IRoute
//            val liveData = route.obtainPopupLiveData()
//            liveData.observe(this) {
//                if (it.equals(Constant.DEVICE_AUDIO_VOLUME)) {
//                    showVolumeFragment()
//                }
//            }
//        }
//    }

    private fun showVolumeFragment() {
        val fragment = VolumeDialogFragment()
        activity?.supportFragmentManager?.let { it ->
            fragment.show(it, fragment.javaClass.simpleName)
        }
        cleanPopupSerial(Constant.DEVICE_AUDIO_VOLUME)
    }

    override fun onLazyLoad() {

    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow = PopWindow(activity,
            R.layout.pop_window,
            activity?.let { AppCompatResources.getDrawable(it, R.drawable.popup_bg_qipao172_5) })
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view, 30, -160)
    }

    override fun onPause() {
        super.onPause()
        val toList = convertEqValues(6, "onPause")
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val systemHint =
            getSwitchManager().doGetSwitchOption(SwitchNode.TOUCH_PROMPT_TONE)?.data//???????????????
        val speedVolumeCompensation =
            getSwitchManager().doGetSwitchOption(VoiceManager.instance.volumeSpeedSwitch)?.data//??????????????????
        val loudnessControl =
            getSwitchManager().doGetSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS)?.data//????????????
        val navigationMixing =
            getRadioManager().doGetRadioOption(RadioNode.NAVI_AUDIO_MIXING)?.data//????????????
        val fadeValue = EffectManager.instance.audioFade()//????????????-???????????????
        val balanceValue = EffectManager.instance.getAudioBalance()//????????????-????????????
        val json = "{\"systemHint\":\"" + systemHint + "\",\"speedVolumeCompensation\":\"" +
                speedVolumeCompensation + "\",\"loudnessControl\":\"" +
                loudnessControl + "\",\"navigationMixing\":\"" +
                navigationMixing + "\",\"fadeValue\":\"" +
                fadeValue + "\",\"balanceValue\":\"" +
                balanceValue + "\",\"equalizerValue\":\"" +
                toList + "\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("soundEffects", json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
        Timber.d("soundEffects intent json:$json")
    }

    private fun convertEqValues(eqId: Int, serial: String, reverse: Boolean = false): List<Float> {
        val values = viewModel.getEffectValues(eqId).toList()
        Timber.d("convert b serial:$serial, serialId:$eqId, values:%s", values)
        val list = values.map {
            val value = it.toFloat() - 1
            if (value < 0f) {
                0f
            } else if (value > 2 * offset) {
                2 * offset
            } else {
                value
            }
        }.toList()
        val result = if (reverse) list.reversed() else list
        Timber.d("convert e serial:$serial, serialId:$eqId, values:%s", result)
        return result
    }

    private val offset: Float by lazy {
        if (VcuUtils.isAmplifier) 9f else 5f
    }
}