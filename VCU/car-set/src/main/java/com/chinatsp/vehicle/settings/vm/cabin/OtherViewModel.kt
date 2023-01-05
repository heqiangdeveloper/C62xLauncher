package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.GlobalManager
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
class OtherViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: OtherManager by lazy { OtherManager.instance }

    val trailerRemind: LiveData<SwitchState>
        get() = _trailerRemind

    private val _trailerRemind: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val batteryOptimize: LiveData<SwitchState>
        get() = _batteryOptimize

    private val _batteryOptimize: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val wirelessCharging: LiveData<SwitchState>
        get() = _wirelessCharging

    private val _wirelessCharging: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val wirelessChargingLamp: LiveData<SwitchState>
        get() = _wirelessChargingLamp

    private val _wirelessChargingLamp: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val wirelessChargingState: LiveData<RadioState> get() = _wirelessChargingState

    private val _wirelessChargingState: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.WIRELESS_CHARGING_STATE
        MutableLiveData(manager.doGetRadioOption(node))
    }
    val node362: LiveData<SwitchState>
        get() = _node362

    private val _node362: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_362
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    val node66F: LiveData<SwitchState>
        get() = _node66F

    private val _node66F: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_66F
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }
    val node2E5: LiveData<SwitchState>
        get() = _node2E5

    private val _node2E5: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_2E5
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                doUpdate(_trailerRemind, status)
            }
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                doUpdate(_batteryOptimize, status)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                doUpdate(_wirelessCharging, status)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                doUpdate(_wirelessChargingLamp, status)
            }
            SwitchNode.NODE_VALID_362->{
                doUpdate(_node362,status)
            }
            SwitchNode.NODE_VALID_66F->{
                doUpdate(_node66F,status)
            }
            SwitchNode.NODE_VALID_2E5->{
                doUpdate(_node2E5,status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.WIRELESS_CHARGING_STATE -> {
                doUpdate(_wirelessChargingState, value)
            }
            else -> {}
        }
    }

}