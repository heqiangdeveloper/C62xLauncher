package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
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
class SeatViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener, ISwitchListener {

    private val manager: SeatManager by lazy { SeatManager.instance }

    val mainMeet: LiveData<Boolean>
        get() = _mainMeetFunction

    private val _mainMeetFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.SEAT_MAIN_DRIVE_MEET
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val forkMeet: LiveData<Boolean>
        get() = _forkMeetFunction

    private val _forkMeetFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.SEAT_FORK_DRIVE_MEET
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val seatHeat: LiveData<Boolean>
        get() = _seatHeatFunction

    private val _seatHeatFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.SEAT_HEAT_ALL
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val sillTemp: LiveData<Volume>
        get() = _sillTemp

    private val _sillTemp: MutableLiveData<Volume> by lazy {
        val type = Progress.SEAT_ONSET_TEMPERATURE
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(type)?.copy()
        }
    }

    val epsMode: LiveData<Int>
        get() = _epsMode

    private val _epsMode: MutableLiveData<Int> by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            doUpdate(this, value, node.isValid(value))
        }
    }


    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial, keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.SEAT_MAIN_DRIVE_MEET -> {
                doUpdate(_mainMeetFunction, status)
            }
            SwitchNode.SEAT_FORK_DRIVE_MEET -> {
                doUpdate(_forkMeetFunction, status)
            }
            SwitchNode.SEAT_HEAT_ALL -> {
                doUpdate(_seatHeatFunction, status)
            }
            else -> {}
        }
    }

    override fun onSoundVolumeChanged(vararg array: Volume) {
        array.forEach {
            when (it.type) {
                Progress.SEAT_ONSET_TEMPERATURE -> {
                    updateVolume(_sillTemp, it)
                }
                else -> {}
            }
        }
    }

    private fun updateVolume(target: MutableLiveData<Volume>, expect: Volume) {
        target.takeIf { it.value?.type == expect.type }?.let {
            it.takeUnless { it.value == expect }?.let { liveData ->
                liveData.value?.pos = expect.pos
                liveData.postValue(liveData.value)
            }
        }
    }

}