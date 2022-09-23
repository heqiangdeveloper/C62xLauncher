package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
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

    val dowValue: LiveData<Boolean> by lazy { _dowValue }

    private val _dowValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_DOW
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val bsdValue: LiveData<Boolean> by lazy { _bsdValue }

    private val _bsdValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_BSD
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val bscValue: LiveData<Boolean> by lazy { _bscValue }

    private val _bscValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_BSC
        MutableLiveData(manager.doGetSwitchOption(node))
    }
    val guidesValue: LiveData<Boolean> by lazy { _guidesValue }

    private val _guidesValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_GUIDES
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val showAreaValue: LiveData<Int> by lazy { _showAreaValue }

    private val _showAreaValue: MutableLiveData<Int> by lazy {
        val node = RadioNode.ADAS_SIDE_BACK_SHOW_AREA
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val mebValue: LiveData<Boolean> by lazy { _mebValue }

    private val _mebValue: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.ADAS_MEB
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(0, this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
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
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                doUpdate(_showAreaValue, value)
            }
            else -> {}
        }
    }

}