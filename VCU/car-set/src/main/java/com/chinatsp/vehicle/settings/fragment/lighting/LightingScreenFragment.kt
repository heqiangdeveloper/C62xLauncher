package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingScreenFragmentBinding
import com.chinatsp.vehicle.settings.vm.LightingViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingScreenFragment : BaseFragment<LightingViewModel, LightingScreenFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.lighting_screen_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}