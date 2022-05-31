package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.SoundManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.bean.Volume
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(app: Application, model: BaseModel)
    : BaseViewModel(app, model), ISoundListener{
    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(-1) }

    override fun onCreate() {
        super.onCreate()
        keySerial = VoiceManager.instance.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        VoiceManager.instance.unRegisterVcuListener(keySerial)
    }

    val naviVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            val pos = SoundManager.getInstance().naviVolume
            val max = SoundManager.getInstance().naviMaxVolume
            value = Volume(0, max, pos)
        }
    }

    val mediaVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            val pos = SoundManager.getInstance().mediaVolume
            val max = SoundManager.getInstance().mediaMaxVolume
            value = Volume(0, max, pos)
        }
    }

    val phoneVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            val pos = SoundManager.getInstance().phoneVolume
            val max = SoundManager.getInstance().phoneMaxVolume
            value = Volume(0, max, pos)
        }
    }

    val voiceVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            val pos = SoundManager.getInstance().cruiseVolume
            val max = SoundManager.getInstance().cruiseMaxVolume
            value = Volume(0, max, pos)
        }
    }

    val systemVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            val pos = SoundManager.getInstance().systemVolume
            val max = SoundManager.getInstance().systemMaxVolume
            value = Volume(0, max, pos)
        }
    }

    override fun onSoundVolumeChanged(pos: Int, serial: String) {
        val liveData = when (serial) {
            "NAVI" -> naviVolume
            "VOICE" -> voiceVolume
            "PHONE" -> phoneVolume
            "MEDIA" -> mediaVolume
            "SYSTEM" -> systemVolume
            else -> null
        }
        liveData?.value?.let {
            liveData.value = it.copy(pos = pos)
        }
    }

    override fun isNeedUpdate(version: Int): Boolean {
       return true
    }


}