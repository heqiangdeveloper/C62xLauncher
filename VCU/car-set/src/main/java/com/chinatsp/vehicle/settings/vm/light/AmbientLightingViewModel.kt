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
class AmbientLightingViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: IOptionManager
        get() = AmbientLightingManager.instance

    val frontLighting: LiveData<Boolean>
        get() = _frontLighting

    private val _frontLighting: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val backLighting: LiveData<Boolean>
        get() = _backLighting

    private val _backLighting: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
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
            SwitchNode.FRONT_AMBIENT_LIGHTING -> {
                updateLiveData(_frontLighting, status)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING -> {
                updateLiveData(_backLighting, status)
            }
            else -> {}
        }
    }

}