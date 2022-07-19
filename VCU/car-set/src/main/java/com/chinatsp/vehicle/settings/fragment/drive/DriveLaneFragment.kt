package com.chinatsp.vehicle.settings.fragment.drive

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.adas.LaneManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLaneFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.LaneViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLaneFragment : BaseFragment<LaneViewModel, DriveLaneFragmentBinding>() {

    private val manager: LaneManager
        get() = LaneManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_lane_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initVideoListener()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_auxiliary_system
        binding.video.setVideoURI(Uri.parse(uri));
        binding.video.setOnCompletionListener {
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
    }
    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_LANE_ASSIST_MODE, viewModel.laneAssistMode)
        initRadioOption(RadioNode.ADAS_LDW_STYLE, viewModel.ldwStyle)
        initRadioOption(RadioNode.ADAS_LDW_SENSITIVITY, viewModel.ldwSensitivity)
    }

    private fun addRadioLiveDataListener() {
        viewModel.laneAssistMode.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LANE_ASSIST_MODE, it, false)
        }
        viewModel.ldwStyle.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LDW_STYLE, it, false)
        }
        viewModel.ldwSensitivity.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LDW_SENSITIVITY, it, false)
        }
    }

    private fun setRadioListener() {
        binding.adasLaneLaneAssistRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LANE_ASSIST_MODE, value, viewModel.laneAssistMode, it)
            }
        }
        binding.adasLaneLdwStyleRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LDW_STYLE, value, viewModel.ldwStyle, it)
            }
        }
        binding.adasLaneLdwSensitivityRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(
                    RadioNode.ADAS_LDW_SENSITIVITY, value, viewModel.ldwSensitivity, it
                )
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
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                binding.adasLaneLaneAssistRadio.getChildAt(0).visibility = View.GONE
                binding.adasLaneLaneAssistRadio
            }
            RadioNode.ADAS_LDW_STYLE -> binding.adasLaneLdwStyleRadio
            RadioNode.ADAS_LDW_SENSITIVITY -> binding.adasLaneLdwSensitivityRadio
            else -> null
        }
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            doUpdateRadio(it, value, immediately)
        }
    }


    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.get.values.map { it.toString() }.toTypedArray()
            //tabView.setItems(names, values)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_LANE_ASSIST, viewModel.laneAssistFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.laneAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_LANE_ASSIST, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> binding.adasLaneLaneAssistSwitch
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
        dynamicEffect()
    }

    private fun setSwitchListener() {
        binding.adasLaneLaneAssistSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
           if(isChecked){
               binding.videoImage.visibility = View.GONE
               binding.video.start()
           }else{
               dynamicEffect()
           }
            doUpdateSwitchOption(SwitchNode.ADAS_LANE_ASSIST, buttonView, isChecked)
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

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        if (binding.adasLaneLaneAssistSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_lane_assist) })
        } else {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.intelligent_cruise) })
        }
    }
}

