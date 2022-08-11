package com.chinatsp.vehicle.settings.vm.cabin

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.manager.cabin.WheelManager
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
class SteeringViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISoundListener, ISwitchListener {

    private val manager: WheelManager by lazy { WheelManager.instance }

    val swhFunction: LiveData<Boolean>
        get() = _swhFunction

    private val _swhFunction: MutableLiveData<Boolean> by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
        MutableLiveData(node.default).apply {
            val value = manager.doGetSwitchOption(node)
            setValue(value)
        }
    }

    val sillTemp: LiveData<Volume>
        get() = _sillTemp

    private val _sillTemp: MutableLiveData<Volume> by lazy {
        val type = Volume.Type.STEERING_EDGE_TEMP
        MutableLiveData<Volume>().apply {
            value = manager.doGetVolume(type)?.copy()
        }
    }

    val epsMode: LiveData<Int>
        get() = _epsMode

    private val _epsMode: MutableLiveData<Int> by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        MutableLiveData(node.default).apply {
            value = manager.doGetRadioOption(node)
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
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                _swhFunction.takeIf { it.value != status }?.postValue(status)
            }
            else ->{}
        }
    }

    override fun onSoundVolumeChanged(vararg array: Volume) {
        array.forEach {
            when (it.type) {
                Volume.Type.STEERING_EDGE_TEMP -> {
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
                liveData.value = liveData.value
            }
        }
    }

}