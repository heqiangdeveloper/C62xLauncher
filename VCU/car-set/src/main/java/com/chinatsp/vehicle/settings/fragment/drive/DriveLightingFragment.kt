package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.assistance.CombineManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLightingFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.CombineViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLightingFragment:BaseFragment<CombineViewModel,DriveLightingFragmentBinding>() {


    private val manager: ISwitchManager
        get() = CombineManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_lighting_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        observeSwitchLiveData()
        observeSwitchOptionChange()
    }

    private fun observeSwitchOptionChange() {
        binding.adasLightHmaSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_HMA, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(switchNode: SwitchNode, buttonView: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun observeSwitchLiveData() {
        viewModel.hmaValue.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_HMA, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOptionStatus(
            SwitchNode.ADAS_HMA,
            viewModel.hmaValue.value!!,
            true
        )
    }

    private fun updateSwitchOptionStatus(
        switchNode: SwitchNode,
        status: Boolean,
        immediately: Boolean = false
    ) {
        val switchButton = when (switchNode) {
            SwitchNode.ADAS_HMA -> {
                binding.adasLightHmaSwitch
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