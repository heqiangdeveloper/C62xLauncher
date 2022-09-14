package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener, IProgressListener {

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
            value = manager.doGetVolume(Progress.NAVI)?.copy()
        }
    }

    val mediaVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.MEDIA)?.copy()
        }
    }

    val phoneVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.PHONE)?.copy()
        }
    }

    val voiceVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.VOICE)?.copy()
        }
    }

    val systemVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.SYSTEM)?.copy()
        }
    }

    override fun onSoundVolumeChanged(vararg array: Volume) {
        array.forEach {
            when (it.type) {
                Progress.NAVI -> {
                    updateVolume(naviVolume, it)
                }
                Progress.MEDIA -> {
                    updateVolume(mediaVolume, it)
                }
                Progress.VOICE -> {
                    updateVolume(voiceVolume, it)
                }
                Progress.PHONE -> {
                    updateVolume(phoneVolume, it)
                }
                Progress.SYSTEM -> {
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
                liveData.postValue(liveData.value)
            }
        }
    }

    fun resetDeviceVolume() {
        manager.resetDeviceVolume()
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.NAVI -> {
                updateVolume(naviVolume, value)
            }
            Progress.VOICE -> {
                updateVolume(voiceVolume, value)
            }
            Progress.MEDIA -> {
                updateVolume(mediaVolume, value)
            }
            Progress.PHONE -> {
                updateVolume(phoneVolume, value)
            }
            Progress.SYSTEM -> {
                updateVolume(systemVolume, value)
            }
            else -> {}
        }
    }

    private fun updateVolume(target: MutableLiveData<Volume>, value: Int) {
        target.takeIf { it.value?.pos != value }?.let {
            it.value?.pos = value
            target.postValue(target.value)
        }
    }

}