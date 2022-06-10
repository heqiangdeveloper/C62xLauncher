package com.chinatsp.vehicle.settings.fragment.cabin

import android.annotation.SuppressLint
import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinMeterFragmentBinding
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
class CabinMeterFragment : BaseFragment<CabinACViewModel, CabinMeterFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.cabin_meter_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setTabSelectionChangedListener()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTabSelectionChangedListener() {
        binding.cabinMeterSystemOptions.setOnTabSelectionChangedListener { _, value ->
            if (value.equals("英制")) {
                binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_mph))
            } else {
                binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_km))
            }
        }
    }
}