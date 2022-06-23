package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveForwardFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.ForwardViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveForwardFragment : BaseFragment<ForwardViewModel, DriveForwardFragmentBinding>() {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_forward_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun addSwitchLiveDataListener() {
        viewModel.fcwFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_ELECTRIC, it)
        }
        viewModel.aebFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_LIGHT_ALARM, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_FCW, viewModel.fcwFunction)
        initSwitchOption(SwitchNode.ADAS_AEB, viewModel.aebFunction)
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_FCW -> binding.adasForwardFcwSwitch
            SwitchNode.ADAS_AEB -> binding.adasForwardAebSwitch
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
        binding.adasForwardFcwSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_FCW, buttonView, isChecked)
        }
        binding.adasForwardAebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_AEB, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }
}