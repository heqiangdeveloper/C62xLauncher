package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinWhellFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringHeatingDialogFragment
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
class CabinWheelFragment : BaseFragment<CabinACViewModel, CabinWhellFragmentBinding>() {
    var steeringDialogFragment: SteeringDialogFragment? = null
    private var steeringHeatingDialogFragment: SteeringHeatingDialogFragment? = null
    override fun getLayoutId(): Int {
        return R.layout.cabin_whell_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
    }

    private fun setCheckedChangeListener() {
        binding.cabinWhellPressKey.setOnClickListener {
            steeringDialogFragment = SteeringDialogFragment()
            activity?.let { it1 ->
                steeringDialogFragment!!.show(
                    it1.supportFragmentManager,
                    "steeringDialog"
                )
            }
        }
        binding.cabinWhellAutomaticHeating.setOnClickListener {
            steeringHeatingDialogFragment = SteeringHeatingDialogFragment()
            activity?.let { it1 ->
                steeringHeatingDialogFragment!!.show(
                    it1.supportFragmentManager, "steeringHeatingDialog"
                )
            }

        }
    }
}