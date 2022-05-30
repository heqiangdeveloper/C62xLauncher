package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLightingFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLightingFragment:BaseFragment<DriveViewModel,DriveLightingFragmentBinding>() {
    override fun getLayoutId(): Int {
       return R.layout.drive_lighting_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}