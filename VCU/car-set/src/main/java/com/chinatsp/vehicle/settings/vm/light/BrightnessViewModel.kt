package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BrightnessViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IProgressListener, ISwitchListener {

    private val manager: BrightnessManager by lazy {
        BrightnessManager.instance
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(serial = keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    val lightAutoMode: LiveData<SwitchState> by lazy { _lightAutoMode }

    private val _lightAutoMode: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LIGHT_AUTO_MODE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val node598: LiveData<SwitchState>
        get() = _node598

    private val _node598: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_598
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    val node5D4: LiveData<SwitchState>
        get() = _node5D4

    private val _node5D4: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_5D4
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    val acScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.CONDITIONER_SCREEN_BRIGHTNESS)
        }
    }

    val hostScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.HOST_SCREEN_BRIGHTNESS)
        }
    }

    val meterScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.METER_SCREEN_BRIGHTNESS)
        }
    }

    private fun updateVolumeValue(liveData: MutableLiveData<Volume>, node: Progress, value: Int) {

        liveData.value?.let {
            liveData.postValue(it)
            Timber.e("BrightnessManager $node, v:${it.pos}, value:$value")
            if (true) return
            val isMin = it.min == node.min
            val isMax = it.max == node.max
            val isPos = it.pos == value
            Timber.d("updateVolumeValue mode:$node, value:$value, isMin:$isMin, isMax:$isMax, isPos:$isPos")
            if (isMin && isMax && isPos) {
                return
            }
            if (!isMin) {
                it.min = node.min
            }
            if (!isMax) {
                it.max = node.max
            }
            if (!isPos) {
                it.pos = value
            }
            liveData.postValue(it)
        }
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.HOST_SCREEN_BRIGHTNESS -> {
                updateVolumeValue(hostScreenVolume, node, value)
            }
            Progress.METER_SCREEN_BRIGHTNESS -> {
                updateVolumeValue(meterScreenVolume, node, value)
            }
            Progress.CONDITIONER_SCREEN_BRIGHTNESS -> {
                updateVolumeValue(acScreenVolume, node, value)
            }
            else -> {}
        }
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.LIGHT_AUTO_MODE -> {
                doUpdate(_lightAutoMode, status)
            }
            SwitchNode.NODE_VALID_598->{
                doUpdate(_node598,status)
            }
            SwitchNode.NODE_VALID_5D4->{
                doUpdate(_node5D4,status)
            }
            else -> {

            }
        }
    }
}