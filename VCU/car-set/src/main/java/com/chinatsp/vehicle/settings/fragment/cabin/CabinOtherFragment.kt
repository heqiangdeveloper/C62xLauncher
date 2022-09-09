package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinOtherFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.NoteUsersDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.TrailerRemindDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.OtherViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CabinOtherFragment : BaseFragment<OtherViewModel, CabinOtherFragmentBinding>(),
    ISwitchAction {

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
        initViewsDisplay()
        initDetailsClickListener()
    }

    private fun initViewsDisplay() {
        val levelValue = VcuUtils.getLevelValue()
        //level3, level4 配置无手机无线充电
        if (Level.LEVEL3 == levelValue || Level.LEVEL4 == levelValue) {
            binding.cabinOtherWirelessCharging.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
        if (Level.LEVEL3 == levelValue) {
            binding.cabinOtherWirelessChargingLamp.visibility = View.GONE
            binding.line4.visibility = View.GONE
        }

    }

    private fun initDetailsClickListener() {
        binding.cabinOtherTrailerRemindDetails.setOnClickListener {
            showPopWindow(R.string.cabin_other_trailer_remind_content,it)
        }
        binding.cabinOtherBatteryOptimizationDetails.setOnClickListener {
            showPopWindow(R.string.cabin_other_battery_optimization_content,it)
        }
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
        viewModel.trailerRemind.let { ld ->
            ld.observe(this) {
                updateSwitchTextHint(binding.otherTrailerRemindTextView, ld)
            }
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> binding.otherBatteryOptimizeSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING -> binding.otherWirelessChargingSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> binding.otherWirelessChargingLampSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
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

    private fun setCheckedChangeListener() {
        binding.cabinOtherTrailerRemind.setOnClickListener {
            val fragment = TrailerRemindDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
        binding.notesUsers.setOnClickListener {
            val fragment = NoteUsersDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }
    private fun showPopWindow(id:Int, view: View){
        val popWindow = PopWindow(activity,R.layout.pop_window)
        var text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDown(view)
    }
}