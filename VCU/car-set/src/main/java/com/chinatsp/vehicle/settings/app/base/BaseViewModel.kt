package com.chinatsp.vehicle.settings.app.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chinatsp.vehicle.settings.api.ApiService
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

    val apiService: ApiService by lazy {
        getRetrofitService(ApiService::class.java)
    }

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

    fun doUpdate(liveData: MutableLiveData<Boolean>, status: Boolean) {
        if (null == liveData.value) {
            liveData.postValue(status)
            return
        }
        if (liveData.value!! xor status) {
            liveData.postValue(status)
        }
    }

    fun doUpdate(liveData: MutableLiveData<Int>, value: Int, valid: Boolean = true) {
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
}