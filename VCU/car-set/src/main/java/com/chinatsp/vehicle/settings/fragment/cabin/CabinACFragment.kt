package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinAcFragmentBinding
import com.chinatsp.vehicle.settings.vm.CabinACViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.utils.ViewUtils
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.wait

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinACFragment : BaseFragment<CabinACViewModel, CabinAcFragmentBinding>(), IOptionAction {

    private val manager: ACManager
        get() = ACManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_ac_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initViewsDisplay()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
        updateRadioEnable(RadioNode.AC_COMFORT)
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.expendTouchArea(binding.cabinAcAutoAridSwb, 20)
        ViewUtils.expendTouchArea(binding.cabinAcAutoDemistSwb, 20)
        ViewUtils.expendTouchArea(binding.cabinAcAdvanceWindSwb, 20)
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {//lv3没有预通风功能
            binding.cabinAcAutoWindLayout.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.AC_COMFORT -> binding.cabinAcComfortOption
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AC_AUTO_ARID -> binding.cabinAcAutoAridSwb
            SwitchNode.AC_AUTO_DEMIST -> binding.cabinAcAutoDemistSwb
            SwitchNode.AC_ADVANCE_WIND -> binding.cabinAcAdvanceWindSwb
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
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

    private fun initRadioOption() {
        initRadioOption(RadioNode.AC_COMFORT, viewModel.comfortLiveData)
    }

    private fun addRadioLiveDataListener() {
        viewModel.comfortLiveData.observe(this) {
            doUpdateRadio(RadioNode.AC_COMFORT, it, false)
            updateRadioEnable(RadioNode.AC_COMFORT)
        }
    }

    private fun setRadioListener() {
        binding.cabinAcComfortOption.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.AC_COMFORT, value, viewModel.comfortLiveData, it)
            }
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.AC_COMFORT -> viewModel.comfortLiveData.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

}