package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.access.BackMirrorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MirrorViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), ISwitchListener{

    private val manager:BackMirrorManager
        get() = BackMirrorManager.instance

    val mirrorFoldFunction: LiveData<Boolean> by lazy { _mirrorFoldFunction }

    private val _mirrorFoldFunction: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.BACK_MIRROR_FOLD
        MutableLiveData(switchNode.isOn()).apply {
            value = manager.doGetSwitchOption(switchNode)
        }
    }
    
    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        node.takeIf { it == SwitchNode.BACK_MIRROR_FOLD }?.let {
            _mirrorFoldFunction.value = status
        }
    }

}