package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinAcFragmentBinding
import com.chinatsp.vehicle.settings.vm.CabinACViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinACFragment : BaseFragment<CabinACViewModel, CabinAcFragmentBinding>() {

    private val manager: ACManager
        get() = ACManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_ac_fragment
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
        initRadioOption(RadioNode.AC_COMFORT, viewModel.comfortLiveData)
    }

    private fun addRadioLiveDataListener() {
        viewModel.comfortLiveData.observe(this) {
            doUpdateRadio(RadioNode.AC_COMFORT, it, false)
        }
    }

    private fun setRadioListener() {
        binding.cabinAcComfortOption.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.AC_COMFORT, value, viewModel.comfortLiveData, it)
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
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

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        when (node) {
            RadioNode.AC_COMFORT -> binding.cabinAcComfortOption
            else -> null
        }?.let {
            if (isInit) {
                val names = it.nameArray.map { item -> item.toString() }.toTypedArray()
                val values = node.get.values.map { item -> item.toString() }.toTypedArray()
                it.setItems(names, values)
            }
            doUpdateRadio(it, value, immediately)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AC_AUTO_ARID, viewModel.aridLiveData)
        initSwitchOption(SwitchNode.AC_AUTO_DEMIST, viewModel.demistLiveData)
        initSwitchOption(SwitchNode.AC_ADVANCE_WIND, viewModel.windLiveData)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.aridLiveData.observe(this) {
            doUpdateSwitch(SwitchNode.AC_AUTO_ARID, it)
        }
        viewModel.demistLiveData.observe(this) {
            doUpdateSwitch(SwitchNode.AC_AUTO_DEMIST, it)
        }
        viewModel.windLiveData.observe(this) {
            doUpdateSwitch(SwitchNode.AC_ADVANCE_WIND, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.AC_AUTO_ARID -> binding.cabinAcAutoAridSwb
            SwitchNode.AC_AUTO_DEMIST -> binding.cabinAcAutoDemistSwb
            SwitchNode.AC_ADVANCE_WIND -> binding.cabinAcAdvanceWindSwb
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
        binding.cabinAcAutoAridSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AC_AUTO_ARID, buttonView, isChecked)
        }
        binding.cabinAcAutoDemistSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AC_AUTO_DEMIST, buttonView, isChecked)
        }
        binding.cabinAcAdvanceWindSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.AC_ADVANCE_WIND, buttonView, isChecked)
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