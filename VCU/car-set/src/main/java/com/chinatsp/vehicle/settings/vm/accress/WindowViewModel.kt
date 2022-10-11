package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WindowViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: WindowManager
        get() = WindowManager.instance

    val closeWinFollowLock: LiveData<SwitchState>
        get() = _closeWinFollowLock

    private val _closeWinFollowLock: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.WIN_CLOSE_FOLLOW_LOCK
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val closeWinWhileRain: LiveData<SwitchState>
        get() = _closeWinWhileRain

    private val _closeWinWhileRain: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.WIN_CLOSE_WHILE_RAIN
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val winRemoteControl: LiveData<SwitchState>
        get() = _winRemoteControl

    private val _winRemoteControl: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.WIN_REMOTE_CONTROL
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val rainWiperRepair: LiveData<SwitchState>
        get() = _rainWiperRepair

    private val _rainWiperRepair: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.RAIN_WIPER_REPAIR
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(serial = keySerial)
        super.onDestroy()
    }


    private fun doGetSwitchStatus(switchNode: SwitchNode): Boolean {
        return manager.doGetSwitchStatus(switchNode)
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.WIN_REMOTE_CONTROL -> {
                doUpdate(_winRemoteControl, status)
            }
            SwitchNode.WIN_CLOSE_FOLLOW_LOCK -> {
                doUpdate(_closeWinFollowLock, status)
            }
            SwitchNode.WIN_CLOSE_WHILE_RAIN -> {
                doUpdate(_closeWinWhileRain, status)
            }
            SwitchNode.RAIN_WIPER_REPAIR -> {
                doUpdate(_rainWiperRepair, status)
            }
            else -> {}
        }
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean,
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor liveData.value!! }?.postValue(value)
        return liveData
    }


}