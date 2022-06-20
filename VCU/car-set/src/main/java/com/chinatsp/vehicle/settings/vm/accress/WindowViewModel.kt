package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WindowViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), ISwitchListener{
    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(-1) }

    private val windowManager:WindowManager
        get() = WindowManager.instance

    val remoteRiseFallStatus: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.AS_WIN_REMOTE_CONTROL
        MutableLiveData(switchNode.isOn()).apply {
            this.value = doGetSwitchStatus(switchNode)
        }
    }


    private fun doGetSwitchStatus(switchNode: SwitchNode): Boolean {
        return windowManager.doGetSwitchStatus(switchNode)
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {

    }

}