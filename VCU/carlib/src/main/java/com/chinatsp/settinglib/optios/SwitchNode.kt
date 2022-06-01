package com.chinatsp.settinglib.optios

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/28 10:53
 * @desc   :
 * @version: 1.0
 */
enum class SwitchNode(val on: Int, val off: Int, val def: Int) {

    /**
     * 空调自干燥
     */
    AC_AUTO_ARID(0x01, 0x02, 0x01),

    /**
     * 自动除雾
     */
    AC_AUTO_DEMIST(0x01, 0x02, 0x01),

    /**
     * 预通风功能
     */
    AC_ADVANCE_WIND(0x01, 0x02, 0x01),

    /**
     * 车辆音效-声音-响度控制
     */
    SE_LOUDNESS(0x01, 0x02, 0x01);

    fun obtainValue(status: Boolean): Int {
        return if (status) on else off
    }
}