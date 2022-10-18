package com.chinatsp.vehicle.controller.producer

import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class AccessCommandProducer {


    fun attemptAccessCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (null == command) {
            command = attemptSkylightCommand(slots)
        }
        if (null == command) {
            command = attemptWindowCommand(slots)
        }
        if (null == command) {
            command = attemptDoorCommand(slots)
        }
        return command
    }

    private fun attemptDoorCommand(slots: Slots): CarCmd? {

        return null
    }

    private fun attemptWindowCommand(slots: Slots): CarCmd? {

        return null
    }

    private fun attemptSkylightCommand(slots: Slots): CarCmd? {

        return null
    }


}