package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
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
        updateEnable(binding.seatHeatingStartTemperatureSeekBar,
            true,
            binding.seatAutomaticHeatingSwitch.isChecked)

    }

    private fun addSwitchLiveDataListener() {
        viewModel.seatHeat.observe(this) {
            doUpdateSwitch(SwitchNode.SEAT_HEAT_ALL, it)
            updateEnable(binding.seatHeatingStartTemperatureSeekBar, true, it.get())
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
            updateEnable(binding.seatHeatingStartTemperatureSeekBar,
                true,
                binding.seatAutomaticHeatingSwitch.isChecked)
          if(!isChecked){
              Toast.showToast(context, getString(R.string.cabin_seat_automatic_heating_close), true)
          }
        }
    }

}