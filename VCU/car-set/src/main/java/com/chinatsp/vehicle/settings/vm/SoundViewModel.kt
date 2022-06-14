package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener {

    val manager: VoiceManager by lazy { VoiceManager.instance }

    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(AudioManager.instance.getTabSerial()) }

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
            value = manager.doGetVolume(Volume.Type.NAVI)?.copy()
        }
    }

    val mediaVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.MEDIA)?.copy()
        }
    }

    val phoneVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.PHONE)?.copy()
        }
    }

    val voiceVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.VOICE)?.copy()
        }
    }

    val systemVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Volume.Type.SYSTEM)?.copy()
        }
    }

    override fun onSoundVolumeChanged(
        navi: Volume,
        media: Volume,
        phone: Volume,
        voice: Volume,
        system: Volume
    ) {
        updateVolume(naviVolume, navi)
        updateVolume(mediaVolume, media)
        updateVolume(phoneVolume, phone)
        updateVolume(voiceVolume, voice)
        updateVolume(systemVolume, system)
    }

    fun updateVolume(target: MutableLiveData<Volume>, expect: Volume) {
        target.takeIf { it.value?.type == expect.type }?.let {
            it.takeUnless { it.value == expect }?.let { liveData ->
                liveData.value?.pos = expect.pos
                liveData.value = liveData.value
            }
        }
    }


}