package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.*
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.BaseCmd
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

    //制冷模式
    private const val MODE_COLD = "制冷"

    //制热模式
    private const val MODE_HOT = "制热"

    private const val COMPRESSOR = "压缩机"

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

    // 温度设为中档
    private const val MEDIUM = "MEDIUM"

    // 温度最高/风量最高
    private const val MAX = "MAX"

    // 温度最低
    private const val MIN = "MIN"

    //中风
    private const val WIND_CENTER = "中风"

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
    private const val POST_DEFROST1 = "后除霜"
    private const val POST_DEFROST2 = "外后视镜"
    private const val POST_DEFROST3 = "后视镜加热"

    override fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean {
        var result = false
        val slots: Slots = model.slots
        //true表示是开启操作,false表示未知操作
        val open = isMatch(Keywords.OPT_OPENS, model.operation, slots.insType)
        //true表示关闭操作，false表示未知操作
        val close = !open && isMatch(Keywords.OPT_CLOSES, model.operation, slots.insType)
        LogManager.d(tag,
            "doVoiceController operation:${slots.operation}, insType:${slots.insType}, ${slots.text}")
        LogManager.d(tag, "doVoiceController name:${slots.name}, mode:${slots.mode}, ${slots.text}")
        var cmd: BaseCmd? = attemptCreateSwitchCmd(slots)
        if (null == cmd) {
            cmd = attemptCreateCompressorCmd(slots)
        }
        if (null == cmd) {
            cmd = attemptCreateWindCmd(slots)
        }
        if (null == cmd) {
            cmd = attemptCreateTempCmd(slots)
        }
        if (null == cmd) {
            cmd = attemptCreateLoopModeCmd(slots)
        }
        if (null == cmd) {
            cmd = createColdOrHot(slots)
        }
        if (null == cmd) {
            cmd = attemptCreatePurifierCmd(slots)
        }
        if (null == cmd) {
            cmd = attemptCreateFlowCmd(slots)
        }
        if (null == cmd) {
            cmd = attemptCreateGlassDefrostCmd(slots)
        }
//        if (null == cmd) {
//            cmd = createSwitchCmd(slots, open, close)
//        }
        cmd?.let {
            controller.doAirControlCommand(cmd as AirCmd, callback)
        } ?: doHandleUnknownHint(callback)
        return true
    }

    private fun attemptCreateGlassDefrostCmd(slots: Slots): BaseCmd? {
        var air = IAir.DEFAULT
        var part = IPart.DEFAULT
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
        if (IAir.DEFAULT == air) {
            return null
        }

        val open = isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)
        if (open) {
            val command = AirCmd(Action.TURN_ON)
            command.air = air
            command.part = part
            return command
        }
        val close = isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)
        if (close) {
            val command = AirCmd(Action.TURN_OFF)
            command.air = air
            command.part = part
            return command
        }
        return null
    }

    private fun attemptCreateFlowCmd(slots: Slots): BaseCmd? {
        if (TextUtils.isEmpty(slots.airflowDirection)) {
            return null
        }
        var action = Action.VOID
        var orien = IOrien.DEFAULT
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
        var command = AirCmd(action)
        command.orien = orien
        command.air = IAir.AIR_FLOW
        return command
    }

    private fun attemptCreatePurifierCmd(slots: Slots): BaseCmd? {
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

    private fun attemptCreateCompressorCmd(slots: Slots): BaseCmd? {
        if (isMatch(COLD_MODES, slots.mode)) {
            val air = IAir.MODE_COLD
            var action = Action.VOID
            if (Keywords.SET == slots.operation) {
                action = Action.TURN_ON
            }
            if (Keywords.CLOSE == slots.operation) {
                action = Action.TURN_OFF
            }
            if (Action.VOID != action) {
                val command = AirCmd(action)
                command.air = air
                return command
            }
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
                return AirCmd(Action.OPEN)
            }
            if (Keywords.CLOSE == slots.insType) {
                return AirCmd(Action.CLOSE)
            }
        }
        return null
    }

    private fun createColdOrHot(slots: Slots): BaseCmd? {
        LogManager.d("try check air mode:${slots.mode}")
        var air = IAir.DEFAULT
        var action = Action.VOID
        if (isMatch(COLD_MODES, slots.mode)) {
            air = air xor IAir.MODE_COLD
        }
        if (MODE_HOT == slots.mode) {
            air = air xor IAir.MODE_HOT
        }
        if (IAir.DEFAULT == air) {
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
        command.slots = slots
        command.air = air
        return command
    }

    private fun createSwitchCmd(slots: Slots, open: Boolean, close: Boolean): BaseCmd? {
        if (open) {
            val cmd = AirCmd(action = Action.OPEN, model = Model.CABIN_AIR, status = IStatus.INIT)
            cmd.slots = slots
            return cmd
        }
        if (close) {
            val cmd = AirCmd(action = Action.CLOSE, model = Model.CABIN_AIR, status = IStatus.INIT)
            cmd.slots = slots
            return cmd
        }
        return null
    }

    private fun attemptCreateLoopModeCmd(slots: Slots): BaseCmd? {
        var air = IAir.DEFAULT
        val modeValue = slots.mode
        if (MODE_RECYCLE_AUTO == modeValue) {
            air = IAir.LOOP_AUTO
        } else if (MODE_RECYCLE_IN == modeValue) {
            air = IAir.LOOP_INNER
        } else if (MODE_RECYCLE_OUT == modeValue) {
            air = IAir.LOOP_OUTER
        }
        if (IAir.DEFAULT == air) {
            return null
        }
        val open = isMatch(Keywords.OPT_OPENS, slots.operation, slots.insType)
        if (open) {
            val command = AirCmd(Action.TURN_ON)
            command.air = air
            return command
        }
        val close = isMatch(Keywords.OPT_CLOSES, slots.operation, slots.insType)
        if (close) {
            val command = AirCmd(Action.TURN_OFF)
            command.air = air
            return command
        }
        return null
    }

    private fun attemptCreateTempCmd(slots: Slots): BaseCmd? {
        val value = slots.temperature?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        var action = Action.VOID
        var cmd: AirCmd? = null
        if (!isLikeJson(value)) {
            var step = 1
            if ((PLUS == value) || (PLUS_MORE == value) || (PLUS_LITTLE == value)) {
                step = checkoutStep(value)
                action = Action.PLUS
            } else if ((MINUS == value) || (MINUS_MORE == value) || (MINUS_LITTLE == value)) {
                step = checkoutStep(value)
                action = Action.MINUS
            } else if (MIN == value) {
                action = Action.MIN
            } else if (MAX == value) {
                action = Action.MAX
            }
            if (Action.VOID != action) {
                cmd = AirCmd(action = action)
                cmd.slots = slots
                cmd.step = step
                cmd.air = IAir.AIR_TEMP
                cmd.part = obtainDirection(cmd.slots?.direction)
            }
        } else {
            LogManager.e("temperature", "temperature----$value")
            val jsonObject = JSONObject(value)
            val consult = jsonObject.getString("ref")
            if (REF_CUR == consult) {
                val rule = jsonObject.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
                if (Action.VOID != action) {
                    val offset = jsonObject.getInt("offset")
                    cmd = AirCmd(action = action)
                    cmd.slots = slots
                    cmd.step = offset
                    cmd.air = IAir.AIR_TEMP
                    cmd.part = obtainDirection(cmd.slots?.direction)
                    LogManager.d("",
                        "attemptCreateTempCmd consult:$consult, rule:$rule, offset:$offset")
                }
            } else if (REF_ZERO == consult) {
                val offset = jsonObject.getInt("offset")
                action = Action.FIXED
                cmd = AirCmd(action = action)
                cmd.slots = slots
                cmd.value = offset
                cmd.air = IAir.AIR_TEMP
                cmd.part = obtainDirection(cmd.slots?.direction)
                LogManager.d("", "attemptCreateTempCmd consult:$consult, offset:$offset")
            }
        }
        return cmd
    }


    private fun attemptCreateWindCmd(slots: Slots): BaseCmd? {
        val value = slots.fanSpeed?.toString() ?: ""
        if (TextUtils.isEmpty(value)) {
            return null
        }
        var action = Action.VOID
        var cmd: AirCmd? = null
        if (!isLikeJson(value)) {
            var step = 1
            if ((PLUS == value) || (PLUS_MORE == value) || (PLUS_LITTLE == value)) {
                step = checkoutStep(value)
                action = Action.PLUS
            } else if ((MINUS == value) || (MINUS_MORE == value) || (MINUS_LITTLE == value)) {
                step = checkoutStep(value)
                action = Action.MINUS
            } else if (MIN == value) {
                action = Action.MIN
            } else if (MAX == value) {
                action = Action.MAX
            }
            if (Action.VOID != action) {
                cmd = AirCmd(action = action)
                cmd.slots = slots
                cmd.step = step
                cmd.air = IAir.AIR_WIND
                cmd.part = obtainDirection(slots.direction)
                LogManager.d(tag, "attemptCreateWindCmd value:$value")
            }
        } else {
            val jsonObject = JSONObject(value)
            val consult = jsonObject.getString("ref")
            if (REF_CUR == consult) {
                val rule = jsonObject.getString("direct")
                if ("+" == rule) {
                    action = Action.PLUS
                }
                if ("-" == rule) {
                    action = Action.MINUS
                }
                if (Action.VOID != action) {
                    cmd = AirCmd(action = action)
                    cmd.slots = slots
                    cmd.air = IAir.AIR_WIND
                    cmd.part = obtainDirection(slots.direction)
                    LogManager.d(tag, "attemptCreateWindCmd consult:$consult, rule:$rule")
                }
            } else if (REF_ZERO == consult) {
                val offset = jsonObject.getInt("offset")
                action = Action.FIXED
                cmd = AirCmd(action = action)
                cmd.slots = slots
                cmd.value = offset
                cmd.air = IAir.AIR_WIND
                cmd.part = obtainDirection(slots.direction)
                LogManager.d(tag, "attemptCreateWindCmd consult:$consult, offset:$offset")
            }
        }
        return cmd
    }

    private fun checkoutStep(value: String): Int {
        return when (value) {
            PLUS, MINUS -> 4
            PLUS_MORE, MINUS_MORE -> 6
            PLUS_LITTLE, MINUS_LITTLE -> 2
            else -> 1
        }
    }

    private fun obtainDirection(value: String?): Int {
        if (DRIVER == value) {
            return IPart.LEFT_FRONT
        }
        if (COPILOT == value) {
            return IPart.RIGHT_FRONT
        }
        return IPart.LEFT_FRONT or IPart.RIGHT_FRONT
    }
}