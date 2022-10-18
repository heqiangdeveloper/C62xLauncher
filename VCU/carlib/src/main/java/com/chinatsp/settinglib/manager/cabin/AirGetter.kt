package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.sign.Origin
import kotlin.math.roundToInt

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/13 13:43
 * @desc   :
 * @version: 1.0
 */
class AirGetter(val manager: ACManager) {

    val blowerRange: IntRange = 0x1..0x9

    val tempRange: IntRange = 0x10..0x20

    private fun hvacValue(signal: Int): Int {
        return manager.readIntProperty(signal, Origin.HVAC)
    }

    private fun cabinValue(signal: Int): Int {
        return manager.readIntProperty(signal, Origin.CABIN)
    }



    /**
     * 获取空调风量显示
     * MPU向MCU发送设置按键音信息
     * 0x0到0x1E（0-30）， 音量等级：0到30
     */
    fun getBlowerRateLevel(): Int {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_DIS_BLOWER_LEVEL)
        var result = ((value * blowerRange.last).toFloat() / 0x1E).roundToInt()
        if (result > blowerRange.last) result = blowerRange.last
        if (result < blowerRange.first) result = blowerRange.first
        return result
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
//        return 0x1 == value
        return true
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
//        val value = cabinValue(CarHvacManager.ID_HAVC_AC_REST_MOD_STS)
//        return 0x1 == value
        return false
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
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_RECIRC_AIR)
        return 0x1 == value
    }

    fun isOuterLooper(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_FRESH)
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
     */
    fun isTailDefrost(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_TELLTALE_REAR_DEFROST)
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
        return 0x1 == value
    }

}