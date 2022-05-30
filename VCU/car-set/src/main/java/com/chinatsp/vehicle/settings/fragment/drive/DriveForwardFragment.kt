package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveForwardFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveForwardFragment:BaseFragment<DriveViewModel,DriveForwardFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.drive_forward_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}