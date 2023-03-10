package com.chinatsp.vehicle.settings.fragment.adas

import android.os.Bundle
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CloseBrakeDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.ForwardViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CloseBrakeDialogFragment :
    BaseDialogFragment<ForwardViewModel, CloseBrakeDialogFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.close_brake_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 650f / 1920f
    }

    private fun setBackListener() {
        binding.driveAgree.setOnClickListener {
            ForwardManager.instance.doSetSwitchOption(SwitchNode.ADAS_AEB, false)
            this.dismiss()
        }
        binding.driveCancel.setOnClickListener {
            this.dismiss()
        }
    }
}