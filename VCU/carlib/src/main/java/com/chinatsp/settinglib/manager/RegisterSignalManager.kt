package com.chinatsp.settinglib.manager

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.mcu.CarMcuManager

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
                    /**空调自干燥*/
                    add(CarCabinManager.ID_ACSELFSTSDISP)
                    /**预通风功能*/
                    add(CarCabinManager.ID_ACPREVENTNDISP)
                    /**空调舒适性状态显示*/
                    add(CarCabinManager.ID_ACCMFTSTSDISP)
                    /**车辆音效-声音-响度控制*/
                    add(CarCabinManager.ID_AMP_LOUD_SW_STS)
                    /**雨天自动关窗*/
                    add(CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS)

                    /**行车自动落锁*/
                    add(CarCabinManager.ID_VSPEED_LOCKING_STATUE)
                    /**熄火自动解锁*/
                    add(CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE)
                    /**车门智能进入*/
                    add(CarCabinManager.ID_SMART_ENTRY_STS)
                }
            }

        val hvacSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    add(CarHvacManager.FAN_DIRECTION_FACE)
                }
            }

        val mcuSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    /**【反馈】返回设置音源音量信息*/
                    add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
                    add(CarMcuManager.ID_REVERSE_SIGNAL)
                    add(CarMcuManager.ID_MCU_LOST_CANID)
                    add(CarMcuManager.ID_MCU_ACC_STATE)
                    add(CarMcuManager.ID_VENDOR_MCU_POWER_MODE)
                    add(CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE)
                    add(CarMcuManager.ID_NIGHT_MODE)
                    add(CarMcuManager.ID_VENDOR_PHOTO_REQ)
                }
            }

    }

}