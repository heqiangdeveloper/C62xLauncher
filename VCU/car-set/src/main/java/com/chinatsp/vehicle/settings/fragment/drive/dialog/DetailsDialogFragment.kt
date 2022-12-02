package com.chinatsp.vehicle.settings.fragment.drive.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.DetailsDialogFragmentBinding
import com.common.library.frame.base.BaseDialogFragment
import com.king.base.util.StringUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DetailsDialogFragment :
    BaseDialogFragment<BaseViewModel, DetailsDialogFragmentBinding>() {

    private val str: StringBuffer = StringBuffer()

    var retract = true

    override fun getLayoutId(): Int {
        return R.layout.details_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        context?.let {
            binding.detailsTitle.text = HintHold.getTitle(it)
            val content = HintHold.getContent()
                ?.let { it1 -> resources.getString(it1) }

            val stringValue = content?.split("。")
            if (stringValue != null) {
                for (i in stringValue) {
                    if (!StringUtils.isEmpty(i)) {
                        val contentStr = if (retract) "\u3000\u3000$i。\n" else "$i。\n"
                        str.append(contentStr)
                    }
                }
            }
            Timber.d("StringBuffer() str:$str")
            binding.detailsContent.text = str.toString()
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