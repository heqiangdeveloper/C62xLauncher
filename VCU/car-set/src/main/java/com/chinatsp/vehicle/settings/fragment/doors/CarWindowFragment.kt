package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarWindowFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.WindowViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWindowFragment : BaseFragment<WindowViewModel, CarWindowFragmentBinding>() {

    private var animationCarWindow: AnimationDrawable = AnimationDrawable()

    private var animationWiper: AnimationDrawable = AnimationDrawable()

    private val manager: WindowManager
        get() = WindowManager.instance


    override fun getLayoutId(): Int {
        return R.layout.car_window_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initAnimation()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initViewDisplay()
    }

    private fun initViewDisplay() {

        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.carWindowRainyDay.visibility = View.GONE
            binding.line3.visibility = View.GONE

            binding.carWindowLockCar.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun initAnimation() {
        animationCarWindow.setAnimation(
            activity,
            R.drawable.car_window_animation,
            binding.carWindowIv
        )
        animationWiper.setAnimation(
            activity,
            R.drawable.wiper_animation,
            binding.carWinper
        )
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.WIN_REMOTE_CONTROL, viewModel.winRemoteControl)
        initSwitchOption(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, viewModel.closeWinFollowLock)
        initSwitchOption(SwitchNode.WIN_CLOSE_WHILE_RAIN, viewModel.closeWinWhileRain)
        initSwitchOption(SwitchNode.RAIN_WIPER_REPAIR, viewModel.rainWiperRepair)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.winRemoteControl.observe(this) {
            doUpdateSwitch(SwitchNode.WIN_REMOTE_CONTROL, it)
        }
        viewModel.closeWinFollowLock.observe(this) {
            doUpdateSwitch(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, it)
        }
        viewModel.closeWinWhileRain.observe(this) {
            doUpdateSwitch(SwitchNode.WIN_CLOSE_WHILE_RAIN, it)
        }
        viewModel.rainWiperRepair.observe(this) {
            doUpdateSwitch(SwitchNode.RAIN_WIPER_REPAIR, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.WIN_REMOTE_CONTROL -> binding.carWindowRemoteControlSwb
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> binding.carWindowLockCarSwb
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> binding.carWindowRainyDaySwb
            SwitchNode.RAIN_WIPER_REPAIR -> binding.carWindowWiperSwb
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
        binding.carWindowRemoteControlSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.WIN_REMOTE_CONTROL, buttonView, isChecked)
            if (buttonView.isChecked) {
                binding.carWindowIv.visibility = View.VISIBLE
                animationCarWindow.start(false, 50, null)
            } else {
                binding.carWindowIv.visibility = View.GONE
            }
        }
        binding.carWindowLockCarSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, buttonView, isChecked)
        }
        binding.carWindowRainyDaySwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.WIN_CLOSE_WHILE_RAIN, buttonView, isChecked)
        }
        binding.carWindowWiperSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.RAIN_WIPER_REPAIR, buttonView, isChecked)
            if (buttonView.isChecked) {
                binding.carWinper.visibility = View.VISIBLE
                animationWiper.start(false, 50, null)
            } else {
                binding.carWinper.visibility = View.GONE
            }
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

}