package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingModelDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingSettingViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbientLightingModelDialogFragment : BaseDialogFragment<AmbientLightingSettingViewModel, LightingModelDialogFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.lighting_model_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}