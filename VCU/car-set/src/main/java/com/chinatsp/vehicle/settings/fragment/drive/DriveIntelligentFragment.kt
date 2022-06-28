package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.adas.CruiseManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveIntelligentFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.CruiseViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveIntelligentFragment : BaseFragment<CruiseViewModel, DriveIntelligentFragmentBinding>() {

    private val manager: CruiseManager
        get() = CruiseManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_intelligent_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveRadio)
    }

    private fun addRadioLiveDataListener() {
        viewModel.limberLeaveRadio.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LIMBER_LEAVE, it, false)
        }
    }

    private fun setRadioListener() {
        binding.accessCruiseLimberLeaveRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LIMBER_LEAVE, value, viewModel.limberLeaveRadio, it)
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
    }

    private fun doUpdateRadio(node: RadioNode, value: String,  liveData: LiveData<Int>, tabView: TabControlView) {
        val result = isCanToInt(value) && manager.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.setSelection(liveData.value.toString(), true)
    }

    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false, isInit: Boolean = false) {
        val tabView = when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
                binding.accessCruiseLimberLeaveRadio.getChildAt(0).visibility = View.GONE
                binding.accessCruiseLimberLeaveRadio
            }
            else -> null
        }
        takeIf { null != tabView }?.let {
            bindRadioData(node, tabView!!, isInit)
            doUpdateRadio(tabView!!, value, immediately)
        }
    }

    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.get.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }


    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_IACC, viewModel.cruiseAssistFunction)
        initSwitchOption(SwitchNode.ADAS_TARGET_PROMPT, viewModel.targetPromptFunction)
        initSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.cruiseAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_IACC, it)
        }
        viewModel.targetPromptFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_TARGET_PROMPT, it)
        }
        viewModel.limberLeaveFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_LIMBER_LEAVE, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_IACC -> binding.accessCruiseCruiseAssist
            SwitchNode.ADAS_TARGET_PROMPT -> binding.accessCruiseTargetPrompt
            SwitchNode.ADAS_LIMBER_LEAVE -> binding.accessCruiseLimberLeave
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
        binding.accessCruiseCruiseAssist.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_IACC, buttonView, isChecked)
        }
        binding.accessCruiseTargetPrompt.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_TARGET_PROMPT, buttonView, isChecked)
        }
        binding.accessCruiseLimberLeave.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }

}

