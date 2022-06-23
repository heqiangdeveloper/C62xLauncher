package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.lamp.LightManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.LightingViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingFragment : BaseFragment<LightingViewModel, LightingFragmentBinding>() {
    private var animationHomeOpen: AnimationDrawable = AnimationDrawable()
    private var animationHomeClose: AnimationDrawable = AnimationDrawable()
    private var animationWelcomeLamp: AnimationDrawable = AnimationDrawable()
    private var animationTurnSignal: AnimationDrawable = AnimationDrawable()

    private val manager: IOptionManager
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
    }


    private fun initRadioOption() {
        initRadioOption(RadioNode.LIGHT_DELAYED_OUT, viewModel.lightOutDelayed)
        initRadioOption(RadioNode.LIGHT_FLICKER, viewModel.lightFlicker)
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
    }

    private fun addRadioLiveDataListener() {
        viewModel.lightOutDelayed.observe(this) {
            doUpdateRadio(RadioNode.LIGHT_DELAYED_OUT, it, false)
        }
        viewModel.lightFlicker.observe(this) {
            doUpdateRadio(RadioNode.LIGHT_FLICKER, it, false)
        }
    }

    private fun setRadioListener() {
        binding.lightDelayBlackOutRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.LIGHT_DELAYED_OUT, value, viewModel.lightOutDelayed, it)
                if (value.equals("1")) {
                    binding.homeOpenIv.visibility = View.VISIBLE
                    animationHomeClose.start(
                        false,
                        50,
                        object : AnimationDrawable.AnimationLisenter {
                            override fun startAnimation() {
                            }

                            override fun endAnimation() {
                                binding.homeOpenIv.visibility = View.GONE
                            }
                        })
                } else {
                    binding.homeOpenIv.visibility = View.VISIBLE
                    animationHomeOpen.start(
                        false,
                        50,
                        object : AnimationDrawable.AnimationLisenter {
                            override fun startAnimation() {
                            }

                            override fun endAnimation() {
                                //binding.homeOpenIv.visibility = View.GONE
                            }
                        })
                }
            }
        }
        binding.lightFlickerRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.LIGHT_FLICKER, value, viewModel.lightFlicker, it)
                binding.turnSignalIv.visibility = View.VISIBLE
                animationTurnSignal.start(
                    false,
                    50,
                    object : AnimationDrawable.AnimationLisenter {
                        override fun startAnimation() {
                        }

                        override fun endAnimation() {
                            binding.homeOpenIv.visibility = View.GONE
                        }
                    })
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

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> binding.lightDelayBlackOutRadio
            RadioNode.LIGHT_FLICKER -> binding.lightFlickerRadio
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
        initSwitchOption(SwitchNode.LIGHT_OUTSIDE_MEET, viewModel.outsideLightMeet)
        initSwitchOption(SwitchNode.LIGHT_INSIDE_MEET, viewModel.insideLightMeet)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.outsideLightMeet.observe(this) {
            doUpdateSwitch(SwitchNode.LIGHT_OUTSIDE_MEET, it)
        }
        viewModel.insideLightMeet.observe(this) {
            doUpdateSwitch(SwitchNode.LIGHT_INSIDE_MEET, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.LIGHT_OUTSIDE_MEET -> binding.lightOutsideMeetSwitch
            SwitchNode.LIGHT_INSIDE_MEET -> binding.lightInsideMeetSwitch
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
        binding.lightOutsideMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LIGHT_OUTSIDE_MEET, buttonView, isChecked)
        }
        binding.lightInsideMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LIGHT_INSIDE_MEET, buttonView, isChecked)
            if (isChecked) {
                binding.welcomeLampIv.visibility = View.VISIBLE
                animationWelcomeLamp.start(
                    false,
                    50,
                    object : AnimationDrawable.AnimationLisenter {
                        override fun startAnimation() {
                        }

                        override fun endAnimation() {
                            //binding.homeOpenIv.visibility = View.GONE
                        }
                    })
            } else {
                binding.welcomeLampIv.visibility = View.GONE
            }
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

}