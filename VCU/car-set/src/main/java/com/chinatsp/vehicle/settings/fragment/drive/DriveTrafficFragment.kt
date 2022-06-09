package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.assistance.CombineManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveTrafficFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
import com.chinatsp.vehicle.settings.vm.adas.CombineViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveTrafficFragment : BaseFragment<CombineViewModel, DriveTrafficFragmentBinding>() {

    private val manager: ISwitchManager
        get() = CombineManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_traffic_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        observeSwitchLiveData()
        observeSwitchOptionChange()
    }

    private fun observeSwitchOptionChange() {
        binding.adasTrafficSlaSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_SLA, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(switchNode: SwitchNode, buttonView: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun observeSwitchLiveData() {
        viewModel.slaValue.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_SLA, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOptionStatus(
            SwitchNode.ADAS_SLA,
            viewModel.slaValue.value!!,
            true
        )
    }

    private fun updateSwitchOptionStatus(
        switchNode: SwitchNode,
        status: Boolean,
        immediately: Boolean = false
    ) {
        val switchButton = when (switchNode) {
            SwitchNode.ADAS_SLA -> {
                binding.adasTrafficSlaSwitch
            }
            else -> null
        }
        switchButton?.let {
            if (!immediately) {
                it.setCheckedNoEvent(status)
            } else {
                it.setCheckedImmediatelyNoEvent(status)
            }
        }
    }
}