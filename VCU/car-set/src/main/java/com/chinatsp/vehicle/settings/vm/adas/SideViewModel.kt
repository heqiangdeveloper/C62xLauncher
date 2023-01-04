package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.adas.SideBackManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SideViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: SideBackManager
        get() = SideBackManager.instance

    val dowValue: LiveData<SwitchState> by lazy { _dowValue }

    private val _dowValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_DOW
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val bsdValue: LiveData<SwitchState> by lazy { _bsdValue }

    private val _bsdValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_BSD
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val bscValue: LiveData<SwitchState> by lazy { _bscValue }

    private val _bscValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_BSC
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val guidesValue: LiveData<SwitchState> by lazy { _guidesValue }

    private val _guidesValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_GUIDES
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val showAreaValue: LiveData<RadioState> by lazy { _showAreaValue }

    private val _showAreaValue: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.ADAS_SIDE_BACK_SHOW_AREA
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val mebValue: LiveData<SwitchState> by lazy { _mebValue }

    private val _mebValue: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.ADAS_MEB
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val node591: LiveData<SwitchState>
        get() = _node591

    private val _node591: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_591
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val node581: LiveData<SwitchState>
        get() = _node581

    private val _node581: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_581
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val node582: LiveData<SwitchState>
        get() = _node582

    private val _node582: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_582
        MutableLiveData(manager.doGetSwitchOption(node))
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
            SwitchNode.ADAS_DOW -> {
                doUpdate(_dowValue, status)
            }
            SwitchNode.ADAS_BSD -> {
                doUpdate(_bsdValue, status)
            }
            SwitchNode.ADAS_BSC -> {
                doUpdate(_bscValue, status)
            }
            SwitchNode.ADAS_GUIDES -> {
                doUpdate(_guidesValue, status)
            }
            SwitchNode.ADAS_MEB -> {
                doUpdate(_mebValue, status)
            }
            SwitchNode.NODE_VALID_591->{
                doUpdate(_node591,status)
            }
            SwitchNode.NODE_VALID_581->{
                doUpdate(_node581,status)
            }
            SwitchNode.NODE_VALID_582->{
                doUpdate(_node582,status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                doUpdate(_showAreaValue, value)
            }
            else -> {}
        }
    }

}