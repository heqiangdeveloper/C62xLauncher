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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener, IProgressListener {

    val manager: VoiceManager by lazy { VoiceManager.instance }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    val naviVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.NAVI)
        }
    }

    val mediaVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.MEDIA)
        }
    }

    val phoneVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.PHONE)
        }
    }

    val voiceVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.VOICE)
        }
    }

    val systemVolume: MutableLiveData<Volume> by lazy {
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(Progress.SYSTEM)
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
        target.postValue(expect)
        if (true) return
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
        target.postValue(target.value)
        if (true) return
        target.takeIf { it.value?.pos != value }?.let {
            it.value?.pos = value
            target.postValue(target.value)
        }
    }

}