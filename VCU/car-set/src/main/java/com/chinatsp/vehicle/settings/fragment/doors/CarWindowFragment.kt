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
import com.chinatsp.vehicle.settings.databinding.CarWindowFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.WindowViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWindowFragment : BaseFragment<WindowViewModel, CarWindowFragmentBinding>(), ISwitchAction {

    private val animationCarWindow: AnimationDrawable by lazy {
        val animDrawable = AnimationDrawable()
        animDrawable.setAnimation(context, R.drawable.car_window_animation, binding.carWindowIv)
        animDrawable
    }

    private val animationWiper: AnimationDrawable by lazy {
        val animDrawable = AnimationDrawable()
        animDrawable.setAnimation(context, R.drawable.wiper_animation, binding.carWinper)
        animDrawable
    }

    private val duration: Int get() = 50

    private var firstWiperReceive = true

    private var firstRemoteReceive = true

    private val map: HashMap<Int, View> = HashMap()

    private val manager: WindowManager
        get() = WindowManager.instance


    override fun getLayoutId(): Int {
        return R.layout.car_window_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initClickView()

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

            binding.carWindowRemoteControl.visibility = View.GONE
            binding.line1.visibility = View.GONE
        }
    }

    private fun initDetailsClickListener() {
        binding.carLockDetails.setOnClickListener(this::onViewClick)
        binding.carWiperDetails.setOnClickListener(this::onViewClick)
        binding.remoteRoseWindowDetails.setOnClickListener(this::onViewClick)
        binding.carWindowRainyDetails.setOnClickListener(this::onViewClick)
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
            binding.carWindowRainyDetails -> {
                showPopWindow(R.string.car_window_rainy_content, it)
            }
        }
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
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
            executeWindowAnimation(it.get())
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
            executeWiperAnimation(it.get())
        }
    }

    private fun executeWiperAnimation(status: Boolean) {
        if (firstWiperReceive) {
            firstWiperReceive = false
            binding.carWinper.visibility = if (status) View.VISIBLE else View.INVISIBLE
            return
        }
        executeAnimation(binding.carWinper, animationWiper, status)
    }

    private fun executeAnimation(view: View, animation: AnimationDrawable, status: Boolean) {
        animation.stop()
        view.visibility = if (status) View.VISIBLE else View.INVISIBLE
        if (status) {
            animation.start(false, duration, null)
        }
    }

    private fun executeWindowAnimation(status: Boolean) {
        if (firstRemoteReceive) {
            firstRemoteReceive = false
            binding.carWindowIv.visibility = if (status) View.VISIBLE else View.INVISIBLE
            return
        }
        executeAnimation(binding.carWindowIv, animationCarWindow, status)
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
            executeAnimation(binding.carWindowIv, animationCarWindow, buttonView.isChecked)
        }
        binding.carWindowLockCarSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.WIN_CLOSE_FOLLOW_LOCK, buttonView, isChecked)
        }
        binding.carWindowRainyDaySwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.WIN_CLOSE_WHILE_RAIN, buttonView, isChecked)
        }
        binding.carWindowWiperSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.RAIN_WIPER_REPAIR, buttonView, isChecked)
            executeAnimation(binding.carWinper, animationWiper, buttonView.isChecked)
        }
    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow: PopWindow
        if (view.id == binding.carWiperDetails.id) {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao451_424
                    )
                })
            popWindow.showDownLift(view, 30, -170)
        } else if (view.id == binding.remoteRoseWindowDetails.id) {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172_2
                    )
                })
            popWindow.showDownLift(view, 30, -80)
        } else if (view.id == binding.carWindowRainyDetails.id) {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao451_424
                    )
                })
            if (VcuUtils.getLanguage() == 1) {
                popWindow.showDownLift(view, 30, -200)
            } else {
                popWindow.showDownLift(view, 30, -180)
            }

        } else {
            popWindow = PopWindow(activity, R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172_3
                    )
                })
            popWindow.showDownLift(view, 30, -80)
        }
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)

    }
}

