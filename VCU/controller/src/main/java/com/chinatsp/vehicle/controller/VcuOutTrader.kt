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
import com.chinatsp.vehicle.controller.bean.BaseCmd
import com.chinatsp.vehicle.controller.semantic.CmdVoiceModel
import com.chinatsp.vehicle.controller.semantic.GsonUtil
import com.chinatsp.vehicle.controller.semantic.VoiceModel
import com.chinatsp.vehicle.controller.semantic.VoiceJson
import com.iflytek.autofly.voicecore.tts.TtsUtil
import org.json.JSONObject

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/18 10:01
 * @desc   :
 * @version: 1.0
 */
class VcuOutTrader private constructor() : ServiceConnection, Handler.Callback, IDataResolver {

    private lateinit var app: Application

    private val context: Context get() = app.baseContext.applicationContext

    private var controller: IOuterController? = null

    private val handler: Handler

    companion object {
        val instance: VcuOutTrader by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VcuOutTrader()
        }
        const val WHAT_SR_ACTION: Int = 0x11
        const val WHAT_MVW_ACTION: Int = 0x12
    }

    val TAG: String get() = "VehicleService"


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

    private fun onMvwAction(cmdVoiceModel: CmdVoiceModel?) {
        LogManager.d(TAG, "onMvwAction() called with: cmdVoiceModel = $cmdVoiceModel")
        val message = handler.obtainMessage(WHAT_MVW_ACTION)
        message.obj = cmdVoiceModel
        message.sendToTarget()
    }

    private fun onSrAction(nlpVoiceModel: VoiceModel?) {
        nlpVoiceModel?.let {
            val message = handler.obtainMessage(WHAT_SR_ACTION)
            message.obj = nlpVoiceModel
            message.sendToTarget()
        } ?: defaultHandleSpeech()
    }


    /**
     * ?????????????????? ???????????????????????????
     */
    private fun defaultHandleSpeech() {
        audioHintActionResult("mainC14", "", "?????????????????????????????????")
    }

    private fun audioHintActionResult(audioSerial: String, desc: String, msg: String = "") {
        val voiceName = Settings.System.getString(context.contentResolver, "aware") ?: ""
        val map: MutableMap<String, String> = HashMap()
        map["#VOICENAME#"] = voiceName
        LogManager.d(TAG, "audioResult  desc:$desc, msg:$msg")
        TtsUtil.ttsPlayAsynchronous(context, msg)
//        speechService.ttsSpeakListener(
//            shownIfly = false,
//            secondsr = false,
//            priority = 1,
//            conditionId = audioSerial,
//            data = map,
//            listener = ttsResultListener,
//            listener2 = ttsStatusListener
//        )
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        LogManager.d(TAG, "onServiceConnected name:${name?.packageName}, service:$service")
        service?.let {
            controller = IOuterController.Stub.asInterface(service)
            controller?.doBindDataResolver(this)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (null != controller) {
            controller = null
        }
    }


    inner class CmdHandleCallback : ICmdCallback.Stub() {
        override fun onCmdHandleResult(cmd: BaseCmd) {
            LogManager.d(TAG, "onCmdHandleResult $cmd")
            audioHintActionResult("audio",
                desc = (cmd.slots?.area ?: "desc is null") + ", text=" + cmd.slots?.text,
                msg = cmd.message)
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
        if (obj !is VoiceModel) {
            LogManager.e("", "obj is not NlpVoiceModel")
//            defaultHandleSpeech()
            return
        }
        var result = null != controller && !TextUtils.isEmpty(obj.service);
        if (!result) {
            LogManager.e("", "controller or service is invalid")
//            defaultHandleSpeech()
            return
        }
        result = CommandParser().doDispatchSrAction(obj, controller!!, CmdHandleCallback())
//        val array = ConditionerConstants.KT_USED
//        audioHintActionResult("", "",  array[Random.nextInt(array.size)])//?????????????????????????????????
        if (!result && ("airControl" == obj.service || "carControl" == obj.service || "vehicleInfo" == obj.service)) {
            LogManager.e("", "execute default audio hint")
            defaultHandleSpeech()
        }
    }

    override fun asBinder(): IBinder? {
        return null
    }

    override fun doResolverData(data: String) {
        try {
            var voiceJson: VoiceJson? = null
            try {
                val jsonObject = JSONObject(data)
                val jsonData = jsonObject.getString("intent")
                voiceJson = GsonUtil.stringToObject(jsonData, VoiceJson::class.java)
            } catch (e: Exception) {
                e.message
            }
            if (null == voiceJson) {
                voiceJson = GsonUtil.stringToObject(data, VoiceJson::class.java)
            }
            voiceJson?.answer?.let {
                LogManager.d("ReceiveVoice", "voiceJson-answer: $it")
            }
            voiceJson?.semantic?.slots?.let {
                LogManager.d("ReceiveVoice", "voiceJson-semantic-slots: $it")
            }
            val convert = voiceJson!!.convert()
            convert.slots.json = data
            onSrAction(convert)
        } catch (e: Exception) {
            e.printStackTrace()
            LogManager.e("ReceiveVoice", "parse error:${e.message}")
        }
    }

}