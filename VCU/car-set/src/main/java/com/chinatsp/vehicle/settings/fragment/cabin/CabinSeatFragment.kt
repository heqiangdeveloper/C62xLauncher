package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinSeatFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.CopilotGuestsDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SeatHeatingDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinSeatFragment : BaseFragment<SeatViewModel, CabinSeatFragmentBinding>() {

    private val manager:ISoundManager
        get() = SeatManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_seat_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.SEAT_MAIN_DRIVE_MEET, viewModel.mainMeet)
        updateSwitchTextHint(binding.seatForkMeetTv, viewModel.forkMeet)
        updateSwitchTextHint(binding.seatSeatHeatTv, viewModel.seatHeat)
    }

    private fun updateSwitchTextHint(textView: TextView, liveData: LiveData<Boolean>) {
        val hintId = if (liveData.value == true) {
            R.string.switch_turn_on
        } else {
            R.string.switch_turn_off
        }
        textView.setText(hintId)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.mainMeet.observe(this) {
            doUpdateSwitch(binding.seatMainMeetSwitch, it)
        }
        viewModel.forkMeet.observe(this) {
            updateSwitchTextHint(binding.seatForkMeetTv, viewModel.forkMeet)
        }
        viewModel.seatHeat.observe(this) {
            updateSwitchTextHint(binding.seatSeatHeatTv, viewModel.seatHeat)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> binding.seatMainMeetSwitch
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
        binding.seatMainMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.SEAT_MAIN_DRIVE_MEET, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }


    private fun setCheckedChangeListener() {
        binding.cabinSeatAutomaticHeating.setOnClickListener {
            val fragment = SeatHeatingDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }

        binding.cabinSeatCopilotGuests.setOnClickListener {
            val fragment = CopilotGuestsDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }
}