package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.cabin.access.DoorManager
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

    val autoDoorUnlock: LiveData<RadioState>
        get() = _autoDoorUnlock

    private val _autoDoorUnlock: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DOOR_QUENCH_UNLOCK
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val smartDoorAccess: LiveData<SwitchState>
        get() = _smartDoorAccess

    private val _smartDoorAccess: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DOOR_SMART_ENTER
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val nfcInner: LiveData<SwitchState>
        get() = _nfcInner

    private val _nfcInner: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.INNER_NFC
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val nfcOuter: LiveData<SwitchState>
        get() = _nfcOuter

    private val _nfcOuter: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.OUTER_NFC
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
            SwitchNode.INNER_NFC -> {
                doUpdate(_nfcInner, status)
            }
            SwitchNode.OUTER_NFC -> {
                doUpdate(_nfcOuter, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.DOOR_DRIVE_LOCK -> {
                doUpdate(_automaticDoorLock, value)
            }
            RadioNode.DOOR_QUENCH_UNLOCK -> {
                doUpdate(_autoDoorUnlock, value)
            }
            else -> {}
        }
    }

}