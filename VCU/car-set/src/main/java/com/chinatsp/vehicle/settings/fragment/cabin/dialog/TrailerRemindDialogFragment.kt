package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.settinglib.listener.IThemeChangeListener
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.service.ThemeService
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.TrailerRemindDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.TrailerViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrailerRemindDialogFragment :
    BaseDialogFragment<TrailerViewModel, TrailerRemindDialogFragmentBinding>(),
    IThemeChangeListener, IOptionAction {

    private lateinit var service: ThemeService

    override fun getLayoutId(): Int {
        return R.layout.trailer_remind_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
        initService()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initService() {
        service = ThemeService(activity)
        service.addListener("TrailerRemindDialog", this)
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service.removeListener("TrailerRemindDialog")
    }

    override fun onChange(night: Boolean) {

    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DEVICE_TRAILER_DISTANCE, viewModel.distance)
        initRadioOption(RadioNode.DEVICE_TRAILER_SENSITIVITY, viewModel.sensitivity)
    }

    private fun addRadioLiveDataListener() {
        viewModel.distance.observe(this) {
            doUpdateRadio(RadioNode.DEVICE_TRAILER_DISTANCE, it, false)
        }
        viewModel.sensitivity.observe(this) {
            doUpdateRadio(RadioNode.DEVICE_TRAILER_SENSITIVITY, it, false)
        }
    }

    private fun setRadioListener() {
        binding.trailerDistanceRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.DEVICE_TRAILER_DISTANCE
                doUpdateRadio(node, value, viewModel.distance, it)
            }
        }
        binding.trailerSensitivityRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
                doUpdateRadio(node, value, viewModel.sensitivity, it)
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_TRAILER_REMIND, viewModel.trailerFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.trailerFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_TRAILER_REMIND, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> binding.trailerRemindSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return OtherManager.instance
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> binding.trailerDistanceRadio
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> binding.trailerSensitivityRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return OtherManager.instance
    }

    private fun setSwitchListener() {
        binding.trailerRemindSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_TRAILER_REMIND, buttonView, isChecked)
        }
    }

}