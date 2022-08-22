package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val trailerRemind: LiveData<Boolean>
        get() = _trailerRemind

    private val _trailerRemind: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val batteryOptimize: LiveData<Boolean>
        get() = _batteryOptimize

    private val _batteryOptimize: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val wirelessCharging: LiveData<Boolean>
        get() = _wirelessCharging

    private val _wirelessCharging: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val wirelessChargingLamp: LiveData<Boolean>
        get() = _wirelessChargingLamp

    private val _wirelessChargingLamp: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }


    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor liveData.value!! }?.postValue(value)
        return liveData
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
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                updateLiveData(_trailerRemind, status)
            }
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                updateLiveData(_batteryOptimize, status)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                updateLiveData(_wirelessCharging, status)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                updateLiveData(_wirelessChargingLamp, status)
            }
            else -> {}
        }
    }

}