package com.chinatsp.vehicle.settings.app.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.vehicle.settings.bean.Result
import com.common.library.frame.base.BaseModel
import com.common.library.frame.base.DataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application, model: BaseModel) :
    DataViewModel(application, model) {

    var keySerial: Int = 0

//    val apiService: ApiService by lazy {
//        getRetrofitService(ApiService::class.java)
//    }

    fun isSuccess(result: Result<*>?, showError: Boolean = true): Boolean {
        if (result?.isSuccess == true) {
            return true
        }
        if (showError) {
            result?.message?.let {
                sendMessage(it)
            }
        }
        return false
    }

    fun launch(showLoading: Boolean = true, block: suspend () -> Unit) =
        launch(showLoading, block) {
            Timber.w(it)
            sendMessage(it.message)
        }

    fun launch(
        showLoading: Boolean,
        block: suspend () -> Unit,
        error: suspend (Throwable) -> Unit,
    ) = viewModelScope.launch {
        try {
            if (showLoading) {
                showLoading()
            }
            block()
        } catch (e: Throwable) {
            error(e)
        }
        if (showLoading) {
            hideLoading()
        }
    }

    fun doUpdate(liveData: MutableLiveData<SwitchState>, value: SwitchState) {
        liveData.postValue(value)
        if (true) return
        do {
            if (null == liveData.value) {
                liveData.postValue(value)
                break
            }
            val state = liveData.value!!
            val modelSerial = System.identityHashCode(state)
            val dataSerial = System.identityHashCode(value)
            val sameObj = modelSerial == dataSerial
            val statusChanged = state.get() xor value.get()
            val enableChanged = state.enable() xor state.enable(value.enable)
            if (statusChanged) {
                state.set(value.get())
            }
            if (enableChanged) {
                state.enable = value.enable
            }
            Timber.d("doUpdate switch modelSerial:$modelSerial, dataSerial:$dataSerial, " +
                    "sameObj:$sameObj statusChanged：$statusChanged, enableChanged:$enableChanged")
            if (statusChanged || enableChanged) {
                liveData.postValue(state)
            }
        } while (false)
    }

    fun doUpdate(liveData: MutableLiveData<RadioState>, value: RadioState, valid: Boolean = true) {
        liveData.postValue(value)
        if (true) return
        if (!valid) {
            return
        }
        if (null == liveData.value) {
            liveData.postValue(value)
            return
        }

        val state = liveData.value!!
        val modelSerial = System.identityHashCode(state)
        val dataSerial = System.identityHashCode(value)
        val sameObj = modelSerial == dataSerial
        Timber.d("doUpdate radio modelSerial:$modelSerial, dataSerial:$dataSerial, sameObj:$sameObj")
        val statusChanged = state.get() != value.get()
        val enableChanged = state.enable != value.enable
        if (statusChanged) {
            state.set(value.get())
        }
        if (enableChanged) {
            state.enable = value.enable
        }
        if (statusChanged || enableChanged) {
            liveData.postValue(state)
        }
    }

    fun doUpdate(liveData: MutableLiveData<Int>, value: Int, valid: Boolean = true) {
        Timber.d("AmbientLightingManager ${liveData.value}, value:$value")
        liveData.postValue(value)
        if (!valid) {
            return
        }
        if (null == liveData.value) {
            liveData.postValue(value)
            return
        }
        if (liveData.value!! != value) {
            liveData.postValue(value)
        }
    }

    fun doUpdateProgress(liveData: MutableLiveData<Volume>, value: Int, valid: Boolean = true) {
        Timber.d("AmbientLightingManager---------- ${liveData.value?.pos}, value:$value")
        liveData.postValue(liveData.value)
    }
}