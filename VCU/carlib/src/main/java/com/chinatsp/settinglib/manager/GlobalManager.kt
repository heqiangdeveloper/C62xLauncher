package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
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

    private val panoramaConsumer: PanoramaCommandConsumer by lazy {
        PanoramaCommandConsumer(this)
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
        if (Origin.CABIN == origin) {
            if (CarCabinManager.ID_BDC_VEHICLE_MODE == property.propertyId) {
                onVehicleModeChanged(property.value)
                return true
            }
            /**无线充电状态*/
            if (CarCabinManager.ID_WCM_WORK_STATE == property.propertyId) {
                onWirelessChargingModeChanged(property.value)
                return true
            }
            /**开关机状态*/
            if (CarCabinManager.ID_POWER_MODE_BCM == property.propertyId) {
                onPowerModeChanged(property.value)
                return true
            }
            /**电源等级状态*/
            if (CarCabinManager.ID_LOUPWRMNGTSTATLVL == property.propertyId) {
                onPowerLevelChanged(property.value)
                return true
            }
            if (CarCabinManager.ID_AVM_AVM_DISP_REQ == property.propertyId) {
                val value = if (property.value is Int) property.value as Int else 0
                Applet.updateAvmDisplay(value = value)
                return true
            }
        }
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    private fun onPowerLevelChanged(voltageLevel: Any?) {
        if (voltageLevel !is Int) {
            Timber.e("onPowerLevelChanged but property value is not Int!")
            return
        }
        if (VcuUtils.isEngineRunning()) {
            Timber.d("onPowerLevelChanged break by isEngine == true")
            startDialogService(Hint.ALL_HINT, Hint.HIDE)
            return
        }

        val isPower = VcuUtils.isPower()
        if (!isPower) {
            Timber.d("onPowerLevelChanged break by power is off")
            return
        }
        /**电源管理是否有效 LoUPwrStatMngtVld  0x0*/
        val staticPower = readIntProperty(CarCabinManager.ID_LOUPWRSTATMNGTVLD, Origin.CABIN)
        if (0x0 != staticPower) {
            Timber.d("onPowerLevelChanged break by STATIC_POWER_VALID is invalid")
            return
        }
        Timber.d("onPowerLevelChanged voltageLevel:$voltageLevel")
        //点火但没有启动发动机
        /**LoUPwrStatMngtVld=0x0 且LoUPwrMngtStatLvl=0x1或0x2时*/
        if (voltageLevel == 0x1 || voltageLevel == 0x2) {
            /**弹出储电量过低*/
            startDialogService(Hint.powerSupply)
        }
    }

    /**
     * 电源模式状态改变
     * @param mode 当前的电源模式--0x0: OFF 0x1: ACC 0x2: IGN ON 0x3: CRANK
     */
    private fun onPowerModeChanged(mode: Any?) {
        if (mode !is Int) {
            Timber.e("onPowerModeChanged but property value is not Int!")
            return
        }
        /**发动机状态*/
        val isEngine = VcuUtils.isEngineRunning()
        if (isEngine) {
            Timber.d("onPowerModeChanged break by isEngine == true")
            startDialogService(Hint.ALL_HINT, Hint.HIDE)
            return
        }
        Constant.POWER_STATE = mode
        if (Constant.POWER_ON != mode) {
            Timber.d("onPowerModeChanged break by mode != POWER_ON")
            return
        }
        if (!VcuUtils.isPowerValid()) {
            Timber.d("onPowerModeChanged break by POWER_MODE is invalid")
            return
        }
        /**电源管理是否有效  0x0*/
//      【反馈】Low voltage Power Static Management valid  0x0:valid; 0x1:invalid; 0x2~0x3:Reserved
        val staticPower = readIntProperty(CarCabinManager.ID_LOUPWRSTATMNGTVLD, Origin.CABIN)
        if (0x0 != staticPower) {
            Timber.d("onPowerModeChanged break by STATIC_POWER_VALID is invalid")
            return
        }
        /**电源等级*/
//      Low voltage Power Management static Level
//      0x0:IGON level 0………… 0x5:IGON level 5; 0x8:IGOFF level 0; 0x9:IGOFF level 1; 0x6-0x7,0xA-0xF:reserved
        val powerLevel = readIntProperty(CarCabinManager.ID_LOUPWRMNGTSTATLVL, Origin.CABIN)
        Timber.d("onPowerModeChanged execute powerLevel:$powerLevel")
        //ON 发动机未打火 电源等级LV1的时候延迟15分钟弹“5分钟即将关闭”弹框，
        //电源等级LV2的时候OFF——>ON马上弹
        if (0x1 == powerLevel) {
            startDialogService(Hint.leve1)//level1延迟15分钟弹出提示
            return
        }
        if (0x2 == powerLevel) {
            startDialogService(Hint.leve2) //leve2的时候立马弹出
            return
        }
    }

    /**
     * 车辆模式状态改变
     * @param vehicleMode
     * BDC Vehicle mode,used for 62 F06
     * 0x0: Normal Mode（default） 0x1: Transport Mode  0x2: Exhibition Mode
     * 0x3: Factory Mode（reserved）  0x4: Crash Mode（reserved）
     * 0x5: Test Mode（reserved）  0x6: Reserved  0x7: Rerserved
     */
    private fun onVehicleModeChanged(vehicleMode: Any?) {
        if (vehicleMode !is Int) {
            return
        }
        when (vehicleMode) {
            0x0 -> startDialogService(Hint.default)
            /**正常模式*/
            0x1 -> startDialogService(Hint.transportMode)
            /**运输模式*/
            0x2 -> startDialogService(Hint.exhibitionMode)
            /**展车模式*/
//            0x4 -> startDialogService(HintType.exhibitionModeError)/**展车模式切换失败*/
        }
    }

    /**
     * 无线充电状态
     * 0x0: WCM处于关机状态及CDC或DA的显示
    WCM关机状态需要满足以下条件：
    1）	常电上电、触发电未上电/WCM开关处于关闭状态
    满足以上条件， WCM进入关机状态，WCM发送“WcmWSts：OX00”信号，CDC或DA在显示屏右上角的无线充电状态标识无显示！
     * 0x1: WCM处于待机状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM未检测到接收端（手机）；
    满足以上条件， WCM进入待机状态即WCM可以进行无线充电，但此时未检测到接收端（手机）.WCM发送“WcmWSts：OX01”信号，HUM在显示屏上显示此状态；
     * 0x2: WCM处于充电中状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM无故障信息；
    3）	检测到接收端（手机）。
    满足以上条件， WCM进入充电中状态.WCM发送“WcmWSts：OX02信号，则音响显示屏上显示此状态
     * 0x3: WCM处于过压状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM检测到输入电压过高（19.2V以上）。
    满足以上条件， WCM进入过压状态.WCM发送“WcmWSts：OX03信号，音响显示屏上显示此状态
    同时HUM屏幕额外通过图片的文字提示故障内容。
    HUM的警告面策略：HUM弹出的警告画面会有“无线充电异常”的文字。
     * 0x4: WCM处于欠压状态及CDC或DA的显示
    4）	WCM检测到输入电压过低（8.5V以下）。
    满足以上条件， WCM进入欠压状态.WCM发送“WcmWSts：OX04信号，HUM在显示屏上显示此状态
    HUM弹出的警告画面会有“无线充电异常”
     * 0x5: WCM处于检测到异物（FOD）状态
    WCM进入FOD状态.WCM发送“WcmWSts：OX05信号
    HUM弹出的警告画面会有“检测到金属异物，请移开异物”的文字
     * 0x6: WCM处于过流状态
    WCM进入过流状态.WCM发送“WcmWSts：OX06信号
    HUM弹出的警告画面会有“无线充电异常”的文字。
     * 0x7: WCM处于过温状态
    WCM进入过温状态.WCM发送“WcmWSts：OX07信号
    HUM弹出的警告画面会有“无线充电温度过高，请移开手机”的文字
     * 0x8: WCM处于过功率状态
     * 0x9:
     * 0xA:
     * 0xB:
     * @param wirelessChargingMode
     */
    private fun onWirelessChargingModeChanged(wirelessChargingMode: Any?) {
        if (wirelessChargingMode !is Int) {
            return
        }
        when (wirelessChargingMode) {
            /**无线充电正常*/
            0x2 -> {
                startDialogService(Hint.wirelessChargingNormal)
            }
            /**无线充电异常*/
            0x3, 0x4, 0x6 -> {
                startDialogService(Hint.wirelessChargingAbnormal)
            }
            /**检测到金属异物，请移开异物*/
            0x5 -> {
                startDialogService(Hint.wirelessChargingMetal)
            }
            /**无线充电温度过高，请移开手机*/
            0x7 -> {
                startDialogService(Hint.wirelessChargingTemperature)
            }
        }
    }

    private fun startDialogService(signal: Int, action: Int = Hint.SHOW) {
        val intent = Intent()
        intent.setPackage("com.chinatsp.vehicle.settings")
        intent.action = "com.chinatsp.vehicle.settings.service.SystemService"
        val bundle = Bundle()
        bundle.putInt(Hint.type, signal)
        bundle.putInt(Hint.action, action)
        intent.putExtras(bundle)
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
            panoramaConsumer.consumerCommand(command, callback, fromUser)
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
            return
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
            val value = readFloatProperty(CarCabinManager.ID_FUELTANK_REMAINING, Origin.CABIN)
            command.message = "您的爱车${command.slots?.name}为百分之${value}"
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