package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
class CabinACViewModel @Inject constructor(app: Application, model: BaseModel): BaseViewModel(app, model) {

    private val acManager: ACManager by lazy { ACManager.instance }

    val aridLiveData: MutableLiveData<Boolean> by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value = acManager.aridStatus.get()
        mutableLiveData
    }

    val demistLiveData: MutableLiveData<Boolean> by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value = acManager.demistStatus.get()
        mutableLiveData
    }

    val windLiveData: MutableLiveData<Boolean> by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value = acManager.windStatus.get()
        mutableLiveData
    }

    val comfortLiveData: MutableLiveData<Int> by lazy {
        val mutableLiveData = MutableLiveData<Int>()
        mutableLiveData.value = acManager.obtainAutoComfortOption()
        mutableLiveData
    }

}