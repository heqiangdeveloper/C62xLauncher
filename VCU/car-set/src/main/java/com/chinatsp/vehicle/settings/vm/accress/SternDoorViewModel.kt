package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SternDoorViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener, IProgressListener {

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    val electricFunction: LiveData<Boolean> get() = _electricFunction

    private val _electricFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.AS_STERN_ELECTRIC
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val lightAlarmFunction: LiveData<Boolean> get() = _lightAlarmFunction

    private val _lightAlarmFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.STERN_LIGHT_ALARM
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val audioAlarmFunction: LiveData<Boolean> get() = _audioAlarmFunction

    private val _audioAlarmFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.STERN_AUDIO_ALARM
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val sternSmartEnter: LiveData<Int> get() = _sternSmartEnter

    private val _sternSmartEnter: MutableLiveData<Int> by lazy {
        val node = RadioNode.STERN_SMART_ENTER
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val trunkStopPosition: LiveData<Volume> get() = _trunkStopPosition

    private val _trunkStopPosition: MutableLiveData<Volume> by lazy {
        val node = Progress.TRUNK_STOP_POSITION
        MutableLiveData<Volume>().apply {
            this.value = manager.doGetVolume(node)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(serial = keySerial)
        super.onDestroy()
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                doUpdate(_electricFunction, status)
            }
            SwitchNode.STERN_LIGHT_ALARM -> {
                doUpdate(_lightAlarmFunction, status)
            }
            SwitchNode.STERN_AUDIO_ALARM -> {
                doUpdate(_audioAlarmFunction, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        if (RadioNode.STERN_SMART_ENTER == node) {
            doUpdate(_sternSmartEnter, value)
        }
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.TRUNK_STOP_POSITION -> {
                updateVolume(_trunkStopPosition, value)
            }
            else -> {}
        }
    }

    private fun updateVolume(target: MutableLiveData<Volume>, value: Int) {
        target.takeIf { it.value?.pos != value }?.let {
            it.value?.pos = value
            target.postValue(target.value)
        }
    }

}