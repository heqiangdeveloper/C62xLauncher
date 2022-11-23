package com.chinatsp.vehicle.settings.fragment.sound

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.SettingManager
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
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.EqualizerDialogFragment
import com.chinatsp.vehicle.settings.fragment.doors.dialog.VolumeDialogFragment
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.chinatsp.vehicle.settings.widget.SoundFieldView
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.smooth.SmoothLineChartView
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class SoundEffectFragment : BaseFragment<SoundEffectViewModel, SoundEffectFragmentBinding>(),
    IOptionAction {

    private val map: HashMap<Int, View> = HashMap()
    private lateinit var xValue: List<String>
    private lateinit var vList: List<Float>
    private var value by Delegates.notNull<Int>()
    private val offset: Float by lazy {
        if (VcuUtils.isAmplifier) 9f else 5f
    }

    override fun getLayoutId(): Int {
        return R.layout.sound_effect_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initClickView()
        initSmoothLineData()
        setCheckedChangeListener()
        initViewsDisplay()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()


        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
        initDetailsClickListener()
        updateOptionActive()

        initView()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.soundEqualizerCompensation
        map[2] = binding.soundVolumeBalanceCompensation
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
            binding.soundEqualizerCompensation -> showEqualizerFragment()
            binding.soundVolumeBalanceCompensation -> showVolumeFragment()
        }
    }

    private fun updateOptionActive() {
        updateRadioEnable(RadioNode.AUDIO_ENVI_AUDIO)
        val depend = !binding.soundEnvironmentalSw.isChecked
        updateEnable(binding.equalizer, true, depend)
        updateEnable(binding.volumeBalance, true, depend)
        updateEnable(binding.audioEffectLoudnessSwitch, true, depend)
    }

    private fun initViewsDisplay() {
        //LV3-LV4无环境音效功能
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, expect = true)) {
            binding.soundEnvironmentalCompensation.visibility = View.GONE
            binding.line1.visibility = View.GONE
            binding.LV3Layout.visibility = View.VISIBLE
            binding.lv5Layout.visibility = View.GONE
            initDataBalance()
        } else {
            binding.soundLoudnessControlCompensation.visibility = View.VISIBLE
            binding.line3.visibility = View.VISIBLE
            binding.LV3Layout.visibility = View.GONE
            binding.lv5Layout.visibility = View.VISIBLE
        }
    }

    private fun initDetailsClickListener() {
        binding.soundLoudnessDetails.setOnClickListener {
            showPopWindow(R.string.sound_loudness_control_content, it)
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.AUDIO_ENVI_AUDIO, viewModel.effectOption)
        initRadioOption(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect)
    }

    private fun addRadioLiveDataListener() {
        viewModel.effectOption.observe(this) {
            doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, it, false)
        }
        viewModel.currentEffect.observe(this) {
            val array = resources.getStringArray(R.array.sound_equalizer_option)
            binding.soundEffectHint.text = array[it.data]
        }
        viewModel.currentEffect.observe(this) {
            doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, it, false)
            binding.smoothChartView.setEnableView(it.data == 6)
        }
    }

    private fun setRadioListener() {
        binding.soundEnvironmentalTab.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.AUDIO_ENVI_AUDIO, value, viewModel.effectOption, it)
            }
        }
        binding.soundEffectRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, value, viewModel.currentEffect, it)
//                onPostSelected(it, value.toInt())
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AUDIO_ENVI_AUDIO, viewModel.effectStatus)
        initSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, viewModel.audioLoudness)
//        binding.soundEnvironmentalSw.run {
//            checkDisableOtherDiv(this, this.isChecked)
//        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.effectStatus.observe(this) {
            doUpdateSwitch(SwitchNode.AUDIO_ENVI_AUDIO, it)
//            binding.soundEnvironmentalSw.run {
//                checkDisableOtherDiv(this, this.isChecked)
//            }
            updateOptionActive()
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

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> viewModel.effectOption.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalSw.isChecked
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_LOUDNESS -> viewModel.audioLoudness.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AUDIO_SOUND_LOUDNESS -> binding.soundEnvironmentalSw.isChecked
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return EffectManager.instance
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> binding.soundEnvironmentalTab
            RadioNode.SYSTEM_SOUND_EFFECT -> binding.soundEffectRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return EffectManager.instance
    }

    private fun setSwitchListener() {
        binding.soundEnvironmentalSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_ENVI_AUDIO, buttonView, isChecked)
