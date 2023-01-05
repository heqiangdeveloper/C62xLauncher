package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.listener.IProgressListener
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.lamp.LightManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LightingViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener, IProgressListener {

    private val manager: LightManager
        get() = LightManager.instance

    val insideLightMeet: LiveData<SwitchState>
        get() = _insideLightMeet

    private val _insideLightMeet: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val outsideLightMeet: LiveData<SwitchState>
        get() = _outsideLightMeet

    private val _outsideLightMeet: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val ceremonySenseSwitch: LiveData<SwitchState>
        get() = _ceremonySenseSwitch

    private val _ceremonySenseSwitch: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.LIGHT_CEREMONY_SENSE
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val lightOutDelayed: LiveData<RadioState>
        get() = _lightOutDelayed

    private val _lightOutDelayed: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.LIGHT_DELAYED_OUT
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val lightFlicker: LiveData<RadioState>
        get() = _lightFlicker

    private val _lightFlicker: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.LIGHT_FLICKER
        MutableLiveData(manager.doGetRadioOption(node))
    }

    val ceremonySense: LiveData<RadioState>
        get() = _ceremonySense

    private val _ceremonySense: MutableLiveData<RadioState> by lazy {
        val node = RadioNode.LIGHT_CEREMONY_SENSE
        MutableLiveData(manager.doGetRadioOption(node))
    }


    val switchBacklight: LiveData<Volume>
        get() = _switchBacklight

    private val _switchBacklight: MutableLiveData<Volume> by lazy {
        val node = Progress.SWITCH_BACKLIGHT_BRIGHTNESS
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(node)
        }
    }
    val node5B3: LiveData<SwitchState>
        get() = _node5B3

    private val _node5B3: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_5B3
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    val node362: LiveData<SwitchState>
        get() = _node362

    private val _node362: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.NODE_VALID_362
        MutableLiveData(GlobalManager.instance.doGetSwitchOption(node))
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
        GlobalManager.instance.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        GlobalManager.instance.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

//    private fun updateLiveData(
//        liveData: MutableLiveData<Boolean>,
//        value: Boolean,
//    ): MutableLiveData<Boolean> {
//        liveData.takeIf { value xor (liveData.value == true) }?.postValue(value)
//        return liveData
//    }
//
//    private fun updateLiveData(
//        liveData: MutableLiveData<Int>,
//        value: Int,
//    ): MutableLiveData<Int> {
//        liveData.takeIf { value != liveData.value }?.postValue(value)
//        return liveData
//    }

    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.LIGHT_OUTSIDE_MEET -> {
                doUpdate(_outsideLightMeet, status)
            }
            SwitchNode.LIGHT_INSIDE_MEET -> {
                doUpdate(_insideLightMeet, status)
            }
            SwitchNode.LIGHT_CEREMONY_SENSE -> {
                doUpdate(_ceremonySenseSwitch, status)
            }
            SwitchNode.NODE_VALID_362 ->{
                doUpdate(_node362,status)
            }
            SwitchNode.NODE_VALID_5B3 ->{
                doUpdate(_node5B3,status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: RadioState) {
        when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> {
                doUpdate(_lightOutDelayed, value)
            }
            RadioNode.LIGHT_FLICKER -> {
                doUpdate(_lightFlicker, value)
            }
            RadioNode.LIGHT_CEREMONY_SENSE -> {
                doUpdate(_ceremonySense, value)
            }
            else -> {}
        }
    }

    override fun onProgressChanged(node: Progress, value: Int) {
        when (node) {
            Progress.SWITCH_BACKLIGHT_BRIGHTNESS -> {
                updateVolumeValue(_switchBacklight, node, value)
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

}