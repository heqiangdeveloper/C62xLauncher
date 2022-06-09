package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.access.BackMirrorManager
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

    val electricFunction: LiveData<Boolean> by lazy { _electricFunction }

    private val _electricFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.AS_STERN_ELECTRIC
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val lightAlarmFunction: LiveData<Boolean> by lazy { _lightAlarmFunction }

    private val _lightAlarmFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.AS_STERN_LIGHT_ALARM
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val audioAlarmFunction: LiveData<Boolean> by lazy { _audioAlarmFunction }

    private val _audioAlarmFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.AS_STERN_AUDIO_ALARM
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val sternSmartEnterFunction: LiveData<Int> by lazy { _sternSmartEnterFunction }

    private val _sternSmartEnterFunction: MutableLiveData<Int> by lazy {
        val radioNode = RadioNode.ACCESS_STERN_SMART_ENTER
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(radioNode)
        }
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                _electricFunction.value = status
            }
            SwitchNode.AS_STERN_LIGHT_ALARM -> {
                _lightAlarmFunction.value = status
            }
            SwitchNode.AS_STERN_AUDIO_ALARM -> {
                _audioAlarmFunction.value = status
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        if (RadioNode.ACCESS_STERN_SMART_ENTER == node) {
            _sternSmartEnterFunction.value = value
        }

    }

}