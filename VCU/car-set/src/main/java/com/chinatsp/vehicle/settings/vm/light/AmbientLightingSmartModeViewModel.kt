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
class AmbientLightingSmartModeViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: IOptionManager
        get() = AmbientLightingManager.instance

    val alcSmartMode: LiveData<Boolean>
        get() = _alcSmartMode

    private val _alcSmartMode: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ALC_SMART_MODE
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
            SwitchNode.ALC_SMART_MODE -> {
                updateLiveData(_alcSmartMode, status)
            }
            else -> {}
        }
    }

}