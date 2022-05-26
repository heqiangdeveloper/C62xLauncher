package com.chinatsp.settinglib.sign

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/26 13:58
 * @desc   :
 * @version: 1.0
 */
class TabBlock(val type: Type) {

    val signals: MutableSet<CarSign> by lazy {
        HashSet<CarSign>().also {
            when (type) {
                Type.COMMON -> initCommonSignal(it)
                Type.WINDOW -> initCabinSignal(it)
                Type.LIGHT -> initCabinSignal(it)
                Type.SOUND -> initCabinSignal(it)
                Type.DRIVE -> initCabinSignal(it)
                Type.CABIN -> initCabinSignal(it)
                Type.SYSTEM -> initCabinSignal(it)
            }
        }
    }



    private fun initCabinSignal(hashSet: HashSet<CarSign>) {
        hashSet.run {
            var carSign = CarSign(CarSign.Type.CAR_CABIN_SERVICE)
            carSign.signals.let {
                /**空调自干燥*/
                it.add(CarCabinManager.ID_ACSELFSTSDISP)
                /**预通风功能*/
                it.add(CarCabinManager.ID_ACPREVENTNDISP)
                /**空调舒适性状态显示*/
                it.add(CarCabinManager.ID_ACCMFTSTSDISP)
            }
            add(carSign)
            carSign = CarSign(CarSign.Type.CAR_HVAC_SERVICE)
            carSign.signals.let {
                /**自动除雾*/
                it.add(CarHvacManager.ID_HVAC_AVN_KEY_DEFROST)
            }
            add(carSign)
        }
    }

    private fun initCommonSignal(hashSet: HashSet<CarSign>) {
        hashSet.run {
            var carSign = CarSign(CarSign.Type.CAR_CABIN_SERVICE)
            carSign.signals.let {
                /**空调自干燥*/
                it.add(CarCabinManager.ID_ACSELFSTSDISP)
                /**预通风功能*/
                it.add(CarCabinManager.ID_ACPREVENTNDISP)
                /**空调舒适性状态显示*/
                it.add(CarCabinManager.ID_ACCMFTSTSDISP)
            }
            add(carSign)
            carSign = CarSign(CarSign.Type.CAR_HVAC_SERVICE)
            carSign.signals.let {
                /**自动除雾*/
                it.add(CarHvacManager.ID_HVAC_AVN_KEY_DEFROST)
            }
            add(carSign)
        }
    }

    fun contains(type: CarSign.Type, propertyId: Int): Boolean {
        try {
            return signals.first { type == it.type }.signals.contains(propertyId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    enum class Type {
        COMMON,
        WINDOW,
        LIGHT,
        SOUND,
        DRIVE,
        CABIN,
        SYSTEM
    }
}