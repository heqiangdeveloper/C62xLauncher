package com.chinatsp.settinglib.manager

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
                    this.remove(-1)
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
                }
            }

        val mcuSignal: Set<Int>
            get() {
                return HashSet<Int>().apply {
                    /**【反馈】返回设置音源音量信息*/
//                    add(CarMcuManager.ID_AUDIO_VOL_SETTING_INFO)
                    add(CarMcuManager.ID_REVERSE_SIGNAL)
                    add(CarMcuManager.ID_MCU_LOST_CANID)
                    add(CarMcuManager.ID_MCU_ACC_STATE)
                    add(CarMcuManager.ID_VENDOR_MCU_POWER_MODE)
                    add(CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE)
                    add(CarMcuManager.ID_NIGHT_MODE)
                    add(CarMcuManager.ID_VENDOR_PHOTO_REQ)
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
                }
            }

    }

}