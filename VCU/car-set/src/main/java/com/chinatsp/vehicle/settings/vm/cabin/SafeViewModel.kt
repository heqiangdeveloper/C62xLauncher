package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.cabin.SafeManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 14:16
 * @desc   :
 * @version: 1.0
 */
@HiltViewModel
class SafeViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: SafeManager by lazy { SafeManager.instance }

    val alcLockHint: LiveData<SwitchState>
        get() = _alcLockHint

    private val _alcLockHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val lockFailedHint: LiveData<SwitchState>
        get() = _lockFailedHint

    private val _lockFailedHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LOCK_FAILED_AUDIO_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val lockSuccessHint: LiveData<SwitchState>
        get() = _lockSuccessHint

    private val _lockSuccessHint: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LOCK_SUCCESS_AUDIO_HINT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

//    val fortifyHint: LiveData<SwitchState>
//        get() = _fortifyHint
//
//    private val _fortifyHint: MutableLiveData<SwitchState> by lazy {
//        val node = SwitchNode.DRIVE_SAFE_FORTIFY_SOUND
//        MutableLiveData(manager.doGetSwitchOption(node))
//    }


    val videoModeFunction: LiveData<SwitchState>
        get() = _videoModeFunction

    private val _videoModeFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
//            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> doUpdate(_fortifyHint, status)
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> doUpdate(_videoModeFunction, status)
            SwitchNode.LOCK_FAILED_AUDIO_HINT -> doUpdate(_lockFailedHint, status)
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT -> doUpdate(_lockSuccessHint, status)
            else -> {}
        }
    }

}