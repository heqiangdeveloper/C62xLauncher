package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.listener.ISignalListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class KanziViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISignalListener {

    private val doorManager: DoorManager get() = DoorManager.instance

    private val windowManager: WindowManager get() = WindowManager.instance

    private val sternDoorManager: SternDoorManager get() = SternDoorManager.instance

    private val OFF = 0x0

    private val ON = 0x1

    private val LOW_ON = 0x1

    private val HIGH_ON = 0x2

    var lowLamp: Int = 0x0

    var highLamp: Int = 0x0

    val lfDoor: LiveData<Int>
        get() = _lfDoor

    private val _lfDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(doorManager.obtainAccessState(IPart.L_F, Model.ACCESS_DOOR))
    }

    val lrDoor: LiveData<Int>
        get() = _lrDoor

    private val _lrDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(doorManager.obtainAccessState(IPart.L_B, Model.ACCESS_DOOR))
    }

    val rfDoor: LiveData<Int>
        get() = _rfDoor

    private val _rfDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(doorManager.obtainAccessState(IPart.R_F, Model.ACCESS_DOOR))
    }

    val rrDoor: LiveData<Int>
        get() = _rrDoor

    private val _rrDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(doorManager.obtainAccessState(IPart.R_B, Model.ACCESS_DOOR))
    }

    val headDoor: LiveData<Int>
        get() = _headDoor

    private val _headDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(sternDoorManager.obtainAccessState(IPart.HEAD, Model.ACCESS_STERN))
    }

    val tailDoor: LiveData<Int>
        get() = _tailDoor

    private val _tailDoor: MutableLiveData<Int> by lazy {
        MutableLiveData(sternDoorManager.obtainAccessState(IPart.TAIL, Model.ACCESS_STERN))
    }


    val lfWindow: LiveData<Int>
        get() = _lfWindow

    private val _lfWindow: MutableLiveData<Int> by lazy {
        var degree = windowManager.obtainAccessState(IPart.L_F, Model.ACCESS_WINDOW)!!
        if (degree < 0 || degree > 100) {
            degree = 0
        }
        val value = ((degree * -40).toFloat() / 100).toInt()
        Timber.d("WindowDegree _lfWindow degree:$degree, value:$value")
        MutableLiveData(value)
    }

    val lrWindow: LiveData<Int>
        get() = _lrWindow

    private val _lrWindow: MutableLiveData<Int> by lazy {
        var degree = windowManager.obtainAccessState(IPart.L_B, Model.ACCESS_WINDOW)!!
        if (degree < 0 || degree > 100) {
            degree = 0
        }
        val value = ((degree * -40).toFloat() / 100).toInt()
        Timber.d("WindowDegree _lrWindow degree:$degree, value:$value")
        MutableLiveData(value)
    }

    val rfWindow: LiveData<Int>
        get() = _rfWindow

    private val _rfWindow: MutableLiveData<Int> by lazy {
        var degree = windowManager.obtainAccessState(IPart.R_F, Model.ACCESS_WINDOW)!!
        if (degree < 0 || degree > 100) {
            degree = 0
        }
        val value = ((degree * -40).toFloat() / 100).toInt()
        Timber.d("WindowDegree _rfWindow degree:$degree, value:$value")
        MutableLiveData(value)
    }

    val rrWindow: LiveData<Int>
        get() = _rrWindow

    private val _rrWindow: MutableLiveData<Int> by lazy {
        var degree = windowManager.obtainAccessState(IPart.R_B, Model.ACCESS_WINDOW)!!
        if (degree < 0 || degree > 100) {
            degree = 0
        }
        val value = ((degree * -40).toFloat() / 100).toInt()
        Timber.d("WindowDegree _rrWindow degree:$degree, value:$value")
        MutableLiveData(value)
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

    //前大灯状态
    //0 关闭
    //1 近光
    //2 远光
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

    override fun onSignalChanged(part: Int, model: Int, signal: Int, value: Int) {
        if (Model.ACCESS_DOOR == model) {
            onDoorSignalChanged(part, value)
        } else if (Model.ACCESS_WINDOW == model) {
            onWindowSignalChanged(part, value)
        } else if (Model.ACCESS_STERN == model) {
            onInclusiveSignalChanged(part, value)
        } else if (Model.LIGHT_COMMON == model) {
            onLightSignalChanged(part, signal, value)
        }
    }

    private fun onHeadLampChanged() {
        val actual = headLamps.value!!
        val isLow = ON == lowLamp
        val isHigh = ON == highLamp
        do {
            if (0x0 == actual) {
                if (isHigh) {
                    doUpdate(_headLamps, HIGH_ON)
                    break
                }
                if (isLow) {
                    doUpdate(_headLamps, LOW_ON)
                    break
                }
            } else if (0x1 == actual) {
                if (isHigh) {
                    doUpdate(_headLamps, HIGH_ON)
                    break
                }
                if (!isLow) {
                    doUpdate(_headLamps, OFF)
                    break
                }
            } else if (0x2 == actual) {
                if (isHigh) {
                    break
                }
                if (isLow) {
                    doUpdate(_headLamps, LOW_ON)
                    break
                }
                doUpdate(_headLamps, OFF)
            }
        } while (false)
    }

    private fun onLightSignalChanged(part: Int, signal: Int, value: Int) {
        when (signal) {
            Constant.LOW_LAMP -> {
                lowLamp = value
                onHeadLampChanged()
            }
            Constant.HIGH_LAMP -> {
                highLamp = value
                onHeadLampChanged()
            }
            Constant.F_FOG_LAMP -> {}
            Constant.B_FOG_LAMP -> {
                doUpdate(_rearFogLamps, value)
            }
            Constant.POS_LAMP -> {
                doUpdate(_positionLamps, value)
            }
            Constant.BRAKE_LAMP -> {}
            else -> {}
        }

    }


    private fun onInclusiveSignalChanged(part: Int, value: Int) {
        when (part) {
            IPart.HEAD -> doUpdate(_headDoor, value)
            IPart.TAIL -> doUpdate(_tailDoor, value)
            else -> {}
        }
    }

    private fun onWindowSignalChanged(part: Int, degree: Int) {
        val newDegree = if (degree < 0 || degree > 100) 0 else degree
        val value = ((newDegree * -40).toFloat() / 100).toInt()
        Timber.d("WindowDegree onWindowSignalChanged part:$part, degree:$degree, newDegree:$newDegree, value:$value")
        when (part) {
            IPart.L_F -> doUpdate(_lfWindow, value)
            IPart.R_F -> doUpdate(_rfWindow, value)
            IPart.L_B -> doUpdate(_lrWindow, value)
            IPart.R_B -> doUpdate(_rrWindow, value)
            else -> {}
        }
    }

    private fun onDoorSignalChanged(part: Int, value: Int) {
        when (part) {
            IPart.L_F -> doUpdate(_lfDoor, value)
            IPart.R_F -> doUpdate(_rfDoor, value)
            IPart.L_B -> doUpdate(_lrDoor, value)
            IPart.R_B -> doUpdate(_rrDoor, value)
            else -> {}
        }
    }


}