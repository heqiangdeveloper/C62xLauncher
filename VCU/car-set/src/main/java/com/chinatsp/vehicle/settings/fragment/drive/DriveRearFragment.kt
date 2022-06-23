package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.adas.SideBackManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveRearFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.SideViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveRearFragment : BaseFragment<SideViewModel, DriveRearFragmentBinding>() {

    private val manager: IOptionManager
        get() = SideBackManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_rear_fragment
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
        initRadioOption(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, viewModel.showAreaValue)
    }

    private fun addRadioLiveDataListener() {
        viewModel.showAreaValue.observe(this) {
            doUpdateRadio(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, it, false)
        }
    }

    private fun setRadioListener() {
        binding.adasSideShowAreaRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, value, viewModel.showAreaValue, it)
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: String,
        liveData: LiveData<Int>,
        tabView: TabControlView
    ) {
        val result = isCanToInt(value) && manager.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.setSelection(liveData.value.toString(), true)
    }

    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false) {
        val tabView = when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> binding.adasSideShowAreaRadio
            else -> null
        }
        takeIf { null != tabView }?.doUpdateRadio(tabView!!, value, immediately)
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_DOW, viewModel.dowValue)
        initSwitchOption(SwitchNode.ADAS_BSC, viewModel.bscValue)
        initSwitchOption(SwitchNode.ADAS_BSD, viewModel.bsdValue)
        initSwitchOption(SwitchNode.ADAS_GUIDES, viewModel.guidesValue)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.dowValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_DOW, it)
        }
        viewModel.bscValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSC, it)
        }
        viewModel.bsdValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSD, it)
        }
        viewModel.guidesValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_GUIDES, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_DOW -> binding.adasSideDowSwitch
            SwitchNode.ADAS_BSC -> binding.adasSideBscSwitch
            SwitchNode.ADAS_BSD -> binding.adasSideBsdSwitch
            SwitchNode.ADAS_GUIDES -> binding.adasSideGuidesSwitch
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
        binding.adasSideDowSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_DOW, buttonView, isChecked)
        }
        binding.adasSideBscSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_BSC, buttonView, isChecked)
        }
        binding.adasSideBsdSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_BSD, buttonView, isChecked)
        }
        binding.adasSideGuidesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_GUIDES, buttonView, isChecked)
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