package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import com.chinatsp.settinglib.manager.assistance.CruiseManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveIntelligentFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.CruiseViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveIntelligentFragment : BaseFragment<CruiseViewModel, DriveIntelligentFragmentBinding>() {

    private val manager: CruiseManager
        get() = CruiseManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_intelligent_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        observeSwitchLiveData()

        observeRadioOptionChange()
        observeSwitchOptionChange()
    }

    private fun observeSwitchOptionChange() {
        binding.accessCruiseCruiseAssist.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_CRUISE_ASSIST, buttonView, isChecked)
        }
        binding.accessCruiseTargetPrompt.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_TARGET_PROMPT, buttonView, isChecked)
        }
        binding.accessCruiseLimberLeave.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, buttonView, isChecked)
        }
    }

    private fun observeRadioOptionChange() {
        binding.accessCruiseLimberLeaveRadio.setOnTabSelectionChangedListener { _, value ->
            val result = isCanToInt(value) && manager.doSetRadioOption(
                RadioNode.ACCESS_STERN_SMART_ENTER, value.toInt()
            )
            if (!result) {
                val oldValue = viewModel.limberLeaveRadio.value
                binding.accessCruiseLimberLeaveRadio.setSelection(oldValue.toString(), false)
            }
        }
    }

    private fun doUpdateSwitchOption(
        switchNode: SwitchNode,
        buttonView: CompoundButton,
        status: Boolean
    ) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }


    private fun observeSwitchLiveData() {
        viewModel.cruiseAssistFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_CRUISE_ASSIST, it)
        }
        viewModel.targetPromptFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_TARGET_PROMPT, it)
        }
        viewModel.limberLeaveFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.ADAS_LIMBER_LEAVE, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOptionStatus(
            SwitchNode.ADAS_CRUISE_ASSIST,
            viewModel.cruiseAssistFunction.value!!,
            true
        )
        updateSwitchOptionStatus(
            SwitchNode.ADAS_TARGET_PROMPT,
            viewModel.targetPromptFunction.value!!,
            true
        )
        updateSwitchOptionStatus(
            SwitchNode.ADAS_LIMBER_LEAVE,
            viewModel.limberLeaveFunction.value!!,
            true
        )
    }

    private fun updateSwitchOptionStatus(
        switchNode: SwitchNode,
        status: Boolean,
        immediately: Boolean = false
    ) {
        val switchButton = when (switchNode) {
            SwitchNode.ADAS_CRUISE_ASSIST -> {
                binding.accessCruiseCruiseAssist
            }
            SwitchNode.ADAS_TARGET_PROMPT -> {
                binding.accessCruiseTargetPrompt
            }
            SwitchNode.ADAS_LIMBER_LEAVE -> {
                binding.accessCruiseLimberLeave
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

