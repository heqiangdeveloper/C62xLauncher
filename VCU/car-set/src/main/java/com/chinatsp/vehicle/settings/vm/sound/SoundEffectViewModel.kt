package com.chinatsp.vehicle.settings.vm.sound

import android.app.Application
import com.chinatsp.settinglib.LogManager.Companion.d
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.SettingManager.Companion.EQ_MODE_PEOPLE
import com.chinatsp.settinglib.SettingManager.Companion.EQ_MODE_STANDARD
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * 音效调节
 */
@HiltViewModel
class SoundEffectViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model) {

    private val manager: SettingManager by lazy { SettingManager.instance }


    fun setAudioBalance(uiBalanceLevelValue: Int,uiFadeLevelValue:Int){
        manager?.setAudioBalance(uiBalanceLevelValue,uiFadeLevelValue)
    }
    fun getAudioBalance(): Int {
        return manager?.getAudioBalance()
    }
     fun getAudioFade(): Int {
        return manager?.audioFade
    }

     fun getAudioEQ(): Int  {
        return manager?.getAudioEQ()
    }

    fun setAudioEQ(
        position: Int,
        lev1: Int = getAudioVoice(SettingManager.VOICE_LEVEL1),
        lev2: Int = getAudioVoice(SettingManager.VOICE_LEVEL2),
        lev3: Int = getAudioVoice(SettingManager.VOICE_LEVEL3),
        lev4: Int = getAudioVoice(SettingManager.VOICE_LEVEL4),
        lev5: Int = getAudioVoice(SettingManager.VOICE_LEVEL5),
    ) {

       var mode = when(position){
            0-> SettingManager.EQ_MODE_STANDARD
            1-> SettingManager.EQ_MODE_CLASSIC
            2-> SettingManager.EQ_MODE_PEOPLE
            3-> SettingManager.EQ_MODE_JAZZ
            4-> SettingManager.EQ_MODE_POP
            5-> SettingManager.EQ_MODE_ROCK
            else -> SettingManager.EQ_MODE_CUSTOM
        }
        manager?.setAudioEQ(mode, lev1, lev2, lev3,lev4,lev5)
    }

    private fun getAudioVoice(id: Int): Int {
        return manager?.getAudioVoice(id)
    }

    fun setAudioVoice(id: Int, value: Int) {
        manager?.setAudioVoice(id,value)
    }
}