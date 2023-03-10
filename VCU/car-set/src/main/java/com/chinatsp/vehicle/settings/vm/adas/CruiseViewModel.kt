package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
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

    val cruiseAssistFunction: LiveData<SwitchState> by lazy { _cruiseAssistFunction }

    private val _cruiseAssistFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_IACC
        MutableLiveData(manager.doGetSwitchOption(node))
    }


    val limberLeaveFunction: LiveData<SwitchState> by lazy { _limberLeaveFunction }

    private val _limberLeaveFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_LIMBER_LEAVE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val limberLeaveRadio: LiveData<RadioState> by lazy { _limberLeaveRadio }

    private val _limberLeaveRadio: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ADAS_LIMBER_LEAVE
        MutableLiveData(manager.doGetRadioOption(node))
    }
    val node33F: LiveData<SwitchState>
        get() = _node33F

    private val _node33F: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_33F
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
        manager.unRegisterVcuListener(keySerial, keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_IACC -> doUpdate(_cruiseAssistFunction, status)
            SwitchNode.ADAS_LIMBER_LEAVE -> doUpdate(_limberLeaveFunction, status)
            SwitchNode.NODE_VALID_33F -> doUpdate(_node33F, status)
            SwitchNode.NODE_VALID_621 -> doUpdate(_node621, status)
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        if (RadioNode.ADAS_LIMBER_LEAVE == node) {
            doUpdate(_limberLeaveRadio, value)
        }

    }

}