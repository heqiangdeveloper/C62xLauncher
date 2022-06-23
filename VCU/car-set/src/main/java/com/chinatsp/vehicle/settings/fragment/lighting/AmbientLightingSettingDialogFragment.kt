package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingInstallDialogBinding
import com.chinatsp.vehicle.settings.vm.light.AmbientLightingSettingViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbientLightingSettingDialogFragment :
    BaseDialogFragment<AmbientLightingSettingViewModel, LightingInstallDialogBinding>() {

    private val manager: ISwitchManager
        get() = AmbientLightingManager.instance

    override fun getLayoutId(): Int {
        return R.layout.lighting_install_dialog
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        binding.closeDialog.setOnClickListener {
            dismiss()
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ALC_DOOR_HINT, viewModel.alcDoorHint)
        initSwitchOption(SwitchNode.ALC_LOCK_HINT, viewModel.alcLockHint)
        initSwitchOption(SwitchNode.ALC_BREATHE_HINT, viewModel.alcBreatheHint)
        initSwitchOption(SwitchNode.ALC_COMING_HINT, viewModel.alcComingHint)
        initSwitchOption(SwitchNode.ALC_RELATED_TOPICS, viewModel.alcRelatedTopics)
    }

    private fun addSwitchLiveDataListener() {
        addSwitchLiveDataListener(viewModel.alcDoorHint, SwitchNode.ALC_DOOR_HINT)
        addSwitchLiveDataListener(viewModel.alcLockHint, SwitchNode.ALC_LOCK_HINT)
        addSwitchLiveDataListener(viewModel.alcBreatheHint, SwitchNode.ALC_BREATHE_HINT)
        addSwitchLiveDataListener(viewModel.alcComingHint, SwitchNode.ALC_COMING_HINT)
        addSwitchLiveDataListener(viewModel.alcRelatedTopics, SwitchNode.ALC_RELATED_TOPICS)
    }

    private fun setSwitchListener() {
        setSwitchListener(binding.alcDoorSwitch, SwitchNode.ALC_DOOR_HINT)
        setSwitchListener(binding.alcLockSwitch, SwitchNode.ALC_LOCK_HINT)
        setSwitchListener(binding.alcBreatheSwitch, SwitchNode.ALC_BREATHE_HINT)
        setSwitchListener(binding.alcComingSwitch, SwitchNode.ALC_COMING_HINT)
        setSwitchListener(binding.alcRelatedTopicsSwitch, SwitchNode.ALC_RELATED_TOPICS)
    }

    private fun addSwitchLiveDataListener(liveData: LiveData<Boolean>, node: SwitchNode) {
        liveData.observe(this) {
            doUpdateSwitch(node, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ALC_DOOR_HINT -> binding.alcDoorSwitch
            SwitchNode.ALC_LOCK_HINT -> binding.alcLockSwitch
            SwitchNode.ALC_BREATHE_HINT -> binding.alcBreatheSwitch
            SwitchNode.ALC_COMING_HINT -> binding.alcComingSwitch
            SwitchNode.ALC_RELATED_TOPICS -> binding.alcRelatedTopicsSwitch
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

    private fun setSwitchListener(button: SwitchButton, node: SwitchNode) {
        button.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(node, buttonView!!, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

}