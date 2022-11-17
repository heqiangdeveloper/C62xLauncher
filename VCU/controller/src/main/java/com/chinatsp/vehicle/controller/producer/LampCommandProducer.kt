package com.chinatsp.vehicle.controller.producer

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.MAX
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.MIN
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.MINUS
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.MINUS_LITTLE
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.MINUS_MORE
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.PLUS
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.PLUS_LITTLE
import com.chinatsp.vehicle.controller.utils.Keywords.Companion.PLUS_MORE

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class LampCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (!TextUtils.isEmpty(slots.name)) {
            if (null == command) {
                command = attemptCreateDistantHeadlightCommand(slots)
            }
            if (null == command) {
                command = attemptCreateDippedHeadlightCommand(slots)
            }
            if (null == command) {
                command = attemptCreateSidelightsCommand(slots)
            }
            if (null == command) {
                command = attemptCreateFogLightCommand(slots)
            }
            if (null == command) {
                command = attemptCreateLightCommand(slots)
            }
        }
        return command
    }

    /**
     * 远光灯
     */
    private fun attemptCreateDistantHeadlightCommand(slots: Slots): CarCmd? {
        if (!isMatch(Keywords.DISTANT_HEAD_LIGHT, slots.name)) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_COMMON)
        command.slots = slots
        command.car = ICar.LAMPS
        command.act = IAct.DISTANT_LIGHT
        return command
    }

    /**
     * 位置灯
     */
    private fun attemptCreateSidelightsCommand(slots: Slots): CarCmd? {
        if (Keywords.SIDE_LIGHT != slots.name) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_COMMON)
        command.slots = slots
        command.car = ICar.LAMPS
        command.act = IAct.SIDE_LIGHT
        return command
    }

    /**
     * 近光灯
     */
    private fun attemptCreateDippedHeadlightCommand(slots: Slots): CarCmd? {
        if (!isMatch(Keywords.DIPPED_HEAD_LIGHT, slots.name)) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_COMMON)
        command.slots = slots
        command.car = ICar.LAMPS
        command.act = IAct.DIPPED_LIGHT
        return command
    }

    private fun attemptCreateLightCommand(slots: Slots): CarCmd? {
        if (!slots.name.contains("灯光")) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_COMMON)
        command.slots = slots
        command.car = ICar.LAMPS
        command.act = IAct.DIPPED_LIGHT
        return command
    }

    /**
     * 雾灯
     */
    private fun attemptCreateFogLightCommand(slots: Slots): CarCmd? {
        if (!slots.name.contains(Keywords.FOG_LIGHT)) {
            return null
        }
        val action = obtainSwitchAction(slots.operation)
        if (Action.VOID == action) {
            return null
        }
        val command = CarCmd(action = action, model = Model.LIGHT_COMMON)
        command.slots = slots
        command.car = ICar.LAMPS
        command.act = IAct.FOG_LIGHT
        command.part = checkoutPart(slots.name)
        return command
    }

    private fun checkoutPart(name: String): Int {
        return if (isContains(name, Keywords.F_R)) {
            IPart.HEAD
        } else if (isContains(name, Keywords.B_R)) {
            IPart.TAIL
        } else if (name.contains("前后") || name.contains("后前")) {
            IPart.HEAD or IPart.TAIL
        } else if (name.contains("前")) {
            IPart.HEAD
        } else if (name.contains("后")) {
            IPart.TAIL
        } else {
            IPart.HEAD or IPart.TAIL
        }

    }

    private fun isContains(value: String, array: Array<String>): Boolean {
        for (item in array) {
            if (value.contains(item)) {
                return true
            }
        }
        return false
    }

    private fun obtainActionPair(value: String): Pair<Int, Int> {
        val step = 2
        var action = Action.VOID
        do {
            if (MIN == value) {
                action = Action.MIN
                break
            }
            if (MAX == value) {
                action = Action.MAX
                break
            }
            if (PLUS_MORE == value) {
                action = Action.PLUS
                break
            }
            if (MINUS_MORE == value) {
                action = Action.MINUS
                break
            }
            if ((PLUS == value) || (PLUS_LITTLE == value)) {
                action = Action.PLUS
                break
            }
            if ((MINUS == value) || (MINUS_LITTLE == value)) {
                action = Action.MINUS
                break
            }
        } while (false)
        return Pair(action, step)
    }

    private fun obtainSwitchAction(value: String): Int {
        if (isMatch(Keywords.OPT_OPENS, value)) {
            return Action.TURN_ON
        }
        if (isMatch(Keywords.OPT_CLOSES, value)) {
            return Action.TURN_OFF
        }
        return Action.VOID
    }

}