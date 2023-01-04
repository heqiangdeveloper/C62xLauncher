package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
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
    BaseViewModel(app, model), IOptionListener {

    private val manager: ACManager by lazy { ACManager.instance }

    private val _aridLiveData: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AC_AUTO_ARID
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val aridLiveData: LiveData<SwitchState> by lazy { _aridLiveData }

    private val _demistLiveData: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AC_AUTO_DEMIST
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val demistLiveData: LiveData<SwitchState> by lazy { _demistLiveData }


    private val _windLiveData: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.AC_ADVANCE_WIND
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val windLiveData: LiveData<SwitchState>
        get() = _windLiveData


    private val _comfortLiveData: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.AC_COMFORT
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val comfortLiveData: LiveData<RadioState> by lazy { _comfortLiveData }

    val node514: LiveData<SwitchState>
        get() = _node514

    private val _node514: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_514
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val node513: LiveData<SwitchState>
        get() = _node513

    private val _node513: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_513
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.unRegisterVcuListener(keySerial, keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        val liveData = when (node) {
            SwitchNode.AC_AUTO_ARID -> _aridLiveData
            SwitchNode.AC_AUTO_DEMIST -> _demistLiveData
            SwitchNode.AC_ADVANCE_WIND -> _windLiveData
            SwitchNode.NODE_VALID_514 -> _node514
            SwitchNode.NODE_VALID_513 -> _node513
            else -> null
        }
        liveData?.let {
            doUpdate(it, status)
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.AC_COMFORT -> {
                doUpdate(_comfortLiveData, value)
            }
            else -> {}
        }
    }

}