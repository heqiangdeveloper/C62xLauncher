package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForwardViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), ISwitchListener {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    val fcwFunction: LiveData<Boolean>
        get() = _fcwFunction

    private val _fcwFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_FCW
        MutableLiveData(node.isOn()).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    val aebFunction: LiveData<Boolean>
        get() = _aebFunction

    private val _aebFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_AEB
        MutableLiveData(node.isOn()).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_FCW -> {
                _fcwFunction.postValue(status)
            }
            SwitchNode.ADAS_AEB -> {
                _aebFunction.postValue(status)
            }
            else -> {}
        }
    }

}