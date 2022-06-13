package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
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
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        setRadioListener()
    }

    private fun setSwitchListener() {
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

    private fun setRadioListener() {
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

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }


    private fun addSwitchLiveDataListener() {
        viewModel.electricFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_ELECTRIC, it)
        }
        viewModel.lightAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_LIGHT_ALARM, it)
        }
        viewModel.audioAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_AUDIO_ALARM, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AS_STERN_ELECTRIC, viewModel.electricFunction,)
        initSwitchOption(SwitchNode.AS_STERN_LIGHT_ALARM, viewModel.lightAlarmFunction,)
        initSwitchOption(SwitchNode.AS_STERN_AUDIO_ALARM, viewModel.audioAlarmFunction,)
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
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

}