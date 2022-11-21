package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/13 13:43
 * @desc   :
 * @version: 1.0
 */
class AirGetter(val manager: ACManager) {

    val blowerRange: IntRange = 0x1..0x8

    val tempRange: IntRange = 0x10..0x20

    private fun hvacValue(signal: Int): Int {
        return manager.readIntProperty(signal, Origin.HVAC)
    }

    private fun cabinValue(signal: Int): Int {
        return manager.readIntProperty(signal, Origin.CABIN)
    }

    /**
     * 获取空调风量显示
     * 风量显示
    0x0: Level 0
    0x1: Level 1
    0x2: Level 2
    0x3: Level 3
    0x4: Level 4
    0x5: Level 5
    0x6: Level 6
    0x7: Level 7
    0x8: Level 8
    0x9: Reserved
    0xA: Reserved
    0xB: Reserved
    0xC: Reserved
    0xD: Reserved
    0xE: Reserved
    0xF: Error
     */
    fun getBlowerLevel(): Int {
        var value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_BLOWER_LEVEL)
        if (value > blowerRange.last) value = blowerRange.last
        if (value < blowerRange.first) value = blowerRange.first
        return value
    }

    /**
     * 获取空调左侧温度显示 （暂当成空调温度）
     * 左温度显示。依据功能规范，低于17℃时显示Low，高于31℃时显示High。
     * 0x00 : No Temperature Display
     * 0x01~0xF : Reserved
     * 0x10:Low
     * 0x11 :17℃ ………… 0x1F : 31℃
     * 0x20 : High
     */
    fun getDriverTemperature(): Int {
        var value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_LEFT_TEMP)
        if (value < tempRange.first) value = tempRange.first
        if (value > tempRange.last) value = tempRange.last
        return value
    }

    fun getCopilotTemperature(): Int {
        var value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_RIGHT_TEMP)
        if (value < tempRange.first) value = tempRange.first
        if (value > tempRange.last) value = tempRange.last
        return value
    }


    /**
     * 获取空调是否开启
     * 空调工作状态指示（仅适用于C40D）0x0: OFF; 0x1: On
     */
    fun isConditioner(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_SYS_ON_OFF_STATE)
        return 0x1 == value
    }

    /**
     * 获取空调压缩机开关状态
     * AC State Indicator 0x0: LED OFF; 0x1: LED ON; 0x2: Reserved; 0x3: Error
     */
    fun isCompressor(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_AC)
        return 0x1 == value
    }


    /**
     * 获取空调压缩机开关状态
     * AC State Indicator 0x0: LED OFF; 0x1: LED ON; 0x2: Reserved; 0x3: Error
     */
    fun isHeater(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_AC)
        return 0x1 == value
    }


    /**
     * 休息模式状态
     * 0x0: Reserved    0x1: On   0x2: Off   0x3: Invalid
     */
    fun isRestModeStatus(): Boolean {
        val value = cabinValue(CarCabinManager.ID_HAVC_AC_REST_MOD_STS)
        return 0x1 == value
    }

    /**
     * 获取车辆发动机状态
     */
    fun isCarEngine(): Boolean {
        return true
    }

    /**
     * 获取空气净化器工作状态
     */
    fun isAirClean(): Boolean {
//        空气净化状态指示
//        0x0:Air Clean not Working
//        0x1: Air Clean Working
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_AIR_CLEAN_STATUS)
        return 0x1 == value
    }

    fun isInnerLooper(): Boolean {
//        内外循环显示
//        0x0: Not display
//        0x1: Display Fresh Air
//        0x2: Display Recirculation
//        0x3: Error
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_FRESH_RECIR)
        return 0x2 == value
    }

    fun isOuterLooper(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_FRESH_RECIR)
        return 0x1 == value
    }

    fun isAutoLooper(): Boolean {
//        Auto State Indicator
//        0x0: LED OFF
//        0x1: LED ON
//        0x2: Reserved
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_AUTO)
        return 0x1 == value
    }



    /**
     * 前除霜状态
     */
    fun isHeadDefrost(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_DEFROST)
        return 0x1 == value
    }
    /**
     * 后除霜状态
     * 0x0:Off; 0x1:On
     */
    fun isTailDefrost(): Boolean {
        val value = cabinValue(CarCabinManager.ID_REAR_DEMIST_ON)
        return 0x1 == value
    }

    /**
     * 后除霜状态
     */
    fun isDoubleMode(): Boolean {
//        DUAL State Indicator
//        0x0: LED OFF
//        0x1: LED ON
//        0x2: Reserved
//        0x3: Error
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_DUAL)
        return 0x0 == value
    }

    fun isAuto(): Boolean {
//        Auto 显示图标
//        0x0: Not display
//        0x1: Display
//        0x2: Reserved
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_AUTO)
        return 0x1 == value
    }

}