package com.chinatsp.settinglib

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 15:04
 * @desc   :
 * @version: 1.0
 */
interface IConcernChanged {

    fun onPropertyChanged(type: Origin, property: CarPropertyValue<*>)
}