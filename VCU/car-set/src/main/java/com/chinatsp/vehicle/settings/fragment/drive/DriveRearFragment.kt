package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.assistance.LaneManager
import com.chinatsp.settinglib.manager.assistance.SideBackManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveRearFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
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
        initSwitchOptions()
        initRadioOptions()

        observeRadioLiveData()
        observeSwitchLiveData()

        observeRadioOptionChange()
        observeSwitchOptionChange()
    }

    private fun observeRadioLiveData() {
        viewModel.showAreaValue.observe(this) {
            updateRadioOption(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, it, false)
        }
    }

    private fun observeSwitchOptionChange() {
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

    private fun observeRadioOptionChange() {
        listeningRadioOption(
            binding.adasSideShowAreaRadio,
            viewModel.showAreaValue,
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA
        )
    }

    private fun listeningRadioOption(
        tabView: TabControlView,
        liveData: LiveData<Int>,
        radioNode: RadioNode
    ) {
        tabView.setOnTabSelectionChangedListener { _, value ->
            val result = isCanToInt(value) && manager.doSetRadioOption(radioNode, value.toInt())
            if (!result) {
                val oldValue = liveData.value!!
//                tabView.setSelection(oldValue.toString(), false)
                updateRadioOption(radioNode, oldValue, false)
            }
        }
    }


    private fun doUpdateSwitchOption(
        switchNode: SwitchNode,
        buttonView: CompoundButton,
        status: Boolean
    ) {
        val result = manager.doSetSwitchOption(switchNode, status)
        if (!result && buttonView is SwitchButton) {
            buttonView.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }


    private fun observeSwitchLiveData() {
        viewModel.dowValue.observe(this) {
            updateSwitchOption(SwitchNode.ADAS_DOW, it)
        }
        viewModel.bscValue.observe(this) {
            updateSwitchOption(SwitchNode.ADAS_BSC, it)
        }
        viewModel.bsdValue.observe(this) {
            updateSwitchOption(SwitchNode.ADAS_BSD, it)
        }
        viewModel.guidesValue.observe(this) {
            updateSwitchOption(SwitchNode.ADAS_GUIDES, it)
        }
    }

    private fun initSwitchOptions() {
        updateSwitchOption(SwitchNode.ADAS_DOW, viewModel.dowValue.value!!, true)
        updateSwitchOption(SwitchNode.ADAS_BSC, viewModel.bscValue.value!!, true)
        updateSwitchOption(SwitchNode.ADAS_BSD, viewModel.bsdValue.value!!, true)
        updateSwitchOption(SwitchNode.ADAS_GUIDES, viewModel.guidesValue.value!!, true)
    }

    private fun initRadioOptions() {
        updateRadioOption(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, viewModel.showAreaValue.value!!, true)
    }

    private fun updateSwitchOption(node: SwitchNode, value: Boolean, immediately: Boolean = false) {
        val switchButton = when (node) {
            SwitchNode.ADAS_DOW -> {
                binding.adasSideDowSwitch
            }
            SwitchNode.ADAS_BSC -> {
                binding.adasSideBscSwitch
            }
            SwitchNode.ADAS_BSD -> {
                binding.adasSideBsdSwitch
            }
            SwitchNode.ADAS_GUIDES -> {
                binding.adasSideGuidesSwitch
            }
            else -> null
        }
        switchButton?.let {
            if (!immediately) {
                it.setCheckedNoEvent(value)
            } else {
                it.setCheckedImmediatelyNoEvent(value)
            }
        }
    }

    private fun updateRadioOption(node: RadioNode, value: Int, immediately: Boolean = false) {
        val tabView = when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                binding.adasSideShowAreaRadio
            }
            else -> null
        }
        tabView?.let {
            it.setSelection(value.toString(), false)
        }
    }
}