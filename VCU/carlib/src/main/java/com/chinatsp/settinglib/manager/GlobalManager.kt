package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Applet
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
//            /**无线充电状态*/
//            if (CarCabinManager.ID_WCM_WORK_STATE == property.propertyId) {
//                onWirelessChargingModeChanged(property.value)
//                return true
//            }
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
        val isPower = VcuUtils.isPower()
        if (!isPower) {
            Timber.d("onPowerLevelChanged break by power is off")
            return
        }
        if (VcuUtils.isEngineRunning()) {
            Timber.d("onPowerLevelChanged break by isEngine == true")
            VcuUtils.startDialogService(Hint.ALL_HINT, Hint.HIDE)
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
//        if (voltageLevel == 0x1 || voltageLevel == 0x2) {
//            /**弹出储电量过低*/
//            VcuUtils.startDialogService(Hint.powerSupply)
//        }

        if (0x1 == voltageLevel) {
            VcuUtils.startDialogService(Hint.leve1)//level1延迟15分钟弹出提示
            return
        }
        if (0x2 == voltageLevel) {
            VcuUtils.startDialogService(Hint.leve2) //leve2的时候立马弹出
            return
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
        if (!VcuUtils.isPowerValid()) {
            Timber.d("onPowerModeChanged break by POWER_MODE is invalid")
            return
        }
        /**发动机状态*/
        if (VcuUtils.isEngineRunning()) {
            Timber.d("onPowerModeChanged break by isEngine == true")
            VcuUtils.startDialogService(Hint.ALL_HINT, Hint.HIDE)
            return
        }
        Constant.POWER_STATE = mode
        if (Constant.POWER_ON != mode) {
            Timber.d("onPowerModeChanged break by mode != POWER_ON")
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
            VcuUtils.startDialogService(Hint.leve1)//level1延迟15分钟弹出提示
            return
        }
        if (0x2 == powerLevel) {
            VcuUtils.startDialogService(Hint.leve2) //leve2的时候立马弹出
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
            /**正常模式*/
            0x0 -> VcuUtils.startDialogService(Hint.default)
            /**运输模式*/
            0x1 -> VcuUtils.startDialogService(Hint.transportMode)
            /**展厅模式*/
            0x2 -> exhibitionHallMode()
            //VcuUtils.startDialogService(Hint.exhibitionMode)
            /**展车模式切换失败*/
            //0x4 -> switchFailed()
        }
    }

    private fun exhibitionHallMode() {
        /**
         * vehicle mode status feedback.车辆模式状态反馈
        0x0: Vehicle Normal mode
        0x1: Exhibition Mode
        0x2~0x3: Reserved
         */
        val staticPower = readIntProperty(CarCabinManager.ID_EPBVEHMODSTS, Origin.CABIN)
        if (staticPower == 0x0) {
            /**展厅模式切换失败
             * 收到BDC反馈的车辆模式状态信号为Exhibition Mode且收到ESP反馈的车辆模式状态信号为Normal mode
             * */
            VcuUtils.startDialogService(Hint.exhibitionModeError)
        } else if (staticPower == 0x1) {
            /**展厅模式切换成功
             * 收到BDC和ESP反馈的车辆模式状态信号均为Exhibition Mode
             * */
            VcuUtils.startDialogService(Hint.exhibitionMode)
        }
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
        }
//        else if (Model.AUTO_PARK == modelSerial) {
//            sendAutoParkCommand(command)
//        }
        else if (Model.GLOBAL == modelSerial) {
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