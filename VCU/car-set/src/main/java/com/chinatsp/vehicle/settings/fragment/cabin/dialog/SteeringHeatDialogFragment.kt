package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SteeringHeatingDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SteeringViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SteeringHeatDialogFragment :
    BaseDialogFragment<SteeringViewModel, SteeringHeatingDialogFragmentBinding>() {

    val manager: WheelManager by lazy {
        WheelManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.steering_heating_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRangeProgress()
        addRangeLiveDataListener()
        setRangeListener()
    }

    private fun setRangeListener() {
        binding.steeringHeatingStartTemperatureSeekBar.setOnSeekBarListener { seekBar, newValue ->
            manager.doSetVolume(Volume.Type.STEERING_SILL_TEMP, newValue)
        }
    }

    private fun addRangeLiveDataListener() {
        viewModel.sillTemp.observe(this) {
            binding.steeringHeatingStartTemperatureSeekBar.setValueNoEvent(it.pos)
        }
    }

    private fun initRangeProgress() {
        binding.steeringHeatingStartTemperatureSeekBar.apply {
            val volume = viewModel.sillTemp.value
            volume?.let {
                this.min = it.min
                this.max = it.max
                this.setValueNoEvent(it.pos)
            }
        }
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, viewModel.swhFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.swhFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_DOW, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> binding.steeringAutomaticHeatingSwitch
            else -> null
        }
        takeIf { null != swb }?.doUpdateSwitch(swb!!, status, immediately)
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
    }

    private fun setSwitchListener() {
        binding.steeringAutomaticHeatingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

}

