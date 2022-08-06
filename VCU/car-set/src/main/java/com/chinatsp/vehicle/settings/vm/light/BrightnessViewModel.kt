package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrightnessViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model) {

    private val manager: IProgressManager by lazy {
        BrightnessManager.instance
    }

    val acScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.AC_SCREEN)?.copy()
        }
    }

    val carScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.CAR_SCREEN)?.copy()
        }
    }

    val meterScreenVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.METER_SCREEN)?.copy()
        }
    }
}