package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.Model
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

    //内循环
    private const val MODE_RECYCLE_IN = "内循环"

    //外循环
    private const val MODE_RECYCLE_OUT = "外循环"

    //自动循环
    private const val MODE_RECYCLE_AUTO = "自动循环"

    // 除霜
    private const val MODE_DEFROST = "除霜"

    //制冷模式
    private const val MODE_COLD = "制冷"

    //制热模式
    private const val MODE_HOT = "制热"

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
    private const val FRONT_DEFROST = "前除霜"
    private const val REAR_DEFROST = "后除霜"
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
        LogManager.d(tag, "doVoiceController open:$open, close:$close, $slots")
        var action = Action.VOID
        var cmd: BaseCmd? = createWindCmd(slots)
        if (null == cmd) {
            cmd = createTempCmd(slots)
        }
        if (null == cmd) {
            cmd = createLoopModeCmd(slots, open, close)
        }
        if (null == cmd) {
            cmd = createSwitchCmd(slots, open, close)
        }
        cmd?.let {
            controller.doAirControlCommand(cmd as AirCmd, callback)
        }
        return true
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

    private fun createLoopModeCmd(slots: Slots, open: Boolean, close: Boolean): BaseCmd? {
        val isNotSame = open xor close
        if (!isNotSame) {
            return null
        }
        var action = Action.VOID
        var cmd: BaseCmd? = null
        val coreValue = slots.mode
        if (MODE_RECYCLE_AUTO == coreValue) {
            action = if (open) Action.OPEN else Action.CLOSE
        } else if (MODE_RECYCLE_IN == coreValue) {
            action = if (open) Action.OPEN else Action.CLOSE
        } else if (MODE_RECYCLE_OUT == coreValue) {
            action = if (open) Action.OPEN else Action.CLOSE
        }
        return null
    }

    private fun createTempCmd(slots: Slots): BaseCmd? {
        var action = Action.VOID
        var cmd: BaseCmd? = null
        val coreValue = slots.temperature
        if (!TextUtils.isEmpty(coreValue)) {
            if (!isLikeJson(coreValue)) {
                if (PLUS == coreValue) {
                    action = Action.PLUS
                } else if (MINUS == coreValue) {
                    action = Action.MINUS
                } else if (MIN == coreValue) {
                    action = Action.MIN
                } else if (MAX == coreValue) {
                    action = Action.MAX
                }
                if (Action.VOID != action) {
                    cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                    cmd.slots = slots
                }
            } else {
                val jsonObject = JSONObject(coreValue)
                val step = jsonObject.getString("ref")
                if (REF_CUR == step) {
                    val rule = jsonObject.getString("direct")
                    if ("+" == rule) {
                        action = Action.PLUS
                    } else if ("-" == rule) {
                        action = Action.MINUS
                    }
                    if (Action.VOID != action) {
                        val offset = jsonObject.getInt("offset")
                        cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                        cmd.slots = slots
                        cmd.step = offset
                    }
                } else if (REF_ZERO == step) {
                    val offset = jsonObject.getInt("offset")
                    action = Action.FIXED
                    cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                    cmd.slots = slots
                    cmd.value = offset
                }
            }
        }
        return cmd
    }

    private fun createWindCmd(slots: Slots): BaseCmd? {
        var action = Action.VOID
        var cmd: BaseCmd? = null
        val coreValue = slots.fanSpeed
        if (!TextUtils.isEmpty(coreValue)) {
            if (!isLikeJson(coreValue)) {
                if (PLUS == coreValue) {
                    action = Action.PLUS
                } else if (MINUS == coreValue) {
                    action = Action.MINUS
                } else if (MIN == coreValue) {
                    action = Action.MIN
                } else if (MAX == coreValue) {
                    action = Action.MAX
                }
                if (Action.VOID != action) {
                    cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                    cmd.slots = slots
                }
            } else {
                val jsonObject = JSONObject(coreValue)
                val step = jsonObject.getString("ref")
                if (REF_CUR == step) {
                    val rule = jsonObject.getString("direct")
                    if ("+" == rule) {
                        action = Action.PLUS
                    } else if ("-" == rule) {
                        action = Action.MINUS
                    }
                    cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                    cmd.slots = slots
                } else if (REF_ZERO == step) {
                    val offset = jsonObject.getInt("offset")
                    action = Action.FIXED
                    cmd = AirCmd(action = action, model = Model.CABIN_AIR, status = IStatus.INIT)
                    cmd.slots = slots
                    cmd.value = offset
                }
            }
        }
        return cmd
    }
}