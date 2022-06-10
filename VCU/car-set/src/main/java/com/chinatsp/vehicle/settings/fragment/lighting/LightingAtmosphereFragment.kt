package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingAtmosphereFragmentBinding
import com.chinatsp.vehicle.settings.fragment.sound.SoundDialogFragment
import com.chinatsp.vehicle.settings.vm.LightingViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingAtmosphereFragment :
    BaseFragment<LightingViewModel, LightingAtmosphereFragmentBinding>() {
    var installDialog: LightingInstallDialogFragment? = null
    var modeDialog: LightingModelDialogFragment? = null
    override fun getLayoutId(): Int {
        return R.layout.lighting_atmosphere_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
    }

    private fun setCheckedChangeListener() {
        binding.lightingInstall.setOnClickListener {
            installDialog = LightingInstallDialogFragment()
            activity?.let { it1 ->
                installDialog!!.show(
                    it1.supportFragmentManager,
                    "installDialog"
                )
            }
        }
        binding.lightingIntelligentModel.setOnClickListener {
            modeDialog = LightingModelDialogFragment()
            activity?.let { it1 ->
                modeDialog!!.show(
                    it1.supportFragmentManager,
                    "modeDialog"
                )
            }
        }
    }
}