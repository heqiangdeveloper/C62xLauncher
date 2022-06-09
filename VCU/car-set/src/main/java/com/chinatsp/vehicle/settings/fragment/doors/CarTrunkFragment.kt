package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.widget.CompoundButton
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarTrunkFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.SternDoorViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarTrunkFragment : BaseFragment<SternDoorViewModel, CarTrunkFragmentBinding>() {

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    override fun getLayoutId(): Int {
        return R.layout.car_trunk_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptions()
        observeSwitchLiveData()

        observeRadioOptionChange()
        observeSwitchOptionChange()
    }

    private fun observeSwitchOptionChange() {
        binding.accessSternElectricSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AS_STERN_ELECTRIC, buttonView, isChecked)
        }
        binding.accessSternLightAlarmSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AS_STERN_LIGHT_ALARM, buttonView, isChecked)
        }
        binding.accessSternAudioAlarmSw.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AS_STERN_AUDIO_ALARM, buttonView, isChecked)
        }
    }

    private fun observeRadioOptionChange() {
        binding.accessSternSmartEnterRadio.setOnTabSelectionChangedListener { _, value ->
            val result = isCanToInt(value) && manager.doSetRadioOption(
                RadioNode.ACCESS_STERN_SMART_ENTER, value.toInt()
            )
            if (!result) {
                val oldValue = viewModel.sternSmartEnterFunction.value
                binding.accessSternSmartEnterRadio.setSelection(oldValue.toString(), false)
            }
        }
    }

    private fun doUpdateSwitchOption(switchNode: SwitchNode, buttonView: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }


    private fun observeSwitchLiveData() {
        viewModel.electricFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.AS_STERN_ELECTRIC, it)
        }
        viewModel.lightAlarmFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.AS_STERN_LIGHT_ALARM, it)
        }
        viewModel.audioAlarmFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.AS_STERN_AUDIO_ALARM, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOptionStatus(
            SwitchNode.AS_STERN_ELECTRIC,
            viewModel.electricFunction.value!!,
            true
        )
        updateSwitchOptionStatus(
            SwitchNode.AS_STERN_LIGHT_ALARM,
            viewModel.lightAlarmFunction.value!!,
            true
        )
        updateSwitchOptionStatus(
            SwitchNode.AS_STERN_AUDIO_ALARM,
            viewModel.audioAlarmFunction.value!!,
            true
        )
    }

    private fun updateSwitchOptionStatus(
        switchNode: SwitchNode,
        status: Boolean,
        immediately: Boolean = false
    ) {
        val switchButton = when (switchNode) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                binding.accessSternElectricSw
            }
            SwitchNode.AS_STERN_LIGHT_ALARM -> {
                binding.accessSternLightAlarmSw
            }
            SwitchNode.AS_STERN_AUDIO_ALARM -> {
                binding.accessSternAudioAlarmSw
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