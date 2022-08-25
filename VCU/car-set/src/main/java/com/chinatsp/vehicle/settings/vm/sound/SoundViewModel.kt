package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: VoiceManager by lazy { VoiceManager.instance }

    val audioMixing: LiveData<Int>
        get() = _audioMixing

    private val _audioMixing: MutableLiveData<Int> by lazy {
        val node = RadioNode.NAVI_AUDIO_MIXING
        MutableLiveData(node.default).apply {
            launch {
                val value = manager.doGetRadioOption(node)
                postValue(value)
            }
        }
    }
    val volumeOffset: LiveData<Int>
        get() = _volumeOffset

    private val _volumeOffset: MutableLiveData<Int> by lazy {
        val node = RadioNode.SPEED_VOLUME_OFFSET
        MutableLiveData(node.default).apply {
            launch {
                val value = manager.doGetRadioOption(node)
                postValue(value)
            }
        }
    }
    val volumeLevel: LiveData<Int>
        get() = _volumeLevel

    private val _volumeLevel: MutableLiveData<Int> by lazy {
        val node = RadioNode.ICM_VOLUME_LEVEL
        MutableLiveData(node.default).apply {
            launch {
                val value = manager.doGetRadioOption(node)
                postValue(value)
            }
        }
    }
    val toneStatus: LiveData<Boolean>
        get() = _toneStatus

    private val _toneStatus: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            postValue(value)
        }
    }
    val huaweiStatus: LiveData<Boolean>
        get() = _huaweiStatus
    private val _huaweiStatus: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            postValue(value)
        }
    }
    val loudnessStatus: LiveData<Boolean>
        get() = _loudnessStatus

    private val _loudnessStatus: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            postValue(value)
        }
    }

    val touchToneStatus: LiveData<Boolean>
        get() = _touchToneStatus

    private val _touchToneStatus: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.TOUCH_PROMPT_TONE
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            postValue(value)
        }
    }

    val speedVolumeOffset: LiveData<Boolean>
        get() = _speedVolumeOffset

    private val _speedVolumeOffset: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.SPEED_VOLUME_OFFSET
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            postValue(value)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = VoiceManager.instance.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        VoiceManager.instance.unRegisterVcuListener(keySerial)
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                _toneStatus.postValue(status)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                _loudnessStatus.postValue(status)
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                _huaweiStatus.postValue(status)
            }
            SwitchNode.TOUCH_PROMPT_TONE -> {
                _touchToneStatus.postValue(status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> {
                _volumeLevel.postValue(value)
            }
            RadioNode.SPEED_VOLUME_OFFSET -> {
                _volumeOffset.postValue(value)
            }
            RadioNode.NAVI_AUDIO_MIXING -> {
                _audioMixing.postValue(value)
            }
            else -> {}
        }
    }


}