package com.chinatsp.settinglib.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.access.AccessManager
import com.chinatsp.settinglib.manager.adas.AdasManager
import com.chinatsp.settinglib.manager.cabin.ACManager
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.settinglib.manager.consumer.PanoramaCommandConsumer
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.settinglib.optios.SwitchNode
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
class GlobalManager private constructor() : BaseManager(), ISwitchManager {

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

    private val nodeValid33F: SwitchState by lazy { SwitchState(true) }
    private val nodeValid362: SwitchState by lazy { SwitchState(true) }
    private val nodeValid332: SwitchState by lazy { SwitchState(true) }
    private val nodeValid591: SwitchState by lazy { SwitchState(true) }
    private val nodeValid581: SwitchState by lazy { SwitchState(true) }

    private val nodeValid582: SwitchState by lazy { SwitchState(true) }
    private val nodeValid598: SwitchState by lazy { SwitchState(true) }
    private val nodeValid580: SwitchState by lazy { SwitchState(true) }
    private val nodeValid514: SwitchState by lazy { SwitchState(true) }
    private val nodeValid5D4: SwitchState by lazy { SwitchState(true) }

    private val nodeValid513: SwitchState by lazy { SwitchState(true) }
    private val nodeValid58F: SwitchState by lazy { SwitchState(true) }
    private val nodeValid523: SwitchState by lazy { SwitchState(true) }
    private val nodeValid5B3: SwitchState by lazy { SwitchState(true) }
    private val nodeValid65A: SwitchState by lazy { SwitchState(true) }

    private val nodeValid621: SwitchState by lazy { SwitchState(true) }
    private val nodeValid645: SwitchState by lazy { SwitchState(true) }
    private val nodeValid654: SwitchState by lazy { SwitchState(true) }
    private val nodeValid66F: SwitchState by lazy { SwitchState(true) }
    private val nodeValid2E5: SwitchState by lazy { SwitchState(true) }

    private val nodeValidNFC: SwitchState by lazy { SwitchState(true) }

    private val nodeValidList: Array<SwitchNode> by lazy {
        arrayOf(
            SwitchNode.NODE_VALID_33F, SwitchNode.NODE_VALID_362, SwitchNode.NODE_VALID_332, SwitchNode.NODE_VALID_591, SwitchNode.NODE_VALID_581,
            SwitchNode.NODE_VALID_582, SwitchNode.NODE_VALID_598, SwitchNode.NODE_VALID_580, SwitchNode.NODE_VALID_514, SwitchNode.NODE_VALID_5D4,
            SwitchNode.NODE_VALID_513, SwitchNode.NODE_VALID_58F, SwitchNode.NODE_VALID_523, SwitchNode.NODE_VALID_5B3, SwitchNode.NODE_VALID_65A,
            SwitchNode.NODE_VALID_621, SwitchNode.NODE_VALID_645, SwitchNode.NODE_VALID_654, SwitchNode.NODE_VALID_66F, SwitchNode.NODE_VALID_2E5,
            SwitchNode.NODE_VALID_NFC
        )
    }

