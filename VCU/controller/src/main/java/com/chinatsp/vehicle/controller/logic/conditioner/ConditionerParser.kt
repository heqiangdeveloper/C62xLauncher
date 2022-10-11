package com.chinatsp.vehicle.controller.logic.conditioner

import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.IOuterController
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel

object ConditionerParser {
    val TAG: String get() = "ConditionerParser"
    fun isMatch(arrays: Array<String>, cmd: String?): Boolean {
        return arrays.contains(cmd)
    }

    fun doDispatchSrAction(
        nlpVoiceModel: NlpVoiceModel,
        controller: IOuterController,
        callback: ICmdCallback,
    ) {
//        val semantic: Semantic =
//            Gson().fromJson(nlpVoiceModel.semantic, Semantic::class.java)
//        val name: String = semantic.slots.name
//        //true表示是开启操作,false表示未知操作
//        val isOpen = isMatch(ConstantsVolume.OPT_OPENS, nlpVoiceModel.operation);
//        //true表示关闭操作，false表示未知操作
//        val isClose = !isOpen && isMatch(ConstantsVolume.OPT_CLOSES, nlpVoiceModel.operation);
//        if (isMatch(ConditionerConstants.KT_COMPRESSOR_OPEN, name) && isOpen) {//打开空调
//            val cmd =
//                Cmd(action = Action.OPEN, model = Model.ACCESS_WINDOW, message = "打开空调")
//            controller.doOuterControlCommand(cmd, callback)
//            LogManager.d(TAG, "execute open window!!!cmd：$cmd")
//        } else if (isMatch(ConditionerConstants.KT_COMPRESSOR_CLOSE, name) && isClose) {//关闭空调
//            val cmd = Cmd(Action.CLOSE, Model.ACCESS_WINDOW, message = "关闭空调")
//            controller.doOuterControlCommand(cmd, callback)
//        } else if (isMatch(ConditionerConstants.KT_WIND_BIG, name)) {//调大空调风力
//
//        } else if (isMatch(ConditionerConstants.KT_WIND_SMALL, name)) {//降低空调风力
//
//        }

    }
}