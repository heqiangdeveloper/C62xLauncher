package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
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
class CabinSeatFragment : BaseFragment<SeatViewModel, CabinSeatFragmentBinding>(), ISwitchAction {

    private val manager: ISoundManager
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

    private fun updateSwitchTextHint(textView: TextView, liveData: LiveData<SwitchState>) {
        val static = liveData.value?.get() ?: false
        val hintId = if (static) {
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

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> binding.seatMainMeetSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.seatMainMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.SEAT_MAIN_DRIVE_MEET, buttonView, isChecked)
        }
    }

    private fun setCheckedChangeListener() {
        binding.cabinSeatAutomaticHeating.setOnClickListener {
            activity?.supportFragmentManager?.let {
                showDialogFragment(SeatHeatingDialogFragment())
            }
        }

        binding.cabinSeatCopilotGuests.setOnClickListener {
            activity?.supportFragmentManager?.let {
                showDialogFragment(CopilotGuestsDialogFragment())
            }
        }
    }

}