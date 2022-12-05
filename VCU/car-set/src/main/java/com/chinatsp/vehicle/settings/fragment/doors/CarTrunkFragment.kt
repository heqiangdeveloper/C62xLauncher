package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarTrunkFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.SternDoorViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.ArcSeekBar
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class CarTrunkFragment : BaseFragment<SternDoorViewModel, CarTrunkFragmentBinding>(),
    ArcSeekBar.OnChangeListener, IOptionAction {

    private var firstTrunk = true

    private val animTrunk: AnimationDrawable by lazy {
        AnimationDrawable()
    }

    private var firstFlash = true

    private val animFlash: AnimationDrawable by lazy {
        val animationDrawable = AnimationDrawable()
        val animationId = R.drawable.flash_alarm_animation
        animationDrawable.setAnimation(activity, animationId, binding.ivFlashAlarm)
        animationDrawable
    }

    private var firstBuzzer = true

    private val animBuzzer: AnimationDrawable by lazy {
        val animationDrawable = AnimationDrawable()
        val animationId = R.drawable.buzzer_alarms_animation
        animationDrawable.setAnimation(activity, animationId, binding.ivBuzzerAlarms)
        animationDrawable
    }

    private val duration: Int get() = 40

    private var location: Int = 0

    private val map: HashMap<Int, View> = HashMap()

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    private val trunkResource: IntArray by lazy {
        intArrayOf(
            R.drawable.trunk_door_close_lv5_00,
            R.drawable.trunk_door_close_lv5_01,
            R.drawable.trunk_door_close_lv5_02,
            R.drawable.trunk_door_close_lv5_03,
            R.drawable.trunk_door_close_lv5_04,
            R.drawable.trunk_door_close_lv5_05,
            R.drawable.trunk_door_close_lv5_06,
            R.drawable.trunk_door_close_lv5_07,
            R.drawable.trunk_door_close_lv5_08,
            R.drawable.trunk_door_close_lv5_09,
            R.drawable.trunk_door_close_lv5_10
        )

//        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
//            intArrayOf(
//                R.drawable.trunk_door_lv5_10,
//                R.drawable.trunk_door_lv5_09,
//                R.drawable.trunk_door_lv5_08,
//                R.drawable.trunk_door_lv5_07,
//                R.drawable.trunk_door_lv5_06,
//                R.drawable.trunk_door_lv5_05,
//                R.drawable.trunk_door_lv5_04,
//                R.drawable.trunk_door_lv5_03,
//                R.drawable.trunk_door_lv5_02,
//                R.drawable.trunk_door_lv5_01,
//                R.drawable.trunk_door_lv5_00
//            )
//        } else {
//            intArrayOf(
//                R.drawable.trunk_door_10,
//                R.drawable.trunk_door_09,
//                R.drawable.trunk_door_08,
//                R.drawable.trunk_door_07,
//                R.drawable.trunk_door_06,
//                R.drawable.trunk_door_05,
//                R.drawable.trunk_door_04,
//                R.drawable.trunk_door_03,
//                R.drawable.trunk_door_02,
//                R.drawable.trunk_door_01,
//                R.drawable.trunk_door_00
//            )
//        }
    }
    private val trunkCloseAnimationResource: IntArray by lazy {
        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            intArrayOf(
                R.drawable.trunk_door_close_lv5_10,
                R.drawable.trunk_door_close_lv5_09,
                R.drawable.trunk_door_close_lv5_08,
                R.drawable.trunk_door_close_lv5_07,
                R.drawable.trunk_door_close_lv5_06,
                R.drawable.trunk_door_close_lv5_05,
                R.drawable.trunk_door_close_lv5_04,
                R.drawable.trunk_door_close_lv5_03,
                R.drawable.trunk_door_close_lv5_02,
                R.drawable.trunk_door_close_lv5_01,
                R.drawable.trunk_door_close_lv5_00
            )
        } else {
            intArrayOf(
                R.drawable.trunk_door_close_lv3_10,
                R.drawable.trunk_door_close_lv3_09,
                R.drawable.trunk_door_close_lv3_08,
                R.drawable.trunk_door_close_lv3_07,
                R.drawable.trunk_door_close_lv3_06,
                R.drawable.trunk_door_close_lv3_05,
                R.drawable.trunk_door_close_lv3_04,
                R.drawable.trunk_door_close_lv3_03,
                R.drawable.trunk_door_close_lv3_02,
                R.drawable.trunk_door_close_lv3_01,
                R.drawable.trunk_door_close_lv3_00
            )
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.car_trunk_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initClickView()
        initArcSeekBar()
        setProgressLiveDataListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initViewDisplay()
        initDetailsClickListener()

        updateOptionActive()

        initRouteListener()

    }

    private fun initClickView() {
        map[1] = binding.electricTailDetails
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.AS_STERN_ELECTRIC)
        updateSwitchEnable(SwitchNode.STERN_LIGHT_ALARM)
        updateSwitchEnable(SwitchNode.STERN_AUDIO_ALARM)
        updateRadioEnable(RadioNode.STERN_SMART_ENTER)
        updateSeekBarEnable()
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
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid, true) }
                            }
                        }
                }
            }
        }
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun setProgressLiveDataListener() {
        viewModel.trunkStopPoint.observe(this) {
            initLocation(it.pos)
//            binding.ivCarTrunk.setImageResource(trunkResource[location])
        }
    }

    private fun updateSeekBarEnable() {
        val isPark = isPark()
        val alpha = if (isPark) 1.0f else 0.6f
        binding.arcSeekBar.alpha = alpha
        binding.arcSeekBar.isEnabled = isPark
    }


    private fun initLocation(progress: Int) {
        val size = trunkResource.size
        val scale = size.toFloat() / 100f
        var pros = progress
        if (pros < 0) pros = 0
        if (pros > 100) pros = 100
        val value = pros * scale
        var position = value.roundToInt()
        if (position >= size) position = size - 1
        location = position
    }

    private fun initViewDisplay() {
        /* if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
             binding.linearLayout.visibility = View.GONE
             binding.line2.visibility = View.GONE
         }*/
    }

    private fun initAnimation() {
        val status = viewModel.electricFunction.value?.get() ?: false
        val showSeek = binding.arcSeekBar.visibility == View.VISIBLE
        val visibility = if (status && !showSeek) View.VISIBLE else View.INVISIBLE
        var animationId = R.drawable.flash_alarm_animation
        animFlash.setAnimation(activity, animationId, binding.ivFlashAlarm)
        animationId = R.drawable.buzzer_alarms_animation
        animBuzzer.setAnimation(activity, animationId, binding.ivBuzzerAlarms)
        binding.ivFlashAlarm.visibility = visibility
        binding.ivBuzzerAlarms.visibility = visibility
    }

    private fun initDetailsClickListener() {
        binding.electricTailDetails.setOnClickListener(this::onViewClick)
        binding.carTrunkDoorHeight.setOnClickListener(this::onViewClick)
    }

    private fun onViewClick(view: View, clickUid: Int, frank: Boolean) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(view: View) {
        if (view == binding.electricTailDetails) {
            showPopWindow(R.string.car_trunk_content, view)
        } else if (view == binding.carTrunkDoorHeight) {
            if (binding.carTrunkDoorHeight.text.equals(activity?.getString(R.string.car_trunk_door_height))) {
                showPopWindow(R.string.car_trunk_height_content, view)
            }
        }
    }

    private fun initArcSeekBar() {
        binding.arcSeekBar.setOnChangeListener(this)
        val status = viewModel.electricFunction.value?.get() ?: false
        initLocation(0)
        if (status) {
            viewModel.trunkStopPoint.value?.let {
                binding.arcSeekBar.progress = it.pos
                initLocation(it.pos)
            }
        }
        binding.arcSeekBar.allowableOffsets = 50f
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.STERN_SMART_ENTER, viewModel.sternSmartEnter)
    }

    private fun addRadioLiveDataListener() {
        viewModel.sternSmartEnter.observe(this) {
            doUpdateRadio(RadioNode.STERN_SMART_ENTER, it, false)
        }
        viewModel.gearsFunction.observe(this) {
            updateOptionActive()
//            doElectricTrunkFollowing(true)
            updateRadioEnable(RadioNode.GEARS)
        }
    }

    private fun isPark(): Boolean {
        //return 0x1 == viewModel.gearsFunction.value?.get()
        return true
    }

    private fun setRadioListener() {
        binding.sternSmartEnterRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.STERN_SMART_ENTER, value, viewModel.sternSmartEnter, it)
                showFollowRadio(it.checked)
            }
        }
    }

    private fun showFollowRadio(value: String) {
        when (value) {
            "1" -> {
                binding.intelligenceInto.visibility = View.INVISIBLE
                binding.zero.visibility = View.VISIBLE
                binding.zeroLine.visibility = View.VISIBLE
                binding.arcSeekBar.visibility = View.VISIBLE
            }
            "2" -> {
                binding.zero.visibility = View.INVISIBLE
                binding.zeroLine.visibility = View.INVISIBLE
                binding.arcSeekBar.visibility = View.INVISIBLE
                binding.intelligenceInto.visibility = View.VISIBLE
                setAnimation()
            }
            else -> {
                binding.zero.visibility = View.INVISIBLE
                binding.zeroLine.visibility = View.INVISIBLE
                binding.arcSeekBar.visibility = View.INVISIBLE
                binding.intelligenceInto.visibility = View.VISIBLE
                setAnimation()
            }
        }
    }

    /**
     * 电动尾门智能进入动画
     */
    private fun setAnimation() {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(1F, 0F)
        alphaAnimation.duration = 2000
        animationSet.addAnimation(alphaAnimation)
        binding.intelligenceInto.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                binding.intelligenceInto.visibility = View.INVISIBLE
                binding.zero.visibility = View.VISIBLE
                binding.zeroLine.visibility = View.VISIBLE
                binding.arcSeekBar.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun setSwitchListener() {
        binding.sternElectricSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.AS_STERN_ELECTRIC, buttonView, isChecked)
                updateOptionActive()
                doElectricTrunkFollowing(it.isChecked)
            }
        }
        binding.accessSternLightAlarmSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.STERN_LIGHT_ALARM, buttonView, isChecked)
                doLightBlinkFollowing(it.isChecked)
            }
        }
        binding.accessSternAudioAlarmSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.STERN_AUDIO_ALARM, buttonView, isChecked)
                doBuzzerFollowing(it.isChecked)
            }
        }
    }

    private fun doBuzzerFollowing(status: Boolean) {
        animFlash.stop()
        animBuzzer.stop()
        animBuzzer.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                if (isShowSeek()) {
                    binding.arcSeekBar.visibility = View.INVISIBLE
                    binding.zero.visibility = View.INVISIBLE
                    binding.zeroLine.visibility = View.INVISIBLE
                }
                binding.ivFlashAlarm.visibility = View.INVISIBLE
                binding.ivBuzzerAlarms.visibility = View.VISIBLE
            }

            override fun endAnimation() {
                binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                if (isShowSeek()) {
                    binding.zero.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.VISIBLE
                    binding.zeroLine.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun doLightBlinkFollowing(status: Boolean) {
        animBuzzer.stop()
        animFlash.stop()
        animFlash.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                if (isShowSeek()) {
                    binding.zero.visibility = View.INVISIBLE
                    binding.arcSeekBar.visibility = View.INVISIBLE
                    binding.zeroLine.visibility = View.INVISIBLE
                }
                binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                binding.ivFlashAlarm.visibility = View.VISIBLE
            }

            override fun endAnimation() {
                binding.ivFlashAlarm.visibility = View.INVISIBLE
                if (isShowSeek()) {
                    binding.zero.visibility = View.VISIBLE
                    binding.zeroLine.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun addSwitchLiveDataListener() {
        viewModel.electricFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_ELECTRIC, it)
            updateOptionActive()
            doElectricTrunkFollowing(it.get())
        }
        viewModel.lightAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_LIGHT_ALARM, it)
            updateOptionActive()
            doLightAlarmHint(it.get())
        }
        viewModel.audioAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_AUDIO_ALARM, it)
            updateOptionActive()
            doAudioAlarmHint(it.get())
        }
    }

    private fun doAudioAlarmHint(status: Boolean) {
        if (!status || firstBuzzer) {
            firstBuzzer = false
            return
        }
        animBuzzer.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                Timber.d("doAudioAlarmHint ------animation--------------")
                if (isShowSeek()) {
                    binding.zero.visibility = View.INVISIBLE
                    binding.zeroLine.visibility = View.INVISIBLE
                    binding.arcSeekBar.visibility = View.INVISIBLE
                }
                binding.ivFlashAlarm.visibility = View.INVISIBLE
                binding.ivBuzzerAlarms.visibility = View.VISIBLE
                binding.carTrunkDoorHeight.visibility = View.VISIBLE
            }

            override fun endAnimation() {
                binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                if (isShowSeek()) {
                    binding.zero.visibility = View.VISIBLE
                    binding.zeroLine.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun doLightAlarmHint(status: Boolean) {
        if (!status || firstFlash) {
            firstFlash = false
            return
        }
        animFlash.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                Timber.d("doLightAlarmHint ------animation--------------")
                if (isShowSeek()) {
                    binding.arcSeekBar.visibility = View.INVISIBLE
                    binding.zero.visibility = View.INVISIBLE
                    binding.zeroLine.visibility = View.INVISIBLE
                }
                binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                binding.ivFlashAlarm.visibility = View.VISIBLE
                binding.carTrunkDoorHeight.visibility = View.VISIBLE
            }

            override fun endAnimation() {
                binding.ivFlashAlarm.visibility = View.INVISIBLE
                if (isShowSeek()) {
                    binding.zero.visibility = View.VISIBLE
                    binding.zeroLine.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AS_STERN_ELECTRIC, viewModel.electricFunction)
        initSwitchOption(SwitchNode.STERN_LIGHT_ALARM, viewModel.lightAlarmFunction)
        initSwitchOption(SwitchNode.STERN_AUDIO_ALARM, viewModel.audioAlarmFunction)
//        initSwitchOption(SwitchNode.GEARS, viewModel.gearsFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> binding.sternElectricSwitch
            SwitchNode.STERN_LIGHT_ALARM -> binding.accessSternLightAlarmSw
            SwitchNode.STERN_AUDIO_ALARM -> binding.accessSternAudioAlarmSw
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> viewModel.electricFunction.value?.enable() ?: false
            SwitchNode.STERN_LIGHT_ALARM -> viewModel.lightAlarmFunction.value?.enable() ?: false
            SwitchNode.STERN_AUDIO_ALARM -> viewModel.audioAlarmFunction.value?.enable() ?: false
            else -> false
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> isPark()
            SwitchNode.STERN_LIGHT_ALARM -> isPark() && binding.sternElectricSwitch.isChecked
            SwitchNode.STERN_AUDIO_ALARM -> isPark() && binding.sternElectricSwitch.isChecked
            else -> false
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> viewModel.sternSmartEnter.value?.enable() ?: false
            else -> false
        }
    }

    override fun obtainDependByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> isPark() && binding.sternElectricSwitch.isChecked
            else -> false
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
//        checkDisableOtherDiv(button, status)
        updateOptionActive()
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> binding.sternSmartEnterRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    override fun onStartTrackingTouch(isCanDrag: Boolean) {

    }

    override fun onProgressChanged(progress: Float, max: Float, fromUser: Boolean) {
//        this.progress = progress.toInt()
        if (fromUser) {
            //manager.doSetVolume(Progress.TRUNK_STOP_POSITION, progress.toInt())
            //viewModel.onProgressChanged(Progress.TRUNK_STOP_POSITION, progress.toInt())
            initLocation(progress.toInt())
            binding.ivCarTrunk.setImageResource(trunkResource[location])
        }
//        animOpenDoor.progressStart(progress.toInt())
    }

    override fun onStopTrackingTouch(isCanDrag: Boolean, lastValue: Float) {
        if (isCanDrag) {
            manager.doSetVolume(Progress.TRUNK_STOP_POSITION, lastValue.toInt())
            viewModel.onProgressChanged(Progress.TRUNK_STOP_POSITION, lastValue.toInt())
        }
    }

    override fun onSingleTapUp() {
        manager.doSetVolume(Progress.TRUNK_STOP_POSITION, binding.arcSeekBar.lastValue)
        viewModel.onProgressChanged(Progress.TRUNK_STOP_POSITION, binding.arcSeekBar.lastValue)
    }

    private fun showPopWindow(id: Int, view: View) {
        var popWindow: PopWindow? = null
        if (view.id == binding.electricTailDetails.id) {
            popWindow = PopWindow(activity,
                R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172
                    )
                })
            popWindow.showDownLift(view, 30, -130)
        } else if (view.id == binding.carTrunkDoorHeight.id) {
            popWindow = PopWindow(activity,
                R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao776_298
                    )
                })
            popWindow.showDownLift(view, -270, -15)
        }
        val text: TextView = popWindow?.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
    }

    private fun doElectricTrunkFollowing(status: Boolean) {
        var resArray = trunkResource.slice(0..location)
        if (!status) {
            resArray = resArray.reversed()
        }
        val visibility = if (status) View.VISIBLE else View.INVISIBLE
        if (firstTrunk) {
            binding.ivCarTrunk.setImageResource(resArray.last())
            binding.zero.visibility = visibility
            binding.zeroLine.visibility = visibility
            binding.arcSeekBar.visibility = visibility
            binding.carTrunkDoorHeight.visibility = visibility
            firstTrunk = false
            return
        }
        animTrunk.setAnimation(binding.ivCarTrunk, resArray)
        animTrunk.start(false, 20, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                val visibility = if (isShowSeek()) View.VISIBLE else View.INVISIBLE
                binding.zero.visibility = visibility
                binding.zeroLine.visibility = visibility
                binding.arcSeekBar.visibility = visibility
                binding.carTrunkDoorHeight.visibility = visibility
            }

            override fun endAnimation() {
                val visibility = if (isShowSeek()) View.VISIBLE else View.INVISIBLE
                binding.zero.visibility = visibility
                binding.zeroLine.visibility = visibility
                binding.arcSeekBar.visibility = visibility
            }
        })
    }

    fun isShowSeek(): Boolean {
        return binding.sternElectricSwitch.isChecked
    }

    fun isShowKey(): Boolean {
        return binding.sternElectricSwitch.isChecked && (binding.sternSmartEnterRadio.checked != "1")
    }


}