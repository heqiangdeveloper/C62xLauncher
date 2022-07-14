package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingAtmosphereFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.ColorPickerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbientLightingFragment :
    BaseFragment<AmbientLightingViewModel, LightingAtmosphereFragmentBinding>() {

    private val manager: ISwitchManager
        get() = AmbientLightingManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_atmosphere_fragment
    }

    private val modeFragmentSerial: String
        get() = "AmbientLightingMode"

    private val settingFragmentSerial: String
        get() = "AmbientLightingSetting"

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        initRouteListener()
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, viewModel.frontLighting)
        initSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, viewModel.backLighting)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.frontLighting.observe(this) {
            doUpdateSwitch(SwitchNode.FRONT_AMBIENT_LIGHTING, it)
        }
        viewModel.backLighting.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_AMBIENT_LIGHTING, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.FRONT_AMBIENT_LIGHTING -> binding.ambientFrontLightingSwitch
            SwitchNode.BACK_AMBIENT_LIGHTING -> binding.ambientBackLightingSwitch
            else -> null
        }
        takeIf { null != swb }?.doUpdateSwitch(swb!!, status, immediately)
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
    }

    private fun setSwitchListener() {
        binding.ambientFrontLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, buttonView, isChecked)
        }
        binding.ambientBackLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun setCheckedChangeListener() {
        binding.lightingInstall.setOnClickListener {
            showSettingFragment()
        }
        binding.lightingIntelligentModel.setOnClickListener {
            showModeFragment()
        }
        binding.picker.setOnColorPickerChangeListener(object :
            ColorPickerView.OnColorPickerChangeListener {
            override fun onColorChanged(picker: ColorPickerView?, color: Int) {
                binding.picker.indicatorColor =color
            }

            override fun onStartTrackingTouch(picker: ColorPickerView?) {

            }

            override fun onStopTrackingTouch(picker: ColorPickerView?) {

            }

        }

        )
        binding.brightnessLayout.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.GONE
            binding.brightnessAdjust.visibility = View.VISIBLE
        }
        binding.closeIv.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.VISIBLE
            binding.brightnessAdjust.visibility = View.GONE
        }
        binding.colorLayout.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.GONE
            binding.pickerLayout.visibility = View.VISIBLE
        }
        binding.pickerCloseIv.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.VISIBLE
            binding.pickerLayout.visibility = View.GONE
        }
    }

    private fun showModeFragment() {
        val fragment = AmbientLightingModelDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment::javaClass.name)
        }
        cleanPopupSerial(modeFragmentSerial)
    }

    private fun showSettingFragment() {
        val fragment = AmbientLightingSettingDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment::javaClass.name)
        }
        cleanPopupSerial(settingFragmentSerial)
    }

    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            iroute.cleanPopupLiveDate(serial)
        }
    }

    private fun initRouteListener() {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            val liveData = iroute.obtainPopupLiveData()
            liveData.observe(this) {
                if (it.equals(modeFragmentSerial)) {
                    showModeFragment()
                } else if (it.equals(settingFragmentSerial)) {
                    showSettingFragment()
                }
            }
        }
    }


}


