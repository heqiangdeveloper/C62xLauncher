package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.optios.RadioNode
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
class TrailerViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: OtherManager by lazy { OtherManager.instance }

    val trailerFunction: LiveData<SwitchState>
        get() = _trailerFunction

    private val _trailerFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val sensitivity: LiveData<RadioState>
        get() = _sensitivity

    private val _sensitivity: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val distance: LiveData<RadioState>
        get() = _distance

    private val _distance: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DEVICE_TRAILER_DISTANCE
        MutableLiveData(manager.doGetRadioOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                doUpdate(_trailerFunction, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> {
                doUpdate(_distance, value)
            }
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> {
                doUpdate(_sensitivity, value)
            }
            else -> {}
        }
    }

//    fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
//        Thread { manager.doSetRadioOption(node, value) }.start()
//        return true
////        return manager.doSetRadioOption(node, value)
//    }
//
//    fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
//        Thread { manager.doSetSwitchOption(node, status) }.start()
//        return true
////        return manager.doSetSwitchOption(node, status)
//    }

}