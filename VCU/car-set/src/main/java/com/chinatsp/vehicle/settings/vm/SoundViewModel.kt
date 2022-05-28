package com.chinatsp.vehicle.settings.vm

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.SoundManager
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import com.common.xui.widget.picker.VerticalSeekBar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(app: Application, model: BaseModel)
    : BaseViewModel(app, model){
    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(-1) }

    val phoneSoundVolume: MutableLiveData<Int> by lazy {
        MutableLiveData(1).also { it.value = SoundManager.getInstance().phoneVolume }
    }
    val naviSoundVolume: MutableLiveData<Int> by lazy {
        MutableLiveData(1).also { it.value = SoundManager.getInstance().naviVolume }
    }
    val voiceSoundVolume: MutableLiveData<Int> by lazy {
        MutableLiveData(1).also { it.value = SoundManager.getInstance().cruiseVolume }
    }
    val mediaSoundVolume: MutableLiveData<Int> by lazy {
        MutableLiveData(1).also { it.value = SoundManager.getInstance().mediaVolume }
    }
    val systemSoundVolume: MutableLiveData<Int> by lazy {
        MutableLiveData(1).also { it.value = SoundManager.getInstance().systemVolume }
    }

    val phoneMaxVolume:Int
        get() = SoundManager.getInstance().phoneMaxVolume
    val naviMaxVolume:Int
        get() = SoundManager.getInstance().naviMaxVolume
    val voiceMaxVolume:Int
        get() = SoundManager.getInstance().cruiseMaxVolume
    val mediaMaxVolume:Int
        get() = SoundManager.getInstance().mediaMaxVolume
    val systemMaxVolume:Int
        get() = SoundManager.getInstance().systemMaxVolume

}