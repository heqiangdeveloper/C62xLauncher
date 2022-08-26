package com.chinatsp.vehicle.settings.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.bean.TabPage
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/15 12:00
 * @desc   :
 * @version: 1.0
 */
@HiltViewModel
class MainViewModel @Inject constructor(app: Application, model: BaseModel) : BaseViewModel(app, model) {

    private val liveDataTabPage: MutableLiveData<List<TabPage>> by lazy {
        MutableLiveData<List<TabPage>>().apply {
            loadTabPageArray(this)
        }
    }

    private fun loadTabPageArray(it: MutableLiveData<List<TabPage>>) {
        val values = TabPage.values()
        val toList = values.toList()
        it.value = toList
    }


}