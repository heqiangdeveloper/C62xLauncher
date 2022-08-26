package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.CabinWhellFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringHeatDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringKeysDialogFragment
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
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

    private val steeringKeysCustom: String
        get() = "STEERING_KEYS_CUSTOM"

    private val steeringAutoHeating: String
        get() = "STEERING_AUTO_HEATING"

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initRouteListener()

        initViewsDisplay()
    }

    private fun showHintDialog(title: Int, content: Int) {
        HintHold.setTitle(title)
        HintHold.setContent(content)
        val fragment = DetailsDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4)) {
            binding.wheelAutomaticHeating.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
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
                if (!Applet.isCanSwitchEps(15f)) {
                    showHintDialog(
                        R.string.vcu_action_switch_failed,
                        R.string.vcu_eps_action_switch_check
                    )
                    it.setSelection(viewModel.epsMode.value.toString(), true)
                } else {
                    doUpdateRadio(RadioNode.DRIVE_EPS_MODE, value, viewModel.epsMode, it)
                    Toast.showToast(context, getString(R.string.vcu_eps_action_switching), true)
                }
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
        tabView.takeIf { !result }?.let {
            val result = node.obtainSelectValue(liveData.value!!)
            it.setSelection(result.toString(), true)
        }
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
            bindRadioData(node, tabView, isInit)
            val result = node.obtainSelectValue(value)
            doUpdateRadio(it, result, immediately)
        }
    }

    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.set.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun setCheckedChangeListener() {
        binding.wheelCustomKeys.setOnClickListener {
            showDialogFragment(steeringKeysCustom)
        }
        binding.wheelAutomaticHeating.setOnClickListener {
            showDialogFragment(steeringAutoHeating)
        }
    }

    private fun showDialogFragment(serial: String) {
        var fragment: DialogFragment? = null
        if (steeringKeysCustom == serial) {
            cleanPopupSerial(serial)
            fragment = SteeringKeysDialogFragment()
        } else if (steeringAutoHeating == serial) {
            cleanPopupSerial(serial)
            fragment = SteeringHeatDialogFragment()
        }
        activity?.supportFragmentManager?.let {
            fragment?.show(it, fragment::javaClass.name)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }

    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            iroute.cleanPopupLiveDate(serial)
        }
    }

    private fun initRouteListener() {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            val liveData = iroute.obtainPopupLiveData()
            liveData.observe(this) {
                if (it.equals(steeringKeysCustom)) {
                    showDialogFragment(it)
                } else if (it.equals(steeringAutoHeating)) {
                    showDialogFragment(it)
                }
            }
        }
    }
}