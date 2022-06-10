package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import com.chinatsp.settinglib.manager.assistance.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveForwardFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.ForwardViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveForwardFragment:BaseFragment<ForwardViewModel,DriveForwardFragmentBinding>() {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_forward_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        observeSwitchLiveData()

        observeSwitchOptionChange()
    }

    private fun observeSwitchOptionChange() {
        binding.adasForwardFcwSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_FCW, buttonView, isChecked)
        }
        binding.adasForwardAebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_AEB, buttonView, isChecked)
        }
    }



    private fun doUpdateSwitchOption(switchNode: SwitchNode, buttonView: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }


    private fun observeSwitchLiveData() {
        viewModel.fcwFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_FCW, it)
        }
        viewModel.aebFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_AEB, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOptionStatus(
            SwitchNode.ADAS_FCW,
            viewModel.fcwFunction.value!!,
            true
        )
        updateSwitchOptionStatus(
            SwitchNode.ADAS_AEB,
            viewModel.aebFunction.value!!,
            true
        )
    }

    private fun updateSwitchOptionStatus(
        switchNode: SwitchNode,
        status: Boolean,
        immediately: Boolean = false
    ) {
        val switchButton = when (switchNode) {
            SwitchNode.ADAS_FCW -> {
                binding.adasForwardFcwSwitch
            }
            SwitchNode.ADAS_AEB -> {
                binding.adasForwardAebSwitch
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