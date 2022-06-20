package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IRadioListener
import com.chinatsp.settinglib.manager.cabin.MeterManager
import com.chinatsp.settinglib.optios.RadioNode
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
    BaseViewModel(app, model), IRadioListener {

    private val manager: MeterManager by lazy { MeterManager.instance }

    val systemRadioOption: LiveData<Int>
        get() = _systemRadioOption

    private val _systemRadioOption: MutableLiveData<Int> by lazy {
        val node = RadioNode.DRIVE_METER_SYSTEM
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
        super.onDestroy()
        manager.unRegisterVcuListener(keySerial, keySerial)
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.DRIVE_METER_SYSTEM -> {
                if (node.isValid(value)) {
                    _systemRadioOption.value = value
                }
            }
            else -> {}
        }
    }

}