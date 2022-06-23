package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.cabin.MeterManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinMeterFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.MeterViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.utils.ResUtils
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
class CabinMeterFragment : BaseFragment<MeterViewModel, CabinMeterFragmentBinding>() {

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
    }

    private fun addRadioLiveDataListener() {
        viewModel.systemRadioOption.observe(this) {
            doUpdateRadio(RadioNode.DRIVE_METER_SYSTEM, it, false)
        }
    }

    private fun setRadioListener() {
        binding.cabinMeterSystemOptions.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.DRIVE_METER_SYSTEM, value, viewModel.systemRadioOption, it)
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

//    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false) {
//        val tabView = when (node) {
//            RadioNode.DRIVE_METER_SYSTEM -> binding.cabinMeterSystemOptions
//            else -> null
//        }
//        tabView?.let {
//            doUpdateRadio(tabView, value, immediately)
//            var selectIndex = 0
//            node.get.values.filterIndexed { index, valueItem ->
//                val result = valueItem == value
//                if (result) {
//                    selectIndex = index
//                }
//                return@filterIndexed result
//            }
//            if (selectIndex == 1) {
//                binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_mph))
//            } else {
//                binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_km))
//            }
//        }
//    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> binding.cabinMeterSystemOptions
            else -> null
        }?.let {
            bindRadioData(node, it, isInit)
            doUpdateRadio(it, value, immediately)
            updateSystemImage(node, value)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }

    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.get.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
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
            binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_mph))
        } else {
            binding.ivMeasurement.setImageDrawable(resources.getDrawable(R.drawable.company_km))
        }
    }

}