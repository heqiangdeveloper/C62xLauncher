package com.chinatsp.vehicle.controller.producer

import com.chinatsp.vehicle.controller.LogManager
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/17 15:54
 * @desc   :
 * @version: 1.0
 */
class PanoramaCommandProducer : ICommandProducer {

    fun attemptCreateCommand(slots: Slots): CarCmd? {
        var command: CarCmd? = null
        if (!slots.text.contains("倒车")) {
            if (null == command) {
                command = attemptSwitchCommand(slots)
            }
            if (null == command) {
                command = attemptModeCommand(slots)
            }
            if (null == command) {
                command = attemptCameraCommand(slots)
            }
        }
        return command
    }

//    AVM view set request signal 切换全景视图命令，Reserved
//    0x0: Inactive
//    0x1: Front view
//    0x2: Rear view
//    0x3: Front-Left view
//    0x4: Front-Right view
//    0x5: Rear-Left view
//    0x6: Rear-Right view
//    0x7: TurnLeft view-3D
//    0x8: TurnRight view-3D
//    0x9: Front view-3D
//    0xA: Rear view-3D
//    0xB: Left view-3D
//    0xC: Right view-3D
//    0xD: Front-Left view-3D
//    0xE: Front-Right view-3D
//    0xF: Rear-Left view-3D
//    0x10: Rear-Right view-3D
//    0x11: Large single view-front
//    0x12: Large single view-rear
//    0x13: EOL/Test-view
//    0x14: Top View
//    0x15~0x1F: Reserved
    private fun attemptCameraCommand(slots: Slots): CarCmd? {
        LogManager.e("", "${slots.insType}, ${slots.text}")
        val part: Int
        var action = Action.VOID
        if (contains(slots.text, "前摄像头", "前视角")) {
            part = IPart.HEAD
        } else if (contains(slots.text, "后摄像头", "后视角")) {
            part = IPart.TAIL
        } else if (contains(slots.text, "左摄像头", "左视角")) {
            part = IPart.L_F or IPart.L_B
        } else if (contains(slots.text, "右摄像头", "右视角")) {
            part = IPart.R_F or IPart.R_B
        } else {
            part = IPart.HEAD
        }
        if (contains(slots.text, "打开", "切换")) {
            action = Action.CHANGED
        }
        if (Action.VOID != action && IPart.VOID != part) {
            val command = CarCmd(action = action, model = Model.PANORAMA)
            command.part = part
            return command
        }
        return null
    }

    private fun attemptModeCommand(slots: Slots): CarCmd? {
        LogManager.e("", "attemptModeCommand text:${slots.text}")
        if ("2D模式" == slots.text || "打开2D模式" == slots.text || "关闭3D模式" == slots.text) {
            //切换到2D模式
            val command = CarCmd(action = Action.OPTION, model = Model.PANORAMA)
            command.value = Action.OPTION shl 1
            return command
        }
        if ("3D模式" == slots.text || "打开3D模式" == slots.text || "关闭2D模式" == slots.text) {
            //切换到3D模式
            val command = CarCmd(action = Action.OPTION, model = Model.PANORAMA)
            command.value = Action.OPTION shl 2
            return command
        }
        return null
    }

    private fun attemptSwitchCommand(slots: Slots): CarCmd? {
        val keyCode = "360"
        if (keyCode == slots.name && keyCode == slots.mode) {
            val model = Model.PANORAMA
            var action = Action.VOID
            if (Keywords.OPEN == slots.operation) {
                action = Action.TURN_ON
            }
            if (Keywords.CLOSE == slots.operation) {
                action = Action.TURN_OFF
            }
            return CarCmd(action = action, model = model)
        }
        if (contains(slots.text, "关闭", "退出")) {
            val model = Model.PANORAMA
            val action = Action.TURN_OFF
            return CarCmd(action = action, model = model)
        }
        return null
    }

    private fun contains(source: String, target: String, target2: String = ""): Boolean {
        return source.contains(target) || source.contains(target2)
    }


}