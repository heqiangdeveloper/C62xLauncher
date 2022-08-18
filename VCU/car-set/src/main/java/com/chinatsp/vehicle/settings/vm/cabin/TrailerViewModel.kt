package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val trailerFunction: LiveData<Boolean>
        get() = _trailerFunction

    private val _trailerFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val sensitivity: LiveData<Int>
        get() = _sensitivity

    private val _sensitivity: MutableLiveData<Int> by lazy {
        val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            setValue(value)
        }
    }

    val distance: LiveData<Int>
        get() = _distance

    private val _distance: MutableLiveData<Int> by lazy {
        val node = RadioNode.DEVICE_TRAILER_DISTANCE
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            setValue(value)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                _trailerFunction.takeIf { it.value != status }?.value = status
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> {
                _distance.takeIf { it.value != value }?.value = value
            }
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> {
                _sensitivity.takeIf { it.value != value }?.value = value
            }
            else -> {}
        }
    }

    fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        Thread{ manager.doSetRadioOption(node, value) }.start()
        return true
//        return manager.doSetRadioOption(node, value)
    }

    fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        Thread{ manager.doSetSwitchOption(node, status) }.start()
        return true
//        return manager.doSetSwitchOption(node, status)
    }

}