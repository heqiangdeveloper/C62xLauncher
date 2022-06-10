package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarMirrorFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.MirrorViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMirrorFragment : BaseFragment<MirrorViewModel, CarMirrorFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.car_mirror_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOptionStatus(SwitchNode.AS_BACK_MIRROR_FOLD, viewModel.mirrorFoldFunction.value!!)
        observeSwitchLiveData()
    }

    private fun observeSwitchLiveData() {
        viewModel.mirrorFoldFunction.observe(this) {
            updateSwitchOptionStatus(SwitchNode.AS_BACK_MIRROR_FOLD, it!!)
        }
    }

    private fun initSwitchOptionStatus(switchNode: SwitchNode, status: Boolean) {
        binding.accessMirrorMirrorFoldSw.isChecked = status
    }

    private fun updateSwitchOptionStatus(switchNode: SwitchNode, status: Boolean) {
        binding.accessMirrorMirrorFoldSw.isChecked = status
    }
}