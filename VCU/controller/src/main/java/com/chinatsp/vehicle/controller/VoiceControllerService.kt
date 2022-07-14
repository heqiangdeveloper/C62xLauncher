package com.chinatsp.vehicle.controller

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Button
import com.chinatsp.ifly.ISpeechTtsStatusListener
import com.chinatsp.ifly.aidlbean.CmdVoiceModel
import com.chinatsp.ifly.aidlbean.NlpVoiceModel
import com.chinatsp.ifly.voiceadapter.Business
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent
import com.chinatsp.ifly.voiceadapter.abs.SpeechControlListenerAbs
import com.chinatsp.settinglib.LogManager

class VoiceControllerService : Service(),  SpeechServiceAgent.ServiceConnect {

    val TAG: String get() = "VoiceControllerService"

    private val mSpeechServiceAgent: SpeechServiceAgent by lazy {
        SpeechServiceAgent.instance
    }



    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        mSpeechServiceAgent.setServiceConnect(this)
        mSpeechServiceAgent.initService(this, Business.SYSTEM_SETTING, mSpeechControlListener)
//        SpeechServiceAgent.instance.ttsSpeakListener(
//            conditionId = "理发店覅 及覅及覅",
//            listener2 = object : ISpeechTtsStatusListener.Stub() {
//                override fun onPlayStatusChanged(status: Int) {
//                    LogManager.d(TAG, "onPlayStatusChanged() called with: status = $status")
//                }
//
//            })

    }

    override fun onConnectStatusChanged(connect: Int) {
        LogManager.d(TAG, "onConnectStatusChanged() called with: connect = $connect")
    }

    private val mSpeechControlListener = object : SpeechControlListenerAbs(){

        override fun onMvwAction(cmdVoiceModel: CmdVoiceModel?) {
            LogManager.d(TAG, "onMvwAction() called with: cmdVoiceModel = $cmdVoiceModel")
        }

        override fun onSrAction(nlpVoiceModel: NlpVoiceModel?) {
            LogManager.d(TAG, "onSrAction() called with: nlpVoiceModel = $nlpVoiceModel")
        }
    }

}