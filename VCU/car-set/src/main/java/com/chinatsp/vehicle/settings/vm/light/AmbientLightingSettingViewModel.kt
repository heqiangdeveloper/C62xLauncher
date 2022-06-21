package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.manager.lamp.LightManager
import com.chinatsp.settinglib.optios.RadioNode
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

    val alcDoorHint: LiveData<Boolean>
        get() = _alcDoorHint

    private val _alcDoorHint: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_DOOR_HINT
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val alcLockHint: LiveData<Boolean>
        get() = _alcLockHint

    private val _alcLockHint: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val alcBreatheHint: LiveData<Boolean>
        get() = _alcBreatheHint

    private val _alcBreatheHint: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val alcComingHint: LiveData<Boolean>
        get() = _alcComingHint

    private val _alcComingHint: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_COMING_HINT
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val alcRelatedTopics: LiveData<Boolean>
        get() = _alcRelatedTopics

    private val _alcRelatedTopics: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
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

    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor (liveData.value == true) }?.value = value
        return liveData
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Int>,
        value: Int
    ): MutableLiveData<Int> {
        liveData.takeIf { value != liveData.value }?.value = value
        return liveData
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ALC_DOOR_HINT -> {
                updateLiveData(_alcDoorHint, status)
            }
            SwitchNode.ALC_LOCK_HINT -> {
                updateLiveData(_alcLockHint, status)
            }
            SwitchNode.ALC_BREATHE_HINT -> {
                updateLiveData(_alcBreatheHint, status)
            }
            SwitchNode.ALC_COMING_HINT -> {
                updateLiveData(_alcComingHint, status)
            }
            SwitchNode.ALC_RELATED_TOPICS -> {
                updateLiveData(_alcRelatedTopics, status)
            }
            else -> {}
        }
    }

}