//            checkDisableOtherDiv(binding.soundEnvironmentalSw, buttonView.isChecked)
            updateOptionActive()
        }
        binding.audioEffectLoudnessSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS, buttonView, isChecked)
        }
    }

//    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
//        if (swb == binding.soundEnvironmentalSw) {
//            val child = binding.soundEnvironmentalTab
//            child.alpha = if (status) 1.0f else 0.6f
//            binding.soundEnvironmentalTab.updateEnable(status)
//            val childCount = binding.layoutContent.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.layoutContent.getChildAt(it)
//                if (null != childAt && childAt != binding.soundEnvironmentalCompensation) {
//                    childAt.alpha = if (status) 0.7f else 1.0f
//                    updateViewEnable(childAt, status, filterView = swb)
//                }
//            }
//        }
//    }

//    private fun updateViewEnable(view: View?, status: Boolean, filterView: View? = null) {
//        if (null == view) {
//            return
//        }
//        if (view is SwitchButton && view != filterView) {
//            view.isEnabled = !status
//            return
//        }
//        if (view is TabControlView) {
//            view.updateEnable(status)
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status, filterView) }
//        }
//    }

    private fun setCheckedChangeListener() {
        binding.soundEqualizerCompensation.setOnClickListener(this::onViewClick)
        binding.soundVolumeBalanceCompensation.setOnClickListener(this::onViewClick)
    }

    private fun showVolumeFragment() {
        if (!binding.soundEnvironmentalSw.isChecked) {
            val fragment = VolumeDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

    private fun showEqualizerFragment() {
        if (!binding.soundEnvironmentalSw.isChecked) {
            val fragment = EqualizerDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
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
        val values = viewModel.getEffectValues(6).toList()
        val toList = values.map {//均衡器
            var value = it.toFloat() - 1
            if (value < 0f) {
                value = 0f
            } else if (value > 2 * offset) {
                value = 2 * offset
            }
            value
        }.toList()
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val systemHint =
            VoiceManager.instance.doGetSwitchOption(SwitchNode.TOUCH_PROMPT_TONE)?.data//系统提示音
        val speedVolumeCompensation =
            getSwitchManager().doGetSwitchOption(VoiceManager.instance.volumeSpeedSwitch)?.data//速度音量补偿
        val loudnessControl =
            getSwitchManager().doGetSwitchOption(SwitchNode.AUDIO_SOUND_LOUDNESS)?.data//响度控制
        val navigationMixing =
            VoiceManager.instance.doGetRadioOption(RadioNode.NAVI_AUDIO_MIXING)?.data//导航混音
        val fadeValue = EffectManager.instance.audioFade()//音量补偿-逐渐消失值
        val balanceValue = EffectManager.instance.getAudioBalance()//音量补偿-平衡音量
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

    private fun initSmoothLineData() {
        if (VcuUtils.isAmplifier) {
            xValue = listOf(
                activity?.resources?.getString(R.string.bass),
                activity?.resources?.getString(R.string.medium_bass),
                activity?.resources?.getString(R.string.medium),
                activity?.resources?.getString(R.string.medium_treble),
                activity?.resources?.getString(R.string.treble)
            ) as List<String>
        } else {
            xValue = listOf(
                activity?.resources?.getString(R.string.sixty_five),
                activity?.resources?.getString(R.string.two_hundred_fifty),
                activity?.resources?.getString(R.string.seven_hundred_fifty),
                activity?.resources?.getString(R.string.one_thousand_three_hundred),
                activity?.resources?.getString(R.string.two_thousand_three_hundred),
                activity?.resources?.getString(R.string.three_thousand_five_hundred),
                activity?.resources?.getString(R.string.six_thousand_five_hundred),
                activity?.resources?.getString(R.string.eight_thousand_five_hundred),
                activity?.resources?.getString(R.string.eighteen_thousand)
            ) as List<String>
        }
    }

    private fun initView() {

        binding.smoothChartView.setInterval(-1 * offset, offset)
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = -1 * offset
        binding.smoothChartView.maxY = offset
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.smooth_line_color)
        binding.smoothChartView.circleColor = resources.getColor(R.color.smooth_circle_color)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        binding.smoothChartView.setOnChartClickListener { position, value ->
//            viewModel.setAudioEQ(position)
            doSendCustomEqValue()
        }
//        onPostSelected(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect.value!!)

    }

    override fun onPostSelected(tabView: TabControlView, value: Int) {
//        val result = node.obtainSelectValue(value)
        val values = viewModel.getEffectValues(value).toList()
        Timber.d("onPostSelected 11111111111111 tabView:$tabView, value:$value, values:%s", values)
        val toList = values.map {
            var value = it.toFloat() - 1
            if (value < 0f) {
                value = 0f
            } else if (value > 2 * offset) {
                value = 2 * offset
            }
            value
        }.toList()
        Timber.d("onPostSelected 22222222222222 tabView:$tabView, value:$value, toList:%s", toList)
        this.vList = toList
        this.value = value
        binding.smoothChartView.setData(toList, xValue)
    }

    private fun doSendCustomEqValue() {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val values = node.get.values
        viewModel.currentEffect.let {
            if (it.value!!.data == values.last()) {
                val progress = binding.smoothChartView.obtainProgress()
                if (null != progress && progress.size == Constant.EQ_SIZE) {
                    EffectManager.instance.doSetEQ(it.value!!.data, progress)
                }
            }
        }
    }
//    ========= 音效平衡 start =============
    private var OFFSET = 1
    private var DEFALUT_BALANCE = 0
    private var DEFALUT_FADE = 0
    private fun initDataBalance() {

        Timber.d("SoundEffectFragment getAmpType type=${SettingManager.getAmpType()}")
        if (SettingManager.getAmpType() == 0) {// 1 外置 1-11  ||  0 内置 ——》1-19
            SoundFieldView.BALANCE_MAX = 18.0;
            SoundFieldView.FADE_MAX = 18.0;
        }else{
            SoundFieldView.BALANCE_MAX = 10.0;
            SoundFieldView.FADE_MAX = 10.0;
        }
        Timber.d("SoundEffectFragment getAmpType OFFSET=${OFFSET} BALANCE_MAX= ${SoundFieldView.BALANCE_MAX}   FADE_MAX =${SoundFieldView.FADE_MAX}")
        DEFALUT_BALANCE = (SoundFieldView.BALANCE_MAX / 2).toInt();
        DEFALUT_FADE = (SoundFieldView.FADE_MAX / 2).toInt();

        binding?.apply {
            soundField.onValueChangedListener = object : SoundFieldView.OnValueChangedListener {
                override fun onValueChange(balance: Int, fade: Int, x: Float, y: Float) {
                    Timber.d("SoundEffectFragment onValueChange balance:$balance fade:$fade")
                    viewModel?.setAudioBalance(balance + OFFSET, fade + OFFSET)
                }

                override fun onDoubleClickChange(balance: Int, fade: Int, x: Float, y: Float) {
                    soundField.reset()
                    Timber.d(" SoundEffectFragmentonDoubleClickChange reset balance:${DEFALUT_BALANCE + OFFSET} fade:${DEFALUT_FADE + OFFSET}")
                    viewModel?.setAudioBalance(DEFALUT_BALANCE + OFFSET, DEFALUT_FADE + OFFSET)
                }
            }
            refreshDialog.setOnClickListener {
                soundField.reset()
                Timber.d("SoundEffectFragment onValueChange reset balance:${DEFALUT_BALANCE + OFFSET} fade:${DEFALUT_FADE + OFFSET}")
                viewModel?.setAudioBalance(DEFALUT_BALANCE + OFFSET, DEFALUT_FADE + OFFSET)
            }
            initBlance()
        }
    }

    private fun initBlance() {
        try {
            val balance: Int? = viewModel?.getAudioBalance()
            val fade: Int? = viewModel?.getAudioFade()
            Timber.d("SoundEffectFragment before getAudioBalFadInfo balance:$balance fade:$fade")
            binding?.apply {
                if (balance != null) {
                    // soundField?.balanceValue = balance + OFFSET
                    soundField?.balanceValue = balance - OFFSET
                }
                if (fade != null) {
                    //  soundField.fadeValue = -fade + OFFSET
                    soundField.fadeValue = fade - OFFSET
                }
                Timber.d("SoundEffectFragment after getAudioBalFadInfo balance:${soundField?.balanceValue} fade:${soundField.fadeValue}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    ========= 音效平衡 end =============
}