package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.listener.IThemeChangeListener
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.service.ThemeService
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.TrailerRemindDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.TrailerViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TrailerRemindDialogFragment :
    BaseDialogFragment<TrailerViewModel, TrailerRemindDialogFragmentBinding>(),
    IThemeChangeListener {

    private lateinit var service: ThemeService

    override fun getLayoutId(): Int {
        return R.layout.trailer_remind_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
        initService()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initService() {
        service = ThemeService(activity)
        service.addListener("TrailerRemindDialog", this)
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service.removeListener("TrailerRemindDialog")
    }

    override fun onChange(night: Boolean) {

    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DEVICE_TRAILER_DISTANCE, viewModel.distance)
        initRadioOption(RadioNode.DEVICE_TRAILER_SENSITIVITY, viewModel.sensitivity)
    }

    private fun addRadioLiveDataListener() {
        viewModel.distance.observe(this) {
            doUpdateRadio(RadioNode.DEVICE_TRAILER_DISTANCE, it, false)
        }
        viewModel.sensitivity.observe(this) {
            doUpdateRadio(RadioNode.DEVICE_TRAILER_SENSITIVITY, it, false)
        }
    }

    private fun setRadioListener() {
        binding.trailerDistanceRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.DEVICE_TRAILER_DISTANCE
                doUpdateRadio(node, value, viewModel.distance, it)
            }
        }
        binding.trailerSensitivityRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
                doUpdateRadio(node, value, viewModel.sensitivity, it)
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
        Timber.d("doUpdateRadio start node:$node, value:$value")
        val result = isCanToInt(value) && viewModel.doSetRadioOption(node, value.toInt())
        Timber.d("doUpdateRadio end node:$node, value:$value, result:$result")
        tabView.takeIf { !result }?.let {
            val result = node.obtainSelectValue(liveData.value!!)
            it.setSelection(result.toString(), true)
        }
    }

//    private fun doUpdateRadio(node: RadioNode, value: Int, immediately: Boolean = false) {
//        val tabView = when (node) {
//            RadioNode.DEVICE_TRAILER_DISTANCE -> binding.trailerDistanceRadio
//            RadioNode.DEVICE_TRAILER_SENSITIVITY -> binding.trailerSensitivityRadio
//            else -> null
//        }
//        takeIf { null != tabView }?.doUpdateRadio(tabView!!, value, immediately)
//    }

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

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> binding.trailerDistanceRadio
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> binding.trailerSensitivityRadio
            else -> null
        }
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            val result = node.obtainSelectValue(value)
            doUpdateRadio(it, result, immediately)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_TRAILER_REMIND, viewModel.trailerFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.trailerFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_TRAILER_REMIND, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> binding.trailerRemindSwitch
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
        binding.trailerRemindSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_TRAILER_REMIND, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = viewModel.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }
}