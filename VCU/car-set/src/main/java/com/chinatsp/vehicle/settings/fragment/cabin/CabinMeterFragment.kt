package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.cabin.MeterManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.vehicle.settings.IRadioAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinMeterFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.MeterViewModel
import com.common.library.frame.base.BaseFragment
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
class CabinMeterFragment : BaseFragment<MeterViewModel, CabinMeterFragmentBinding>(), IRadioAction {

    private val manager: IRadioManager
        get() = MeterManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_meter_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DRIVE_METER_SYSTEM, viewModel.systemRadioOption)
        updateRadioEnable(RadioNode.DRIVE_METER_SYSTEM)
    }

    private fun addRadioLiveDataListener() {
        viewModel.systemRadioOption.observe(this) {
            doUpdateRadio(RadioNode.DRIVE_METER_SYSTEM, it, false)
            updateRadioEnable(RadioNode.DRIVE_METER_SYSTEM)
        }
    }

    private fun setRadioListener() {
        binding.cabinMeterSystemOptions.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.DRIVE_METER_SYSTEM, value, viewModel.systemRadioOption, it)
            }
        }
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> binding.cabinMeterSystemOptions
            else -> null
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> viewModel.systemRadioOption.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    override fun onPostSelected(node: RadioNode, value: Int) {
        updateSystemImage(node, value)
    }

    private fun updateSystemImage(node: RadioNode, value: Int) {
        var selectIndex = 0
        node.get.values.filterIndexed { index, valueItem ->
            val result = valueItem == value
            if (result) {
                selectIndex = index
            }
            return@filterIndexed result
        }
        if (selectIndex == 1) {
            binding.ivMeasurement.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, R.drawable.company_mph)
            })
        } else {
            binding.ivMeasurement.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, R.drawable.company_km)
            })
        }
    }

}