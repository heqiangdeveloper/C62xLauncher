package com.chinatsp.vehicle.settings.fragment.lighting

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingModelDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingSmartModeViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbientLightingModelDialogFragment :
    BaseDialogFragment<AmbientLightingSmartModeViewModel, LightingModelDialogFragmentBinding>(),
    ISwitchAction {
    private val colorList: List<Color> = Applet.getLampSupportColor()
    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance
//    var keypad: View? = null
    override fun getLayoutId(): Int {
        return R.layout.lighting_model_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        binding.closeDialog.setOnClickListener { dismiss() }
        initViewsDisplay()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initViewSelect()
        initViewSelectListener()

        updateEnable(binding.speedRhythm, true, binding.alcSmartModelSwitch.isChecked)
    }

    private fun initViewSelectListener() {
        binding.speedRhythm.setOnClickListener {
            if (binding.alcSmartModelSwitch.isChecked) {
                val status = it.isSelected
                val result: Boolean = viewModel.doUpdateViewStatus(SwitchNode.SPEED_RHYTHM, !status)
                takeIf { result }?.doUpdateViewSelect(it, viewModel.speedRhythm.value!!, false)
            }
        }
        binding.musicRhythm.setOnClickListener {
            if (binding.alcSmartModelSwitch.isChecked) {
                val status = it.isSelected
                val result: Boolean = viewModel.doUpdateViewStatus(SwitchNode.MUSIC_RHYTHM, !status)
                takeIf { result }?.doUpdateViewSelect(it, viewModel.musicRhythm.value!!, false)
            }
        }
        binding.colourBreathe.setOnClickListener {
            if (binding.alcSmartModelSwitch.isChecked) {
                val status = it.isSelected
                val result: Boolean =
                    viewModel.doUpdateViewStatus(SwitchNode.COLOUR_BREATHE, !status)
                takeIf { result }?.doUpdateViewSelect(it, viewModel.colourBreathe.value!!, false)
            }
        }
    }

    private fun initViewsDisplay() {
        val color = colorList[HintHold.getContent()!!]
        val colorId = Color.rgb(color.red().toInt(), color.green().toInt(), color.blue().toInt())
        binding.lightingView.setBackgroundColor(colorId)
        //根据不同车型选择不同车模及灯光
        if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
            binding.carModel.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it,
                    R.drawable.img_light_small_lv3)
            })
        } else if (VcuUtils.isCareLevel(Level.LEVEL4, expect = true)) {
            binding.carModel.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it,
                    R.drawable.img_light_small_lv4)
            })
        } else if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            binding.carModel.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it,
                    R.drawable.img_light_small_lv5)
            })
        }
    }

    override fun getWidthRatio(): Float {
        return 960f / 1920f
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ALC_SMART_MODE, viewModel.alcSmartMode)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.alcSmartMode.observe(this) {
            doUpdateSwitch(SwitchNode.ALC_SMART_MODE, it)
        }
        viewModel.colourBreathe.observe(this) {
            doUpdateViewSelect(SwitchNode.COLOUR_BREATHE, it)
        }
        viewModel.musicRhythm.observe(this) {
            doUpdateViewSelect(SwitchNode.MUSIC_RHYTHM, it)
        }
        viewModel.speedRhythm.observe(this) {
            doUpdateViewSelect(SwitchNode.SPEED_RHYTHM, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ALC_SMART_MODE -> binding.alcSmartModelSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

//    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<SwitchState>) {
//        val status = liveData.value ?: node.default
//        liveData.value?.let {
//            doUpdateSwitch(node, it, true)
//        }
//    }
//
//    override fun doUpdateSwitch(node: SwitchNode, status: SwitchState, immediately: Boolean) {
//        val swb = when (node) {
//            SwitchNode.ALC_SMART_MODE -> binding.alcSmartModelSwitch
//            else -> null
//        }
//    }

//    private fun doUpdateSwitch(swb: SwitchButton, status: SwitchState, immediately: Boolean = false) {
//        if (!immediately) {
//            swb.setCheckedNoEvent(status.get())
//        } else {
//            swb.setCheckedImmediatelyNoEvent(status.get())
//        }
//        checkDisableOtherDiv(swb, status.get())
//    }

    private fun initViewSelect() {
        initViewSelect(SwitchNode.SPEED_RHYTHM, viewModel.speedRhythm)
        initViewSelect(SwitchNode.MUSIC_RHYTHM, viewModel.musicRhythm)
        initViewSelect(SwitchNode.COLOUR_BREATHE, viewModel.colourBreathe)
    }

    private fun initViewSelect(node: SwitchNode, liveData: LiveData<SwitchState>) {
        val status = liveData.value ?: node.default
        liveData.value?.let {
            doUpdateViewSelect(node, it, true)
        }
    }

    private fun doUpdateViewSelect(
        node: SwitchNode,
        status: SwitchState,
        immediately: Boolean = false,
    ) {
        val swb = when (node) {
            SwitchNode.SPEED_RHYTHM -> binding.speedRhythm
            SwitchNode.MUSIC_RHYTHM -> binding.musicRhythm
            SwitchNode.COLOUR_BREATHE -> binding.colourBreathe
            else -> null
        }
        takeIf { null != swb }?.doUpdateViewSelect(swb!!, status, immediately)
    }

    private fun doUpdateViewSelect(view: View, status: SwitchState, immediately: Boolean = false) {
        val selected = status.get()
        view.isSelected = selected
//        keypad?.isEnabled = !selected
        view.isEnabled = !selected
//        keypad = view
    }

    private fun setSwitchListener() {
        binding.alcSmartModelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ALC_SMART_MODE, buttonView, isChecked)
            updateEnable(binding.speedRhythm, true, binding.alcSmartModelSwitch.isChecked)
        }
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        when (button) {
            binding.alcSmartModelSwitch -> {
                updateEnable(binding.speedRhythm, true, button.isChecked)
            }
            else -> {}
        }
    }

//    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
//        if (swb == binding.alcSmartModelSwitch) {
//            val childCount = binding.layoutContent.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.layoutContent.getChildAt(it)
//                if (null != childAt && childAt != binding.alcSmartModelSwitch) {
//                    childAt.alpha = if (status) 1.0f else 0.6f
//                    updateViewEnable(childAt, status)
//                }
//
//            }
//        }
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
//        if (view is AppCompatTextView) {
//            view.isClickable = status
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
//        }
//    }
//
//    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
//        val result = manager.doSetSwitchOption(node, status)
//        if (!result && button is SwitchButton) {
//            button.setCheckedImmediatelyNoEvent(!status)
//        }
//    }

}