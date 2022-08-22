package com.chinatsp.vehicle.settings.vm

import android.app.Application
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.common.library.frame.base.BaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DriveViewModel @Inject constructor(app: Application, model: BaseModel) :
    BaseViewModel(app, model) {

}