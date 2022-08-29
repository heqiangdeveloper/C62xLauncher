package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CopilotGuestsDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CopilotGuestsDialogFragment :
    BaseDialogFragment<SeatViewModel, CopilotGuestsDialogFragmentBinding>(), ISwitchAction {

    private val manager: SeatManager by lazy {
        SeatManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.copilot_guests_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.SEAT_FORK_DRIVE_MEET, viewModel.forkMeet)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.forkMeet.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_DOW, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.SEAT_FORK_DRIVE_MEET -> binding.seatCopilotGuestsSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.seatCopilotGuestsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.SEAT_FORK_DRIVE_MEET, buttonView, isChecked)
        }
    }

}