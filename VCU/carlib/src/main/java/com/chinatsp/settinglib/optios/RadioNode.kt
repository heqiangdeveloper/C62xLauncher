package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.bean.RNorm
import com.chinatsp.settinglib.sign.Origin

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:19
 * @desc   :
 * @version: 1.0
 */
enum class RadioNode(val get: RNorm, val set: RNorm, val default:Int, val area: Area = Area.GLOBAL) {

    AC_COMFORT(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    ),

    /**
     * 车门与车窗--车门--行车自动落锁
     * set -> 0x1: off 0x2: 5km/h    0x3: 10km/h    0x4: 15km/h   0x5: 20km/h
     * get -> 0x0: Inactive 0x1: off 0x2: 5km/h 0x3: 10km/h  0x4: 15km/h 0x5: 20km/h(default)
     */
    ACCESS_DOOR_DRIVE_LOCK(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5), signal = CarCabinManager.ID_VSPEED_LOCKING_STATUE),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5), signal = CarCabinManager.ID_VSPEED_LOCK),
        default = 0x5
    ),
    /**
     * 车门与车窗--车门--熄火自动解锁
     * set -> 0x1: unlock FL door 0x2: unlock all doors(default)   0x3: FunctionDisable
     * get -> 0x0: Inactive 0x1: unlock FL door 0x2: unlock all doors(default) 0x3: FunctionDisable
     */
    ACCESS_DOOR_FLAMEOUT_UNLOCK(
        get = RNorm(values = intArrayOf(0x3, 0x1, 0x2), signal = CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE),
        set = RNorm(values = intArrayOf(0x3, 0x1, 0x2), signal = CarCabinManager.ID_CUT_OFF_UNLOCK_DOORS),
        default = 0x2
    ),

    /**
     * 车门与车窗--电动尾门--电动尾门智能进入
     */
    ACCESS_STERN_SMART_ENTER(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    ),

    /**
     * 驾驶辅助-智能巡航-前车驶离提示
     * set ->
        object distingguish and disappear switch,if not set'OBJ_DETECTION',
        the value of signal is 0x0(inactive)[0x1,0,0x0,0x5]
        0x0: Inactive
        0x1: Detect warning
        0x2: Disappare warning
        0x3: Detect and disappear warning(default)
        0x4: Warning off
        0x5~0x7:Reserved
     */
    ADAS_LIMBER_LEAVE(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_OBJ_DETECTION_SWT),
        default = 0x1
    ),

    /**
     * 驾驶辅助-车道辅助-车道辅助系统
     * Operation mode of LDW/RDP/LKS. The default value is 0x1 LDW in C53F,
     * 0x3 LKS in C62X. 0x0:Initial 0x1:LDW 0x2:RDP 0x3:LKS
     */
    ADAS_LANE_ASSIST_MODE(
        get = RNorm(values = intArrayOf(0x3, 0x1, 0x2), signal = CarCabinManager.ID_LANE_ASSIT_TYPE),
        set = RNorm(values = intArrayOf(0x3, 0x1, 0x2), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    ),
    /**
     * 驾驶辅助-车道辅助-报警方式
     */
    ADAS_LDW_STYLE(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    ),
    /**
     * 驾驶辅助-车道辅助-灵敏度
     * get ->
        LKS sensitivity车道保持的灵敏度 0x0:lowSensitivity 0x1:highSensitivity 0x2: Initial 0x3:reserved
     * set ->
        LDW/LKS sensitivity switch,if not set 'LDW_LKS_SENSITIVITY_SWITCH ',the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
        0x0: Inacitve
        0x1: Low Sensitivity
        0x2: High Sensitivity(default)
        0x3: Reserved
     */
    ADAS_LDW_SENSITIVITY(
        get = RNorm(values = intArrayOf(0x1, 0x0), signal = CarCabinManager.ID_LKS_SENSITIVITY),
        set = RNorm(values = intArrayOf(0x2, 0x1), signal = CarCabinManager.ID_LDW_LKS_SENSITIVITY_SWT),
        default = 0x1
    ),


    /**
     * 驾驶辅助-侧后辅助-显示区域
     */
    ADAS_SIDE_BACK_SHOW_AREA(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    ),

    /**
     * 行车--仪表--制式
     */
    DRIVE_METER_SYSTEM(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = CarCabinManager.ID_ACCMFTSTSDISP),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), origin = Origin.HVAC, signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT),
        default = 0x1
    );

    fun isValid(value: Int, isGet: Boolean = true): Boolean {
        return if (isGet) {
            get.isValid(value)
        } else {
            set.isValid(value)
        }
    }

}