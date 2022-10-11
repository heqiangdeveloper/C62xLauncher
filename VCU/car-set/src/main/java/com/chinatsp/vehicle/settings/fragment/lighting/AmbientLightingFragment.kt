package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AmbientLightingFragment :
    BaseFragment<AmbientLightingViewModel, LightingAtmosphereFragmentBinding>(),
    ColorPickerView.OnColorPickerChangeListener, ISwitchAction {

    var modeFragment: DialogFragment? = null
    var settingFragment: DialogFragment? = null

    private val map: HashMap<Int, View> = HashMap()

    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_atmosphere_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

        initClickView()

        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initViewsDisplay()

        addSeekBarLiveDataListener()
        initBrightnessSeekBar()
        initColorSeekBar()
        initViewLight()

        updateOptionActive()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.lightingInstall
        map[2] = binding.lightingIntelligentModel
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                it.takeIf { it.valid && it.uid == pid }?.let { level1 ->
                    level1.cnode?.takeIf { child -> child.valid && child.uid == uid }
                        .let { level2 ->
                            level2?.cnode?.let { lv3Node ->
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid, true) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int, frank: Boolean) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.lightingInstall -> showSettingFragment()
            binding.lightingIntelligentModel -> showModeFragment()
        }
    }

    private fun isLightingActive(): Boolean {
        return isFront || isBack
    }

    private val isFront: Boolean
        get() = binding.ambientFrontLightingSwitch.isChecked && (viewModel.frontLighting.value?.enable()
            ?: false)

    private val isBack: Boolean
        get() = binding.ambientBackLightingSwitch.isChecked && (viewModel.backLighting.value?.enable()
            ?: false)

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.FRONT_AMBIENT_LIGHTING)
        updateSwitchEnable(SwitchNode.BACK_AMBIENT_LIGHTING)

        updateEnable(binding.brightnessLayout, true, isLightingActive())
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, expect = true)) {
            binding.lightingFrontLayout.visibility = View.VISIBLE
            binding.lightingBackLayout.visibility = View.GONE
        } else {
            binding.lightingFrontLayout.visibility = View.VISIBLE
            binding.lightingBackLayout.visibility = View.VISIBLE
        }
    }

    private fun initViewLight() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
            if (isFront) {
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
        } else if (VcuUtils.isCareLevel(Level.LEVEL4, expect = true)) {
            if (isFront) {
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
            if (isFront && isBack) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.VISIBLE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.VISIBLE
                binding.imgLight5.visibility = View.VISIBLE
            } else if (isFront && !isBack) {
                binding.imgLight1.visibility = View.VISIBLE
                binding.imgLight2.visibility = View.VISIBLE
                binding.imgLight3.visibility = View.VISIBLE
                binding.imgLight4.visibility = View.VISIBLE
                binding.imgLight5.visibility = View.GONE
            } else if (!isFront && isBack) {
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
//        updateLayoutEnable()
    }

//    private fun updateLayoutEnable() {
//        val status =
//            binding.ambientFrontLightingSwitch.isChecked || binding.ambientBackLightingSwitch.isChecked
//        checkDisableOtherDiv(status, binding.lightingTitleLayout)
//    }

    private fun addSwitchLiveDataListener() {
        viewModel.frontLighting.observe(this) {
            doUpdateSwitch(SwitchNode.FRONT_AMBIENT_LIGHTING, it)
            updateEnable(binding.brightnessLayout, true, isLightingActive())
            resetDisplay()
        }
        viewModel.backLighting.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_AMBIENT_LIGHTING, it)
            updateEnable(binding.brightnessLayout, true, isLightingActive())
            resetDisplay()
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.FRONT_AMBIENT_LIGHTING -> binding.ambientFrontLightingSwitch
            SwitchNode.BACK_AMBIENT_LIGHTING -> binding.ambientBackLightingSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.FRONT_AMBIENT_LIGHTING -> viewModel.frontLighting.value?.enable() ?: false
            SwitchNode.BACK_AMBIENT_LIGHTING -> viewModel.backLighting.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }


    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.ambientFrontLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, buttonView, isChecked)
            initViewLight()
            updateEnable(binding.brightnessLayout, true, isLightingActive())
            resetDisplay()
        }
        binding.ambientBackLightingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, buttonView, isChecked)
            initViewLight()
            updateEnable(binding.brightnessLayout, true, isLightingActive())
            resetDisplay()
        }
    }

    private fun setCheckedChangeListener() {
        binding.lightingInstall.setOnClickListener {
            showSettingFragment()
        }
        binding.lightingIntelligentModel.setOnClickListener {
            showModeFragment()
        }

        binding.brightnessLayout.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.GONE
            binding.brightnessAdjust.visibility = View.VISIBLE
        }
        binding.closeIv.setOnClickListener {
            binding.brightnessAdjust.visibility = View.GONE
            binding.lightingTitleLayout.visibility = View.VISIBLE
        }
        binding.colorLayout.setOnClickListener {
            binding.lightingTitleLayout.visibility = View.GONE
            binding.pickerLayout.visibility = View.VISIBLE
        }

        binding.pickerCloseIv.setOnClickListener {
            binding.pickerLayout.visibility = View.GONE
            binding.lightingTitleLayout.visibility = View.VISIBLE
        }
    }

    private fun showModeFragment() {
        if (isLightingActive()) {
            modeFragment = AmbientLightingModelDialogFragment()
            activity?.supportFragmentManager?.let {
                modeFragment!!.show(it, modeFragment!!::javaClass.name)
            }
        }
        cleanPopupSerial(Constant.AMBIENT_LIGHTING_MODE)
    }

    private fun showSettingFragment() {
        if (isLightingActive()) {
            settingFragment = AmbientLightingSettingDialogFragment()
            activity?.supportFragmentManager?.let {
                settingFragment!!.show(it, settingFragment!!::javaClass.name)
            }
        }
        cleanPopupSerial(Constant.AMBIENT_LIGHTING_SETTING)
    }

    private fun resetDisplay() {
        if (!isLightingActive()) {
            var view: View = binding.brightnessAdjust
            val visibility = View.GONE
            if (visibility != view.visibility) {
                view.visibility = visibility
            }
            view = binding.pickerLayout
            if (visibility != view.visibility) {
                view.visibility = visibility
            }
            modeFragment?.let {
                if (it.showsDialog || it.isAdded) {
                    it.dismiss()
                }
                modeFragment = null
            }
            settingFragment?.let {
                if (it.showsDialog || it.isAdded) {
                    it.dismiss()
                }
                settingFragment = null
            }
            view = binding.lightingTitleLayout
            if (View.VISIBLE != view.visibility) {
                view.visibility = View.VISIBLE
            }
        }
    }

    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val route = activity as IRoute
            route.cleanPopupLiveDate(serial)
        }
    }

