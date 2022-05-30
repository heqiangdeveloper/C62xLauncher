package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveRearFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveRearFragment : BaseFragment<DriveViewModel, DriveRearFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.drive_rear_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

}