package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IAccessListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KanziViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IAccessListener {

    private val doorManager: DoorManager get() = DoorManager.instance

    private val windowManager: WindowManager get() = WindowManager.instance

    private val sternDoorManager: SternDoorManager get() = SternDoorManager.instance

    val lfDoor: LiveData<Int>
        get() = _lfDoor

    private val _lfDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val lrDoor: LiveData<Int>
        get() = _lrDoor

    private val _lrDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rfDoor: LiveData<Int>
        get() = _rfDoor

    private val _rfDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rrDoor: LiveData<Int>
        get() = _rrDoor

    private val _rrDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val headDoor: LiveData<Int>
        get() = _headDoor

    private val _headDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val tailDoor: LiveData<Int>
        get() = _tailDoor

    private val _tailDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }


    val lfWindow: LiveData<Int>
        get() = _lfWindow

    private val _lfWindow: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val lrWindow: LiveData<Int>
        get() = _lrWindow

    private val _lrWindow: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rfWindow: LiveData<Int>
        get() = _rfWindow

    private val _rfWindow: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rrWindow: LiveData<Int>
        get() = _rrWindow

    private val _rrWindow: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val fWiper: LiveData<Int>
        get() = _fWiper

    private val _fWiper: MutableLiveData<Int> by lazy {
        MutableLiveData(1)
    }

    val rWiper: LiveData<Int>
        get() = _rWiper

    private val _rWiper: MutableLiveData<Int> by lazy {
        MutableLiveData(1)
    }

    val lIndicator: LiveData<Int>
        get() = _lIndicator

    private val _lIndicator: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rIndicator: LiveData<Int>
        get() = _rIndicator

    private val _rIndicator: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val headLamps: LiveData<Int>
        get() = _headLamps

    private val _headLamps: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val brakeLamps: LiveData<Int>
        get() = _brakeLamps

    private val _brakeLamps: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val positionLamps: LiveData<Int>
        get() = _positionLamps

    private val _positionLamps: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    val rearFogLamps: LiveData<Int>
        get() = _rearFogLamps

    private val _rearFogLamps: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }


    override fun onCreate() {
        super.onCreate()
        keySerial = doorManager.onRegisterVcuListener(listener = this)
        windowManager.onRegisterVcuListener(listener = this)
        sternDoorManager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        doorManager.unRegisterVcuListener(serial = keySerial)
        windowManager.unRegisterVcuListener(serial = keySerial)
        sternDoorManager.unRegisterVcuListener(serial = keySerial)
        super.onDestroy()
    }

    override fun onAccessChanged(part: Int, model: Int, value: Int) {
        if (Model.ACCESS_DOOR == model) {
            when (part) {
                IPart.L_F -> doUpdate(_lfDoor, value, true)
                IPart.R_F -> doUpdate(_rfDoor, value, true)
                IPart.L_B -> doUpdate(_lrDoor, value, true)
                IPart.R_B -> doUpdate(_rrDoor, value, true)
                else -> {}
            }
            return
        }
        if (Model.ACCESS_WINDOW == model) {
            when (part) {
                IPart.L_F -> doUpdate(_lfWindow, value, true)
                IPart.R_F -> doUpdate(_rfWindow, value, true)
                IPart.L_B -> doUpdate(_lrWindow, value, true)
                IPart.R_B -> doUpdate(_rrWindow, value, true)
                else -> {}
            }
            return
        }

        if (Model.ACCESS_STERN == model) {
            when (part) {
                IPart.HEAD -> doUpdate(_headDoor, value, true)
                IPart.TAIL -> doUpdate(_tailDoor, value, true)
                else -> {}
            }
            return
        }
    }



}