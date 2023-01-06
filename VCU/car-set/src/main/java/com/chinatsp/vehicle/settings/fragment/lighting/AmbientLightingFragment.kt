package com.chinatsp.vehicle.settings.fragment.lighting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.*
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
    ColorPickerView.OnColorPickerChangeListener, ISwitchAction, IProgressAction {

    private val COLOR_CHANGED = 0x11
    private val LIGHT_CHANGED = 0x22

    private var modeFragment: DialogFragment? = null
    private var settingFragment: DialogFragment? = null

    private val map: HashMap<Int, View> = HashMap()
    private val colorList: List<Color> = Applet.getLampSupportColor()

    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance

    private val handler: Handler by lazy {
        Handler(
            Looper.getMainLooper()
        ) {
            when (it.what) {
                LIGHT_CHANGED -> {
                    val value = viewModel.ambientBrightness.value!!.pos
                    binding.ambientLightingBrightness.setValueNoEvent(value)
                }
                COLOR_CHANGED -> {
                    var value = viewModel.ambientColor.value!!.pos
                    binding.picker.setIndicatorIndex(value)
                    if (value <= 0) {
                        value = 0
                    } else if (value >= 64) {
                        value = 63
                    } else {
                        value -= 1
                    }
                    val color = colorList[value]
                    val colorId =
                        Color.rgb(color.red().toInt(), color.green().toInt(), color.blue().toInt())
//                    binding.lightingView.setBackgroundColor(colorId)
                    updateLightingColor(isLightingActive(), colorId)
                }
                else -> {}
            }
            return@Handler true
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.lighting_atmosphere_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

        binding.picker.setSupportColors(Applet.getLampSupportColor())

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
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int) {
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
        get() = binding.ambientFrontLightingSwitch.isChecked
                && (viewModel.frontLighting.value?.enable() ?: false)

    private val isBack: Boolean
        get() = hasBack && binding.ambientBackLightingSwitch.isChecked
                && (viewModel.backLighting.value?.enable() ?: false)

    private val hasBack: Boolean by lazy {
        !VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4)
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.FRONT_AMBIENT_LIGHTING)
        updateSwitchEnable(SwitchNode.BACK_AMBIENT_LIGHTING)
        updateEnable(binding.brightnessLayout, true, isLightingActive())
    }

    private fun initViewsDisplay() {
        binding.lightingFrontLayout.visibility = View.VISIBLE
        binding.lightingBackLayout.visibility = if (hasBack) View.GONE else View.VISIBLE
    }

    private fun initViewLight() {
        val color = colorList[binding.picker.pickerIndex - 1]
        val colorId = Color.rgb(color.red().toInt(), color.green().toInt(), color.blue().toInt())
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.lightingImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, R.drawable.img_light_lv3)
            })
            updateLightingColor(isLightingActive(), colorId)
        } else if (VcuUtils.isCareLevel(Level.LEVEL4)) {
            binding.lightingImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, R.drawable.img_light_lv4)
            })
            updateLightingColor(isLightingActive(), colorId)
        } else if (VcuUtils.isCareLevel(Level.LEVEL5)) {
            val sourceId: Int
            val isLampValid: Boolean
            if (isFront && isBack) {
                isLampValid = true
                sourceId = R.drawable.img_light_lv5
            } else if (!isFront && isBack) {
                isLampValid = true
                sourceId = R.drawable.img_light_lv5_1
            } else if (isFront && !isBack) {
                isLampValid = true
                sourceId = R.drawable.img_light_lv4
            } else {
                isLampValid = false
                sourceId = R.drawable.img_light_lv5_1
            }
            binding.lightingImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, sourceId)
            })
            updateLightingColor(isLampValid, colorId)
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
            initViewLight()
        }
        viewModel.backLighting.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_AMBIENT_LIGHTING, it)
            updateEnable(binding.brightnessLayout, true, isLightingActive())
            resetDisplay()
            initViewLight()
        }
        viewModel.node362.observe(this) {
            updateSwitchEnable(SwitchNode.BACK_AMBIENT_LIGHTING)
            updateSwitchEnable(SwitchNode.FRONT_AMBIENT_LIGHTING)
        }
        viewModel.node65A.observe(this) {
            updateProgressEnable(Progress.AMBIENT_LIGHT_BRIGHTNESS)
            updateProgressEnable(Progress.AMBIENT_LIGHT_COLOR)
            val dependActive = obtainDependByNode(Progress.AMBIENT_LIGHT_COLOR)
            binding.picker.isSlide = dependActive
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
            else -> super<ISwitchAction>.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.BACK_AMBIENT_LIGHTING -> viewModel.node362.value?.get() ?: true
            SwitchNode.FRONT_AMBIENT_LIGHTING -> viewModel.node362.value?.get() ?: true
            else -> super<ISwitchAction>.obtainActiveByNode(node)
        }
    }


    override fun obtainDependByNode(node: Progress): Boolean {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> viewModel.node65A.value?.get() ?: true
            Progress.AMBIENT_LIGHT_COLOR -> viewModel.node65A.value?.get() ?: true
            else -> super<IProgressAction>.obtainActiveByNode(node)
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
            HintHold.setTitle(1)
            HintHold.setContent(binding.picker.pickerIndex)
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
//        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
//        binding.ambientLightingBrightness.min = node.min
//        binding.ambientLightingBrightness.max = node.max
        val volume = viewModel.ambientBrightness.value!!
        binding.ambientLightingBrightness.updateRangeValue(volume.min, volume.max, 1)
        binding.ambientLightingBrightness.setValueNoEvent(volume.pos)
        binding.ambientLightingBrightness.setOnSeekBarListener { _, value ->
            viewModel.doBrightnessChanged(Progress.AMBIENT_LIGHT_BRIGHTNESS, value)
//            binding.ambientLightingBrightness.setValueNoEvent()
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
//            val value = it.pos
//            binding.ambientLightingBrightness.setValueNoEvent(value)
            handler.removeMessages(LIGHT_CHANGED)
            handler.sendEmptyMessageDelayed(LIGHT_CHANGED, 150)
        }
        viewModel.ambientColor.observe(this) {
//            val value = it.pos
//            binding.picker.setIndicatorIndex(value)
            handler.removeMessages(COLOR_CHANGED)
            handler.sendEmptyMessageDelayed(COLOR_CHANGED, 100)
        }
    }

    private fun initColorSeekBar() {
        val value = viewModel.ambientColor.value!!.pos
        binding.picker.setIndicatorIndex(value)
        binding.picker.setOnColorPickerChangeListener(this)
    }

    private fun updateLightingColor(isShowColor: Boolean, color: Int) {
        if (isShowColor) {
            binding.lightingView.setBackgroundColor(color)
        } else {
            activity?.let { binding.lightingView.setBackgroundColor(it.getColor(R.color.lighting_bg)) }
        }
    }

    override fun onColorChanged(picker: ColorPickerView?, color: Int, index: Int) {
        Timber.d("onColorChanged color:%s, index:%s", color, index)
        viewModel.onAmbientColorChanged(index)
        picker?.setIndicatorColorIndex(index)
        val colorIndexValue = colorList[index]
        val colorId = Color.rgb(
            colorIndexValue.red().toInt(),
            colorIndexValue.green().toInt(),
            colorIndexValue.blue().toInt()
        )
        updateLightingColor(isLightingActive(), colorId)
    }

    override fun onStartTrackingTouch(picker: ColorPickerView?) {

    }

    override fun onStopTrackingTouch(picker: ColorPickerView?) {

    }


    override fun onPause() {
        super.onPause()
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val json =
            "{\"color\":\"" + binding.picker.pickerIndex + "\",\"lighting\":\"" + binding.ambientLightingBrightness.mSelectedNumber + "\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("atmosphereLamp", json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
        Timber.d("lighting intent json:$json")
    }

    override fun onDestroyView() {
        handler.removeMessages(LIGHT_CHANGED)
        handler.removeMessages(COLOR_CHANGED)
        super.onDestroyView()
    }

    override fun findProgressByNode(node: Progress): View? {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> binding.ambientLightingBrightness
            Progress.AMBIENT_LIGHT_COLOR -> binding.picker
            else -> null
        }
    }
}





