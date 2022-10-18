package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.ShellUtils
import com.chinatsp.vehicle.settings.databinding.CarWindowFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.WindowViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWindowFragment : BaseFragment<WindowViewModel, CarWindowFragmentBinding>(), ISwitchAction {

    private var animationCarWindow: AnimationDrawable = AnimationDrawable()

    private var animationWiper: AnimationDrawable = AnimationDrawable()

    private val map: HashMap<Int, View> = HashMap()

    private val manager: WindowManager
        get() = WindowManager.instance


    override fun getLayoutId(): Int {
        return R.layout.car_window_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

        initClickView()

        initAnimation()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initViewDisplay()
        initDetailsClickListener()
        updateOptionActive()

        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.remoteRoseWindowDetails
        map[2] = binding.carLockDetails
        map[3] = binding.carWiperDetails
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                it.takeIf { it.valid && it.uid == pid }?.let { level1 ->
                    level1.cnode?.takeIf { child -> child.valid && child.uid == uid }.let { level2 ->
                        level2?.cnode?.let { lv3Node ->
                            map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid, true) }
                        }
                    }
                }
            }
        }
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.WIN_REMOTE_CONTROL)
        updateSwitchEnable(SwitchNode.WIN_CLOSE_FOLLOW_LOCK)
        updateSwitchEnable(SwitchNode.WIN_CLOSE_WHILE_RAIN)
        updateSwitchEnable(SwitchNode.RAIN_WIPER_REPAIR)
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
        binding.carLockDetails.setOnClickListener(this::onViewClick)
        binding.carWiperDetails.setOnClickListener(this::onViewClick)
        binding.remoteRoseWindowDetails.setOnClickListener(this::onViewClick)
    }

    private fun onViewClick(view: View, clickUid: Int, frank: Boolean) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.carLockDetails -> {
                showPopWindow(R.string.car_window_lock_car_content, it)
            }
            binding.carWiperDetails -> {
                showPopWindow(R.string.car_window_wiper_content, it)
            }
            binding.remoteRoseWindowDetails -> {
                showPopWindow(R.string.car_window_lock_content, it)
            }
        }
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initAnimation() {
        val cxt = activity
        animationCarWindow.setAnimation(cxt, R.drawable.car_window_animation, binding.carWindowIv)
        animationWiper.setAnimation(cxt, R.drawable.wiper_animation, binding.carWinper)
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
            updateSwitchEnable(SwitchNode.WIN_REMOTE_CONTROL)
        }
        viewModel.closeWinFollowLock.observe(this) {
            doUpdateSwitch(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, it)
            updateSwitchEnable(SwitchNode.WIN_CLOSE_FOLLOW_LOCK)
        }
        viewModel.closeWinWhileRain.observe(this) {
            doUpdateSwitch(SwitchNode.WIN_CLOSE_WHILE_RAIN, it)
            updateSwitchEnable(SwitchNode.WIN_CLOSE_WHILE_RAIN)
        }
        viewModel.rainWiperRepair.observe(this) {
            doUpdateSwitch(SwitchNode.RAIN_WIPER_REPAIR, it)
            updateSwitchEnable(SwitchNode.RAIN_WIPER_REPAIR)
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

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.WIN_REMOTE_CONTROL -> viewModel.winRemoteControl.value?.enable() ?: false
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> viewModel.closeWinFollowLock.value?.enable()
                ?: false
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> viewModel.closeWinWhileRain.value?.enable() ?: false
            SwitchNode.RAIN_WIPER_REPAIR -> viewModel.rainWiperRepair.value?.enable() ?: false
            else -> false
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

    private fun showPopWindow(id: Int, view: View) {
        val popWindow: PopWindow
        if (view.id == binding.carWiperDetails.id) {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(it,
                        R.drawable.popup_bg_qipao172_1)
                })
            popWindow.showDownLift(view, 30, -340)
        } else if (view.id == binding.remoteRoseWindowDetails.id) {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(it,
                        R.drawable.popup_bg_qipao172_2)
                })
            popWindow.showDownLift(view, 30, -80)
        } else {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(it,
                        R.drawable.popup_bg_qipao172_3)
                })
            popWindow.showDownLift(view, 30, -80)
        }
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)

    }
}