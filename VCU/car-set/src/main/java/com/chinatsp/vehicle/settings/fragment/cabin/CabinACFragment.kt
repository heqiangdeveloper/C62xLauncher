package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import com.chinatsp.settinglib.ACManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinAcFragmentBinding
import com.chinatsp.vehicle.settings.vm.CabinACViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.utils.ResUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinACFragment : BaseFragment<CabinACViewModel, CabinAcFragmentBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.cabin_ac_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
//        val result = ACManager.instance.doUpdateACComfort(0x02)
//        Timber.d("initData doUpdateACComfort result:$result")
        monitorRadioOption()
        monitorSwitchLiveData()
        setCheckedChangeListener()
    }

    private fun monitorRadioOption() {
        val stringArray = ResUtils.getStringArray(R.array.cabin_ac_comfort_options)
        viewModel.comfortLiveData.let {
            val defIndex = it.value?.let { value ->
                return@let if (value >= 0 && value < stringArray.size)  value else 0x01
            } ?: 0x01
            updateComfortOption(defIndex, stringArray)
            it.observe(this) { value ->
                updateComfortOption(value, stringArray)
            }
        }
    }

    private fun updateComfortOption(value: Int, stringArray: Array<String>) {
        when (value) {
            0x01 -> {
                binding.cabinAcComfortOption.setSelection(stringArray[0])
            }
            0x02 -> {
                binding.cabinAcComfortOption.setSelection(stringArray[1])
            }
            0x03 -> {
                binding.cabinAcComfortOption.setSelection(stringArray[2])
            }
            else -> {
                binding.cabinAcComfortOption.setSelection(stringArray[0])
            }
        }
    }

    private fun monitorSwitchLiveData() {
        viewModel.aridLiveData.let {
            binding.cabinAcAutoAridSwb.isChecked = it.value == true
            it.observe(this) { checked ->
                binding.cabinAcAutoAridSwb.setCheckedNoEvent(checked)
            }
        }
        viewModel.demistLiveData.let {
            binding.cabinAcAutoDemistSwb.isChecked = it.value == true
            it.observe(this) { checked ->
                binding.cabinAcAutoDemistSwb.setCheckedNoEvent(checked)
            }
        }
        viewModel.windLiveData.let {
            binding.cabinAcAdvanceWindSwb.isChecked = it.value == true
            it.observe(this) { checked ->
                binding.cabinAcAdvanceWindSwb.setCheckedNoEvent(checked)
            }
        }
    }

    private fun setCheckedChangeListener() {
        binding.cabinAcAutoAridSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            val result =
                ACManager.instance.doSwitchACOption(ACManager.SwitchNape.AC_AUTO_ARID, isChecked)
            if (!result) {
                binding.cabinAcAutoAridSwb.setCheckedNoEvent(!isChecked)
            }
            Timber.d("doSwitchACOption arid result:$result")
        }
        binding.cabinAcAutoDemistSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            val result =
                ACManager.instance.doSwitchACOption(ACManager.SwitchNape.AC_AUTO_DEMIST, isChecked)
            if (!result) {
                binding.cabinAcAutoDemistSwb.setCheckedNoEvent(!isChecked)
            }
            Timber.d("doSwitchACOption demist result:$result")
        }
        binding.cabinAcAdvanceWindSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            val result =
                ACManager.instance.doSwitchACOption(ACManager.SwitchNape.AC_ADVANCE_WIND, isChecked)
            if (!result) {
                binding.cabinAcAdvanceWindSwb.setCheckedNoEvent(!isChecked)
            }
            Timber.d("doSwitchACOption wind result:$result")
        }

    }
}