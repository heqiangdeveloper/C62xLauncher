package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/14 9:45
 * @desc   :
 * @version: 1.0
 */
@HiltViewModel
class DoorViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model) {

    val liveDataAutoLockDoor: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            loadDoorAutoLockOption(it)
        }
    }

    val liveDataAutoUnlockOption: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            loadDoorAutoLockOption(it)
        }
    }


    private fun loadDoorAutoLockOption(it: MutableLiveData<String>) {
        it.value = "loadDoorAutoLockOption"
    }


}
