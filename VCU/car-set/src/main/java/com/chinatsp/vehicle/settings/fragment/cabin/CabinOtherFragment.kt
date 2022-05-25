package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinOtherFragmentBinding
import com.chinatsp.vehicle.settings.vm.CabinACViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CabinOtherFragment : BaseFragment<CabinACViewModel, CabinOtherFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.cabin_other_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}