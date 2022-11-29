package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarDoorsFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
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
        initDetailsClickListener()

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
        /* if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, Level.LEVEL5)) {
             //lv3\lv4\lv5智能钥匙版本有车门智能进入功能
             binding.wheelAutomaticHeating.visibility = View.VISIBLE
             binding.line3.visibility = View.VISIBLE
         }*/
    }

    private fun initDetailsClickListener() {
        binding.wheelAutomaticHeatingDetails.setOnClickListener {
            showPopWindow(R.string.wheel_automatic_heating_content, it)
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
        syncNfcDisplay(timely = true)
    }

    private fun syncNfcDisplay(timely: Boolean = true) {
        val inner = viewModel.nfcInner.value?.get() ?: false
        val outer = viewModel.nfcOuter.value?.get() ?: false
        if (!timely) {
            binding.nfcSwitch.setCheckedNoEvent(inner && outer)
        } else {
            binding.nfcSwitch.setCheckedImmediatelyNoEvent(inner && outer)
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.smartDoorAccess.observe(this) {
            doUpdateSwitch(SwitchNode.DOOR_SMART_ENTER, it)
        }
        viewModel.nfcInner.observe(this) {
            syncNfcDisplay(timely = true)
        }
        viewModel.nfcOuter.observe(this) {
            syncNfcDisplay(timely = true)
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

    private fun showPopWindow(id: Int, view: View) {
        var popWindow: PopWindow? = null
        if (view.id == binding.wheelAutomaticHeatingDetails.id) {
            popWindow = PopWindow(activity, R.layout.car_doors_pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao451_214
                    )
                })
            popWindow.showDownLift(view, 30, -140)
        }
        var text: TextView = popWindow?.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)

    }
}