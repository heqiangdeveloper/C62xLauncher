package com.chinatsp.settinglib

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.util.Log
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.optios.Area
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

val TAG = ACManager::class.simpleName

class ACManager private constructor() : IConcernChanged, IAcManager {

    private val autoAridProperty = CarCabinManager.ID_ACSELFSTSDISP

    private val autoWindAdvanceProperty = CarCabinManager.ID_ACPREVENTNDISP

    private val autoComfortProperty = CarCabinManager.ID_ACCMFTSTSDISP

//    private val autoDemistProperty = CarDYSensorManager.ID_HVAC_AVN_KEY_DEFROST

    private val selfSerial by lazy { System.identityHashCode(this) }

    private val listenerMap by lazy {
        HashMap<Int, WeakReference<IBaseListener>>()
    }

    var autoDrySwitchStatus: Boolean = false

    var advanceHairSwitchStatus: Boolean = false

    init {
        val value =
            SettingManager.getInstance().obtainCabinIntProperty(autoAridProperty, Area.GLOBAL)
        autoDrySwitchStatus = Status1.ON.value == value
    }

    override fun obtainAutoAridStatus(): Boolean {
        val value = SettingManager.getInstance().obtainCabinIntProperty(autoAridProperty, Area.GLOBAL)
        return Status1.ON.value == value
    }

    override fun obtainAutoWindStatus(): Boolean {
        val value = SettingManager.getInstance().obtainCabinIntProperty(autoWindAdvanceProperty, Area.GLOBAL)
        return Status1.ON.value == value
    }

    override fun obtainAutoDemistStatus(): Boolean {
        val value = SettingManager.getInstance().obtainCabinIntProperty(autoWindAdvanceProperty, Area.GLOBAL)
        return Status1.ON.value == value
    }

    override fun obtainAutoComfortOption(): Int {
        return SettingManager.getInstance().obtainCabinIntProperty(autoComfortProperty, Area.GLOBAL)
    }


    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        Log.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerMap) {
            val contains = listenerMap.containsKey(serial)
            if (contains) listenerMap.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial = System.identityHashCode(listener)
        synchronized(listenerMap) {
            unRegisterVcuListener(serial, selfSerial)
            listenerMap.put(serial, WeakReference(listener))
        }
        return serial
    }

    private fun issueCabinIntProperty(id: Int, value: Int, area: Area = Area.GLOBAL): Boolean {
        val settingManager = SettingManager.getInstance()
        return settingManager.issueCabinIntProperty(id, value, area)
    }

    /**
     * 更新空调舒适性
     * @param value (空调舒适性状态显示
     * 0x0: Reserved
     * 0x1: Gentle
     * 0x2: Standard
     * 0x3: Powerful
     * 0x4~0x6: Reserved
     * 0x7: Invalid)
     */
    fun doUpdateACComfort(value: Int): Boolean {
        val invalidValue = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!invalidValue) {
            return false
        }
        return issueCabinIntProperty(CarCabinManager.ID_ACCMFTSTSDISP, value)
    }

    /**
     *
     * @param switchNape 开关选项
     * @param isStatus 开关期望状态
     */
    fun doSwitchACOption(switchNape: SwitchNape, isStatus: Boolean): Boolean {
        val status = if (isStatus) {
            Status1.ON
        } else {
            Status1.OFF
        }
        val id = when (switchNape) {
            SwitchNape.AC_AUTO_ARID -> autoAridProperty
//            SwitchNape.AC_AUTO_DEMIST -> CarCabinManager.ID_HVAC_AVN_KEY_DEFROST
            SwitchNape.AC_ADVANCE_WIND -> autoWindAdvanceProperty
            else -> {
                null
            }
        }
        if (null != id) {
            return issueCabinIntProperty(id, status.value)
        }
        return false
    }

    override fun onPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            autoAridProperty -> {
                onAutoDryStatusChanged(property.value)
            }
            //预通风功能
            autoWindAdvanceProperty -> {
                onAdvanceHairStatusChanged(property.value)
            }
            //自动空调舒适性
            CarCabinManager.ID_ACCMFTSTSDISP -> {

            }
            else -> {}
        }
    }

    private fun onAutoDryStatusChanged(value: Any?) {
        Log.d("luohong", "onAutoDryStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (autoDrySwitchStatus xor status) {
                autoDrySwitchStatus = status
                notifySwitchStatus(autoDrySwitchStatus, SwitchNape.AC_AUTO_ARID)
            }
        }
    }

    private fun onAdvanceHairStatusChanged(value: Any?) {
        Log.d("luohong", "onAdvanceHairStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (advanceHairSwitchStatus xor status) {
                advanceHairSwitchStatus = status
                notifySwitchStatus(advanceHairSwitchStatus, SwitchNape.AC_ADVANCE_WIND)
            }
        }
    }

    private fun notifySwitchStatus(status: Boolean, type: SwitchNape) {
        synchronized(listenerMap) {
            listenerMap.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is IACListener) {
                        listener.onACSwitchStatusChanged(status, type)
                    }
                }
        }
    }

    companion object {

        val instance: ACManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ACManager()
        }

        @JvmStatic
        val concernIdList: List<Int> by lazy {
            val list = ArrayList<Int>()
            list.add(CarCabinManager.ID_ACSELFSTSDISP)
            list.add(CarCabinManager.ID_ACPREVENTNDISP)
            list.add(CarCabinManager.ID_ACC_DISTANCE_LEVEL)
            list.add(CarCabinManager.ID_AC_DIS_AC_MAX)
            list.add(CarCabinManager.ID_AC_DIS_API_INSIDE)
            list.add(CarCabinManager.ID_AC_DIS_INSIDE_PM2_5_DATA)
            list.add(CarCabinManager.ID_AC_DIS_OUTSIDE_PM2_5_DATA)
            list.add(CarCabinManager.ID_AC_DIS_IN_CAR_TEMPERATURE)
            list.add(CarCabinManager.ID_AC_DIS_AMBIENT_TEMPERATURE)
            list.add(CarCabinManager.ID_AC_REFRESH_MODE_ACT_STS)
            list.add(CarCabinManager.ID_AC_REFRESH_MODE_SET_STATUS)
            list.add(CarCabinManager.ID_AC_AUTOMATICDE_FOGGING_STATUS)
            return@lazy list
        }
    }


    /**
     * 空调开关选项
     */
    enum class SwitchNape {
        /**
         * 空调自干燥
         */
        AC_AUTO_ARID,

        /**
         * 自动除雾
         */
        AC_AUTO_DEMIST,

        /**
         * 预通风功能
         */
        AC_ADVANCE_WIND
    }


}