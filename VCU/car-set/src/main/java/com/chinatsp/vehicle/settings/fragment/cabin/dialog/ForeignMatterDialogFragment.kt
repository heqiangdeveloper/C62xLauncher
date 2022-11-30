package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.ForeignMatterDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForeignMatterDialogFragment :
    BaseDialogFragment<SeatViewModel, ForeignMatterDialogFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.foreign_matter_dialog_fragment
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