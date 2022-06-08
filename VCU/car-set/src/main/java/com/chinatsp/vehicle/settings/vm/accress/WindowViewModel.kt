package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.access.IDoorListener
import com.chinatsp.settinglib.listener.access.IWindowListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import com.common.xui.utils.ResUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WindowViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), IWindowListener{
    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(-1) }

    private val windowManager:WindowManager
        get() = WindowManager.instance

    val remoteRiseFallStatus: MutableLiveData<Boolean> by lazy {
        val switchNode = SwitchNode.AS_REMOTE_RISE_AND_FALL
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