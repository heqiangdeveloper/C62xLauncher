package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAir
import com.chinatsp.vehicle.controller.annotation.IOrien
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords
import org.json.JSONObject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 16:23
 * @desc   :
 * @version: 1.0
 */
object AirController : IController {
    private const val DRIVER = "主驾"
    private const val COPILOT = "副驾"

    //内循环
    private const val MODE_RECYCLE_IN = "内循环"

    //外循环
    private const val MODE_RECYCLE_OUT = "外循环"

    //自动循环
    private const val MODE_RECYCLE_AUTO = "自动循环"


    private val COLD_MODES = arrayOf("制冷", "压缩机")

    private val HEAT_MODES = arrayOf("制热", "加热")

    //太热了
    private const val MINUS_MORE = "MINUS_MORE"

    //有点热
    private const val MINUS_LITTLE = "MINUS_LITTLE"

    // 太冷了
    private const val PLUS_MORE = "PLUS_MORE"

    //有点冷
    private const val PLUS_LITTLE = "PLUS_LITTLE"

    // 温度增高/风量增高
    private const val PLUS = "PLUS"

    // 温度降低/风量降低
    private const val MINUS = "MINUS"

//    // 温度设为中档
//    private const val MEDIUM = "MEDIUM"

    // 温度最高/风量最高
    private const val MAX = "MAX"

    // 温度最低
    private const val MIN = "MIN"

//    //中风
//    private const val WIND_CENTER = "中风"

    // 升高两度参数
    private const val REF_CUR = "CUR"
    private const val REF_ZERO = "ZERO"

    //面
    private const val AIR_FLOW_FACE = "面"

    //脚
    private const val AIR_FLOW_FOOT = "脚"

    //吹面吹脚
    private const val AIR_FLOW_FACE_FOOT = "吹面吹脚"

    // 除霜
    private val DOUBLE_DEFROST = arrayOf("除霜", "除雾")
    private val FRONT_DEFROST = arrayOf("前除霜", "前除雾")
    private val REAR_DEFROST = arrayOf("后除霜", "后除雾")
    private const val PURIFIER = "空气净化器"
//    private const val POST_DEFROST1 = "后除霜"
//    private const val POST_DEFROST2 = "外后视镜"
//    private const val POST_DEFROST3 = "后视镜加热"

    override fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean {
        val slots: Slots = model.slots
        var command: AirCmd? = null
        if (null == command) {
            command = attemptCreateSwitchCmd(slots)
        }
        if (null == command) {
            command = attemptCreateWindCmd(slots)
        }
        if (null == command) {
            command = attemptCreateTempCmd(slots)
        }
        if (null == command) {
            command = attemptCreateLoopModeCmd(slots)
        }
        if (null == command) {
            command = attemptCreateColdHeatCmd(slots)
        }
        if (null == command) {
            command = attemptCreatePurifierCmd(slots)
        }
        if (null == command) {
            command = attemptCreateFlowCmd(slots)
        }
        if (null == command) {
            command = attemptCreateGlassDefrostCmd(slots)
        }
        if (null == command) {
            command = attemptCreateAutoModeCmd(slots)
        }
        if (null != command) {
            controller.doAirControlCommand(command, callback)
        }
        return null != command
    }

