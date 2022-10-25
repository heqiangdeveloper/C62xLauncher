package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.GlobalDialogFragmentBinding
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 全局类系统弹窗
 */
@AndroidEntryPoint
class GlobalDialogFragment :
    BaseDialogFragment<BaseViewModel, GlobalDialogFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.global_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        context?.let {
            binding.detailsContent.text = HintHold.getContent(it)
        }
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 680f / 1920f
    }

    override fun isCanceledOnTouchOutside(): Boolean = false

    private fun setBackListener() {
        binding.hintConform.setOnClickListener {
            dismiss()
        }
    }
}