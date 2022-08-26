package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val automaticDoorLock: LiveData<Int>
        get() = _automaticDoorLock

    private val _automaticDoorLock: MutableLiveData<Int> by lazy {
        val node = RadioNode.DOOR_DRIVE_LOCK
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val automaticDoorUnlock: LiveData<Int>
        get() = _automaticDoorUnlock

    private val _automaticDoorUnlock: MutableLiveData<Int> by lazy {
        val node = RadioNode.DOOR_FLAMEOUT_UNLOCK
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val smartDoorAccess: LiveData<Boolean>
        get() = _smartDoorAccess

    private val _smartDoorAccess: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DOOR_SMART_ENTER
        MutableLiveData(node.default).apply {
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
            SwitchNode.DOOR_SMART_ENTER -> {
                doUpdate(_smartDoorAccess, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
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