package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarDoorsFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarDoorsFragment : BaseFragment<DoorsViewModel, CarDoorsFragmentBinding>() {
    private var animationOpenLock: AnimationDrawable = AnimationDrawable()
    private var animationCloseLock: AnimationDrawable = AnimationDrawable()
    private var animationFlameout: AnimationDrawable = AnimationDrawable()
    private var animationCarDoor: AnimationDrawable = AnimationDrawable()
    private val manager: IOptionManager
        get() = DoorManager.instance

    override fun getLayoutId(): Int {
        return R.layout.car_doors_fragment
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
        initRadioOption(RadioNode.DOOR_DRIVE_LOCK, viewModel.automaticDoorLock)
        initRadioOption(RadioNode.DOOR_FLAMEOUT_UNLOCK, viewModel.automaticDoorUnlock)
    }

    private fun initAnimation() {
        animationOpenLock.setAnimation(
            activity,
            R.drawable.lock_animation,
            binding.lockIv
        )
        animationCloseLock.setAnimation(
            activity,
            R.drawable.close_lock_animation,
            binding.lockIv
        )
        animationFlameout.setAnimation(
            activity,
            R.drawable.flameout_animation,
            binding.rightFlameout
        )
        animationCarDoor.setAnimation(
            activity,
            R.drawable.car_door_animation,
            binding.rightCarDoorlock
        )
    }

    private fun addRadioLiveDataListener() {
        viewModel.automaticDoorLock.observe(this) {
            doUpdateRadio(RadioNode.DOOR_DRIVE_LOCK, it, false)
        }
        viewModel.automaticDoorUnlock.observe(this) {
            doUpdateRadio(RadioNode.DOOR_FLAMEOUT_UNLOCK, it, false)
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
                    RadioNode.DOOR_FLAMEOUT_UNLOCK,
                    value,
                    viewModel.automaticDoorUnlock,
                    it
                )
                if (value.equals("3")) {
                    binding.rightCarDoorlock.visibility = View.GONE
                    binding.rightFlameout.visibility = View.GONE
                    animationCloseLock.start(false, 50, null)
                } else if (value.equals("1")) {
                    binding.rightCarDoorlock.visibility = View.GONE
                    binding.rightFlameout.visibility = View.VISIBLE
                    animationOpenLock.start(false, 50, null)
                    animationFlameout.start(false, 50, null)
                } else {
                    binding.rightCarDoorlock.visibility = View.VISIBLE
                    binding.rightFlameout.visibility = View.GONE
                    animationCarDoor.start(false, 50, null)
                }
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value)
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

    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false) {
        val tabView = when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> binding.doorAutomaticLockRadio
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> binding.doorAutomaticUnlockRadio
            else -> null
        }
        takeIf { null != tabView }?.doUpdateRadio(tabView!!, value, immediately)
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }


    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DOOR_SMART_ENTER, viewModel.smartDoorAccess)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.smartDoorAccess.observe(this) {
            doUpdateSwitch(SwitchNode.DOOR_SMART_ENTER, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.DOOR_SMART_ENTER -> binding.doorSmartAccessSwitch
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
        binding.doorSmartAccessSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DOOR_SMART_ENTER, buttonView, isChecked)
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