package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * 音效调节
 */
@HiltViewModel
class SoundEffectViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: EffectManager by lazy { EffectManager.instance }

    val currentEffect: LiveData<RadioState>
        get() = _currentEffect

    private val _currentEffect: MutableLiveData<RadioState> by lazy {
        val value = EffectManager.instance.doGetRadioOption(RadioNode.SYSTEM_SOUND_EFFECT)
        MutableLiveData(value)
    }

    val effectOption: LiveData<RadioState>
        get() = _effectOption

    private val _effectOption: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.AUDIO_ENVI_AUDIO
        MutableLiveData(EffectManager.instance.doGetRadioOption(node))
    }

    val effectStatus: LiveData<SwitchState>
        get() = _effectStatus

    private val _effectStatus: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AUDIO_ENVI_AUDIO
        MutableLiveData(EffectManager.instance.doGetSwitchOption(node))
    }

    val audioLoudness: LiveData<SwitchState>
        get() = _audioLoudness

    private val _audioLoudness: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
        MutableLiveData(EffectManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = EffectManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        EffectManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    fun getEffectValues(eqId: Int): IntArray {
        val eqValues = Constant.EQ_LEVELS.map { getAudioVoice(it) }.toIntArray()
//        val effect = SettingManager.instance.getEQ()
//        Timber.d("getEffectValues effect:$effect, eqId:$eqId, lev1:$lev1, lev2:$lev2, " +
//                "lev3:$lev3, lev4:$lev4, lev5:$lev5, lev6:$lev6, lev7:$lev7," +
//                " lev8:$lev8, lev9:$lev9")
        return eqValues
    }

    fun setAudioBalance(uiBalanceLevelValue: Int, uiFadeLevelValue: Int) {
        manager.setAudioBalance(uiBalanceLevelValue, uiFadeLevelValue)
    }

    fun getAudioBalance(): Int {
        return manager.getAudioBalance()
    }

    fun getAudioFade(): Int {
        return manager.audioFade()
    }

    private fun getAudioVoice(id: Int): Int {
        return manager.getAudioVoice(id)
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.AUDIO_ENVI_AUDIO -> {
                doUpdate(_effectStatus, status)
            }
            SwitchNode.AUDIO_SOUND_LOUDNESS -> {
                doUpdate(_audioLoudness, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> {
                doUpdate(_effectOption, value)
            }
            RadioNode.SYSTEM_SOUND_EFFECT -> {
                doUpdate(_currentEffect, value)
            }
            else -> {}
        }
    }
}