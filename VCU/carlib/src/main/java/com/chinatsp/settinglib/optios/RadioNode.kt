package com.chinatsp.settinglib.optios

import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.hvac.CarHvacManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RNorm
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:19
 * @desc   :
 * @version: 1.0
 */
enum class RadioNode(
    val get: RNorm,
    val set: RNorm,
    val default: Int,
    val area: Area = Area.GLOBAL,
) {

    AC_COMFORT(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_ACCMFTSTSDISP
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            origin = Origin.HVAC,
            signal = CarHvacManager.ID_HVAC_AVN_AC_AUTO_CMFT_SWT
        ),
        default = 0x1
    ),

    //-------------------车门车窗--开始-------------------
    /**
     * 车门与车窗--车门--行车自动落锁
     * set -> 0x1: off 0x2: 5km/h    0x3: 10km/h    0x4: 15km/h   0x5: 20km/h
     * get -> 0x0: Inactive 0x1: off 0x2: 5km/h 0x3: 10km/h  0x4: 15km/h 0x5: 20km/h(default)
     * UE 黑夜关闭
     */
    DOOR_DRIVE_LOCK(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5),
            signal = CarCabinManager.ID_VSPEED_LOCKING_STATUE
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5),
            signal = CarCabinManager.ID_VSPEED_LOCK
        ),
        default = 0x1
    ),

    /**
     * 车门与车窗--车门--熄火自动解锁
     * set -> 0x1: unlock FL door 0x2: unlock all doors(default)  0x3: FunctionDisable
     * get -> 0x0: Inactive 0x1: unlock FL door 0x2: unlock all doors(default) 0x3: FunctionDisable
     * UE 默认关闭
     */
    DOOR_FLAMEOUT_UNLOCK(
        get = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = CarCabinManager.ID_CUTOFF_UNLOCK_DOORS_STATUE
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = CarCabinManager.ID_CUT_OFF_UNLOCK_DOORS
        ),
        default = 0x1
    ),

    /**
     * 车门与车窗--电动尾门--电动尾门智能进入
     * set -> 0x1: OFF  0x2: On Mode 1  0x3: On Mode 2
     * get -> 0x0: Reserved 0x1: OFF  0x2: On Mode 1  0x3: On Mode 2 0x4~0x6: Reserved 0x7: Invalid
     */
    STERN_SMART_ENTER(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_PTM_SMART_ENTRY_PTM_STS
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_PTM_SMT_ENTRY_SET
        ),
        default = 0x1
    ),
    //-------------------车门车窗--结束-------------------


    /**
     * 驾驶辅助-智能巡航-前车驶离提示
     * set -> object distingguish and disappear switch,if not set'OBJ_DETECTION',
     *       the value of signal is 0x0(inactive)[0x1,0,0x0,0x5]
     *       0x0: Inactive;  0x1: Detect warning; 0x2: Disappare warning
     *       0x3: Detect and disappear warning(default); 0x4: Warning off; 0x5~0x7:Reserved
     */
    ADAS_LIMBER_LEAVE(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = -1//暂时没有找到获取信号
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_OBJ_DETECTION_SWT
        ),
        default = 0x1
    ),

    /**
     * 驾驶辅助-车道辅助-车道辅助系统
     * Operation mode of LDW/RDP/LKS. The default value is 0x1 LDW in C53F,
     * 0x3 LKS in C62X. 0x0:Initial 0x1:LDW 0x2:RDP 0x3:LKS
     * set -> LDW/RDP/LKS function enable switch,if not set 'LDW_RDP_LKS_FUNC_ENABLE',
     *       the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *       C53F send the signal 0x0 all the time
     *       0x0: Inactive; 0x1: LDW Enable; 0x2: RDP Enable; 0x3: LKS Enable（C62 default）
     *
     */
    ADAS_LANE_ASSIST_MODE(
        get = RNorm(
//            values = intArrayOf(0x0, 0x2, 0x1, 0x3),
            values = intArrayOf(0x2, 0x1, 0x3),
            signal = CarCabinManager.ID_LANE_ASSIT_TYPE
        ),
        set = RNorm(
//            values = intArrayOf(0x0, 0x2, 0x1, 0x3),
            values = intArrayOf(0x2, 0x1, 0x3),
            signal = CarCabinManager.ID_LDW_RDP_LKS_FUNC_EN
        ),
        default = 0x1
    ),

    /**
     * 驾驶辅助-车道辅助-报警方式
     * get mcu -> LDW_ENABLE_RESPONSE (暂缺中间件信号)
     * set -> LDW/LKS/TJAICA enable switch,if not set 'LDW_LKS_TJAICA_SWITCH',
     *       the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *       0x0: Inacitve; 0x1: UI warning; 0x2: UI and SPEAKER warning(default); 0x3: OFF
     */
    ADAS_LDW_STYLE(
        get = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = -1
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = CarCabinManager.ID_LDW_LKS_TJAICA_SWT
        ),
        default = 0x2
    ),

    /**
     * 驾驶辅助-车道辅助-灵敏度
     * get -> LKS sensitivity车道保持的灵敏度 0x0:lowSensitivity 0x1:highSensitivity 0x2:
     *        Initial 0x3:reserved
     * set -> LDW/LKS sensitivity switch,if not set 'LDW_LKS_SENSITIVITY_SWITCH ',
     *        the value of signal is 0x0(inactive)[0x1,0,0x0,0x3]
     *        0x0: Inacitve; 0x1: Low Sensitivity; 0x2: High Sensitivity(default); 0x3: Reserved
     */
    ADAS_LDW_SENSITIVITY(
        get = RNorm(
            values = intArrayOf(0x1, 0x0),
            signal = CarCabinManager.ID_LKS_SENSITIVITY
        ),
        set = RNorm(
            values = intArrayOf(0x2, 0x1),
            signal = CarCabinManager.ID_LDW_LKS_SENSITIVITY_SWT
        ),
        default = 0x1
    ),


    /**
     * 驾驶辅助-侧后辅助-显示区域
     */
    ADAS_SIDE_BACK_SHOW_AREA(
        get = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = -1
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = -1
        ),
        default = 0x1
    ),

    /**
     * 行车--仪表--制式
     * set -> 仪表公制英制切换开光触发[0x1,0,0x0,0x3]
     *        0x0: Inactive; 0x1: Metric; 0x2: Imperial; 0x3: Reserved
     */
    DRIVE_METER_SYSTEM(
        get = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = -1
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = CarCabinManager.ID_HUM_ICM_METRIC_IMPERIAL_SWT
        ),
        default = 0x1
    ),

    /**
     * 行车--方向盘--电子助力转向模式
     */
    DRIVE_EPS_MODE(
        get = RNorm(values = intArrayOf(0x8, 0x9), signal = -1),
        set = RNorm(values = intArrayOf(0x8, 0x9), signal = -1),
        default = 0x8
    ),

    //-------------------灯光设置--开始-------------------

    /**
     * 灯光设置--灯光--伴我回家
     * set -> 0x1: off; 0x2: 10s; 0x3: 20s; 0x4: 30s(default); 0x5: 60s;  0x6: 120s
     * get -> 0x0: Inactive 0x1: off 0x2: 10s 0x3: 20s 0x4: 30s(default) 0x5: 60s 0x6: 120s 0x7: reserved
     */
    LIGHT_DELAYED_OUT(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0x6),
            signal = CarCabinManager.ID_FOLLOW_ME_HOME_STATUE
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0x6),
            signal = CarCabinManager.ID_FOLLOW_ME_HOME_SET
        ),
        default = 0x1
    ),

    /**
     * 灯光设置--灯光--转向灯变道闪烁次数
     * set -> 0x1: off 0x2: 3 flasher(default) 0x3: 5 flasher  0x4: 7 flasher
     * get -> 0x0:Inactive; 0x1: off; 0x2: 3 flasher(default);  0x3: 5 flasher; 0x4: 7 flasher
     */
    LIGHT_FLICKER(
        get = RNorm(
            values = intArrayOf(0x2, 0x3, 0x4),
            signal = CarCabinManager.ID_TURNLIGHT_FOR_LANE_CHANGE_STATUE
        ),
        set = RNorm(
            values = intArrayOf(0x2, 0x3, 0x4),
            signal = CarCabinManager.ID_TURNLIGHT_FOR_LANE_CHANGE_SET
        ),
        default = 0x2
    ),

    /**
     * 灯光设置--灯光--外部灯光仪式感
     * set -> 0x1: Mode 1; 0x2: Mode 2; 0x3: Mode 3
     * get -> 0x0:OFF 0x1:Unlock Ceremoial Sense 0x2:Lock Ceremoial Sense 0x3:Panic Ceremoial Sense 0x4~0x7:reserved
     */
    LIGHT_CEREMONY_SENSE(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_BCM_CERE_SENSE_CMD
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_HUM_CERE_SENSE_SW_SET
        ),
        default = 0x1
    ),
    //-------------------灯光设置--结束-------------------

    /**
     * 车辆音效--声音--仪表报警音量等级 (no signal)
     * get -> 【反馈】仪表报警音量等级反馈信号 0x0: High; 0x1: medium; 0x2: Low; 0x3: Reserved
     * set -> 仪表报警音量等级开关触发[0x1,0,0x0,0x3] 0x0: Inactive; 0x1: High; 0x2: medium; 0x3: Low
     */
    ICM_VOLUME_LEVEL(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_ICM_HUM_VOLUME_STS
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = CarCabinManager.ID_HUM_ICM_VOLUME_LEVEL
        ),
        default = 0x3
    ),

    /**
     * 车辆音效--声音--导航混音 (no signal)
     * set -> 车机混音策略[0x1,-1,0x0,0x3]
     *        0x0:not used; 0x1: MIX0((default));  0x2: Mix1; 0x3: Mix; 0x4~0x7: reserved
     */
    NAVI_AUDIO_MIXING(
        get = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = -1
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2),
            signal = CarCabinManager.ID_HUM_SOUND_MIX
        ),
        default = 0x1
    ),

    /**
     * 车辆音效--声音--速度音量补偿
     */
    SPEED_VOLUME_OFFSET(
        get = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = -1
        ),
        set = RNorm(
            values = intArrayOf(0x1, 0x2, 0x3),
            signal = -1
        ),
        default = 0x1
    ),

    /**
     * 车辆音效--音效--均衡器
     * get -> EQ switch  type status EQ开关种类选择状态 0x0: Default 0x1: Classic
     *        0x2: POP 0x3: Jazze 0x4: Beats 0x5: Rock 0x6: Reserved 0x7: Reserved
     */
    SYSTEM_SOUND_EFFECT(
        get = RNorm(
            values = intArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6),
            signal = CarCabinManager.ID_AMP_EQ_TYPE_SW_STS
        ),
        set = RNorm(
            values = intArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6),
            signal = -1
        ),
        default = 0x0
    ) {
        override fun isValid(value: Int, isGet: Boolean): Boolean {
            if (VcuUtils.isAmplifier && value == 0) {
                return false
            }
            return if (isGet) {
                get.isValid(value)
            } else {
                set.isValid(value)
            }
        }

    },

    /**
     * 车辆音效--音效--环境音效
     * set -> 环境音效种类选择[0x1,0,0x0,0x4] 0x0: Inactive 0x1: Natural  0x2: Club 0x3: Live 0x4: Lounge 0x5: Reserved 0x6~0x7: Reserved
     * get -> 环境音效种类选择状态 0x0:Inactive 0x1: Natural(default) 0x2: Club 0x3: Live 0x4: Lounge 0x5:Reserved 0x6~0x7: Reserved
     */
    AUDIO_ENVI_AUDIO(
        get = RNorm(
//            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7),
            values = intArrayOf(0x1, 0x2, 0x3, 0x4),
            signal = CarCabinManager.ID_AMP_ATMOS_MOD_TYPE_SW_STS
        ),
        set = RNorm(
//            values = intArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7),
            values = intArrayOf(0x1, 0x2, 0x3, 0x4),
            signal = CarCabinManager.ID_HUM_ATMOS_MOD_TYPE
        ),
        default = 0x1
    ),


    /**
     * 行车--拖车提醒--传感器灵敏度
     * 此信号走TBOX信号 而非走CAN信号， 所以需要特殊处理
     */
    DEVICE_TRAILER_SENSITIVITY(
        get = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = -1),
        set = RNorm(values = intArrayOf(0x1, 0x2, 0x3), signal = -1),
        default = 0x1
    ),

    /**
     * 行车--拖车提醒--拖车提醒距离
     * 此信号走TBOX信号 而非走CAN信号， 所以需要特殊处理
     */
    DEVICE_TRAILER_DISTANCE(
        get = RNorm(values = intArrayOf(200, 500, 1000, 2000), signal = -1),
        set = RNorm(values = intArrayOf(200, 500, 1000, 2000), signal = -1),
        default = 200
    )
    ;

    open fun isValid(value: Int, isGet: Boolean = true): Boolean {
        return if (isGet) {
            get.isValid(value)
        } else {
            set.isValid(value)
        }
    }

    fun indexOf(value: Int, isGet: Boolean = true): Int {
        return if (isGet) get.values.indexOf(value) else set.values.indexOf(value)
    }

    fun obtainSelectValue(value: Int, isGet: Boolean = true): Int {
        return if (isGet) {
            getTargetValue(get.values, set.values, value)
        } else {
            getTargetValue(set.values, get.values, value)
        }
    }

    private fun getTargetValue(queryArray: IntArray, resultArray: IntArray, value: Int): Int {
        var index = queryArray.indexOf(value)
        Timber.d("obtainSelectValue value:$value, index:$index, node:$this")
        if (index !in 0..resultArray.size) {
            index = 0
        }
        return resultArray[index]
    }

}