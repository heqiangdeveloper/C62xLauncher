package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
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
class SeatViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IProgressListener, ISwitchListener {

    private val manager: SeatManager by lazy { SeatManager.instance }

    val mainMeet: LiveData<SwitchState>
        get() = _mainMeet

    private val _mainMeet: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.MAIN_SEAT_WELCOME
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val forkMeet: LiveData<SwitchState>
        get() = _forkMeet

    private val _forkMeet: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.FORK_SEAT_WELCOME
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val seatHeat: LiveData<SwitchState>
        get() = _seatHeatFunction

    private val _seatHeatFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.SEAT_HEAT_ALL
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val sillTemp: LiveData<Volume>
        get() = _sillTemp

    private val _sillTemp: MutableLiveData<Volume> by lazy {
        val type = Progress.SEAT_ONSET_TEMPERATURE
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(type)
        }
    }

    val epsMode: LiveData<RadioState>
        get() = _epsMode

    private val _epsMode: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val node654: LiveData<SwitchState>
        get() = _node654

    private val _node654: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_654
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.MAIN_SEAT_WELCOME -> {
                doUpdate(_mainMeet, status)
            }
            SwitchNode.FORK_SEAT_WELCOME -> {
                doUpdate(_forkMeet, status)
            }
            SwitchNode.SEAT_HEAT_ALL -> {
                doUpdate(_seatHeatFunction, status)
            }
            SwitchNode.NODE_VALID_654 -> {
                doUpdate(_node654, status)
            }
            else -> {}
        }
    }

    private fun updateVolumeValue(liveData: MutableLiveData<Volume>, node: Progress, value: Int) {
        liveData.value?.let {
            liveData.postValue(it)
            if (true) return
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
            Progress.SEAT_ONSET_TEMPERATURE -> {
                updateVolumeValue(_sillTemp, node, value)
            }
            else -> {}
        }
    }

}