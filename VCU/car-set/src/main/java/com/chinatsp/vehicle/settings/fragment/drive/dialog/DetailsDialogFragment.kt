package com.chinatsp.vehicle.settings.fragment.drive.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.DetailsDialogFragmentBinding
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsDialogFragment :
    BaseDialogFragment<BaseViewModel, DetailsDialogFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.details_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        context?.let {
            binding.detailsTitle.text = HintHold.getTitle(it)
            binding.detailsContent.text = HintHold.getContent(it)
        }
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 1750f / 1920f
    }

    override fun isCanceledOnTouchOutside(): Boolean = false

    private fun setBackListener() {
        binding.hintConform.setOnClickListener {
            dismiss()
        }
    }
}