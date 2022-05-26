package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.manager.ACManager
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

    private var keySerial: Int = 0

    private val _aridLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            it.value = acManager.aridStatus.get()
        }
    }

    val aridLiveData: LiveData<Boolean> by lazy { _aridLiveData }

    private val _demistLiveData: MutableLiveData<Boolean> by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value = acManager.demistStatus.get()
        mutableLiveData
    }

    val demistLiveData: LiveData<Boolean> by lazy { _demistLiveData }


    private val _windLiveData: MutableLiveData<Boolean> by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value = acManager.windStatus.get()
        mutableLiveData
    }

    val windLiveData: LiveData<Boolean> by lazy { _windLiveData }


    val comfortLiveData: MutableLiveData<Int> by lazy {
        val mutableLiveData = MutableLiveData<Int>()
        mutableLiveData.value = acManager.obtainAutoComfortOption()
        mutableLiveData
    }

    override fun onCreate() {
        super.onCreate()
        LogManager.d("onCreate!!")
        keySerial = acManager.onRegisterVcuListener(0, this)
    }

    override fun onResume() {
        super.onResume()
        LogManager.d("onResume!!")
    }

    override fun onPause() {
        super.onPause()
        LogManager.d("onPause!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogManager.d("onDestroy!!")
        acManager.unRegisterVcuListener(keySerial, keySerial)
    }

    override fun onACSwitchStatusChanged(status: Boolean, type: ACManager.SwitchNape) {
        val liveData = when (type) {
            ACManager.SwitchNape.AC_AUTO_ARID -> {
                _aridLiveData
            }
            ACManager.SwitchNape.AC_AUTO_DEMIST -> {
                _demistLiveData
            }
            ACManager.SwitchNape.AC_ADVANCE_WIND -> {
                _windLiveData
            }
        }
        liveData.takeIf { it.value!! xor status }?.value = status
    }

    override fun isNeedUpdate(version: Int): Boolean {

        return true
    }

}