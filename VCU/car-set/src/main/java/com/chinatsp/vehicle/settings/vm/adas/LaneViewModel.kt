package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.assistance.LaneManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaneViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: LaneManager
        get() = LaneManager.instance

    val laneAssistFunction: LiveData<Boolean> by lazy { _laneAssistFunction }

    private val _laneAssistFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_LANE_ASSIST
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val laneAssistMode: LiveData<Int> by lazy { _laneAssistMode }

    private val _laneAssistMode: MutableLiveData<Int> by lazy {
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(RadioNode.ADAS_LANE_ASSIST_MODE)
        }
    }

    val ldwWarningStyle: LiveData<Int> by lazy { _ldwWarningStyle }

    private val _ldwWarningStyle: MutableLiveData<Int> by lazy {
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(RadioNode.ADAS_LDW_STYLE)
        }
    }

    val ldwWarningSensitivity: LiveData<Int> by lazy { _ldwWarningSensitivity }

    private val _ldwWarningSensitivity: MutableLiveData<Int> by lazy {
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(RadioNode.ADAS_LDW_SENSITIVITY)
        }
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                _laneAssistFunction.value = status
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                _laneAssistMode.value = value
            }
            RadioNode.ADAS_LDW_STYLE -> {
                _ldwWarningStyle.value = value
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                _ldwWarningSensitivity.value = value
            }
            else -> {}
        }
    }

}