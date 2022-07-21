package com.chinatsp.vehicle.controller

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import com.chinatsp.ifly.ISpeechTtsResultListener
import com.chinatsp.ifly.ISpeechTtsStatusListener
import com.chinatsp.ifly.aidlbean.CmdVoiceModel
import com.chinatsp.ifly.aidlbean.NlpVoiceModel
import com.chinatsp.ifly.voiceadapter.Business
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent
import com.chinatsp.ifly.voiceadapter.abs.SpeechControlListenerAbs
import com.chinatsp.vehicle.controller.bean.Cmd

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/18 10:01
 * @desc   :
 * @version: 1.0
 */
class VcuOutTrader private constructor() : ServiceConnection, Handler.Callback {

    private lateinit var app: Application

    private val context: Context get() = app.baseContext.applicationContext

    private var controller: IOuterController? = null

    private var bindVoiceStatus: Int = -1

    private val handler: Handler

    companion object {
        val instance: VcuOutTrader by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VcuOutTrader()
        }
        const val WHAT_SR_ACTION: Int = 0x11
        const val WHAT_MVW_ACTION: Int = 0x12
    }

    val TAG: String get() = "VehicleService"

    private val speechService: SpeechServiceAgent by lazy {
        SpeechServiceAgent.instance
    }

    init {
        val handleThread = HandlerThread("VcuOutTrader")
        handleThread.start()
        handler = Handler(handleThread.looper, this)
    }

    fun initApplication(application: Application) {
        this.app = application
    }

    fun bindServices() {
        onBindVcuService()
        onBindVoiceService()
    }

    private fun onBindVoiceService() {
        if (needBindVoice() && !isBindToVoice()) {
            speechService.setServiceConnect(VoiceConnectListener())
            speechService.initService(context, Business.CAR_CONTROL, mSpeechControlListener)
        }
    }

    private fun needBindVoice(): Boolean {
        return context.packageName.equals("com.chinatsp.vehicle.settings")
    }

    private fun onBindVcuService() {
        if (!isBindToVcu()) {
            val intent = Intent()
            intent.`package` = context.packageName
            val packageName = "com.chinatsp.vehicle.settings"
            val serviceName = "com.chinatsp.settinglib.service.VehicleService"
            intent.component = ComponentName(packageName, serviceName)
            val result: Boolean = context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    private fun isBindToVcu(): Boolean = null != controller

    private fun isBindToVoice(): Boolean = 1 == bindVoiceStatus

    private val mSpeechControlListener = object : SpeechControlListenerAbs() {

        override fun onMvwAction(cmdVoiceModel: CmdVoiceModel?) {
            LogManager.d(TAG, "onMvwAction() called with: cmdVoiceModel = $cmdVoiceModel")
            val message = handler.obtainMessage(WHAT_MVW_ACTION)
            message.obj = cmdVoiceModel
            message.sendToTarget()
        }

        override fun onSrAction(nlpVoiceModel: NlpVoiceModel?) {
            LogManager.d(TAG, "onSrAction() called with: nlpVoiceModel = $nlpVoiceModel")
            val message = handler.obtainMessage(WHAT_SR_ACTION)
            message.obj = nlpVoiceModel
            message.sendToTarget()
        }


    }

    /**
     * 小欧不做处理 播报，防止小欧发呆
     */
    fun defaultHandleSpeech() {
        val voiceName = Settings.System.getString(context.contentResolver, "aware")
        val map: MutableMap<String, String> = HashMap()
        map["#VOICENAME#"] = voiceName
        speechService.ttsSpeakListener(
            shownIfly = false,
            secondsr = false,
            priority = 1,
            conditionId = "mainC14",
            data = map,
            listener = ttsResultListener,
            listener2 = ttsStatusListener
        )
    }


    private val ttsResultListener = object : ISpeechTtsResultListener.Stub() {

        override fun onTtsCallback(ttsMessage: String?) {
            LogManager.d(TAG, "onTtsCallback ttsMessage:$ttsMessage")
        }

    }

    private val ttsStatusListener = object : ISpeechTtsStatusListener.Stub() {

        override fun onPlayStatusChanged(status: Int) {
            LogManager.d(TAG, "onPlayStatusChanged status:$status")
        }

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        LogManager.d(TAG, "onServiceConnected name:${name?.packageName}, service:$service")
        service?.let {
            controller = IOuterController.Stub.asInterface(service)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (null != controller) {
            controller = null
        }
    }

    inner class VoiceConnectListener : SpeechServiceAgent.ServiceConnect {
        override fun onConnectStatusChanged(connect: Int) {
            LogManager.d(TAG, "onConnectStatusChanged() called with: connect = $connect")
            bindVoiceStatus = connect
        }
    }

    inner class CmdHandleCallback : ICmdCallback.Stub() {
        override fun onCmdHandleResult(cmd: Cmd) {
            LogManager.d(TAG, "onCmdHandleResult $cmd")
        }

    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            WHAT_SR_ACTION -> {
                doHandleSrAction(msg.obj)
            }
            WHAT_MVW_ACTION -> {
                doHandleMvwAction(msg.obj)
            }
            else -> {}
        }
        return false
    }

    private fun doHandleMvwAction(obj: Any?) {
        if (obj is CmdVoiceModel) {
            CommandParser().doDispatchCmdAction(obj)
        }
    }

    private fun doHandleSrAction(obj: Any?) {
        if (obj !is NlpVoiceModel) {
            defaultHandleSpeech()
            return
        }
        val result = null != controller
                && !TextUtils.isEmpty(obj.service)
                && CommandParser().doDispatchSrAction(obj, controller!!, CmdHandleCallback())
        if (!result) {
            defaultHandleSpeech()
        }
    }

}