package com.chinatsp.vehicle.settings.vm.accress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.cabin.access.BackMirrorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MirrorViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model), ISwitchListener {

    private val manager: BackMirrorManager
        get() = BackMirrorManager.instance

    val mirrorFoldFunction: LiveData<SwitchState> by lazy { _mirrorFoldFunction }

    private val _mirrorFoldFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.BACK_MIRROR_FOLD
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val mirrorDownFunction: LiveData<SwitchState> by lazy { _mirrorDownFunction }

    private val _mirrorDownFunction: MutableLiveData<SwitchState> by lazy {
        val node = SwitchNode.BACK_MIRROR_DOWN
        MutableLiveData(manager.doGetSwitchOption(node))
    }

    val angleReturnSignal: LiveData<Int>
        get() = _angleReturnSignal

    private val _angleReturnSignal: MutableLiveData<Int> by lazy {
        MutableLiveData(Constant.DEFAULT)
    }

    override fun onCreate() {
        super.onCreate()
        keySerial = manager.onRegisterVcuListener(listener = this)
    }

    override fun onDestroy() {
        manager.unRegisterVcuListener(keySerial)
        super.onDestroy()
    }


    override fun onSwitchOptionChanged(status: SwitchState, node: SwitchNode) {
        when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> {
                doUpdate(_mirrorFoldFunction, status)
            }
            SwitchNode.BACK_MIRROR_DOWN -> {
                doUpdate(_mirrorDownFunction, status)
            }
            else -> {

            }
        }
    }

    override fun isCareSignal(signal: Int): Boolean {
        return Constant.ANGLE_RETURN_SIGNAL == signal
    }

    override fun doNonstopValue(signal: Int, value: Int) {
        if (Constant.ANGLE_RETURN_SIGNAL == signal) {
            _angleReturnSignal.postValue(value)
        }
    }

}