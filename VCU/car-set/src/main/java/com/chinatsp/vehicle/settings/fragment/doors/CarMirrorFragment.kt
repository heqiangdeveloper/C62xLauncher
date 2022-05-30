package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarMirrorFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMirrorFragment : BaseFragment<DoorsViewModel, CarMirrorFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.car_mirror_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}