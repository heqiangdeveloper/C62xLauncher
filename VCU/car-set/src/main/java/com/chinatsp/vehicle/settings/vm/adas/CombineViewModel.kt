package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
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

    val hmaValue: LiveData<Boolean> by lazy { _hmaValue }

    private val _hmaValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_HMA
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val slaValue: LiveData<Boolean> by lazy { _slaValue }

    private val _slaValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_TSR
        MutableLiveData(manager.doGetSwitchOption(node))
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
            SwitchNode.ADAS_HMA -> {
                doUpdate(_hmaValue, status)
            }
            SwitchNode.ADAS_TSR -> {
                doUpdate(_slaValue, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {

    }

}