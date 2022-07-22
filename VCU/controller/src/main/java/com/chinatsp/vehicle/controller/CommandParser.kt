package com.chinatsp.vehicle.controller

import android.text.TextUtils
import com.chinatsp.ifly.aidlbean.CmdVoiceModel
import com.chinatsp.ifly.aidlbean.NlpVoiceModel
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.bean.Cmd
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.data.Semantic
import com.chinatsp.vehicle.controller.utils.ConstantsVolume
import com.google.gson.Gson

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/14 13:34
 * @desc   :
 * @version: 1.0
 */
class CommandParser {
    val TAG: String = "CommandDispatcher"
    fun isMatch(arrays: Array<String>, cmd: String?): Boolean {
        return arrays.contains(cmd)
    }

    fun doDispatchSrAction(nlpVoiceModel: NlpVoiceModel, controller: IOuterController, callback: ICmdCallback): Boolean {
        var result: Boolean = false
        do {
            try {
                val semantic: Semantic = Gson().fromJson(nlpVoiceModel.semantic, Semantic::class.java)
                if (semantic.slots == null) {
//                    LogManager.d(TAG, "onSrAction handle fail. semantic.slots is null.")
                    break
                }
                val name: String = semantic.slots.name
                //true表示是开启操作,false表示未知操作
                val isOpen = isMatch(ConstantsVolume.OPT_OPENS, nlpVoiceModel.operation);
                //true表示关闭操作，false表示未知操作
                val isClose = !isOpen && isMatch(ConstantsVolume.OPT_CLOSES, nlpVoiceModel.operation);
                //TODO 目前语音无反馈 先屏蔽
                if (TextUtils.equals(ConstantsVolume.REFUEL_MODE, name)
                    || TextUtils.equals(ConstantsVolume.REFUEL_TEXT, name)) {
                    result = isOpen || isClose;
                } else if (TextUtils.equals(ConstantsVolume.OIL_SHROUD, name)
                    || TextUtils.equals(ConstantsVolume.OIL_SHROUD_TEXT, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.HOODS, name)) {
                    result = isOpen || isClose;
                } else if (isMatch(ConstantsVolume.TRUNKS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.WIPERS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.REAR_WIPERS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.TIRE_PRESSURE_MONITORS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.SMOKES, semantic.slots.mode)) {
                    result = isOpen || isClose;

                } else if (TextUtils.equals(ConstantsVolume.WIRELESS_CHARGING, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.IDLE_START_AND_STOP, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.AUTO_HEAD_LIGHTS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.LIGHTS, name)) {
                    result = isOpen || isClose;

                } else if (isMatch(ConstantsVolume.FOG_LIGHTS, name)) {
                    result = isOpen || isClose;

                } else if (TextUtils.equals(ConstantsVolume.MODE_DRIVE, semantic.slots.mode)) {
                    result = isOpen || isClose;

                } else if (TextUtils.equals(ConstantsVolume.WINDOW, name)) {
                    result = isOpen || isClose;
                    if (isOpen) {
                        val cmd = Cmd(Action.OPEN, Model.WINDOW, message = "打开天窗")
                        controller.doOuterControlCommand(cmd, callback)
                    }
                    if (isClose) {
                        val cmd = Cmd(Action.CLOSE, Model.WINDOW, message = "关闭天窗")
                        controller.doOuterControlCommand(cmd, callback)
                    }
                } else if (isMatch(ConstantsVolume.VOICE_VENT, name)) {
                    result = isOpen || isClose;
                }
            } catch (e: Exception) {
//                LogManager.e(TAG, " 语音解析异常 error:${e.message}")
            }
        } while (false)
        return result
    }

    fun doDispatchCmdAction(cmdVoiceModel: CmdVoiceModel) {

    }

}