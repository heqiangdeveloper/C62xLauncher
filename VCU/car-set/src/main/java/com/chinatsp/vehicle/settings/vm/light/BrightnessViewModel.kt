package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BrightnessViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IProgressListener {

    private val manager: IProgressManager by lazy {
        BrightnessManager.instance
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(serial = keySerial)
        super.onDestroy()
    }

    val acScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.CONDITIONER_SCREEN_BRIGHTNESS)?.copy()
        }
    }

    val hostScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.HOST_SCREEN_BRIGHTNESS)?.copy()
        }
    }

    val meterScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.METER_SCREEN_BRIGHTNESS)?.copy()
        }
    }

    private fun updateVolumeValue(liveData: MutableLiveData<Volume>, node: Progress, value: Int) {
        liveData.value?.let {
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
}