package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
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

    val laneAssistFunction: LiveData<Boolean> by lazy { _laneAssistFunction }

    private val _laneAssistFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_LANE_ASSIST
        MutableLiveData(node.default).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    val laneAssistMode: LiveData<Int> by lazy { _laneAssistMode }

    private val _laneAssistMode: MutableLiveData<Int> by lazy {
        val node = RadioNode.ADAS_LANE_ASSIST_MODE
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val ldwStyle: LiveData<Int> by lazy { _ldwStyle }

    private val _ldwStyle: MutableLiveData<Int> by lazy {
        val node = RadioNode.ADAS_LDW_STYLE
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val ldwSensitivity: LiveData<Int> by lazy { _ldwSensitivity }

    private val _ldwSensitivity: MutableLiveData<Int> by lazy {
        val node = RadioNode.ADAS_LDW_SENSITIVITY
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> {
                doUpdate(_laneAssistFunction, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
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