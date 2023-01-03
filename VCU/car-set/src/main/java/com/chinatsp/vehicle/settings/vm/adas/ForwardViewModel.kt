package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IRadioListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForwardViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    val fcwFunction: LiveData<SwitchState>
        get() = _fcwFunction

    private val _fcwFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_FCW
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val aebFunction: LiveData<SwitchState>
        get() = _aebFunction

    private val _aebFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_AEB
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val node33F: LiveData<SwitchState>
        get() = _node33F

    private val _node33F: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_33F
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_FCW -> {
                doUpdate(_fcwFunction, status)
            }
            SwitchNode.ADAS_AEB -> {
                doUpdate(_aebFunction, status)
            }
            SwitchNode.NODE_VALID_33F -> {
                doUpdate(_node33F, status)
            }
            else -> {}
        }
    }

}