    private val nodeValidMap: Map<SwitchNode, SwitchState> by lazy {
        val map = mutableMapOf<SwitchNode, SwitchState>()
        map[SwitchNode.NODE_VALID_33F] = nodeValid33F
        map[SwitchNode.NODE_VALID_362] = nodeValid362
        map[SwitchNode.NODE_VALID_332] = nodeValid332
        map[SwitchNode.NODE_VALID_591] = nodeValid591
        map[SwitchNode.NODE_VALID_581] = nodeValid581

        map[SwitchNode.NODE_VALID_582] = nodeValid582
        map[SwitchNode.NODE_VALID_598] = nodeValid598
        map[SwitchNode.NODE_VALID_580] = nodeValid580
        map[SwitchNode.NODE_VALID_514] = nodeValid514
        map[SwitchNode.NODE_VALID_5D4] = nodeValid5D4

        map[SwitchNode.NODE_VALID_513] = nodeValid513
        map[SwitchNode.NODE_VALID_58F] = nodeValid58F
        map[SwitchNode.NODE_VALID_523] = nodeValid523
        map[SwitchNode.NODE_VALID_5B3] = nodeValid5B3
        map[SwitchNode.NODE_VALID_65A] = nodeValid65A

        map[SwitchNode.NODE_VALID_621] = nodeValid621
        map[SwitchNode.NODE_VALID_645] = nodeValid645
        map[SwitchNode.NODE_VALID_654] = nodeValid654
        map[SwitchNode.NODE_VALID_66F] = nodeValid66F
        map[SwitchNode.NODE_VALID_2E5] = nodeValid2E5

        map[SwitchNode.NODE_VALID_NFC] = nodeValidNFC
        map
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
            if (CarCabinManager.ID_EPBVEHMODSTS == property.propertyId) {
                emblemSTS(property.value)
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
        if (Origin.MCU == origin) {
            if (CarMcuManager.ID_MCU_LOST_CANID == property.propertyId) {
                doCanNodeChanged(property.value)
                return true
            }
        }
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    private fun doCanNodeChanged(any: Any?) {
        /**
         * CAN节点丢失 0x00-正常 0x01-丢失
         * int32Values[0]: 空调
         * int32Values[1]: 仪表
         * int32Values[2]: IBCM
         * int32Values[3]: PEPS
         * int32Values[4]: 空调面板
         * int32Values[5]: 倒车雷达
         * int32Values[6]: AVM
         * int32Values[7]: APA
         * int32Values[8]: 人脸识别
         * int32Values[9]: DSM
         * int32Values[10]: GW
         * int32Values[11]: 尾门
         * int32Values[12]: VCU
         * int32Values[13]: BSM
         * int32Values[14]: EMS
         *
         * v0.69 新增
         * int32Values[15]: 车辆设置33F
         * int32Values[16]: 车辆设置362
         * int32Values[17]: 车辆设置332
         * int32Values[18]: 车辆设置591
         * int32Values[19]: 车辆设置581
         * int32Values[20]: 车辆设置盲区
         * int32Values[21]: 车辆设置仪表亮度
         * int32Values[22]: 车辆设置580
         * int32Values[23]: 车辆设置514
         * int32Values[24]: 车辆设置5D4
         * int32Values[25]: 车辆设置空调舒适性
         * int32Values[26]: 车辆设置58F
         * int32Values[27]: 车辆设置523
         * int32Values[28]: 车辆设置5B3
         * int32Values[29]: 车辆设置65A
         * int32Values[30]: 车辆设置621
         * int32Values[31]: 车辆设置645
         * int32Values[32]: 车辆设置654
         * int32Values[33]: 车辆设置66F
         * int32Values[34]: 车辆设置电源管家
         * int32Values[35]: 车辆设置NFC
         **/

        val values = any as? Array<*>
        if (null != values && values.size >= 35) {
            values.filterIndexed { index1, _ -> index1 >= 15 }
                .forEachIndexed { index, value ->
                    val node = nodeValidList[index]
                    val state = nodeValidMap[node]
                    if (null != state) {
                        val status = node.isOn(value as Int)
                        if (state.get() xor status) {
                            Timber.d("luohong index:$index, value:$value, node:$node, status:$status, state:$state")
                            state.set(status)
                            doSwitchChanged(node, state)
                        }
                    }
                }
        }
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

    private fun emblemSTS(vehicleMode: Any?) {
        if (vehicleMode !is Int) {
            return
        }
        val staticPower = readIntProperty(CarCabinManager.ID_BDC_VEHICLE_MODE, Origin.CABIN)
        if (vehicleMode == 0x0 && staticPower == 0x2) {
            /**展厅模式切换失败
             * 收到BDC反馈的车辆模式状态信号为Exhibition Mode且收到ESP反馈的车辆模式状态信号为Normal mode
             * */
            VcuUtils.startDialogService(Hint.exhibitionModeError)
        } else if (vehicleMode == 0x1 && staticPower == 0x2) {
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

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
       return nodeValidMap[node]
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return false
    }

}