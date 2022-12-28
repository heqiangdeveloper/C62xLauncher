package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.ConversionDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.chinatsp.vehicle.settings.widget.AnimationDrawable
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConversionDialogFragment :
    BaseDialogFragment<SeatViewModel, ConversionDialogFragmentBinding>() {

    private var animationloading: AnimationDrawable = AnimationDrawable()

    override fun getLayoutId(): Int {
        return R.layout.conversion_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initAnimation()
    }

    override fun getWidthRatio(): Float {
        return 480f / 1920f
    }

    override fun onDestroy() {
        try {
            animationloading.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun dismissLoading() {
        try {
            dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initAnimation() {
        val cxt = activity
        animationloading.setAnimation(cxt, R.drawable.loading_animation, binding.warning)
        animationloading.start(false, 15, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
            }

            override fun endAnimation() {
                dismissLoading()
            }
        })
    }



}