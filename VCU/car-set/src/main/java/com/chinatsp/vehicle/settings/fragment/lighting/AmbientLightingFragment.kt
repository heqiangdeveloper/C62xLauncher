package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingAtmosphereFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.ColorPickerView
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AmbientLightingFragment :
    BaseFragment<AmbientLightingViewModel, LightingAtmosphereFragmentBinding>(),
    ColorPickerView.OnColorPickerChangeListener, ISwitchAction {
    var status: Boolean = false
    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_atmosphere_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        initRouteListener()

        initViewsDisplay()

        addSeekBarLiveDataListener()
        initBrightnessSeekBar()
        initColorSeekBar()
        initViewLight()
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel( Level.LEVEL3,Level.LEVEL4, expect = true)) {
            binding.lightingFrontLayout.visibility = View.VISIBLE
            binding.lightingBackLayout.visibility = View.GONE
        } else {
            binding.lightingFrontLayout.visibility = View.VISIBLE
            binding.lightingBackLayout.visibility = View.VISIBLE
        }
    }

    private fun initViewLight() {
        if (VcuUtils.isCareLevel(Level.LEVEL3,  expect = true)) {
            if (binding.ambientFrontLightingSwitch.isChecked) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.GONE
                binding.imgLight5.visibility = View.GONE
            } else {
                binding.imgLight1.visibility = View.GONE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.GONE
                binding.imgLight4.visibility = View.GONE
                binding.imgLight5.visibility = View.GONE
            }
        } else if (VcuUtils.isCareLevel( Level.LEVEL4, expect = true)) {
            if (binding.ambientFrontLightingSwitch.isChecked) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.VISIBLE
                binding.imgLight5.visibility = View.GONE
            } else {
                binding.imgLight1.visibility = View.GONE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.GONE
                binding.imgLight4.visibility = View.GONE
                binding.imgLight5.visibility = View.GONE
            }
        } else if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            if (binding.ambientFrontLightingSwitch.isChecked && binding.ambientBackLightingSwitch.isChecked) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.VISIBLE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.VISIBLE
                binding.imgLight5.visibility = View.VISIBLE
            } else if (binding.ambientFrontLightingSwitch.isChecked && !binding.ambientBackLightingSwitch.isChecked) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.VISIBLE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.VISIBLE
                binding.imgLight5.visibility = View.GONE
            } else if (!binding.ambientFrontLightingSwitch.isChecked && binding.ambientBackLightingSwitch.isChecked) {
                binding.imgLight1.visibility = View.GONE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.GONE
                binding.imgLight4.visibility = View.GONE
                binding.imgLight5.visibility = View.VISIBLE
            } else {
                binding.imgLight1.visibility = View.GONE
                binding.imgLight2.visibility = View.GONE
                binding.imgLight3.visibility = View.GONE
                binding.imgLight4.visibility = View.GONE
                binding.imgLight5.visibility = View.GONE
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, viewModel.frontLighting)
        initSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, viewModel.backLighting)
        updateLayoutEnable()
    }

    private fun updateLayoutEnable() {
        val status =
            binding.ambientFrontLightingSwitch.isChecked || binding.ambientBackLightingSwitch.isChecked
        checkDisableOtherDiv(status, binding.lightingTitleLayout)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.frontLighting.observe(this) {
            doUpdateSwitch(SwitchNode.FRONT_AMBIENT_LIGHTING, it)
            updateLayoutEnable()
        }
        viewModel.backLighting.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_AMBIENT_LIGHTING, it)
            updateLayoutEnable()
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.FRONT_AMBIENT_LIGHTING -> binding.ambientFrontLightingSwitch
            SwitchNode.BACK_AMBIENT_LIGHTING -> binding.ambientBackLightingSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.ambientFrontLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, buttonView, isChecked)
            initViewLight()
            updateLayoutEnable()
        }
        binding.ambientBackLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, buttonView, isChecked)
            initViewLight()
            updateLayoutEnable()
        }
    }

    private fun setCheckedChangeListener() {
        binding.lightingInstall.setOnClickListener {
            if (!status) {
                showSettingFragment()
            }
        }
        binding.lightingIntelligentModel.setOnClickListener {
            if (!status) {
                showModeFragment()
            }
        }

        binding.brightnessLayout.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.GONE
            binding.brightnessAdjust.visibility = View.VISIBLE
        }
        binding.closeIv.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.VISIBLE
            binding.brightnessAdjust.visibility = View.GONE
        }
        binding.colorLayout.setOnClickListener {
            if (!status) {
                binding.lightingTitleLayout.visibility = View.GONE
                binding.pickerLayout.visibility = View.VISIBLE
            }
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
        cleanPopupSerial(Constant.AMBIENT_LIGHTING_MODE)
    }

    private fun showSettingFragment() {
        val fragment = AmbientLightingSettingDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment::javaClass.name)
        }
        cleanPopupSerial(Constant.AMBIENT_LIGHTING_SETTING)
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
                if (it.equals(Constant.AMBIENT_LIGHTING_MODE)) {
                    showModeFragment()
                } else if (it.equals(Constant.AMBIENT_LIGHTING_SETTING)) {
                    showSettingFragment()
                }
            }
        }
    }

    private fun initBrightnessSeekBar() {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        binding.ambientLightingBrightness.min = node.min
        binding.ambientLightingBrightness.max = node.max
        binding.ambientLightingBrightness.setValueNoEvent(viewModel.ambientBrightness.value!!)
        binding.ambientLightingBrightness.setOnSeekBarListener { _, value ->
            status = value == 0
//            checkDisableOtherDiv(status)
            viewModel.doBrightnessChanged(node, value)
            binding.ambientLightingBrightness.setValueNoEvent(viewModel.ambientBrightness.value!!)
        }
        status = viewModel.ambientBrightness.value == 0
//        checkDisableOtherDiv(status)
    }

    private fun checkDisableOtherDiv(status: Boolean) {
        val childCount = binding.lightingTitleLayout.childCount
        val intRange = 0 until childCount
        intRange.forEach {
            val childAt = binding.lightingTitleLayout.getChildAt(it)
            if (null != childAt && childAt != binding.brightnessLayout) {
                childAt.alpha = if (status) 0.7f else 1.0f
                updateViewEnable(childAt, status)
            }
        }

    }

    private fun updateViewEnable(view: View?, status: Boolean) {
        if (null == view) {
            return
        }
        if (view is SwitchButton) {
            view.isEnabled = status
            return
        }
        if (view is TabControlView) {
            view.updateEnable(status)
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
        }
    }

    private fun addSeekBarLiveDataListener() {
        viewModel.ambientBrightness.observe(this) {
            binding.ambientLightingBrightness.setValueNoEvent(it)
            Timber.d("addSeekBarLiveDataListener Brightness index:%s", it)
        }
        viewModel.ambientColor.observe(this) {
            Timber.d("addSeekBarLiveDataListener ambientColor index:%s", it)
            binding.picker.setIndicatorColorIndex(it)
        }
    }

    private fun initColorSeekBar() {
        binding.picker.setIndicatorIndex(viewModel.ambientColor.value!!)
        binding.picker.setOnColorPickerChangeListener(this)
    }

    override fun onColorChanged(picker: ColorPickerView?, color: Int, index: Int) {
        Timber.d("onColorChanged color:%s, index:%s", color, index)
        viewModel.onAmbientColorChanged(index)
        picker?.setIndicatorColorIndex(index)
    }

    override fun onStartTrackingTouch(picker: ColorPickerView?) {

    }

    override fun onStopTrackingTouch(picker: ColorPickerView?) {

    }

    private fun checkDisableOtherDiv(status: Boolean, view: View) {
        if (view is ViewGroup) {
            view.isEnabled = status
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                checkDisableOtherDiv(status, child)
            }
        } else {
            view.alpha = if (status) 1.0f else 0.6f
            view.isEnabled = status
        }
    }


}


