package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
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
import com.chinatsp.vehicle.controller.annotation.IAct
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
            if (0x1 == value) {
                /**运输模式*/
                startDialogService("transportMode")
            } else if (0x2 == value) {
                /**展车模式*/
                startDialogService("exhibitionMode")
            }/*else if(0x4 == value){
                */
            /**展车模式切换失败*//*
                startDialogService("exhibitionModeError")
            }*/

            return true
        }
        /**开关机状态*/
        if (origin == Origin.CABIN && CarCabinManager.ID_POWER_MODE_BCM == property.propertyId) {
            val value = property.value as Int

            /**电源管理是否有效  0x0*/
            val loUPwrStatMngtVldValue =
                readIntProperty(CarCabinManager.ID_LOUPWRSTATMNGTVLD, Origin.CABIN)

            /**电源等级*/
            var loUPwrMngtStatlvl =
                readIntProperty(CarCabinManager.ID_LOUPWRMNGTSTATLVL, Origin.CABIN)

            /**发动机状态*/
            //0x0:Engine NOT running 0x1:Cranking 0x2:Engine running 0x3:Fault
            //val engineRunning = readIntProperty(CarCabinManager.ID_ENGINE_RUNNING, Origin.CABIN)
            val status = VcuUtils.isEngineRunning()
            Timber.d("CABIN status:$status")
            Timber.d("CABIN loUPwrStatMngtVldValue:$loUPwrStatMngtVldValue")
            Timber.d("CABIN value:$value")
            Timber.d("CABIN loUPwrMngtStatlvl:$loUPwrMngtStatlvl")
            if (value == 0x0) {
                //0ff 电源等级LV0时，延迟五分钟弹《五分钟即将关闭》
                if (loUPwrMngtStatlvl == 0x0) {
                    startDialogService("ON")
                }
            } else if (value == 0x2) {
                //ON 发动机未打火 电源等级LV1的时候延迟15分钟弹“5分钟即将关闭”弹框，
                // 电源等级LV2的时候OFF——>ON马上弹
                if (loUPwrStatMngtVldValue == 0x0 && !status) {
                    if (loUPwrMngtStatlvl == 0x1) {
                        //level1延迟15分钟弹出提示
                        startDialogService("leve1")
                    } else if (loUPwrMngtStatlvl == 0x2) {
                        //leve2的时候立马弹出
                        startDialogService("leve2")
                    }

                }
            }

            return true
        }

        /**电源等级状态*/
        if (origin == Origin.CABIN && CarCabinManager.ID_LOUPWRMNGTSTATLVL == property.propertyId) {
            /**电源管理是否有效  0x0*/
            val loUPwrStatMngtVldValue =
                readIntProperty(CarCabinManager.ID_LOUPWRSTATMNGTVLD, Origin.CABIN)

            /**发动机状态*/
            //0x0:Engine NOT running 0x1:Cranking 0x2:Engine running 0x3:Fault
            val engineRunning =
                readIntProperty(CarCabinManager.ID_ENGINE_RUNNING, Origin.CABIN)

            /**开关机状态*/
            val powerValue = readIntProperty(CarCabinManager.ID_POWER_MODE_BCM, Origin.CABIN)
            val value = property.value
            Timber.d("CABIN powerValue:$powerValue")
            Timber.d("CABIN loUPwrStatMngtVldValue:$loUPwrStatMngtVldValue")
            Timber.d("CABIN value:$value")
            val status = VcuUtils.isPower() && !VcuUtils.isEngineRunning()
            Timber.d("CABIN powerValue:$powerValue. status:$status")

//            if(powerValue == 0x2 && engineRunning == 0x0){
            if (status) {
                //点火但没有启动发动机
                /**LoUPwrStatMngtVld=0x0 且LoUPwrMngtStatLvl=0x1或0x2时*/
                if (loUPwrStatMngtVldValue == 0x0 && value == 0x1 || value == 0x2) {
                    /**弹出储电量过低*/
                    startDialogService("powerSupply")
                    return true
                }
            }

        }
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    private fun startDialogService(type: String) {
        val intent = Intent("com.chinatsp.vehicle.settings.service.SystemService")
        intent.setPackage("com.chinatsp.vehicle.settings")
        val bundleSimple = Bundle()
        bundleSimple.putString("type", type)
        intent.putExtras(bundleSimple)
        BaseApp.instance.startService(intent)
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

    override fun doAirControlCommand(command: AirCmd, callback: ICmdCallback?, fromUser: Boolean) {
        if (Model.CABIN_AIR == command.model) {
            ACManager.instance.doAirControlCommand(command, callback)
        }
    }

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val modelSerial = Model.obtainEchelon(command.model)
        Timber.d("doOuterControlCommand modelSerial:${Utils.toFullBinary(modelSerial)}")
        if (Model.ACCESS == modelSerial) {
            AccessManager.instance.doCarControlCommand(command, callback, fromUser)
        } else if (Model.LIGHT == modelSerial) {
            LampManager.instance.doCarControlCommand(command, callback, fromUser)
        } else if (Model.AUDIO == modelSerial) {
//            AudioManager.instance.doCarControlCommand(command, callback, fromUser)
        } else if (Model.CABIN == modelSerial) {
            CabinManager.instance.doCarControlCommand(command, callback, fromUser)
        } else if (Model.ADAS == modelSerial) {
//            AdasManager.instance.doCarControlCommand(command, callback, fromUser)
        } else if (Model.PANORAMA == modelSerial) {
            PanoramaCommandConsumer(this).consumerCommand(command, callback, fromUser)
        } else if (Model.AUTO_PARK == modelSerial) {
            sendAutoParkCommand(command)
        } else if (Model.GLOBAL == modelSerial) {
            doConsumerCommand(command, callback, fromUser)
        }
    }

    private fun doConsumerCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        if (IAct.ENDURANCE_MILEAGE == command.act) {
            val value = readIntProperty(CarCabinManager.ID_ENDURANCE_MILEAGE, Origin.CABIN)
            val meterValue = value * 1000
            command.message = "您的爱车${command.slots?.name}为${meterValue}米"
            callback?.onCmdHandleResult(command)
            return
        }
        if (IAct.ENDURANCE_MILEAGE_KM == command.act) {
            val value = readIntProperty(CarCabinManager.ID_ENDURANCE_MILEAGE, Origin.CABIN)
            command.message = "您的爱车${command.slots?.name}为${value}公里"
            callback?.onCmdHandleResult(command)

        }
        if (IAct.MAINTAIN_MILEAGE == command.act) {
            val value = readIntProperty(CarCabinManager.ID_REMAIN_MAINTAIN_MILEAGE, Origin.CABIN)
            command.message = "您的爱车${command.slots?.name}为${value}千米"
            callback?.onCmdHandleResult(command)
            return
        }
        if (IAct.AVERAGE_FUEL_CONSUMPTION == command.act) {
            //本次行程平均油耗
            val value = readFloatProperty(CarCabinManager.ID_IP_AFE_AFTER_IGN_ON, Origin.CABIN)
            //长期行程平均油耗
            val value2 =
                readFloatProperty(CarCabinManager.ID_IP_ALLAVGFUELCONSUMPTION, Origin.CABIN)
            command.message = "您的爱车本次行程平均油耗为每百公里${value}升，长期平均油耗为每百公里${value2}升"
            callback?.onCmdHandleResult(command)
            return
        }
        if (IAct.INSTANTANEOUS_FUEL_CONSUMPTION == command.act) {
            val value = readFloatProperty(CarCabinManager.ID_IP_REALFUELCONSUMPTION, Origin.CABIN)
            command.message = "您的爱车${command.slots?.name}为每百公里${value}升"
            callback?.onCmdHandleResult(command)
            return
        }
        if (IAct.TIRE_PRESSURE == command.act) {
            //val value = readFloatProperty(CarCabinManager.ID_IP_REALFUELCONSUMPTION, Origin.CABIN)
            //北汽还未提供胎压是否正常信号
            command.message = "您的爱车${command.slots?.name}正常"
            callback?.onCmdHandleResult(command)
            return
        }
        if (IAct.REMAINING == command.act) {
            val value = readFloatProperty(CarCabinManager.ID_IP_FUELLEFTOVER, Origin.CABIN)
            command.message = "您的爱车${command.slots?.name}为${value}升"
            callback?.onCmdHandleResult(command)
            return
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