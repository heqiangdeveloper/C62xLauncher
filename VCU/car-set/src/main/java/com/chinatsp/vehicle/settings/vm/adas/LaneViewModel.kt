package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.adas.LaneManager
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

    val laneAssist: LiveData<SwitchState> by lazy { _laneAssist }

    private val _laneAssist: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_LANE_ASSIST
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val laneAssistMode: LiveData<RadioState> by lazy { _laneAssistMode }

    private val _laneAssistMode: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ADAS_LANE_ASSIST_MODE
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val ldwStyle: LiveData<RadioState> by lazy { _ldwStyle }

    private val _ldwStyle: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ADAS_LDW_STYLE
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val ldwSensitivity: LiveData<RadioState> by lazy { _ldwSensitivity }

    private val _ldwSensitivity: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ADAS_LDW_SENSITIVITY
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val node332: LiveData<SwitchState>
        get() = _node332

    private val _node332: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_332
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    val node621: LiveData<SwitchState>
        get() = _node621

    private val _node621: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_621
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                doUpdate(_laneAssist, status)
            }
            SwitchNode.NODE_VALID_332 -> {
                doUpdate(_node332, status)
            }
            SwitchNode.NODE_VALID_621 -> {
                doUpdate(_node621, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
                doUpdate(_laneAssistMode, value)
            }
            RadioNode.ADAS_LDW_STYLE -> {
                doUpdate(_ldwStyle, value)
            }
            RadioNode.ADAS_LDW_SENSITIVITY -> {
                doUpdate(_ldwSensitivity, value)
            }
            else -> {}
        }
    }

}