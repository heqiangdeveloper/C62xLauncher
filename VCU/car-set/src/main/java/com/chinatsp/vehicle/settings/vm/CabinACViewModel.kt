package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.manager.cabin.ACManager
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

    private val acManager: ACManager by lazy { ACManager.instance }

    private val _aridLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            it.value = acManager.aridStatus.get()
        }
    }

    val aridLiveData: LiveData<Boolean> by lazy { _aridLiveData }

    private val _demistLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = acManager.demistStatus.get()
        }
    }

    val demistLiveData: LiveData<Boolean> by lazy { _demistLiveData }


    private val _windLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = acManager.windStatus.get()
        }
    }

    val windLiveData: LiveData<Boolean> by lazy { _windLiveData }


    private val _comfortLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = acManager.obtainAutoComfortOption()
        }
    }

    val comfortLiveData: LiveData<Int> by lazy { _comfortLiveData }

    override fun onCreate() {
        super.onCreate()
        keySerial = acManager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        acManager.unRegisterVcuListener(keySerial, keySerial)
    }

    override fun onACSwitchStatusChanged(status: Boolean, type: SwitchNode) {
        val liveData = when (type) {
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

    override fun isNeedUpdate(version: Int): Boolean {

        return true
    }

}