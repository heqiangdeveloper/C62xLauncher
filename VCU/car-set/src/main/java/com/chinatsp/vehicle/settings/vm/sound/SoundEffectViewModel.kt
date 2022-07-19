package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.LogManager.Companion.d
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.SettingManager.Companion.EQ_MODE_PEOPLE
import com.chinatsp.settinglib.SettingManager.Companion.EQ_MODE_STANDARD
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.SoundEffect
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.contracts.SimpleEffect


/**
 * 音效调节
 */
@HiltViewModel
class SoundEffectViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model) {

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
        MutableLiveData(1)
    }

    fun getEffectValues(effect: SoundEffect): IntArray {
        val id = manager.getSoundEffect();
        if (id != effect.id) {
            manager.setSoundEffect(effect)
        }
        return getCustomEffectValues()
//        when (effect) {
//            SoundEffect.POP -> {
//
//                return popEffect
//            }
//            SoundEffect.FLAT -> {
//                return standardEffect
//            }
//            SoundEffect.JAZZ -> {
//                return jazzEffect
//            }
//            SoundEffect.ROCK -> {
//                return rockEffect
//            }
//            SoundEffect.VOCAL -> {
//                return peopleEffect
//            }
//            SoundEffect.CLASSIC -> {
//                return classicEffect
//            }
//            SoundEffect.CUSTOM -> {
//                setAudioEQ()
//                return getCustomEffectValues()
//            }
//        }
    }

    private fun getCustomEffectValues(): IntArray {
        val lev1: Int = getAudioVoice(SettingManager.VOICE_LEVEL1)
        val lev2: Int = getAudioVoice(SettingManager.VOICE_LEVEL2)
        val lev3: Int = getAudioVoice(SettingManager.VOICE_LEVEL3)
        val lev4: Int = getAudioVoice(SettingManager.VOICE_LEVEL4)
        val lev5: Int = getAudioVoice(SettingManager.VOICE_LEVEL5)
        val effect = manager.getAudioEQ()
        LogManager.d("Effect", "getCustomEffectValues effect:$effect, lev1:$lev1, lev2:$lev2, lev3:$lev3, lev4:$lev4, lev5:$lev5")
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
                SettingManager.EQ_MODE_STANDARD
            }
            1 -> {
                effectArray = classicEffect
                SettingManager.EQ_MODE_CLASSIC
            }
            2 -> {
                effectArray = peopleEffect
                SettingManager.EQ_MODE_PEOPLE
            }
            3 -> {
                effectArray = jazzEffect
                SettingManager.EQ_MODE_JAZZ
            }
            4 -> {
                effectArray = popEffect
                SettingManager.EQ_MODE_POP
            }
            5 -> {
                effectArray = rockEffect
                SettingManager.EQ_MODE_ROCK
            }
            else -> {
                SettingManager.EQ_MODE_CUSTOM
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
        return manager?.getAudioVoice(id)
    }

    fun setAudioVoice(id: Int, value: Int) {
        manager?.setAudioVoice(id, value)
    }

    fun doSwitchSoundEffect(value: Int) {
        if (_currentEffect.value != value) {
            _currentEffect.postValue(value)
        }
    }
}