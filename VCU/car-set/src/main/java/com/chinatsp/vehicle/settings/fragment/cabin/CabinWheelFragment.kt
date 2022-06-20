package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinWhellFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringHeatDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringKeysDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.SteeringViewModel
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
class CabinWheelFragment : BaseFragment<SteeringViewModel, CabinWhellFragmentBinding>() {

    private val manager: WheelManager
        get() = WheelManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_whell_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initSwitchOption() {
        val hintId =
            if (viewModel.swhFunction.value == true) R.string.switch_turn_on else R.string.switch_turn_off
        binding.wheelAutomaticHeatingTv.text = ResUtils.getString(hintId)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.swhFunction.observe(this) {
            val hintId = if (it) R.string.switch_turn_on else R.string.switch_turn_off
            binding.wheelAutomaticHeatingTv.text = ResUtils.getString(hintId)
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DRIVE_EPS_MODE, viewModel.epsMode)
    }

    private fun addRadioLiveDataListener() {
        viewModel.epsMode.observe(this) {
            doUpdateRadio(RadioNode.DRIVE_EPS_MODE, it, false)
        }
    }

    private fun setRadioListener() {
        binding.wheelEpsModeTabView.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.DRIVE_EPS_MODE, value, viewModel.epsMode, it)
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
        val tabView = when (node) {
            RadioNode.DRIVE_EPS_MODE -> binding.wheelEpsModeTabView
            else -> null
        }
        tabView?.let {
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

    private fun setCheckedChangeListener() {
        binding.wheelCustomKeys.setOnClickListener {
            val fragment = SteeringKeysDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment::javaClass.name)
            }
        }
        binding.wheelAutomaticHeating.setOnClickListener {
            val fragment = SteeringHeatDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment::javaClass.name)
            }
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }
}