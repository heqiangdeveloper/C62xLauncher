package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarWindowFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.accress.WindowViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.popupwindow.easypopup.EasyPopup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWindowFragment : BaseFragment<WindowViewModel, CarWindowFragmentBinding>(), ISwitchAction {

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
        initDetailsClickListener()
    }

    private fun initViewDisplay() {

        if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
            binding.carWindowRainyDay.visibility = View.GONE
            binding.line3.visibility = View.GONE

            binding.carWindowLockCar.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun initDetailsClickListener() {
        binding.remoteRoseWindowDetails.setOnClickListener {
            showPopWindow(R.string.car_window_lock_content,it)
        }
        binding.carLockDetails.setOnClickListener{
            showPopWindow(R.string.car_window_lock_car_content,it)
        }
        binding.carWiperDetails.setOnClickListener{
            showPopWindow(R.string.car_window_wiper_content,it)
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

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.WIN_REMOTE_CONTROL -> binding.carWindowRemoteControlSwb
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> binding.carWindowLockCarSwb
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> binding.carWindowRainyDaySwb
            SwitchNode.RAIN_WIPER_REPAIR -> binding.carWindowWiperSwb
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
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

    private fun showPopWindow(id:Int, view:View){
        val popWindow = PopWindow(activity,R.layout.pop_window)
        var text:TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDown(view)
    }
}