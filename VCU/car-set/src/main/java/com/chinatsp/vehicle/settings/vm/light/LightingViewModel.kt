package com.chinatsp.vehicle.settings.vm.light

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.lamp.LightManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LightingViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), IOptionListener {

    private val manager: IOptionManager
        get() = LightManager.instance

    val insideLightMeet: LiveData<Boolean>
        get() = _insideLightMeet

    private val _insideLightMeet: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.LIGHT_INSIDE_MEET
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val outsideLightMeet: LiveData<Boolean>
        get() = _outsideLightMeet

    private val _outsideLightMeet: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.LIGHT_OUTSIDE_MEET
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            updateLiveData(this, value)
        }
    }

    val lightOutDelayed: LiveData<Int>
        get() = _lightOutDelayed

    private val _lightOutDelayed: MutableLiveData<Int> by lazy {
        val node = RadioNode.LIGHT_DELAYED_OUT
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            updateLiveData(this, value)
        }
    }

    val lightFlicker: LiveData<Int>
        get() = _lightFlicker

    private val _lightFlicker: MutableLiveData<Int> by lazy {
        val node = RadioNode.LIGHT_FLICKER
        MutableLiveData(node.default).apply {
            val value = manager.doGetRadioOption(node)
            updateLiveData(this, value)
        }
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Boolean>,
        value: Boolean
    ): MutableLiveData<Boolean> {
        liveData.takeIf { value xor (liveData.value == true) }?.value = value
        return liveData
    }

    private fun updateLiveData(
        liveData: MutableLiveData<Int>,
        value: Int
    ): MutableLiveData<Int> {
        liveData.takeIf { value != liveData.value }?.value = value
        return liveData
    }

    override fun onSwitchOptionChanged(status: Boolean, node: SwitchNode) {
        when (node) {
            SwitchNode.LIGHT_OUTSIDE_MEET -> {
                updateLiveData(_outsideLightMeet, status)
            }
            SwitchNode.LIGHT_INSIDE_MEET -> {
                updateLiveData(_insideLightMeet, status)
            }
            else -> {}
        }
    }

    override fun onRadioOptionChanged(node: RadioNode, value: Int) {
        when (node) {
            RadioNode.LIGHT_DELAYED_OUT -> {
                updateLiveData(_lightOutDelayed, value)
            }
            RadioNode.LIGHT_FLICKER -> {
                updateLiveData(_lightFlicker, value)
            }
            else -> {}
        }
    }


}