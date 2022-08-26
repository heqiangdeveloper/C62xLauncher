package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val ambientBrightness: LiveData<Int>
        get() = _ambientBrightness

    private val _ambientBrightness: MutableLiveData<Int> by lazy {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        MutableLiveData(0).apply {
            val value = manager.doGetProgress(node)
            updateLiveData(this, value)
        }
    }

    val ambientColor: LiveData<Int>
        get() = _ambientColor

    private val _ambientColor: MutableLiveData<Int> by lazy {
        val node = Progress.AMBIENT_LIGHT_COLOR
        MutableLiveData(0).apply {
            val value = manager.doGetProgress(node)
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

    private fun updateLiveData(ld: MutableLiveData<Boolean>, v: Boolean): MutableLiveData<Boolean> {
        ld.takeIf { v xor (ld.value == true) }?.value = v
        return ld
    }

    private fun updateLiveData(ld: MutableLiveData<Int>, v: Int): MutableLiveData<Int> {
        ld.takeIf { v != ld.value }?.value = v
        return ld
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

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                doUpdate(_ambientBrightness, value)
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                doUpdate(_ambientColor, value)
            }
            else -> {}
        }
    }

    fun onAmbientColorChanged(value: Int) {
        val result = manager.doSetProgress(Progress.AMBIENT_LIGHT_COLOR, value)
        if (result) {
            doUpdate(_ambientColor, value)
        }
        Timber.d("onAmbientColorChanged progress:AMBIENT_LIGHT_COLOR, color:%s", value)
    }

    fun doBrightnessChanged(node: Progress, value: Int) {
        val result = manager.doSetProgress(node, value)
        if (result) _ambientBrightness.value = value
    }

}