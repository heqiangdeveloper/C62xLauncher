package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.mcu.CarMcuManager
import android.content.ComponentName
import android.content.Intent
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.access.AccessManager
import com.chinatsp.settinglib.manager.adas.AdasManager
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.settinglib.manager.consumer.PanoramaCommandConsumer
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Utils
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 18:19
 * @desc   :
 * @version: 1.0
 */
class GlobalManager private constructor() : BaseManager() {

    companion object {
        val TAG: String = GlobalManager::class.java.simpleName
        val instance: GlobalManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GlobalManager()
        }
    }

    private val tabSerial: AtomicInteger by lazy {
        val isLevel3 = VcuUtils.isCareLevel(Level.LEVEL3, expect = true)
        AtomicInteger(0)
    }

    fun getTabSerial() = tabSerial.get()

    fun setTabSerial(serial: Int) = tabSerial.set(serial)

    val managers: List<BaseManager> by lazy {
        ArrayList<BaseManager>().apply {
            add(CabinManager.instance)
            add(AudioManager.instance)
            add(LampManager.instance)
            add(AccessManager.instance)
            add(AdasManager.instance)
        }
    }
    override val careSerials: Map<Origin, Set<Int>>
        get() = EnumMap(Origin::class.java)

    override fun onDispatchSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        if (Origin.CABIN == origin && CarCabinManager.ID_BDC_VEHICLE_MODE == property.propertyId) {
            val value = property.value
            if (value !is Int) {
                return true
            }
//            BDC Vehicle mode,used for 62 F06
//            0x0: Normal Mode（default） 0x1: Transport Mode  0x2: Exhibition Mode
//            0x3: Factory Mode（reserved）  0x4: Crash Mode（reserved）
//            0x5: Test Mode（reserved）  0x6: Reserved  0x7: Rerserved
            if (0x0 == value) {
            } else if (0x1 == value) {

            } else if (0x2 == value) {

            }
            return true
        }else if (CarMcuManager.ID_VENDOR_MCU_POWER_MODE == property.propertyId) {//557903874
            val propertyValue = property.value
            val on = 5
            if (propertyValue != on) {
                //系统OFF ON弹窗
                val intent = Intent("com.chinatsp.vehicle.settings.service.SystemService")
                intent.setPackage("com.chinatsp.vehicle.settings")
                BaseApp.instance.startService(intent)
            }
            return true
        }
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        val hashSet = HashSet<Int>()
        managers.forEach { manager ->
            manager.getOriginSignal(origin).let {
                if (it.isNotEmpty()) {
                    hashSet.addAll(it)
                }
            }
        }
        return hashSet
    }

    override fun doAirControlCommand(cmd: AirCmd, callback: ICmdCallback?) {
//        val modelSerial = Model.obtainEchelon(cmd.model)
//        Timber.d("doAirControlCommand modelSerial:${Utils.toFullBinary(modelSerial)}")
        if (Model.CABIN_AIR == cmd.model) {
            ACManager.instance.doAirControlCommand(cmd, callback)
        }
    }

    override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
        val modelSerial = Model.obtainEchelon(cmd.model)
        Timber.d("doOuterControlCommand modelSerial:${Utils.toFullBinary(modelSerial)}")
        if (Model.ACCESS == modelSerial) {
            AccessManager.instance.doCarControlCommand(cmd, callback)
        } else if (Model.LIGHT == modelSerial) {
            LampManager.instance.doCarControlCommand(cmd, callback)
        } else if (Model.AUDIO == modelSerial) {
            AudioManager.instance.doCarControlCommand(cmd, callback)
        } else if (Model.CABIN == modelSerial) {
            CabinManager.instance.doCarControlCommand(cmd, callback)
        } else if (Model.ADAS == modelSerial) {
            AdasManager.instance.doCarControlCommand(cmd, callback)
        } else if (Model.PANORAMA == modelSerial) {
            PanoramaCommandConsumer(this).consumerCommand(cmd, callback)
        } else if (Model.AUTO_PARK == modelSerial) {
            sendAutoParkCommand(cmd)
        }
    }

    private fun sendAutoParkCommand(cmd: CarCmd) {
        val intent = Intent()
        val packageName = "com.haibing.apaparking"
        val serviceName = "com.haibing.apaparking.service.ApaParkingService"
        intent.component = ComponentName(packageName, serviceName)
        intent.putExtra("data", cmd.slots?.json)
        BaseApp.instance.startService(intent)
        Timber.e("sendAutoParkCommand data: ${cmd.slots?.json}")
    }

//    fun onTrailerRemindChanged(onOff: Int, level: Int, dist: Int) {
//        CabinManager.instance.onTrailerRemindChanged(onOff, level, dist)
//    }

    /**
     * 一键升降窗开关  0x0: Inactive; 0x1: Open all; 0x2: Close all; 0x3: Reserved"
     */
    fun doSwitchWindow(status: Boolean): Boolean {
        val value = if (status) 0x01 else 0x02
        Timber.d("doSwitchWindow status:%s, value:%s", status, value)
        writeProperty(CarCabinManager.ID_ONE_KEY_CLICK_ALL_WINDOW_SW, value, Origin.CABIN)
        return true
    }

    fun resetSwitchWindow(): Boolean {
        val value = 0xFE
        Timber.d("resetSwitchWindow value:%s", value)
//        writeProperty(CarCabinManager.ID_FRNTLEWINPOSNSET, value, Origin.CABIN)
//        writeProperty(CarCabinManager.ID_FRNTRIWINPOSNSET, value, Origin.CABIN)
        return true
    }

}