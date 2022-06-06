package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.access.IDoorListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import com.common.xui.utils.ResUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoorsViewModel @Inject constructor(app: Application, model: BaseModel):
    BaseViewModel(app, model), IDoorListener{
    val tabLocationLiveData: MutableLiveData<Int> by lazy { MutableLiveData(-1) }

    private val doorManager:DoorManager
        get() = DoorManager.instance

    val liveDataAutoLockDoor: MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            val value = doorManager.driveLockOption.get()
            this.value = value.toString()
        }
    }

    val liveDataAutoUnlockOption: MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            val value = doorManager.shutDownUnlockOption.get()
            this.value = value.toString()
        }
    }

    val liveDataAutoAccessSwitch: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
           value = doorManager.smartEnterStatus.get()
        }
    }


    override fun onDriveAutoLockOptionChanged(value: Int) {
        val toInt = liveDataAutoLockDoor.value?.toInt()
        if (toInt != value) {
            val stringArray = ResUtils.getStringArray(R.array.drive_auto_lock_door_option_values)
            val toIntArray = stringArray.map { it.toInt() }.toIntArray()
            if (toIntArray.contains(value)) {
                liveDataAutoLockDoor.value = value.toString()
            }
        }
    }

    override fun onShutDownAutoUnlockOptionChanged(value: Int) {
        val toInt = liveDataAutoUnlockOption.value?.toInt()
        if (toInt != value) {
            val stringArray = ResUtils.getStringArray(R.array.auto_unlock_door_option_values)
            val toIntArray = stringArray.map { it.toInt() }.toIntArray()
            if (toIntArray.contains(value)) {
                liveDataAutoUnlockOption.value = value.toString()
            }
        }
    }

    override fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        if (status xor (liveDataAutoAccessSwitch.value == true)) {
            liveDataAutoAccessSwitch.value = status
        }
    }

    override fun isNeedUpdate(version: Int): Boolean {
        return true
    }
}