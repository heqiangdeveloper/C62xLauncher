package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinSeatFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.CopilotGuestsDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SeatHeatingDialogFragment
import com.chinatsp.vehicle.settings.vm.CabinACViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinSeatFragment : BaseFragment<CabinACViewModel, CabinSeatFragmentBinding>() {
    var seatHeatingDialogFragment: SeatHeatingDialogFragment? = null
    var copilotGuestsDialogFragment: CopilotGuestsDialogFragment? = null
    override fun getLayoutId(): Int {
        return R.layout.cabin_seat_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
    }

    private fun setCheckedChangeListener() {
        binding.cabinSeatAutomaticHeating.setOnClickListener {
            seatHeatingDialogFragment = SeatHeatingDialogFragment()
            activity?.let { it1 ->
                seatHeatingDialogFragment!!.show(
                    it1.supportFragmentManager,
                    "seatHeatingDialog"
                )
            }
        }
        binding.cabinSeatCopilotGuests.setOnClickListener {
            copilotGuestsDialogFragment = CopilotGuestsDialogFragment()
            activity?.let { it1 ->
                copilotGuestsDialogFragment!!.show(
                    it1.supportFragmentManager,
                    "copilotGuestsDialog"
                )
            }
        }
    }
}