package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.access.AccessManager
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
        val node = RadioNode.ACCESS_DOOR_DRIVE_LOCK
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            this.value = value
        }
    }

    val automaticDoorUnlock: LiveData<Int>
        get() = _automaticDoorUnlock

    private val _automaticDoorUnlock: MutableLiveData<Int> by lazy {
        val node = RadioNode.ACCESS_DOOR_FLAMEOUT_UNLOCK
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            this.value = value
        }
    }

    val smartDoorAccess: LiveData<Boolean>
        get() = _smartDoorAccess

    private val _smartDoorAccess: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AS_SMART_ENTER_DOOR
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
            SwitchNode.AS_SMART_ENTER_DOOR -> {
                _smartDoorAccess.takeIf { status xor (it.value == true) }?.value = status
            }
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.ACCESS_DOOR_DRIVE_LOCK -> {
                _automaticDoorLock.takeIf {
                    node.isValid(value) && (value != it.value)
                }?.value = value
            }
            RadioNode.ACCESS_DOOR_FLAMEOUT_UNLOCK -> {
                _automaticDoorUnlock.takeIf {
                    node.isValid(value) && (value != it.value)
                }?.value = value
            }
            else -> {}
        }
    }

}