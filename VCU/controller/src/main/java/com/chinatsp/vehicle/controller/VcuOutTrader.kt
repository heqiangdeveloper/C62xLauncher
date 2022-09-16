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
import com.chinatsp.vehicle.controller.bean.Cmd
import com.chinatsp.vehicle.controller.logic.conditioner.ConditionerConstants
import com.chinatsp.vehicle.controller.semantic.CmdVoiceModel
import com.chinatsp.vehicle.controller.semantic.GsonUtil
import com.chinatsp.vehicle.controller.semantic.NlpVoiceModel
import org.json.JSONObject
import kotlin.random.Random

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

    private fun onSrAction(nlpVoiceModel: NlpVoiceModel?) {
        LogManager.d(TAG, "onSrAction() called with: nlpVoiceModel = $nlpVoiceModel")
        val message = handler.obtainMessage(WHAT_SR_ACTION)
        message.obj = nlpVoiceModel
        message.sendToTarget()
    }



    /**
     * 小欧不做处理 播报，防止小欧发呆
     */
    private fun defaultHandleSpeech() {
        audioHintActionResult("mainC14", "")
    }

    private fun audioHintActionResult(audioSerial: String, description: String) {
        val voiceName = Settings.System.getString(context.contentResolver, "aware")
        val map: MutableMap<String, String> = HashMap()
        map["#VOICENAME#"] = voiceName
        LogManager.d(
            TAG, "audioHintActionResult invoke voiceName:$voiceName, " +
                    "audioSerial:$audioSerial, description:$description"
        )
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
        override fun onCmdHandleResult(cmd: Cmd) {
            LogManager.d(TAG, "onCmdHandleResult $cmd")
            audioHintActionResult("yydsC10", cmd.message)
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
        var result = null != controller && !TextUtils.isEmpty(obj.service);
        if (!result) {
            defaultHandleSpeech()
            return
        }
        result = controller!!.isEngineStatus(context.packageName)
        val carSpeed = 120//获取系统车速
        if (!result) {
            LogManager.d(TAG, "发动机没有开启，请打开发动机！")
            audioHintActionResult("", "发动机没有开启，请打开发动机！")
            return
        }
        if(carSpeed>120){//车速大于120，不许开窗
            LogManager.d(TAG, "车速过快，建议不要开启天窗！")
            audioHintActionResult("", "车速过快，建议不要开启天窗！")
            return
        }
        result = CommandParser().doDispatchSrAction(obj, controller!!, CmdHandleCallback())
        val array = ConditionerConstants.KT_USED
        audioHintActionResult("", array[Random.nextInt(array.size)])//车机反馈应答，随机抽取
        if (!result) {
            defaultHandleSpeech()
        }
    }

    override fun asBinder(): IBinder? {
        return null
    }

    override fun doResolverData(data: String?) {
        data?.let {
            if (BuildConfig.DEBUG) {
                val jsonObject = JSONObject(data)
                val intentStr = jsonObject.getString("intent")
                val entity = GsonUtil.stringToObject(
                    intentStr,
                    com.chinatsp.vehicle.controller.semantic.Intent::class.java
                )
                onSrAction(entity.convert2NlpVoiceModel())
            } else {
                val entity = GsonUtil.stringToObject(
                    data,
                    com.chinatsp.vehicle.controller.semantic.Intent::class.java
                )
                onSrAction(entity.convert2NlpVoiceModel())
            }
        }
    }

}