package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AmbientLightingViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener, IProgressListener {

    private val manager: AmbientLightingManager
        get() = AmbientLightingManager.instance

    val frontLighting: LiveData<SwitchState>
        get() = _frontLighting

    private val _frontLighting: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val backLighting: LiveData<SwitchState>
        get() = _backLighting

    private val _backLighting: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val ambientBrightness: LiveData<Volume>
        get() = _ambientBrightness

    private val _ambientBrightness: MutableLiveData<Volume> by lazy {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        MutableLiveData<Volume>().apply {
            val value = manager.doGetVolume(node)
            this.value = value
        }
    }

    val ambientColor: LiveData<Volume>
        get() = _ambientColor

    private val _ambientColor: MutableLiveData<Volume> by lazy {
        val node = Progress.AMBIENT_LIGHT_COLOR
        MutableLiveData<Volume>().apply {
            val value = manager.doGetVolume(node)
            this.value = value
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


    private fun updateLiveData(ld: MutableLiveData<Boolean>, v: Boolean): MutableLiveData<Boolean> {
        ld.takeIf { v xor (ld.value == true) }?.value = v
        return ld
    }

    private fun updateLiveData(ld: MutableLiveData<Int>, v: Int): MutableLiveData<Int> {
        ld.takeIf { v != ld.value }?.value = v
        return ld
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.FRONT_AMBIENT_LIGHTING -> {
                doUpdate(_frontLighting, status)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING -> {
                doUpdate(_backLighting, status)
            }
            else -> {}
        }
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                doUpdateProgress(_ambientBrightness, value)
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                doUpdateProgress(_ambientColor, value)
            }
            else -> {}
        }
    }

    fun onAmbientColorChanged(value: Int) {
//        val result = manager.doSetProgress(Progress.AMBIENT_LIGHT_COLOR, value)
//        if (result) {
//            doUpdate(_ambientColor, value)
//        }
    }

    fun doBrightnessChanged(node: Progress, value: Int) {
        val result = manager.doSetVolume(node, value)
        if (result) _ambientBrightness.value?.pos = value
    }

}