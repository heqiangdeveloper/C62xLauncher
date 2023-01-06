package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.IRadioListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.cabin.MeterManager
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
class MeterViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {


    private val manager: MeterManager by lazy { MeterManager.instance }

    val systemRadioOption: LiveData<RadioState>
        get() = _systemRadioOption

    private val _systemRadioOption: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DRIVE_METER_SYSTEM
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val node621: LiveData<SwitchState>
        get() = _node621

    private val _node621: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_621
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> {
                doUpdate(_systemRadioOption, value)
            }
            else -> {}
        }
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.NODE_VALID_621 -> doUpdate(_node621, status)
            else -> {}
        }
    }

}