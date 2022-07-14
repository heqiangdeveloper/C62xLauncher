package com.chinatsp.vehicle.settings

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/25 16:42
 * @desc   :
 * @version: 1.0
 */
interface IRoute {

    fun obtainLevelLiveData(): LiveData<Node>

    fun obtainPopupLiveData(): LiveData<String>

    fun cleanPopupLiveDate(serial: String): Boolean

}