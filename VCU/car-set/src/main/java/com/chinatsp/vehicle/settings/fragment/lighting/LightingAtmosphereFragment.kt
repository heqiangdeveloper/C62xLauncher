package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingAtmosphereFragmentBinding
import com.chinatsp.vehicle.settings.fragment.sound.SoundDialogFragment
import com.chinatsp.vehicle.settings.vm.LightingViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.picker.ColorPickerView
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
        binding.picker.setOnColorPickerChangeListener(object :
            ColorPickerView.OnColorPickerChangeListener {
            override fun onColorChanged(picker: ColorPickerView?, color: Int) {
                binding.picker.indicatorColor = color
            }

            override fun onStartTrackingTouch(picker: ColorPickerView?) {

            }

            override fun onStopTrackingTouch(picker: ColorPickerView?) {

            }


        }
        )
    }
}