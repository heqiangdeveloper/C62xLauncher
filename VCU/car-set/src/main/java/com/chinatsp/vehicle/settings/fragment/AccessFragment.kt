package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AccessFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorViewModel
import com.common.library.frame.base.BaseLazyFragment
import com.common.xui.utils.ResUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccessFragment: BaseLazyFragment<DoorViewModel, AccessFragmentBinding>() {

    override fun onLazyLoad() {
//        binding.accessCarWindow.text = viewModel.liveDataAutoLockDoor.value
//        viewModel.liveDataAutoLockDoor.observe(this) {
//            binding.accessCarWindow.text = it
//        }
        val array = ResUtils.getStringArray(R.array.three_state_option)
//        with(binding.tabControlView) {
//            this.setEqualWidth(true)
//            this.setItems(array, array)
//        }
        with(binding.tabRadio) {
            this.isSelected = true
        }
    }

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