package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WindowViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), ISwitchListener{

    private val manager: WindowManager
        get() = WindowManager.instance

    val closeWinFollowLock: LiveData<Boolean>
        get() = _closeWinFollowLock

    private val _closeWinFollowLock: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.WIN_CLOSE_FOLLOW_LOCK
        MutableLiveData(node.default).apply {
            updateLiveData(this, manager.doGetSwitchOption(node))
        }
    }

    val closeWinWhileRain: LiveData<Boolean>
        get() = _closeWinWhileRain

    private val _closeWinWhileRain: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.WIN_CLOSE_WHILE_RAIN
        MutableLiveData(node.default).apply {
            updateLiveData(this, manager.doGetSwitchOption(node))
        }
    }

    val winRemoteControl: LiveData<Boolean>
        get() = _winRemoteControl

    private val _winRemoteControl: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.WIN_REMOTE_CONTROL
        MutableLiveData(node.default).apply {
            updateLiveData(this, manager.doGetSwitchOption(node))
        }
    }

    val rainWiperRepair: LiveData<Boolean>
        get() = _rainWiperRepair

    private val _rainWiperRepair: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.RAIN_WIPER_REPAIR
        MutableLiveData(node.default).apply {
            updateLiveData(this, manager.doGetSwitchOption(node))
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


    private fun doGetSwitchStatus(switchNode: SwitchNode): Boolean {
        return manager.doGetSwitchStatus(switchNode)
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
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
            else ->{}
        }
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor liveData.value!! }?.postValue(value)
        return liveData
    }


}