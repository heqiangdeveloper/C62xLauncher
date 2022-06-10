package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.assistance.LaneManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLaneFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.LaneViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLaneFragment : BaseFragment<LaneViewModel, DriveLaneFragmentBinding>() {

    private val manager: LaneManager
        get() = LaneManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_lane_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        initRadioOptions()

        observeRadioLiveData()
        observeSwitchLiveData()

        observeRadioOptionChange()
        observeSwitchOptionChange()
    }

    private fun observeRadioLiveData() {
        viewModel.laneAssistMode.observe(this) {
            updateRadioOption(RadioNode.ADAS_LANE_ASSIST_MODE, it, false)
        }
        viewModel.ldwWarningStyle.observe(this) {
            updateRadioOption(RadioNode.ADAS_LDW_STYLE, it, false)
        }
        viewModel.ldwWarningSensitivity.observe(this) {
            updateRadioOption(RadioNode.ADAS_LDW_SENSITIVITY, it, false)
        }
    }

    private fun observeSwitchOptionChange() {
        binding.adasLaneLaneAssistSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_LANE_ASSIST, buttonView, isChecked)
        }
    }

    private fun observeRadioOptionChange() {
        listeningRadioOption(
            binding.adasLaneLaneAssistRadio,
            viewModel.laneAssistMode,
            RadioNode.ADAS_LANE_ASSIST_MODE
        )
        listeningRadioOption(
            binding.adasLaneLdwStyleRadio,
            viewModel.ldwWarningStyle,
            RadioNode.ADAS_LDW_STYLE
        )
        listeningRadioOption(
            binding.adasLaneLdwSensitivityRadio,
            viewModel.ldwWarningSensitivity,
            RadioNode.ADAS_LDW_SENSITIVITY
        )

    }

    private fun listeningRadioOption(
        tabView: TabControlView,
        liveData: LiveData<Int>,
        radioNode: RadioNode
    ) {
        tabView.setOnTabSelectionChangedListener { _, value ->
            val result = isCanToInt(value) && manager.doSetRadioOption(radioNode, value.toInt())
            if (!result) {
                val oldValue = liveData.value!!
//                tabView.setSelection(oldValue.toString(), false)
                updateRadioOption(radioNode, oldValue, false)
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
        viewModel.laneAssistFunction.observe(this) {
            updateSwitchOption(SwitchNode.ADAS_LANE_ASSIST, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOption(SwitchNode.ADAS_LANE_ASSIST, viewModel.laneAssistFunction.value!!, true)
    }

    private fun initRadioOptions() {
        updateRadioOption(RadioNode.ADAS_LANE_ASSIST_MODE, viewModel.laneAssistMode.value!!, true)
        updateRadioOption(RadioNode.ADAS_LDW_STYLE, viewModel.ldwWarningStyle.value!!, true)
        updateRadioOption(
            RadioNode.ADAS_LDW_SENSITIVITY,
            viewModel.ldwWarningSensitivity.value!!,
            true
        )
    }

    private fun updateSwitchOption(node: SwitchNode, value: Boolean, immediately: Boolean = false) {
        val switchButton = when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                binding.adasLaneLaneAssistSwitch
            }
            else -> null
        }
        switchButton?.let {
            if (!immediately) {
                it.setCheckedNoEvent(value)
            } else {
                it.setCheckedImmediatelyNoEvent(value)
            }
        }
    }

    private fun updateRadioOption(node: RadioNode, value: Int, immediately: Boolean = false) {
        val tabView = when (node) {
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                binding.adasLaneLaneAssistRadio
            }
            RadioNode.ADAS_LDW_STYLE -> {
                binding.adasLaneLdwStyleRadio
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                binding.adasLaneLdwSensitivityRadio
            }
            else -> null
        }
        tabView?.let {
            it.setSelection(value.toString(), false)
        }
    }
}