    private fun attemptCreateAutoModeCmd(slots: Slots): AirCmd? {
        if ("空调" == slots.device) {
            var action = Action.VOID
            if ("自动" == slots.mode) {
                if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)) {
                    action = Action.TURN_ON
                }
                if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)) {
                    action = Action.TURN_OFF
                }
            }
            if ("手动" == slots.mode) {
                if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)) {
                    action = Action.TURN_OFF
                }
                if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)) {
                    action = Action.TURN_ON
                }
            }
            if (Action.VOID != action) {
                val command = AirCmd(action)
                command.air = IAir.AUTO_MODE
                return command
            }
        }
        return null
    }

    private fun attemptCreateGlassDefrostCmd(slots: Slots): AirCmd? {
        var air = IAir.VOID
        var part = IPart.VOID
        if (isMatch(DOUBLE_DEFROST, slots.mode)) {
            air = IAir.AIR_DEFROST
            part = IPart.HEAD or IPart.TAIL
        }
        if (isMatch(FRONT_DEFROST, slots.mode)) {
            air = IAir.AIR_DEFROST
            part = IPart.HEAD
        }
        if (isMatch(REAR_DEFROST, slots.mode)) {
            air = IAir.AIR_DEFROST
            part = IPart.TAIL
        }
        if (IAir.VOID == air) {
            return null
        }

        if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)) {
            val command = AirCmd(Action.TURN_ON)
            command.air = air
            command.part = part
            return command
        }
        if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)) {
            val command = AirCmd(Action.TURN_OFF)
            command.air = air
            command.part = part
            return command
        }
        return null
    }

    private fun attemptCreateFlowCmd(slots: Slots): AirCmd? {
        if (TextUtils.isEmpty(slots.airflowDirection)) {
            return null
        }
        var action = Action.VOID
        var orien = IOrien.VOID
        if (AIR_FLOW_FACE == slots.airflowDirection) {
            action = Action.OPTION
            orien = orien or IOrien.FACE
        }
        if (AIR_FLOW_FOOT == slots.airflowDirection) {
            action = Action.OPTION
            orien = orien or IOrien.FOOT
        }
        if (AIR_FLOW_FACE_FOOT == slots.airflowDirection) {
            action = Action.OPTION
            orien = orien or IOrien.FACE
            orien = orien or IOrien.FOOT
        }
        if (isMatch(DOUBLE_DEFROST, slots.mode)) {
            orien = orien or IOrien.MIDDLE
        }
        if (Action.VOID == action) {
            return null
        }
        val command = AirCmd(action)
        command.orien = orien
        command.air = IAir.AIR_FLOW
        return command
    }

    private fun attemptCreatePurifierCmd(slots: Slots): AirCmd? {
        if (PURIFIER != slots.mode) {
            return null
        }
        val air = IAir.AIR_PURGE
        if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.name)) {
            val command = AirCmd(Action.TURN_ON)
            command.air = air
            return command
        }
        if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.name)) {
            val command = AirCmd(Action.TURN_OFF)
            command.air = air
            return command
        }
        return null
    }

    private fun attemptCreateSwitchCmd(slots: Slots): AirCmd? {
        if ("INSTRUCTION" == slots.operation) {
            if ("双区" == slots.direction) {
                if (Keywords.OPEN == slots.insType) {
                    val command = AirCmd(Action.TURN_ON)
                    command.air = IAir.AIR_DOUBLE
                    return command
                }
                if (Keywords.CLOSE == slots.insType) {
                    val command = AirCmd(Action.TURN_OFF)
                    command.air = IAir.AIR_DOUBLE
                    return command
                }
            }
            if (Keywords.OPEN == slots.insType) {
                val command = AirCmd(Action.TURN_ON)
                command.air = IAir.ENGINE
                return command
            }
            if (Keywords.CLOSE == slots.insType) {
                val command = AirCmd(Action.TURN_OFF)
                command.air = IAir.ENGINE
                return command
            }
        }
        return null
    }

    private fun attemptCreateColdHeatCmd(slots: Slots): AirCmd? {
        var air = IAir.VOID
        var value = IAir.VOID
        var action = Action.VOID
        if (isMatch(COLD_MODES, slots.mode)) {
            air = IAir.COLD_HEAT
            value = (IAir.COLD_HEAT shl 1)
        }
        if (isMatch(HEAT_MODES, slots.mode)) {
            air = IAir.COLD_HEAT
            value = (IAir.COLD_HEAT shl 2)
        }
        if (IAir.VOID == air) {
            return null
        }
        if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)) {
            action = Action.TURN_ON
        }
        if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)) {
            action = Action.TURN_OFF
        }
        if (Action.VOID == action) {
            return null
        }
        val command = AirCmd(action)
        command.air = air
        command.value = value
        command.slots = slots
        return command
    }

    private fun attemptCreateLoopModeCmd(slots: Slots): AirCmd? {
        var air = IAir.VOID
        var value = IAir.VOID
        val modeValue = slots.mode
        if (MODE_RECYCLE_AUTO == modeValue) {
            air = IAir.LOOP_MODE
            value = IAir.LOOP_MODE shl 3
        } else if (MODE_RECYCLE_IN == modeValue) {
            air = IAir.LOOP_MODE
            value = IAir.LOOP_MODE shl 1
        } else if (MODE_RECYCLE_OUT == modeValue) {
            air = IAir.LOOP_MODE
            value = IAir.LOOP_MODE shl 2
        }
        if (IAir.VOID == air) {
            return null
        }
        if (isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)) {
            val command = AirCmd(Action.TURN_ON)
            command.air = air
            command.value = value
            return command
        }
        if (isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)) {
            val command = AirCmd(Action.TURN_OFF)
            command.air = air
            command.value = value
            return command
        }
        return null
    }

    private fun attemptCreateTempCmd(slots: Slots): AirCmd? {
        val value = slots.temperature?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        var action = Action.VOID
        var command: AirCmd? = null
        if (!isLikeJson(value)) {
            if ((PLUS == value) || (PLUS_MORE == value) || (PLUS_LITTLE == value)) {
                action = Action.PLUS
            } else if ((MINUS == value) || (MINUS_MORE == value) || (MINUS_LITTLE == value)) {
                action = Action.MINUS
            } else if (MIN == value) {
                action = Action.MIN
            } else if (MAX == value) {
                action = Action.MAX
            }
            if (Action.VOID != action) {
                command = AirCmd(action = action)
                command.slots = slots
                command.step = checkoutStep(value)
                command.air = IAir.AIR_TEMP
                command.part = obtainDirection(slots.direction)
            }
        } else {
            LogManager.e("temperature", "temperature----$value")
            val json = JSONObject(value)
            val consult = json.getString("ref")
            val offset = json.getInt("offset")
            if (REF_ZERO == consult) {
                action = Action.FIXED
            } else if (REF_CUR == consult) {
                val rule = json.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
            }
            if (Action.VOID != action) {
                command = AirCmd(action = action)
                command.slots = slots
                command.value = offset
                command.step = checkoutStep(value)
                command.air = IAir.AIR_TEMP
                command.part = obtainDirection(slots.direction)
            }
        }
        return command
    }

    private fun attemptCreateWindCmd(slots: Slots): AirCmd? {
        val value = slots.fanSpeed?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        var action = Action.VOID
        var command: AirCmd? = null
        if (!isLikeJson(value)) {
            if ((PLUS == value) || (PLUS_MORE == value) || (PLUS_LITTLE == value)) {
                action = Action.PLUS
            } else if ((MINUS == value) || (MINUS_MORE == value) || (MINUS_LITTLE == value)) {
                action = Action.MINUS
            } else if (MIN == value) {
                action = Action.MIN
            } else if (MAX == value) {
                action = Action.MAX
            }
            if (Action.VOID != action) {
                command = AirCmd(action = action)
                command.slots = slots
                command.step = checkoutStep(value)
                command.air = IAir.AIR_WIND
                command.part = obtainDirection(slots.direction)
            }
        } else {
            val json = JSONObject(value)
            val consult = json.getString("ref")
            val offset = json.getInt("offset")
            if (REF_ZERO == consult) {
                action = Action.FIXED
            } else if (REF_CUR == consult) {
                val rule = json.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
            }
            if (Action.VOID != action) {
                command = AirCmd(action = action)
                command.slots = slots
                command.value = offset
                command.step = checkoutStep(value)
                command.air = IAir.AIR_WIND
                command.part = obtainDirection(slots.direction)
            }
        }
        return command
    }

    private fun checkoutStep(value: String): Int {
        return when (value) {
            PLUS, MINUS -> 2
            PLUS_MORE, MINUS_MORE -> 3
            PLUS_LITTLE, MINUS_LITTLE -> 1
            else -> 1
        }
    }

    private fun obtainDirection(value: String?): Int {
        if (DRIVER == value) {
            return IPart.L_F
        }
        if (COPILOT == value) {
            return IPart.R_F
        }
        return IPart.L_F or IPart.R_F
    }
}