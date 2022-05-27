package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.chinatsp.vehicle.settings.widget.SoundPopup
import com.common.library.frame.base.BaseFragment
import com.king.base.util.DensityUtils.dip2px
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundFragment : BaseFragment<SoundViewModel, SoundFragmentBinding>() {
    var soundPopup: SoundPopup? = null
    override fun getLayoutId(): Int {
        return R.layout.sound_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
    }

    private fun setCheckedChangeListener() {
        binding.soundVolumeAdjustment.setOnClickListener {
            if (soundPopup == null) {
                popWindow()
            }
            soundPopup?.width = dip2px(context, 960f)
            soundPopup?.showPopupWindow(it)
        }
    }

    private fun popWindow() {
        soundPopup = SoundPopup(requireActivity())
    }
}