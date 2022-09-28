package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SeatHeatingDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeatHeatingDialogFragment :
    BaseDialogFragment<SeatViewModel, SeatHeatingDialogFragmentBinding>(), ISwitchAction {

    private val manager: SeatManager by lazy {
        SeatManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.seat_heating_dialog_fragment
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
        binding.seatHeatingStartTemperatureSeekBar.setOnSeekBarListener { seekBar, newValue ->
            manager.doSetVolume(Progress.SEAT_ONSET_TEMPERATURE, newValue)
        }
    }

    private fun addRangeLiveDataListener() {
        viewModel.sillTemp.observe(this) {
            binding.seatHeatingStartTemperatureSeekBar.setValueNoEvent(it.pos)
        }
    }

    private fun initRangeProgress() {
        binding.seatHeatingStartTemperatureSeekBar.apply {
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
        initSwitchOption(SwitchNode.SEAT_HEAT_ALL, viewModel.seatHeat)
        checkDisableOtherDiv(binding.seatAutomaticHeatingSwitch.isChecked, binding.seatHeatingLayout)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.seatHeat.observe(this) {
            doUpdateSwitch(SwitchNode.SEAT_HEAT_ALL, it)
            checkDisableOtherDiv(it, binding.seatHeatingLayout)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.SEAT_HEAT_ALL -> binding.seatAutomaticHeatingSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.seatAutomaticHeatingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.SEAT_HEAT_ALL, buttonView, isChecked)
            checkDisableOtherDiv(binding.seatAutomaticHeatingSwitch.isChecked, binding.seatHeatingLayout)
        }
    }

    private fun checkDisableOtherDiv(status: Boolean, view: View) {
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                checkDisableOtherDiv(status, child)
            }
        } else {
            view.alpha = if (status) 1.0f else 0.6f
            view.isEnabled = status
        }
    }
}