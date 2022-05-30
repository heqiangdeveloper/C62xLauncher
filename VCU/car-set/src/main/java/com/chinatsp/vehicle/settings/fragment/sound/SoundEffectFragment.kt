package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundEffectFragment : BaseFragment<SoundViewModel, SoundEffectFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.sound_effect_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

}