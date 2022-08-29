package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
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
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val colourBreathe: LiveData<Boolean>
        get() = _colourBreathe
    private val _colourBreathe: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.COLOUR_BREATHE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val musicRhythm: LiveData<Boolean>
        get() = _musicRhythm
    private val _musicRhythm: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.MUSIC_RHYTHM
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val speedRhythm: LiveData<Boolean>
        get() = _speedRhythm
    private val _speedRhythm: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.SPEED_RHYTHM
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
                doUpdate(_alcSmartMode, status)
            }
            SwitchNode.SPEED_RHYTHM -> {
                doUpdate(_speedRhythm, status)
            }
            SwitchNode.MUSIC_RHYTHM -> {
                doUpdate(_musicRhythm, status)
            }
            SwitchNode.COLOUR_BREATHE -> {
                doUpdate(_colourBreathe, status)
            }
            else -> {}
        }
    }

    fun doUpdateViewStatus(node: SwitchNode, status: Boolean): Boolean {
        val result = manager.doSetSwitchOption(node, status)
        if (result) {
            val liveData = when (node) {
                SwitchNode.SPEED_RHYTHM -> _speedRhythm
                SwitchNode.MUSIC_RHYTHM -> _musicRhythm
                SwitchNode.COLOUR_BREATHE -> _colourBreathe
                else -> null
            }
            liveData?.value = status
        }
        return result
    }

}