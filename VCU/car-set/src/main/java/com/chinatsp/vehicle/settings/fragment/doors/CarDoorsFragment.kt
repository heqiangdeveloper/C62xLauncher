package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarDoorsFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarDoorsFragment : BaseFragment<DoorsViewModel, CarDoorsFragmentBinding>(), IOptionAction {

    //private var animationOpenLock: AnimationDrawable = AnimationDrawable()
    //private var animationCloseLock: AnimationDrawable = AnimationDrawable()
    private var animationFlameout: AnimationDrawable = AnimationDrawable()
    private var animationCarDoor: AnimationDrawable = AnimationDrawable()

    private val manager: IOptionManager
        get() = DoorManager.instance

    override fun getLayoutId(): Int {
        return R.layout.car_doors_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initViewsDisplay()
        initAnimation()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        updateOptionActive()
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.DOOR_SMART_ENTER)
        updateRadioEnable(RadioNode.DOOR_DRIVE_LOCK)
        updateRadioEnable(RadioNode.DOOR_FLAMEOUT_UNLOCK)
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, Level.LEVEL5)) {
            //lv4跟lv5智能钥匙版本有车门智能进入功能
            binding.wheelAutomaticHeating.visibility = View.VISIBLE
            binding.line3.visibility = View.VISIBLE
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DOOR_DRIVE_LOCK, viewModel.automaticDoorLock)
        initRadioOption(RadioNode.DOOR_FLAMEOUT_UNLOCK, viewModel.automaticDoorUnlock)
    }

    private fun initAnimation() {
        val cxt = activity
        //animationOpenLock.setAnimation(cxt, R.drawable.lock_animation, binding.lockIv)
        //animationCloseLock.setAnimation(cxt, R.drawable.close_lock_animation, binding.lockIv)
        animationFlameout.setAnimation(cxt, R.drawable.flameout_animation, binding.rightFlameout)
        animationCarDoor.setAnimation(cxt, R.drawable.car_door_animation, binding.rightCarDoorlock)
    }

    private fun addRadioLiveDataListener() {
        viewModel.automaticDoorLock.observe(this) {
            doUpdateRadio(RadioNode.DOOR_DRIVE_LOCK, it, false)
//            updateOptionActive()
        }
        viewModel.automaticDoorUnlock.observe(this) {
            doUpdateRadio(RadioNode.DOOR_FLAMEOUT_UNLOCK, it, false)
//            updateOptionActive()
            setAnimation(it.data.toString())
        }
    }

    private fun setRadioListener() {
        binding.doorAutomaticLockRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.DOOR_DRIVE_LOCK, value, viewModel.automaticDoorLock, it)
            }
        }
        binding.doorAutomaticUnlockRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(
                    RadioNode.DOOR_FLAMEOUT_UNLOCK, value, viewModel.automaticDoorUnlock, it
                )
                setAnimation(value)
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DOOR_SMART_ENTER, viewModel.smartDoorAccess)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.smartDoorAccess.observe(this) {
            doUpdateSwitch(SwitchNode.DOOR_SMART_ENTER, it)
//            updateOptionActive()
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> binding.doorSmartAccessSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> binding.doorAutomaticLockRadio
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> binding.doorAutomaticUnlockRadio
            else -> null
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> viewModel.automaticDoorLock.value?.enable() ?: false
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> viewModel.automaticDoorUnlock.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.DOOR_SMART_ENTER -> viewModel.smartDoorAccess.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }


    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.doorSmartAccessSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DOOR_SMART_ENTER, buttonView, isChecked)
        }
    }

    private fun setAnimation(value: String) {
        when (value) {
            "3" -> {
                binding.rightCarDoorlock.visibility = View.GONE
                binding.rightFlameout.visibility = View.GONE
                //animationCloseLock.start(false, 50, null)
            }
            "1" -> {
                binding.rightCarDoorlock.visibility = View.GONE
                binding.rightFlameout.visibility = View.VISIBLE
                //animationOpenLock.start(false, 50, null)
                animationFlameout.start(false, 50, null)
            }
            else -> {
                binding.rightCarDoorlock.visibility = View.VISIBLE
                binding.rightFlameout.visibility = View.GONE
                //animationOpenLock.start(false, 50, null)
                animationCarDoor.start(false, 50, null)
            }
        }
    }
}