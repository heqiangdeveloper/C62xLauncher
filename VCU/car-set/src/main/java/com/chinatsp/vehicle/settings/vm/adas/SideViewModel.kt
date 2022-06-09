package com.chinatsp.vehicle.settings.vm.adas

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.assistance.SideBackManager
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
        val switchNode = SwitchNode.ADAS_DOW
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val bsdValue: LiveData<Boolean> by lazy { _bsdValue }

    private val _bsdValue: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_BSD
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }
    val bscValue: LiveData<Boolean> by lazy { _bscValue }

    private val _bscValue: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_BSC
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }
    val guidesValue: LiveData<Boolean> by lazy { _guidesValue }

    private val _guidesValue: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.ADAS_GUIDES
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }

    val showAreaValue: LiveData<Int> by lazy { _showAreaValue }

    private val _showAreaValue: MutableLiveData<Int> by lazy {
        MutableLiveData(-1).apply {
            value = manager.doGetRadioOption(RadioNode.ADAS_SIDE_BACK_SHOW_AREA)
        }
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.ADAS_DOW -> {
                _dowValue.value = status
            }
            SwitchNode.ADAS_BSD -> {
                _bsdValue.value = status
            }
            SwitchNode.ADAS_BSC -> {
                _bscValue.value = status
            }
            SwitchNode.ADAS_GUIDES -> {
                _guidesValue.value = status
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                _showAreaValue.value = value
            }
            else -> {}
        }
    }

}