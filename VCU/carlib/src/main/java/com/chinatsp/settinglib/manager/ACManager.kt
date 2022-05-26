package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.IConcernChanged
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.bean.Status1
import com.chinatsp.settinglib.listener.IACListener
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.cabin.IAcManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.sign.CarSign
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

val TAG: String = ACManager::class.java.simpleName

class ACManager private constructor() : IConcernChanged, IAcManager {

    private val autoAridProperty = CarCabinManager.ID_ACSELFSTSDISP

    private val autoWindAdvanceProperty = CarCabinManager.ID_ACPREVENTNDISP

    private val autoComfortProperty = CarCabinManager.ID_ACCMFTSTSDISP

    private val autoDemistProperty = CarHvacManager.ID_HVAC_AVN_KEY_DEFROST

    private val selfSerial by lazy { System.identityHashCode(this) }

    private val listenerStore by lazy { HashMap<Int, WeakReference<IBaseListener>>() }

    companion object {
        val instance: ACManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ACManager()
        }
    }

    val aridStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoAridStatus()) }
    }

    val demistStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoDemistStatus()) }
    }

    val windStatus: AtomicBoolean by lazy {
        AtomicBoolean(false).also { it.set(obtainAutoWindStatus()) }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    override fun obtainAutoAridStatus(): Boolean {
        val value =
            SettingManager.getInstance().obtainCabinIntProperty(autoAridProperty, Area.GLOBAL)
        LogManager.d("obtainAutoAridStatus value:$value")
        return Status1.ON.value == value
    }

    override fun obtainAutoWindStatus(): Boolean {
        val value = SettingManager.getInstance()
            .obtainCabinIntProperty(autoWindAdvanceProperty, Area.GLOBAL)
        LogManager.d("obtainAutoWindStatus value:$value")
        return Status1.ON.value == value
    }

    override fun obtainAutoDemistStatus(): Boolean {
        val value = SettingManager.getInstance()
            .obtainCabinIntProperty(autoWindAdvanceProperty, Area.GLOBAL)
        return Status1.ON.value == value
    }

    override fun obtainAutoComfortOption(): Int {
        return SettingManager.getInstance().obtainCabinIntProperty(autoComfortProperty, Area.GLOBAL)
    }


    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
//            listenerStore.let {
//                if (it.containsKey(serial)) it else null
//            }?.remove(serial)
            val contains = listenerStore.containsKey(serial)
            if (contains) listenerStore.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        val serial: Int = System.identityHashCode(listener)
        synchronized(listenerStore) {
            unRegisterVcuListener(serial, selfSerial)
            listenerStore.put(serial, WeakReference(listener))
        }
        return serial
    }

    private fun issueCabinIntProperty(id: Int, value: Int, area: Area = Area.GLOBAL): Boolean {
        val settingManager = SettingManager.getInstance()
        return settingManager.issueCabinIntProperty(id, value, area)
    }

    private fun issueHvacIntProperty(id: Int, value: Int, area: Area = Area.GLOBAL): Boolean {
        val settingManager = SettingManager.getInstance()
        return settingManager.issueHvacIntProperty(id, value, area)
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
        return when (switchNape) {
            SwitchNape.AC_AUTO_ARID -> {
                issueCabinIntProperty(autoAridProperty, status.value)
            }
            SwitchNape.AC_AUTO_DEMIST -> {
                issueHvacIntProperty(autoDemistProperty, status.value)
            }
            SwitchNape.AC_ADVANCE_WIND -> {
                issueCabinIntProperty(autoWindAdvanceProperty, status.value)
            }
        }
    }

    override fun onPropertyChanged(type: CarSign.Type, property: CarPropertyValue<*>) {
        when (type) {
            CarSign.Type.CAR_CABIN_SERVICE -> {

            }
            CarSign.Type.CAR_HVAC_SERVICE -> {

            }
        }
    }

    fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //自动除雾
            autoDemistProperty -> {
                onAutoDemistStatusChanged(property.value)
            }
            else -> {}
        }
    }

    fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //空调自干燥
            autoAridProperty -> {
                onAutoAridStatusChanged(property.value)
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


    private fun onAutoAridStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAutoAridStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (aridStatus.get() xor status) {
                aridStatus.set(status)
                notifySwitchStatus(aridStatus.get(), SwitchNape.AC_AUTO_ARID)
            }
        }
    }

    private fun onAutoDemistStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAutoDemistStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (demistStatus.get() xor status) {
                demistStatus.set(status)
                notifySwitchStatus(demistStatus.get(), SwitchNape.AC_AUTO_DEMIST)
            }
        }
    }

    private fun onAdvanceHairStatusChanged(value: Any?) {
        LogManager.d(TAG, "onAdvanceHairStatusChanged value:$value")
        if (value is Int) {
            val status = value == Status1.ON.value
            if (windStatus.get() xor status) {
                windStatus.set(status)
                notifySwitchStatus(windStatus.get(), SwitchNape.AC_ADVANCE_WIND)
            }
        }
    }

    private fun notifySwitchStatus(status: Boolean, type: SwitchNape) {
        synchronized(listenerStore) {
            listenerStore.filter { null != it.value.get() }
                .forEach {
                    val listener = it.value.get()
                    if (listener is IACListener) {
                        listener.onACSwitchStatusChanged(status, type)
                    }
                }
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