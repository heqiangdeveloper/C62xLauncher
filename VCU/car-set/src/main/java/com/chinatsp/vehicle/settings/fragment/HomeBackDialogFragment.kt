package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.HomeBackDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.MainViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeBackDialogFragment :
    BaseDialogFragment<MainViewModel, HomeBackDialogFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.home_back_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

   /* override fun getWidthRatio(): Float {
        return 650f / 1920f
    }*/

    private fun setBackListener() {
    /*    binding.driveAgree.setOnClickListener {
            ForwardManager.instance.doSetSwitchOption(SwitchNode.ADAS_AEB, false)
            this.dismiss()
        }
        binding.driveCancel.setOnClickListener {
            this.dismiss()
        }*/
    }
}