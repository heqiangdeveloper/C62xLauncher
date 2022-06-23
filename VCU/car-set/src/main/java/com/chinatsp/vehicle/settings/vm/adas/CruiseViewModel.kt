package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.adas.CruiseManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CruiseViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: CruiseManager
        get() = CruiseManager.instance

    val cruiseAssistFunction: LiveData<Boolean> by lazy { _cruiseAssistFunction }

    private val _cruiseAssistFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_IACC
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val targetPromptFunction: LiveData<Boolean> by lazy { _targetPromptFunction }

    private val _targetPromptFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_TARGET_PROMPT
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val limberLeaveFunction: LiveData<Boolean> by lazy { _limberLeaveFunction }

    private val _limberLeaveFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_LIMBER_LEAVE
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val limberLeaveRadio: LiveData<Int> by lazy { _limberLeaveRadio }

    private val _limberLeaveRadio: MutableLiveData<Int> by lazy {
        val radioNode = RadioNode.ADAS_LIMBER_LEAVE
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(radioNode)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_IACC -> _cruiseAssistFunction.value = status
            SwitchNode.ADAS_TARGET_PROMPT -> _targetPromptFunction.value = status
            SwitchNode.ADAS_LIMBER_LEAVE -> _limberLeaveFunction.value = status
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        if (RadioNode.ADAS_LIMBER_LEAVE == node) {
            _limberLeaveRadio.value = value
        }

    }

}