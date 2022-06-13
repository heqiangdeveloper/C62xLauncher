package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.cabin.IACListener
import com.chinatsp.settinglib.manager.cabin.ACManager
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
class CabinACViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IACListener {

    private val manager: ACManager by lazy { ACManager.instance }

    private val _aridLiveData: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AC_AUTO_ARID
        MutableLiveData(node.isOn()).apply {
            val result = manager.doGetSwitchOption(node)
            postValue(result)
        }
    }

    val aridLiveData: LiveData<Boolean> by lazy { _aridLiveData }

    private val _demistLiveData: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AC_AUTO_DEMIST
        MutableLiveData(node.isOn()).apply {
            val result = manager.doGetSwitchOption(node)
            postValue(result)
        }
    }

    val demistLiveData: LiveData<Boolean> by lazy { _demistLiveData }


    private val _windLiveData: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AC_ADVANCE_WIND
        MutableLiveData(node.isOn()).apply {
            val result = manager.doGetSwitchOption(node)
            postValue(result)
        }
    }

    val windLiveData: LiveData<Boolean>
        get() = _windLiveData


    private val _comfortLiveData: MutableLiveData<Int> by lazy {
        val node = RadioNode.AC_COMFORT
        MutableLiveData(node.default).apply {
            value = manager.doGetRadioOption(node)
        }
    }

    val comfortLiveData: LiveData<Int> by lazy { _comfortLiveData }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.unRegisterVcuListener(keySerial, keySerial)
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        val liveData = when (node) {
            SwitchNode.AC_AUTO_ARID -> _aridLiveData
            SwitchNode.AC_AUTO_DEMIST -> _demistLiveData
            SwitchNode.AC_ADVANCE_WIND -> _windLiveData
            else -> null
        }
        liveData?.takeIf { it.value!! xor status }?.value = status
    }

    override fun onAcComfortOptionChanged(location: Int) {
        _comfortLiveData.value = location
    }

}