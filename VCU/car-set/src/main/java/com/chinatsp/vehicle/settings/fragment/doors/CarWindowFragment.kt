package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarWindowFragmentBinding
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWindowFragment : BaseFragment<DoorsViewModel, CarWindowFragmentBinding>() {
    private var animationCarWindow: AnimationDrawable = AnimationDrawable()
    private var animationWiper: AnimationDrawable = AnimationDrawable()
    override fun getLayoutId(): Int {
        return R.layout.car_window_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initAnimation()
        setSwitchListener()
    }

    private fun initAnimation() {
        animationCarWindow.setAnimation(
            activity,
            R.drawable.car_window_animation,
            binding.carWindowIv
        )
        animationWiper.setAnimation(
            activity,
            R.drawable.wiper_animation,
            binding.carWinper
        )
    }

    private fun setSwitchListener() {
        binding.carWindowRemoteControlSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.carWindowIv.visibility = View.VISIBLE
                animationCarWindow.start(false, 50, null)
            } else {
                binding.carWindowIv.visibility = View.GONE
            }
        }
        binding.carWindowWiperSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.carWinper.visibility = View.VISIBLE
                animationWiper.start(false, 50, null)
            } else {
                binding.carWinper.visibility = View.GONE
            }
        }
    }
}