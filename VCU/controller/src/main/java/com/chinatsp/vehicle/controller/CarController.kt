package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import com.chinatsp.vehicle.controller.semantic.Slots
import com.chinatsp.vehicle.controller.utils.Keywords

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/9 16:23
 * @desc   :
 * @version: 1.0
 */
object CarController : IController {

    override fun doVoiceController(
        controller: IOuterController,
        callback: ICmdCallback,
        model: NlpVoiceModel,
    ): Boolean {
        var result = false
        val slots: Slots = model.slots
        //true表示是开启操作,false表示未知操作
        val open = isMatch(Keywords.OPT_OPENS, model.operation)
        //true表示关闭操作，false表示未知操作
        val close = !open && isMatch(Keywords.OPT_CLOSES, model.operation)
        val name: String = slots.name
        LogManager.d(tag, "doVoiceController open:$open, close:$close, $slots")

        if (isMatch(Keywords.DRIVER_WINDOW_COMMS, name)
            || isMatch(Keywords.PASSENGER_WINDOW_COMMS, name)) {
            result = open || close
        } else if (isMatch(Keywords.OIL_SHROUDS, name)) {
            result = open || close
        } else if (isMatch(Keywords.SKYLIGHTS, name)) {
            //天窗 操作
            result = open || close
            var action = Action.VOID
            if (open) action = Action.OPEN
            if (close) action = Action.CLOSE
            val cmd = CarCmd(action = action, model = Model.ACCESS_WINDOW, status = IStatus.INIT)
            cmd.slots = slots
            controller.doCarControlCommand(cmd, callback)
            LogManager.d(tag, "execute open window!!!cmd：$cmd")
        } else if (isMatch(Keywords.HOODS, name)) {
            //引擎盖 操作
            result = open || close
        } else if (isMatch(Keywords.TRUNKS, name)) {
            //后备厢 操作
            result = open || close
        } else if (isMatch(Keywords.WIPERS, name)) {
            //前雨刮 操作
            result = open || close
        } else if (isMatch(Keywords.REAR_WIPERS, name)) {
            //后雨刮 操作
            result = open || close
        }
//
//        else if (isMatch(Keywords.TIRE_PRESSURE_MONITORS, name)) {
//            result = open || close
//        } else if (isMatch(Keywords.SMOKES, slots.mode)) {
//            result = open || close
//        } else if (TextUtils.equals(Keywords.WIRELESS_CHARGING, name)) {
//            result = open || close
//        } else if (isMatch(Keywords.IDLE_START_AND_STOP, name)) {
//            result = open || close
//        }

        else if (isMatch(Keywords.AUTO_HEAD_LIGHTS, name)) {
            result = open || close
        } else if (isMatch(Keywords.LIGHTS, name)) {
            result = open || close
        } else if (isMatch(Keywords.FOG_LIGHTS, name)) {
            result = open || close
        } else if (TextUtils.equals(Keywords.MODE_DRIVE, slots.mode)) {
            result = open || close
        }

//        else if (ConditionerConstants.KT_NAME.contains(name)) {//空调
//            result = open || close
//            ConditionerParser.doDispatchSrAction(model, controller, callback)
//        } else if (isMatch(Keywords.VOICE_VENT, name)) {
//            result = open || close
//        }
        return result
    }

}