package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.cabin.OtherManager
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
    BaseViewModel(app, model), ISwitchListener {

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


//    private fun updateLiveData(
//        liveData: MutableLiveData<Boolean>,
//        value: Boolean,
//    ): MutableLiveData<Boolean> {
//        liveData.takeIf { value xor liveData.value!! }?.postValue(value)
//        return liveData
//    }


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
            else -> {}
        }
    }

}