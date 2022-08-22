package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val fortifyToneFunction: LiveData<Boolean>
        get() = _fortifyToneFunction

    private val _fortifyToneFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_SAFE_FORTIFY_SOUND
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val videoModeFunction: LiveData<Boolean>
        get() = _videoModeFunction

    private val _videoModeFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.unRegisterVcuListener(keySerial, keySerial)
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> {
                _fortifyToneFunction.takeIf { it.value != status }?.value = status
            }
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> {
                _videoModeFunction.takeIf { it.value != status }?.value = status
            }
        }
    }

}