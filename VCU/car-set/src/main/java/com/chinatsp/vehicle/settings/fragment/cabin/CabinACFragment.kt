package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.manager.ACManager
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
        setTabSelectionChangedListener()

        LogManager.d("ViewModel", "ViewModel:${System.identityHashCode(viewModel)}")
    }

    private fun setTabSelectionChangedListener() {
        binding.cabinAcComfortOption.setOnTabSelectionChangedListener { title, value ->
            val stringArray = ResUtils.getStringArray(R.array.cabin_ac_comfort_options)
            stringArray.forEachIndexed { index, name ->
                if (name == value) {
                    val signalValue = index + 1;
                    val oldValue = viewModel.comfortLiveData.value;
                    val result = ACManager.instance.doUpdateACComfort(signalValue)
                    viewModel.comfortLiveData.value = signalValue
                    if (!result) {
                        viewModel.comfortLiveData.value = oldValue
                    } else {
                        val obtainAutoComfortOption = ACManager.instance.obtainAutoComfortOption()
                        LogManager.d("CabinACFragment", "obtainAutoComfortOption: $obtainAutoComfortOption")
                    }
                }
            }
        }
    }

    private fun monitorRadioOption() {
        val stringArray = ResUtils.getStringArray(R.array.cabin_ac_comfort_options)
        viewModel.comfortLiveData.let {
            val defIndex = it.value?.let { value ->
                if (value >= 0 && value < stringArray.size) value else 0x01
            } ?: 0x01
            updateComfortOption(defIndex, stringArray)
            it.observe(this) { value ->
                updateComfortOption(value, stringArray)
            }
        }
    }

    private fun updateComfortOption(value: Int, stringArray: Array<String>) {
        var title: String =  when (value) {
            0x01 -> {
                stringArray[0]
//                binding.cabinAcComfortOption.setSelection(stringArray[0])
            }
            0x02 -> {
                stringArray[1]
//                binding.cabinAcComfortOption.setSelection(stringArray[1])
            }
            0x03 -> {
                stringArray[2]
//                binding.cabinAcComfortOption.setSelection(stringArray[2])
            }
            else -> {
                stringArray[0]
//                binding.cabinAcComfortOption.setSelection(stringArray[0])
            }
        }
        updateComfortOption(title, stringArray)
    }

    private fun updateComfortOption(value: String, stringArray: Array<String>) {
        val any = stringArray.any { it == value }
        if (any) {
            binding.cabinAcComfortOption.setSelection(value, true)
        }
    }

    private fun monitorSwitchLiveData() {
        viewModel.aridLiveData.let {
            binding.cabinAcAutoAridSwb.setCheckedNoEvent(it.value == true)
            it.observe(this) { checked ->
                binding.cabinAcAutoAridSwb.setCheckedNoEvent(checked)
            }
        }
        viewModel.demistLiveData.let {
            binding.cabinAcAutoDemistSwb.setCheckedNoEvent(it.value == true)
            it.observe(this) { checked ->
                binding.cabinAcAutoDemistSwb.setCheckedNoEvent(checked)
            }
        }
        viewModel.windLiveData.let {
            binding.cabinAcAdvanceWindSwb.setCheckedNoEvent(it.value == true)
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
            Timber.d("doSwitchACOption arid result:$result, isChecked:$isChecked")
        }
        binding.cabinAcAutoDemistSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            val result =
                ACManager.instance.doSwitchACOption(ACManager.SwitchNape.AC_AUTO_DEMIST, isChecked)
            if (!result) {
                binding.cabinAcAutoDemistSwb.setCheckedNoEvent(!isChecked)
            }
            Timber.d("doSwitchACOption demist result:$result, isChecked:$isChecked")
        }
        binding.cabinAcAdvanceWindSwb.setOnCheckedChangeListener { buttonView, isChecked ->
            val result =
                ACManager.instance.doSwitchACOption(ACManager.SwitchNape.AC_ADVANCE_WIND, isChecked)
            if (!result) {
                binding.cabinAcAdvanceWindSwb.setCheckedNoEvent(!isChecked)
            }
            Timber.d("doSwitchACOption wind result:$result, isChecked:$isChecked")
        }

    }

}