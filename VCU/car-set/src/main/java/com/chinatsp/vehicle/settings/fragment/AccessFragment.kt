package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AccessFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorViewModel
import com.common.library.frame.base.BaseFragment
import com.common.library.frame.base.BaseLazyFragment
import com.common.xui.utils.ResUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccessFragment: BaseFragment<DoorViewModel, AccessFragmentBinding>() {


    override fun getLayoutId(): Int {
        return R.layout.access_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
//        binding.accessCarWindow.text = viewModel.liveDataAutoLockDoor.value
//        viewModel.liveDataAutoLockDoor.observe(this) {
//            binding.accessCarWindow.text = it
//        }
    }
}