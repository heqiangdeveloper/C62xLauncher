package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SternDoorViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    val electricFunction: LiveData<Boolean> get() = _electricFunction

    private val _electricFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AS_STERN_ELECTRIC
        MutableLiveData(node.default).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    val lightAlarmFunction: LiveData<Boolean> get() = _lightAlarmFunction

    private val _lightAlarmFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.STERN_LIGHT_ALARM
        MutableLiveData(node.default).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    val audioAlarmFunction: LiveData<Boolean> get() = _audioAlarmFunction

    private val _audioAlarmFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.STERN_AUDIO_ALARM
        MutableLiveData(node.default).apply {
            value = manager.doGetSwitchOption(node)
        }
    }

    val sternSmartEnterFunction: LiveData<Int> get() =  _sternSmartEnterFunction

    private val _sternSmartEnterFunction: MutableLiveData<Int> by lazy {
        val node = RadioNode.STERN_SMART_ENTER
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(serial = keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                doUpdate(_electricFunction, status)
            }
            SwitchNode.STERN_LIGHT_ALARM -> {
                doUpdate(_lightAlarmFunction, status)
            }
            SwitchNode.STERN_AUDIO_ALARM -> {
                doUpdate(_audioAlarmFunction, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        if (RadioNode.STERN_SMART_ENTER == node) {
            doUpdate(_sternSmartEnterFunction, value)
        }
    }

}