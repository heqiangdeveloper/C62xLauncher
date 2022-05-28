package com.chinatsp.settinglib.manager

import com.chinatsp.settinglib.manager.cabin.SeatManager

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/27 10:18
 * @desc   :
 * @version: 1.0
 */
interface ISignal {

    val TAG: String

    val mcuConcernedSerial:Set<Int>

    val cabinConcernedSerial:Set<Int>

    val hvacConcernedSerial:Set<Int>

}