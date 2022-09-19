package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingModelDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingSmartModeViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbientLightingModelDialogFragment :
    BaseDialogFragment<AmbientLightingSmartModeViewModel, LightingModelDialogFragmentBinding>() {

    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_model_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        binding.closeDialog.setOnClickListener { dismiss() }
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initViewSelect()
        initViewSelectListener()
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

    override fun getWidthRatio(): Float {
        return 882f / 1920f
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

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ALC_SMART_MODE -> binding.alcSmartModelSwitch
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
        checkDisableOtherDiv(swb, status)
    }

    private fun initViewSelect() {
        initViewSelect(SwitchNode.SPEED_RHYTHM, viewModel.speedRhythm)
        initViewSelect(SwitchNode.MUSIC_RHYTHM, viewModel.musicRhythm)
        initViewSelect(SwitchNode.COLOUR_BREATHE, viewModel.colourBreathe)
    }

    private fun initViewSelect(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateViewSelect(node, status, true)
    }

    private fun doUpdateViewSelect(
        node: SwitchNode,
        status: Boolean,
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

    private fun doUpdateViewSelect(view: View, status: Boolean, immediately: Boolean = false) {
        view.isSelected = status
    }

    private fun setSwitchListener() {
        binding.alcSmartModelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            checkDisableOtherDiv(binding.alcSmartModelSwitch, isChecked)
            doUpdateSwitchOption(SwitchNode.ALC_SMART_MODE, buttonView, isChecked)
        }
    }

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.alcSmartModelSwitch) {
            val childCount = binding.layoutContent.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.layoutContent.getChildAt(it)
                if (null != childAt && childAt != binding.alcSmartModelSwitch) {
                    childAt.alpha = if (status) 1.0f else 0.7f
                    updateViewEnable(childAt, status)
                }

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
        if (view is AppCompatTextView) {
            view.isClickable = status
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

}