//    private fun initRouteListener() {
//        if (activity is IRoute) {
//            val route = activity as IRoute
//            val liveData = route.obtainPopupLiveData()
//            liveData.observe(this) {
//                if (it.equals(Constant.AMBIENT_LIGHTING_MODE)) {
//                    showModeFragment()
//                } else if (it.equals(Constant.AMBIENT_LIGHTING_SETTING)) {
//                    showSettingFragment()
//                }
//            }
//        }
//    }

    private fun initBrightnessSeekBar() {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        binding.ambientLightingBrightness.min = node.min
        binding.ambientLightingBrightness.max = node.max
        binding.ambientLightingBrightness.setValueNoEvent(viewModel.ambientBrightness.value!!)
        binding.ambientLightingBrightness.setOnSeekBarListener { _, value ->
            viewModel.doBrightnessChanged(node, value)
            binding.ambientLightingBrightness.setValueNoEvent(viewModel.ambientBrightness.value!!)
        }
    }

//    private fun checkDisableOtherDiv(status: Boolean) {
//        val childCount = binding.lightingTitleLayout.childCount
//        val intRange = 0 until childCount
//        intRange.forEach {
//            val childAt = binding.lightingTitleLayout.getChildAt(it)
//            if (null != childAt && childAt != binding.brightnessLayout) {
//                childAt.alpha = if (status) 0.7f else 1.0f
//                updateViewEnable(childAt, status)
//            }
//        }
//
//    }
//
//    private fun updateViewEnable(view: View?, status: Boolean) {
//        if (null == view) {
//            return
//        }
//        if (view is SwitchButton) {
//            view.isEnabled = status
//            return
//        }
//        if (view is TabControlView) {
//            view.updateEnable(status)
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
//        }
//    }

    private fun addSeekBarLiveDataListener() {
        viewModel.ambientBrightness.observe(this) {
            binding.ambientLightingBrightness.setValueNoEvent(it)
        }
        viewModel.ambientColor.observe(this) {
            binding.picker.setIndicatorIndex(it)
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

//    private fun checkDisableOtherDiv(status: Boolean, view: View) {
//        if (view is ViewGroup) {
//            view.isEnabled = status
//            for (index in 0 until view.childCount) {
//                val child = view.getChildAt(index)
//                checkDisableOtherDiv(status, child)
//            }
//        } else {
//            view.alpha = if (status) 1.0f else 0.6f
//            view.isEnabled = status
//        }
//    }


}


