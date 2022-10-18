package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.IOrien
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.utils.Utils

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/13 13:43
 * @desc   :
 * @version: 1.0
 */
class AirSetter(val manager: ACManager, private val getter: AirGetter) {

    private fun hvacSignal(signal: Int, value: Int) {
        manager.writeProperty(signal, value, Origin.HVAC)
    }

    /**
     * 开关空调
     * @param expect 期望状态 true:表示开; false:表示关
     */
    fun doSwitchConditioner(expect: Boolean): Boolean {
        val actual = getter.isConditioner()
        val result = actual xor expect
        if (result) {
//            空调系统状态按钮if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON(not used); 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_ON_OFF, value)
        }
        return result
    }

    /**
     * 开关 AC 模式
     * @param expect 期望状态 true:表示开; false:表示关
     */
    fun doSwitchCompressor(expect: Boolean): Boolean {
        val actual = getter.isCompressor()
        val result = actual xor expect
        if (result) {
//          压缩机if not set ,the value of signal is 0x0(inactive)
//          0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_AC, value)
        }
        return result
    }

    /**
     * 开关 AC 模式
     * @param expect 期望状态 true:表示开; false:表示关
     */
    fun doSwitchHeater(expect: Boolean): Boolean {
        val actual = getter.isHeater()
        val result = actual xor expect
        if (result) {
//          压缩机if not set ,the value of signal is 0x0(inactive)
//          0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_AC, value)
        }
        return result
    }

    /**
     * 开关 AC 模式
     * @param expect 期望状态 true:表示开; false:表示关
     */
    fun doSwitchAirClean(expect: Boolean): Boolean {
        val actual = getter.isAirClean()
        val result = actual xor expect
        if (result) {
//            空气净化功能 if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_AC_AIR_CLEAN, value)
        }
        return result
    }

    fun doUpdateTemperature(value: Int, @IPart part: Int) {
        if (IPart.LEFT_FRONT == part) {
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_TEMP_LEFT, value)
        }
        if (IPart.RIGHT_FRONT == part) {
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_TEMP_RIGHT, value)
        }
    }

    fun doUpdateBlowerRate(expect: Int, @IPart part: Int): Boolean {
//        风量 if not set ,the value of signal is 0x0(inactive)
//        0x0: Inactive
//        0x1: Level 0 …… 0x9: Level 8
//        0xA: Reserved …… 0xE: Reserved
//        0xF: Error
        if (expect !in getter.blowerRange) return false
        val actual = getter.getBlowerRateLevel()
        val result = actual != expect
        if (result) {
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_BLOWER, expect)
        }
        return result
    }

    fun doSwitchInnerLooper(expect: Boolean): Boolean {
        val actual = getter.isInnerLooper()
        val result = actual xor expect
        if (result) {
//            内循环if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_RECIRCAIR, value)
        }
        return result
    }

    fun doSwitchOuterLooper(expect: Boolean): Boolean {
        val actual = getter.isOuterLooper()
        val result = actual xor expect
        if (result) {
//            外循环if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_FRESH, value)
        }
        return result
    }

    fun doSwitchAutoLooper(expect: Boolean): Boolean {
        val actual = getter.isAutoLooper()
        val result = actual xor expect
        if (result) {
//            解锁主动换气使能开关
//            0x0: Inactive; 0x1: Enabled; 0x2: Disabled; 0x3: Reserved
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_UNLOCK_BREATHABLE_ENABLE, value)
        }
        return result
    }

    fun doSwitchHeadDefrost(expect: Boolean): Boolean {
        val actual = getter.isHeadDefrost()
        val result = actual xor expect
        if (result) {
//            前除霜if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON; 0x2: OFF;0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_DEFROST, value)
        }
        return result
    }

    fun doSwitchTailDefrost(expect: Boolean): Boolean {
        val actual = getter.isTailDefrost()
        val result = actual xor expect
        if (result) {
//            前除霜if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive; 0x1: ON; 0x2: OFF;0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(-1, value)
        }
        return result
    }


    fun doSwitchDoubleMode(expect: Boolean): Boolean {
        val actual = getter.isDoubleMode()
        val result = actual xor expect
        if (result) {
//            切换单双区，C40、C53预留。if not set ,the value of signal is 0x0(inactive)
//            0x0: Inactive;0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (expect) 0x1 else 0x2
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_DUAL, value)
        }
        return result
    }


    fun doUpdateAirFlowing(@IOrien orien: Int, @IPart part: Int): String {
//        模式 if not set ,the value of signal is 0x0(inactive)
//        0x0: Inactive
//        0x1: Face
//        0x2: Face+Foot
//        0x3: Foot
//        0x4: Foot+Defrost
//        0x5: Defrost（Reserved）
//        0x6: Reserved
//        0x7: Error
        var mask =  IOrien.FACE
        val isFace = mask == (mask and orien)
        mask =  IOrien.FOOT
        val isFoot = mask == (mask and orien)
        mask =  IOrien.MIDDLE //吹中间表示除霜
        val isMiddle = mask == (mask and orien)
        LogManager.d("luohong", "-------${Utils.toFullBinary(orien)}")
        var value = Constant.INVALID
        var result = "小北还不会这个操作"
        do {
            if (isFace && isFoot) {
                value = 2
                result = "吹脸吹脚已打开"
                break
            }
            if (isFoot && isMiddle) {
                value = 4
                result = "吹脚除霜已打开"
                break
            }
            if (isFoot) {
                value = 3
                result = "吹脚模式已打开"
                break
            }
            if (isFace) {
                value = 1
                result = "吹脸模式已打开"
                break
            }
        } while (false)
        if (Constant.INVALID != value) {
            hvacSignal(CarHvacManager.ID_HVAC_AVN_KEY_AIR_DISTRIBUTION, value)
        }
        return result
    }


}