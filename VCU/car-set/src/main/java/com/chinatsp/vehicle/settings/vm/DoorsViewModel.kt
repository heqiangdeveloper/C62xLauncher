package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoorsViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: DoorManager
        get() = DoorManager.instance

    val automaticDoorLock: LiveData<RadioState>
        get() = _automaticDoorLock

    private val _automaticDoorLock: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DOOR_DRIVE_LOCK
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val automaticDoorUnlock: LiveData<RadioState>
        get() = _automaticDoorUnlock

    private val _automaticDoorUnlock: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DOOR_FLAMEOUT_UNLOCK
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val smartDoorAccess: LiveData<SwitchState>
        get() = _smartDoorAccess

    private val _smartDoorAccess: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DOOR_SMART_ENTER
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

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.DOOR_SMART_ENTER -> {
                doUpdate(_smartDoorAccess, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> {
                doUpdate(_automaticDoorLock, value)
            }
            RadioNode.DOOR_FLAMEOUT_UNLOCK -> {
                doUpdate(_automaticDoorUnlock, value)
            }
            else -> {}
        }
    }

}