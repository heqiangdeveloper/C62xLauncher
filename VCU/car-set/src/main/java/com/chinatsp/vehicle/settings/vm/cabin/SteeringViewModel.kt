package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 14:16
 * @desc   :
 * @version: 1.0
 */
@HiltViewModel
class SteeringViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IProgressListener, IOptionListener {

    private val manager: WheelManager by lazy { WheelManager.instance }

    val swhFunction: LiveData<SwitchState>
        get() = _swhFunction

    private val _swhFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val sillTemp: LiveData<Volume>
        get() = _sillTemp

    private val _sillTemp: MutableLiveData<Volume> by lazy {
        val type = Progress.STEERING_ONSET_TEMPERATURE
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(type)?.deepCopy()
        }
    }

    val epsMode: LiveData<RadioState>
        get() = _epsMode

    private val _epsMode: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        MutableLiveData(manager.doGetRadioOption(node))
    }


    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                doUpdate(_swhFunction, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.DRIVE_EPS_MODE -> {
                doUpdate(_epsMode, value)
            }
            else -> {}
        }
    }

//    override fun onSoundVolumeChanged(vararg array: Volume) {
//        array.forEach {
//            when (it.type) {
//                Progress.STEERING_ONSET_TEMPERATURE -> {
//                    updateVolume(_sillTemp, it)
//                }
//                else -> {}
//            }
//        }
//    }

    private fun updateVolumeValue(liveData: MutableLiveData<Volume>, node: Progress, value: Int) {
        liveData.value?.let {
            val isMin = it.min == node.min
            val isMax = it.max == node.max
            val isPos = it.pos == value
            Timber.d("updateVolumeValue mode:$node, value:$value, isMin:$isMin, isMax:$isMax, isPos:$isPos")
            if (isMin && isMax && isPos) {
                return
            }
            if (!isMin) {
                it.min = node.min
            }
            if (!isMax) {
                it.max = node.max
            }
            if (!isPos) {
                it.pos = value
            }
            liveData.postValue(it)
        }
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.STEERING_ONSET_TEMPERATURE -> {
                updateVolumeValue(_sillTemp, node, value)
            }
            else -> {}
        }
    }

}