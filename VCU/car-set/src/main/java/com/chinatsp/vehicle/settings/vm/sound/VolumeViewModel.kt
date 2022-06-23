package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener {

    val manager: VoiceManager by lazy { VoiceManager.instance }

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

    override fun onSoundVolumeChanged(vararg array: Volume) {
        array.forEach {
            when (it.type) {
                Volume.Type.NAVI -> {
                    updateVolume(naviVolume, it)
                }
                Volume.Type.MEDIA -> {
                    updateVolume(mediaVolume, it)
                }
                Volume.Type.VOICE -> {
                    updateVolume(voiceVolume, it)
                }
                Volume.Type.PHONE -> {
                    updateVolume(phoneVolume, it)
                }
                Volume.Type.SYSTEM -> {
                    updateVolume(systemVolume, it)
                }
                else -> {}
            }
        }
    }

    private fun updateVolume(target: MutableLiveData<Volume>, expect: Volume) {
        target.takeIf { it.value?.type == expect.type }?.let {
            it.takeUnless { it.value == expect }?.let { liveData ->
                liveData.value?.pos = expect.pos
                liveData.value = liveData.value
            }
        }
    }

}