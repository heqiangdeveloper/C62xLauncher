package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.adas.CombineManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CombineViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: CombineManager
        get() = CombineManager.instance

    val hmaValue: LiveData<SwitchState> by lazy { _hmaValue }

    private val _hmaValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_HMA
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val slaValue: LiveData<SwitchState> by lazy { _slaValue }

    private val _slaValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_TSR
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val node332: LiveData<SwitchState>
        get() = _node332

    private val _node332: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_332
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
            SwitchNode.ADAS_HMA -> {
                doUpdate(_hmaValue, status)
            }
            SwitchNode.ADAS_TSR -> {
                doUpdate(_slaValue, status)
            }
            SwitchNode.NODE_VALID_332 -> {
                doUpdate(_node332, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {

    }

}