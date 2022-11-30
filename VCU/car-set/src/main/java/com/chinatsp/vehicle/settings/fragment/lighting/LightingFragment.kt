package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.LightManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.LightingViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.VSeekBar
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LightingFragment : BaseFragment<LightingViewModel, LightingFragmentBinding>(),
    VSeekBar.OnSeekBarListener, IOptionAction {

    private var animationHomeOpen: AnimationDrawable = AnimationDrawable()
    private var animationHomeClose: AnimationDrawable = AnimationDrawable()
    private var animationWelcomeLamp: AnimationDrawable = AnimationDrawable()
    private var animationTurnSignal: AnimationDrawable = AnimationDrawable()
    private var animationTurnSignal1: AnimationDrawable = AnimationDrawable()
    private var animationTurnSignal2: AnimationDrawable = AnimationDrawable()

    private val manager: LightManager
        get() = LightManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initAnimation()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initSeekBar()
        setSeekBarListener(this)
        initSeekLiveData()

        initViewDisplay()

        updateOptionActive()
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.LIGHT_OUTSIDE_MEET)
        updateSwitchEnable(SwitchNode.LIGHT_INSIDE_MEET)
        updateSwitchEnable(SwitchNode.LIGHT_CEREMONY_SENSE)

        updateRadioEnable(RadioNode.LIGHT_DELAYED_OUT)
        updateRadioEnable(RadioNode.LIGHT_FLICKER)
        updateRadioEnable(RadioNode.LIGHT_CEREMONY_SENSE)
    }

    private fun initViewDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, expect = true)) {
            binding.lightingTurnExternal.visibility = View.GONE
            binding.line4.visibility = View.GONE
        }
        binding.lightingGoHomeDetails.setOnClickListener {
            showPopWindow(R.string.lighting_go_home_content, it)
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.LIGHT_DELAYED_OUT, viewModel.lightOutDelayed)
        initRadioOption(RadioNode.LIGHT_FLICKER, viewModel.lightFlicker)
        initRadioOption(RadioNode.LIGHT_CEREMONY_SENSE, viewModel.ceremonySense)
    }

    private fun initAnimation() {
        animationHomeOpen.setAnimation(
            activity,
            R.drawable.home_open_animation,
            binding.homeOpenIv
        )
        animationHomeClose.setAnimation(
            activity,
            R.drawable.home_close_animation,
            binding.homeOpenIv
        )
        animationWelcomeLamp.setAnimation(
            activity,
            R.drawable.welcome_lamp_animation,
            binding.welcomeLampIv
        )
        animationTurnSignal.setAnimation(
            activity,
            R.drawable.turn_signal_animation,
            binding.turnSignalIv
        )
        animationTurnSignal1.setAnimation(
            activity,
            R.drawable.turn_signal_animation_1,
            binding.turnSignalIv1
        )
        animationTurnSignal2.setAnimation(
            activity,
            R.drawable.turn_signal_animation_2,
            binding.turnSignalIv2
        )
    }

    private fun addRadioLiveDataListener() {
        viewModel.lightOutDelayed.observe(this) {
            doUpdateRadio(RadioNode.LIGHT_DELAYED_OUT, it, false)
        }
        viewModel.lightFlicker.observe(this) {
            doUpdateRadio(RadioNode.LIGHT_FLICKER, it, false)
        }
        viewModel.ceremonySense.observe(this) {
            doUpdateRadio(RadioNode.LIGHT_CEREMONY_SENSE, it, false)
        }
    }

    private fun setRadioListener() {
        binding.lightCeremonySenseRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.LIGHT_CEREMONY_SENSE, value, viewModel.ceremonySense, it)
            }
        }
        binding.lightDelayBlackOutRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.LIGHT_DELAYED_OUT
                doUpdateRadio(node, value, viewModel.lightOutDelayed, it)
                doAnimationFollowHomeRadio(node, value)
            }
        }
        binding.lightFlickerRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.LIGHT_FLICKER, value, viewModel.lightFlicker, it)
                if (value.equals("2")) {
                    binding.turnSignalIv1.visibility = View.VISIBLE
                    binding.turnSignalIv2.visibility = View.GONE
                    binding.turnSignalIv.visibility = View.GONE
                    setTurnAnimation(animationTurnSignal1, value)
                } else if (value.equals("3")) {
                    binding.turnSignalIv2.visibility = View.VISIBLE
                    binding.turnSignalIv1.visibility = View.GONE
                    binding.turnSignalIv.visibility = View.GONE
                    setTurnAnimation(animationTurnSignal2, value)
                } else if (value.equals("4")) {
                    binding.turnSignalIv.visibility = View.VISIBLE
                    binding.turnSignalIv2.visibility = View.GONE
                    binding.turnSignalIv1.visibility = View.GONE
                    setTurnAnimation(animationTurnSignal, value)
                }
            }
        }
    }

    private fun doAnimationFollowHomeRadio(node: RadioNode, value: String) {
        val isOn = value.toInt() != node.set.values[0]
        binding.homeOpenIv.visibility = View.VISIBLE
        if (isOn) {
            animationHomeOpen.start(false, 50, null)
            return
        }
        animationHomeClose.start(false, 50, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
            }

            override fun endAnimation() {
                binding.homeOpenIv.visibility = View.GONE
            }
        })
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.LIGHT_OUTSIDE_MEET, viewModel.outsideLightMeet)
        initSwitchOption(SwitchNode.LIGHT_INSIDE_MEET, viewModel.insideLightMeet)
        initSwitchOption(SwitchNode.LIGHT_CEREMONY_SENSE, viewModel.ceremonySenseSwitch)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.outsideLightMeet.observe(this) {
            val node = SwitchNode.LIGHT_OUTSIDE_MEET
            doUpdateSwitch(node, it)
            updateSwitchEnable(node)
        }
        viewModel.insideLightMeet.observe(this) {
            val node = SwitchNode.LIGHT_INSIDE_MEET
            doUpdateSwitch(node, it)
            updateSwitchEnable(node)
        }
        viewModel.ceremonySenseSwitch.observe(this) {
            val node = SwitchNode.LIGHT_CEREMONY_SENSE
            doUpdateSwitch(node, it)
            updateSwitchEnable(node)
            updateRadioEnable(RadioNode.LIGHT_CEREMONY_SENSE)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.LIGHT_OUTSIDE_MEET -> binding.lightOutsideMeetSwitch
            SwitchNode.LIGHT_INSIDE_MEET -> binding.lightInsideMeetSwitch
            SwitchNode.LIGHT_CEREMONY_SENSE -> binding.lightCeremonySenseSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.LIGHT_OUTSIDE_MEET -> viewModel.outsideLightMeet.value?.enable() ?: false
            SwitchNode.LIGHT_INSIDE_MEET -> viewModel.insideLightMeet.value?.enable() ?: false
            SwitchNode.LIGHT_CEREMONY_SENSE -> viewModel.ceremonySenseSwitch.value?.enable()
                ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> viewModel.lightOutDelayed.value?.enable() ?: false
            RadioNode.LIGHT_FLICKER -> viewModel.lightFlicker.value?.enable() ?: false
            RadioNode.LIGHT_CEREMONY_SENSE -> viewModel.ceremonySense.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.LIGHT_CEREMONY_SENSE -> obtainActiveByNode(RadioNode.LIGHT_CEREMONY_SENSE)
                    && binding.lightCeremonySenseSwitch.isChecked
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> binding.lightDelayBlackOutRadio
            RadioNode.LIGHT_FLICKER -> binding.lightFlickerRadio
            RadioNode.LIGHT_CEREMONY_SENSE -> binding.lightCeremonySenseRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.lightCeremonySenseSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LIGHT_CEREMONY_SENSE, buttonView, isChecked)
