package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AbnormalChargeDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AbnormalChargeDialogFragment :
    BaseDialogFragment<SeatViewModel, AbnormalChargeDialogFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.abnormal_charge_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 740f / 1920f
    }

    private fun setBackListener() {

    }
}