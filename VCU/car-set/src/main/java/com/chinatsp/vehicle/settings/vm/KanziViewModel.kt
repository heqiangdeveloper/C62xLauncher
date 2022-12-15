package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.listener.ISignalListener
import com.chinatsp.settinglib.manager.access.DoorManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.manager.access.WindowManager
import com.chinatsp.settinglib.manager.lamp.LightManager
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

    private val lightManager: LightManager get() = LightManager.instance

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

    //前大灯状态: 0 关闭; 1 近光; 2 远光
    private val _headLamps: MutableLiveData<Int> by lazy {
        lowLamp = lightManager.obtainLightState(Model.LIGHT_COMMON, Constant.LOW_LAMP) ?: lowLamp
        highLamp = lightManager.obtainLightState(Model.LIGHT_COMMON, Constant.HIGH_LAMP) ?: highLamp
//        val expect = mergeHeadLampValue()
        val expect = if (highLamp == ON) 0x2 else if (ON == lowLamp) 0x1 else 0x0
        MutableLiveData(expect)
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
        val value = lightManager.obtainLightState(Model.LIGHT_COMMON, Constant.B_FOG_LAMP)
        MutableLiveData(value)
    }


    override fun onCreate() {
        super.onCreate()
        keySerial = doorManager.onRegisterVcuListener(listener = this)
        windowManager.onRegisterVcuListener(listener = this)
        sternDoorManager.onRegisterVcuListener(listener = this)
        lightManager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        doorManager.unRegisterVcuListener(serial = keySerial)
        windowManager.unRegisterVcuListener(serial = keySerial)
        sternDoorManager.unRegisterVcuListener(serial = keySerial)
        lightManager.unRegisterVcuListener(serial = keySerial)
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

    private fun mergeHeadLampValue(): Int {
        val actual = headLamps.value!!
        val isLow = ON == lowLamp
        val isHigh = ON == highLamp
        var result = actual
        do {
            if (0x0 == actual) {
                if (isHigh) {
                    result = HIGH_ON
                    break
                }
                if (isLow) {
                    result = LOW_ON
                    break
                }
            } else if (0x1 == actual) {
                if (isHigh) {
                    result = HIGH_ON
                    break
                }
                if (!isLow) {
                    result = OFF
                    break
                }
            } else if (0x2 == actual) {
                if (isHigh) {
                    break
                }
                if (isLow) {
                    result = LOW_ON
                    break
                }
                result = OFF
            }
        } while (false)
        return result
    }

    private fun onLightSignalChanged(part: Int, signal: Int, value: Int) {
        when (signal) {
            Constant.LOW_LAMP -> {
                lowLamp = value
                val expect = mergeHeadLampValue()
                doUpdate(_headLamps, expect)
            }
            Constant.HIGH_LAMP -> {
                highLamp = value
                val expect = mergeHeadLampValue()
                doUpdate(_headLamps, expect)
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