//            checkDisableOtherDiv(buttonView as SwitchButton, buttonView.isChecked)
            updateRadioEnable(RadioNode.LIGHT_CEREMONY_SENSE)

        }
        binding.lightOutsideMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LIGHT_OUTSIDE_MEET, buttonView, isChecked)
        }
        binding.lightInsideMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LIGHT_INSIDE_MEET, buttonView, isChecked)
            if (isChecked) {
                binding.welcomeLampIv.visibility = View.VISIBLE
                animationWelcomeLamp.start(false, 50, null)
            } else {
                binding.welcomeLampIv.visibility = View.GONE
            }
        }

    }

    private fun setTurnAnimation(animationDrawable: AnimationDrawable, value: String) {
        animationDrawable.start(false, 50, null)
    }

    private fun initSeekLiveData() {
        viewModel.switchBacklight.observe(this) {
            updateSeekBarValue(binding.lightSwitchBacklightSeekBar, it)
        }
    }

    private fun updateSeekBarValue(bar: VSeekBar, volume: Volume) {
        bar.min = volume.min
        bar.max = volume.max
        bar.setValueNoEvent(volume.pos)
    }

    private fun initSeekBar() {
        binding.lightSwitchBacklightSeekBar.apply {
            viewModel.switchBacklight.value?.let {
                updateSeekBarValue(this, it)
            }
        }
    }

    private fun setSeekBarListener(listener: VSeekBar.OnSeekBarListener) {
        binding.lightSwitchBacklightSeekBar.setOnSeekBarListener(listener)
    }

    override fun onValueChanged(seekBar: VSeekBar?, newValue: Int) {
        seekBar?.run {
            when (this) {
                binding.lightSwitchBacklightSeekBar -> {
                    Timber.d("onValueChanged newValue:$newValue")
                    manager.doSetVolume(Progress.SWITCH_BACKLIGHT_BRIGHTNESS, newValue)
                }
                else -> {}
            }
        }
    }

//    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
//        if (swb == binding.lightCeremonySenseSwitch) {
//            val childCount = binding.lightCeremonySenseRadioConstraint.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.lightCeremonySenseRadioConstraint.getChildAt(it)
//                if (null != childAt && childAt != binding.lightingTurnExternal) {
//                    childAt.alpha = if (status) 1.0f else 0.6f
//                    updateViewEnable(childAt, status)
//                }
//            }
//        }
//    }
//
//    private fun updateViewEnable(view: View?, status: Boolean) {
//        if (null == view) {
//            return
//        }
//        if (view is SwitchButton) {
//            view.isEnabled = status
//            return
//        }
//        if (view is AppCompatImageView) {
//            view.isEnabled = status
//            return
//        }
//        if (view is TabControlView) {
//            view.updateEnable(status)
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
//        }
//    }

    private fun showPopWindow(id: Int, view: View) {
        var popWindow: PopWindow? = null
        if (view.id == binding.lightingGoHomeDetails.id) {
            popWindow = PopWindow(activity,
                R.layout.car_doors_pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao472_214
                    )
                })
            popWindow.showDownLift(view, -103, -6)
        }
        val text: TextView = popWindow?.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
    }
}