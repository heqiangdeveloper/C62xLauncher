package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinOtherFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.CopilotGuestsDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.OtherViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CabinOtherFragment : BaseFragment<OtherViewModel, CabinOtherFragmentBinding>() {

    private val manager: OtherManager
        get() = OtherManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_other_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_BATTERY_OPTIMIZE, viewModel.batteryOptimize)
        initSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING, viewModel.wirelessCharging)
        initSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, viewModel.wirelessChargingLamp)
        updateSwitchTextHint(binding.otherTrailerRemindTextView, viewModel.trailerRemind)
    }

    private fun updateSwitchTextHint(textView: TextView, liveData: LiveData<Boolean>) {
        val hintId = if (liveData.value == true) {
            R.string.switch_turn_on
        } else {
            R.string.switch_turn_off
        }
        textView.setText(hintId)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.batteryOptimize.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_BATTERY_OPTIMIZE, it)
            }
        }
        viewModel.wirelessCharging.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_WIRELESS_CHARGING, it)
            }
        }
        viewModel.wirelessChargingLamp.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, it)
            }
        }

    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> binding.otherBatteryOptimizeSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING -> binding.otherWirelessChargingSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> binding.otherWirelessChargingLampSwitch
            else -> null
        }
        swb?.let {
            doUpdateSwitch(it, status, immediately)
        }
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
    }

    private fun setSwitchListener() {
        binding.otherBatteryOptimizeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_BATTERY_OPTIMIZE, buttonView, isChecked)
        }
        binding.otherWirelessChargingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING, buttonView, isChecked)
        }
        binding.otherWirelessChargingLampSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun setCheckedChangeListener() {
        binding.cabinOtherTrailerRemind.setOnClickListener {
            val fragment = CopilotGuestsDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

}