package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.sound.EffectManager
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

    val audioMixing: LiveData<RadioState>
        get() = _audioMixing

    private val _audioMixing: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.NAVI_AUDIO_MIXING
        MutableLiveData(manager.doGetRadioOption(node))
    }
    val volumeOffset: LiveData<RadioState>
        get() = _volumeOffset

    private val _volumeOffset: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.SPEED_VOLUME_OFFSET
        MutableLiveData(manager.doGetRadioOption(node))
    }

//    val currentEffect: LiveData<Int>
//        get() = _currentEffect
//
//    private val _currentEffect: MutableLiveData<Int> by lazy {
//        val node = RadioNode.SYSTEM_SOUND_EFFECT
//        val value = EffectManager.instance.doGetRadioOption(node)
//        MutableLiveData(value)
//    }

    val volumeLevel: LiveData<RadioState>
        get() = _volumeLevel

    private val _volumeLevel: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ICM_VOLUME_LEVEL
        MutableLiveData(manager.doGetRadioOption(node))
    }
    val toneStatus: LiveData<SwitchState>
        get() = _toneStatus

    private val _toneStatus: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AUDIO_SOUND_TONE
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val huaweiStatus: LiveData<SwitchState>
        get() = _huaweiStatus
    private val _huaweiStatus: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AUDIO_SOUND_HUAWEI
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val loudnessStatus: LiveData<SwitchState>
        get() = _loudnessStatus

    private val _loudnessStatus: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val touchToneStatus: LiveData<SwitchState>
        get() = _touchToneStatus

    private val _touchToneStatus: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.TOUCH_PROMPT_TONE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val speedVolumeOffset: LiveData<SwitchState>
        get() = _speedVolumeOffset

    private val _speedVolumeOffset: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.SPEED_VOLUME_OFFSET
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val node645: LiveData<SwitchState>
        get() = _node645

    private val _node645: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_645
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = VoiceManager.instance.onRegisterVcuListener(0, this)
        EffectManager.instance.onRegisterVcuListener(0, this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        VoiceManager.instance.unRegisterVcuListener(keySerial)
        EffectManager.instance.unRegisterVcuListener(keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.AUDIO_SOUND_TONE -> {
                doUpdate(_toneStatus, status)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                doUpdate(_loudnessStatus, status)
            }
            SwitchNode.AUDIO_SOUND_HUAWEI -> {
                doUpdate(_huaweiStatus, status)
            }
            SwitchNode.TOUCH_PROMPT_TONE -> {
                doUpdate(_touchToneStatus, status)
            }
            SwitchNode.NODE_VALID_645 -> {
                doUpdate(_node645, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.ICM_VOLUME_LEVEL -> {
                doUpdate(_volumeLevel, value)
            }
            RadioNode.SPEED_VOLUME_OFFSET -> {
                doUpdate(_volumeOffset, value)
            }
            RadioNode.NAVI_AUDIO_MIXING -> {
                doUpdate(_audioMixing, value)
            }
//            RadioNode.SYSTEM_SOUND_EFFECT -> {
//                doUpdate(_currentEffect, value)
//            }
            else -> {}
        }
    }

    fun getEffectValues(eqId: Int): IntArray {
//        val lev1 = getAudioVoice(SettingManager.VOICE_LEVEL1)
//        val lev2 = getAudioVoice(SettingManager.VOICE_LEVEL2)
//        val lev3 = getAudioVoice(SettingManager.VOICE_LEVEL3)
//        val lev4 = getAudioVoice(SettingManager.VOICE_LEVEL4)
//        val lev5 = getAudioVoice(SettingManager.VOICE_LEVEL5)
        val eqValues = Constant.EQ_LEVELS.map { getAudioVoice(it) }.toIntArray()

//        val effect = SettingManager.instance.getEQ()
//        Timber.d("getEffectValues effect:$effect, eqId:$eqId, lev1:$lev1, lev2:$lev2, lev3:$lev3, lev4:$lev4, lev5:$lev5")
        return eqValues
    }

    private fun getAudioVoice(id: Int): Int {
        return EffectManager.instance.getAudioVoice(id)
    }

}