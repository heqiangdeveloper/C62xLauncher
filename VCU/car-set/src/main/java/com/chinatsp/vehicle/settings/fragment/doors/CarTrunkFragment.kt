package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarTrunkFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.SternDoorViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.ArcSeekBar
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class CarTrunkFragment : BaseFragment<SternDoorViewModel, CarTrunkFragmentBinding>(),
    ArcSeekBar.OnChangeListener, IOptionAction {

    private var animTrunk: AnimationDrawable = AnimationDrawable()
    private var animFlashAlarm: AnimationDrawable = AnimationDrawable()
    private var animBuzzerAlarms: AnimationDrawable = AnimationDrawable()

    private val duration: Int
        get() = 50

    private var location: Int = 0

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    private val trunkAnimationResource: IntArray by lazy {
        intArrayOf(
            R.drawable.trunk_door_00,
            R.drawable.trunk_door_01,
            R.drawable.trunk_door_02,
            R.drawable.trunk_door_03,
            R.drawable.trunk_door_04,
            R.drawable.trunk_door_05,
            R.drawable.trunk_door_06,
            R.drawable.trunk_door_07,
            R.drawable.trunk_door_08,
            R.drawable.trunk_door_09
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.car_trunk_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

        initArcSeekBar()
        setProgressLiveDataListener()

        initAnimation()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initViewDisplay()
        initDetailsClickListener()
    }

    private fun setProgressLiveDataListener() {
        viewModel.trunkStopPosition.observe(this) {
            initLocation(it.pos)
            binding.ivCarTrunk.setImageResource(trunkAnimationResource[location])
        }
    }

    private fun initLocation(progress: Int) {
        val size = trunkAnimationResource.size
        val scale = size.toFloat() / 50f
        var pros = progress
        if (pros < 50) pros = 50
        if (pros > 100) pros = 100
        val value = (pros - 50) * scale
        var position = value.roundToInt()
        if (position >= size) position = size - 1
        location = position
    }

    private fun initViewDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, Level.LEVEL5_2, expect = true)) {
            binding.linearLayout.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun initAnimation() {
        var animationId = R.drawable.flash_alarm_animation
        animFlashAlarm.setAnimation(activity, animationId, binding.ivFlashAlarm)
        animationId = R.drawable.buzzer_alarms_animation
        animBuzzerAlarms.setAnimation(activity, animationId, binding.ivBuzzerAlarms)
    }

    private fun initDetailsClickListener() {
        binding.electricTailDetails.setOnClickListener {
            showPopWindow(R.string.car_trunk_content, it)
        }
    }

    private fun initArcSeekBar() {
        viewModel.trunkStopPosition.value?.let {
            binding.arcSeekBar.progress = it.pos
            initLocation(it.pos)
        }
        binding.arcSeekBar.setOnChangeListener(this)
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.STERN_SMART_ENTER, viewModel.sternSmartEnter)
    }

    private fun addRadioLiveDataListener() {
        viewModel.sternSmartEnter.observe(this) {
            doUpdateRadio(RadioNode.STERN_SMART_ENTER, it, false)
        }
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
                binding.carTrunkDoorHeight.visibility = View.INVISIBLE
                binding.intelligenceInto.visibility = View.INVISIBLE
                binding.arcSeekBar.visibility = View.VISIBLE
            }
            "2" -> {
                binding.arcSeekBar.visibility = View.INVISIBLE
                binding.carTrunkDoorHeight.visibility = View.VISIBLE
                binding.intelligenceInto.visibility = View.VISIBLE
                binding.carTrunkDoorHeight.setText(R.string.car_trunk_keep_unlock)
            }
            else -> {
                binding.arcSeekBar.visibility = View.INVISIBLE
                binding.carTrunkDoorHeight.visibility = View.VISIBLE
                binding.intelligenceInto.visibility = View.VISIBLE
                binding.carTrunkDoorHeight.setText(R.string.car_trunk_action_unlock)
            }
        }
    }

    private fun setSwitchListener() {
        binding.sternElectricSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.AS_STERN_ELECTRIC, buttonView, isChecked)
                checkDisableOtherDiv(it, isChecked)
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
        if (!status) {
            binding.carTrunkDoorHeight.setText(R.string.car_trunk_buzzer_close)
            return
        }
        binding.carTrunkDoorHeight.setText(R.string.car_trunk_buzzer_open)
        animFlashAlarm.stop()
        animBuzzerAlarms.start(
            false, duration,
            object : AnimationDrawable.AnimationLisenter {
                override fun startAnimation() {
                    if (isShowSeek()) {
                        binding.arcSeekBar.visibility = View.INVISIBLE
                    }
                    if (isShowKey()) {
                        binding.intelligenceInto.visibility = View.INVISIBLE
                    }
                    binding.ivFlashAlarm.visibility = View.INVISIBLE
                    binding.ivBuzzerAlarms.visibility = View.VISIBLE
                    binding.carTrunkDoorHeight.visibility = View.VISIBLE
                }

                override fun endAnimation() {
                    binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                    if (isShowSeek()) {
                        binding.arcSeekBar.visibility = View.VISIBLE
                    }
                    if (isShowKey()) {
                        binding.intelligenceInto.visibility = View.VISIBLE
                    }
                }
            })

    }

    private fun doLightBlinkFollowing(status: Boolean) {
        if (!status) {
            binding.carTrunkDoorHeight.setText(R.string.car_trunk_light_close)
            return
        }
        animBuzzerAlarms.stop()
        binding.carTrunkDoorHeight.setText(R.string.car_trunk_light_open)
        animFlashAlarm.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                if (isShowSeek()) {
                    binding.arcSeekBar.visibility = View.INVISIBLE
                }
                if (isShowKey()) {
                    binding.intelligenceInto.visibility = View.INVISIBLE
                }
                binding.ivBuzzerAlarms.visibility = View.INVISIBLE
                binding.ivFlashAlarm.visibility = View.VISIBLE
                binding.carTrunkDoorHeight.visibility = View.VISIBLE
            }

            override fun endAnimation() {
                binding.ivFlashAlarm.visibility = View.INVISIBLE
                if (isShowSeek()) {
                    binding.arcSeekBar.visibility = View.VISIBLE
                }
                if (isShowKey()) {
                    binding.intelligenceInto.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun doElectricTrunkFollowing(status: Boolean) {
        binding.carTrunkDoorHeight.visibility = if (status) View.VISIBLE else View.INVISIBLE
        binding.intelligenceInto.visibility = if (isShowKey()) View.VISIBLE else View.INVISIBLE
        binding.arcSeekBar.visibility = if (isShowSeek()) View.VISIBLE else View.INVISIBLE
        onTrunkPositionChanged(viewModel.trunkStopPosition.value!!.pos)
    }


    private fun addSwitchLiveDataListener() {
        viewModel.electricFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_ELECTRIC, it)
            doElectricTrunkFollowing(it)
        }
        viewModel.lightAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_LIGHT_ALARM, it)
        }
        viewModel.audioAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_AUDIO_ALARM, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AS_STERN_ELECTRIC, viewModel.electricFunction)
        initSwitchOption(SwitchNode.STERN_LIGHT_ALARM, viewModel.lightAlarmFunction)
        initSwitchOption(SwitchNode.STERN_AUDIO_ALARM, viewModel.audioAlarmFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                binding.sternElectricSwitch
            }
            SwitchNode.STERN_LIGHT_ALARM -> {
                binding.accessSternLightAlarmSw
            }
            SwitchNode.STERN_AUDIO_ALARM -> {
                binding.accessSternAudioAlarmSw
            }
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        checkDisableOtherDiv(button, status)
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

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.sternElectricSwitch) {
            binding.arcSeekBar.let {
                it.isEnabledDrag = status
                it.alpha = if (status) 1.0f else 0.6f
            }
            val childCount = binding.layoutContent.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.layoutContent.getChildAt(it)
                if (null != childAt && childAt != binding.carTrunkElectricFunction) {
                    childAt.alpha = if (status) 1.0f else 0.6f
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
        if (view is AppCompatImageView) {
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

    override fun onStartTrackingTouch(isCanDrag: Boolean) {

    }

    override fun onProgressChanged(progress: Float, max: Float, fromUser: Boolean) {
//        this.progress = progress.toInt()
        if (fromUser) {
            manager.doSetVolume(Progress.TRUNK_STOP_POSITION, progress.toInt())
            viewModel.onProgressChanged(Progress.TRUNK_STOP_POSITION, progress.toInt())
        }
//        animOpenDoor.progressStart(progress.toInt())
    }

    override fun onStopTrackingTouch(isCanDrag: Boolean) {

    }

    override fun onSingleTapUp() {

    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow = PopWindow(activity,
            R.layout.pop_window,
            activity?.let { AppCompatResources.getDrawable(it, R.drawable.popup_bg_qipao172) })
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view, 30, -130)
    }

    private fun onTrunkPositionChanged(progress: Int) {
        val status = binding.sternElectricSwitch.isChecked
        var resArray = trunkAnimationResource.slice(0..location)
        if (!status) {
            resArray = resArray.reversed()
        }
        animTrunk.setAnimation(binding.ivCarTrunk, resArray)
        animTrunk.start(false, duration, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
            }

            override fun endAnimation() {
                val visibility = if (isShowSeek()) View.VISIBLE else View.GONE
                binding.arcSeekBar.visibility = visibility
                val keyVisibility = if (isShowKey()) View.VISIBLE else View.GONE
                binding.intelligenceInto.visibility = keyVisibility
            }
        })
    }

    fun isShowSeek(): Boolean {
        return binding.sternElectricSwitch.isChecked && (binding.sternSmartEnterRadio.checked == "1")
    }

    fun isShowKey(): Boolean {
        return binding.sternElectricSwitch.isChecked && (binding.sternSmartEnterRadio.checked != "1")
    }

}