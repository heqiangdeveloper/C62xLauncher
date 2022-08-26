package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SoundEffect
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

    private val manager: SettingManager by lazy { SettingManager.instance }

    private val popEffect: IntArray
        get() = intArrayOf(30, 23, 37, 49, 59)
    private val rockEffect: IntArray
        get() = intArrayOf(60, 57, 33, 70, 43)
    private val jazzEffect: IntArray
        get() = intArrayOf(60, 57, 60, 70, 35)
    private val peopleEffect: IntArray
        get() = intArrayOf(60, 57, 33, 70, 66)
    private val classicEffect: IntArray
        get() = intArrayOf(60, 57, 33, 70, 50)
    private val standardEffect: IntArray
        get() = intArrayOf(36, 57, 50, 70, 48)

    val currentEffect: LiveData<Int>
        get() = _currentEffect

    private val _currentEffect: MutableLiveData<Int> by lazy {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        MutableLiveData(node.default).apply {
            val value = EffectManager.instance.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val effectOption: LiveData<Int>
        get() = _effectOption

    private val _effectOption: MutableLiveData<Int> by lazy {
        val node = RadioNode.AUDIO_ENVI_AUDIO
        MutableLiveData(node.default).apply {
            val value = EffectManager.instance.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }

    val effectStatus: LiveData<Boolean>
        get() = _effectStatus

    private val _effectStatus: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AUDIO_ENVI_AUDIO
        MutableLiveData(node.default).apply {
            val value = EffectManager.instance.doGetSwitchOption(node)
            doUpdate(this, value)
        }
    }

    val audioLoudness: LiveData<Boolean>
        get() = _audioLoudness

    private val _audioLoudness: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AUDIO_SOUND_LOUDNESS
        MutableLiveData(node.default).apply {
            val value = EffectManager.instance.doGetSwitchOption(node)
            doUpdate(this, value)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = EffectManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        EffectManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    fun getEffectValues(effect: SoundEffect): IntArray {
        val id = manager.getSoundEffect();
        if (id != effect.id) {
            manager.setSoundEffect(effect)
        }
        return getEffectValues()
    }

    fun getEffectValues(): IntArray {
        val lev1 = getAudioVoice(SettingManager.VOICE_LEVEL1)
        val lev2 = getAudioVoice(SettingManager.VOICE_LEVEL2)
        val lev3 = getAudioVoice(SettingManager.VOICE_LEVEL3)
        val lev4 = getAudioVoice(SettingManager.VOICE_LEVEL4)
        val lev5 = getAudioVoice(SettingManager.VOICE_LEVEL5)
        val effect = manager.getAudioEQ()
        Timber.d("getCustomEffectValues effect:$effect, lev1:$lev1, lev2:$lev2, lev3:$lev3, lev4:$lev4, lev5:$lev5")
        return intArrayOf(lev1, lev2, lev3, lev4, lev5)
    }

    fun setAudioBalance(uiBalanceLevelValue: Int, uiFadeLevelValue: Int) {
        manager.setAudioBalance(uiBalanceLevelValue, uiFadeLevelValue)
    }

    fun getAudioBalance(): Int {
        return manager.getAudioBalance()
    }

    fun getAudioFade(): Int {
        return manager?.audioFade
    }


    fun setAudioEQ(
        position: Int,
        lev1: Int = getAudioVoice(SettingManager.VOICE_LEVEL1),
        lev2: Int = getAudioVoice(SettingManager.VOICE_LEVEL2),
        lev3: Int = getAudioVoice(SettingManager.VOICE_LEVEL3),
        lev4: Int = getAudioVoice(SettingManager.VOICE_LEVEL4),
        lev5: Int = getAudioVoice(SettingManager.VOICE_LEVEL5),
    ) {
        var effectArray: IntArray? = null
        val mode = when (position) {
            0 -> {
                effectArray = standardEffect
                SoundEffect.FLAT.id
            }
            1 -> {
                effectArray = classicEffect
                SoundEffect.CLASSIC.id
            }
            2 -> {
                effectArray = peopleEffect
                SoundEffect.POP.id
            }
            3 -> {
                effectArray = jazzEffect
                SoundEffect.JAZZ.id
            }
            4 -> {
                effectArray = popEffect
                SoundEffect.POP.id
            }
            5 -> {
                effectArray = rockEffect
                SoundEffect.ROCK.id
            }
            else -> {
                SoundEffect.CUSTOM.id
            }
        }
        if (null != effectArray) {
            manager.setAudioEQ(
                mode, effectArray[0], effectArray[1],
                effectArray[2], effectArray[3], effectArray[4]
            )
        } else {
            manager.setAudioEQ(mode, lev1, lev2, lev3, lev4, lev5)
        }
    }

    private fun getAudioVoice(id: Int): Int {
        return manager.getAudioVoice(id)
    }

    fun doSwitchSoundEffect(value: Int) {
        if (_currentEffect.value != value) {
            _currentEffect.postValue(value)
        }
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.AUDIO_ENVI_AUDIO -> {
                doUpdate(_effectStatus, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.AUDIO_ENVI_AUDIO -> {
                doUpdate(_effectOption, value)
            }
            else -> {}
        }
    }
}