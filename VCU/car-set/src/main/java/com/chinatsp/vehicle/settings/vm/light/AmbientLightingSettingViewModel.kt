package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AmbientLightingSettingViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: IOptionManager
        get() = AmbientLightingManager.instance

    val alcDoorHint: LiveData<SwitchState>
        get() = _alcDoorHint

    private val _alcDoorHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_DOOR_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val alcLockHint: LiveData<SwitchState>
        get() = _alcLockHint

    private val _alcLockHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val alcBreatheHint: LiveData<SwitchState>
        get() = _alcBreatheHint

    private val _alcBreatheHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val alcComingHint: LiveData<SwitchState>
        get() = _alcComingHint

    private val _alcComingHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_COMING_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val alcRelatedTopics: LiveData<SwitchState>
        get() = _alcRelatedTopics

    private val _alcRelatedTopics: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val node65A: LiveData<SwitchState>
        get() = _node65A

    private val _node65A: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_65A
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

    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean,
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor (liveData.value == true) }?.value = value
        return liveData
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Int>,
        value: Int,
    ): MutableLiveData<Int> {
        liveData.takeIf { value != liveData.value }?.value = value
        return liveData
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.ALC_DOOR_HINT -> {
                doUpdate(_alcDoorHint, status)
            }
            SwitchNode.ALC_LOCK_HINT -> {
                doUpdate(_alcLockHint, status)
            }
            SwitchNode.ALC_BREATHE_HINT -> {
                doUpdate(_alcBreatheHint, status)
            }
            SwitchNode.ALC_COMING_HINT -> {
                doUpdate(_alcComingHint, status)
            }
            SwitchNode.ALC_RELATED_TOPICS -> {
                doUpdate(_alcRelatedTopics, status)
            }
            SwitchNode.NODE_VALID_65A->{
                doUpdate(_node65A,status)
            }
            else -> {}
        }
    }

}