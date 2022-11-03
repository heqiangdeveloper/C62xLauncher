package com.chinatsp.settinglib.manager

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.cabin.CarCabinManager.ID_LOUPWRMNGTSTATLVL
import android.car.hardware.mcu.CarMcuManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 18:19
 * @desc   :
 * @version: 1.0
 */
class RegisterSignalManager private constructor() {

    companion object {

        val TAG: String = RegisterSignalManager::class.java.simpleName

        val instance: RegisterSignalManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RegisterSignalManager()
        }

        val cabinSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    //***********车门信号 start***************
                    add(CarCabinManager.ID_HOOD_LID_OPEN)
                    add(CarCabinManager.ID_TRUNK_LID_OPEN)

                    add(CarCabinManager.ID_DR_DOOR_OPEN)
                    add(CarCabinManager.ID_PA_DOOR_OPEN)
                    add(CarCabinManager.ID_REAR_LEFT_DOOR_OPEN)
                    add(CarCabinManager.ID_REAR_RIGHT_DOOR_OPEN)
                    //***********车门信号 end***************

                    add(CarCabinManager.ID_BDC_VEHICLE_MODE)
                    /**注册电源管理等级指令*/
                    add(ID_LOUPWRMNGTSTATLVL)
                    SwitchNode.values()
                        .filter { it.get.origin == Origin.CABIN && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    RadioNode.values()
                        .filter { it.get.origin == Origin.CABIN && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    Progress.values()
                        .filter { it.get.origin == Origin.CABIN && it.get.signal != Constant.INVALID }
                        .forEach { add(it.get.signal) }
                    //特殊增加 后视镜调节保存反馈信号
                    add(CarCabinManager.ID_R_MIRROR_MEMORY_STS)
                    add(CarCabinManager.ID_SWS_KEY_USER_DEFINED)

                    this.remove(Constant.INVALID)
                }
            }

        val hvacSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    SwitchNode.values()
                        .filter { it.get.origin == Origin.HVAC && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    RadioNode.values()
                        .filter { it.get.origin == Origin.HVAC && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    Progress.values()
                        .filter { it.get.origin == Origin.HVAC && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    remove(Constant.INVALID)
                }
            }

        val mcuSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    /**【反馈】返回设置音源音量信息*/
//                    add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
//                    add(CarMcuManager.ID_REVERSE_SIGNAL)
//                    add(CarMcuManager.ID_MCU_LOST_CANID)
//                    add(CarMcuManager.ID_MCU_ACC_STATE)
                      /**注册OFF ON车机指令*/
                      add(CarMcuManager.ID_VENDOR_MCU_POWER_MODE)
//                    add(CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE)
//                    add(CarMcuManager.ID_NIGHT_MODE)
//                    add(CarMcuManager.ID_VENDOR_PHOTO_REQ)
                    SwitchNode.values()
                        .filter { it.get.origin == Origin.MCU && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    RadioNode.values()
                        .filter { it.get.origin == Origin.MCU && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    Progress.values()
                        .filter { it.get.origin == Origin.MCU && it.get.signal != Constant.INVALID }
                        .forEach {
                            add(it.get.signal)
                        }
                    remove(Constant.INVALID)
                }
            }
    }

}