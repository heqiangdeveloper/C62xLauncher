package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.assistance.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForwardViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    val fcwFunction: LiveData<Boolean> by lazy { _fcwFunction }

    private val _fcwFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_FCW
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val aebFunction: LiveData<Boolean> by lazy { _aebFunction }

    private val _aebFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_AEB
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_FCW -> {
                _fcwFunction.value = status
            }
            SwitchNode.ADAS_AEB -> {
                _aebFunction.value = status
            }
            else -> {}
        }
    }

}