package com.chinatsp.settinglib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.manager.lamp.AmbientLightingManager
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.IDataResolver
import com.chinatsp.vehicle.controller.IOuterController
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.CarCmd
import org.json.JSONObject
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/31 20:53
 * @desc   :
 * @version: 1.0
 */
class VehicleService : Service() {

    private val controller: OuterControllerImpl by lazy { OuterControllerImpl() }
    private val seat = "seat" //座椅调节
    private val rearviewMirror = "rearviewMirror" //电动外后视镜设置
    private val atmosphereLamp = "atmosphereLamp" //氛围灯控制
    private val soundEffects = "soundEffects" //音效设置
    private var OFFSET = 1
    private val seatManager: SeatManager by lazy {
        SeatManager.instance
    }
    private val ambientLightingManager: AmbientLightingManager
        get() = AmbientLightingManager.instance
    private val effectManager: EffectManager
        get() = EffectManager.instance
    private val voiceManager: VoiceManager by lazy { VoiceManager.instance }
    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val packageName = it.`package`
            Timber.d("onBind packageName:$packageName")
        }
        return controller
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val action = it.action
            val data = intent.getStringExtra("data")
            Timber.d("receive action:$action,data:$data")
            if ("com.chinatsp.vcu.actions.USER_SETTING_RECOVER" == action ) {
                val intentSeat = it.getStringExtra(seat)
                val intentRearviewMirror = it.getStringExtra(rearviewMirror)
                val intentAtmosphereLamp = it.getStringExtra(atmosphereLamp)
                val intentSoundEffects = it.getStringExtra(soundEffects)
                Timber.d(
                    " intentSeat:$intentSeat, intentRearviewMirror:$intentRearviewMirror, intentSoundEffects:$intentSoundEffects, intentAtmosphereLamp:%s",
                    intentAtmosphereLamp
                )
                if (intentSeat != null) {
                    val jsonObject = JSONObject(intentSeat)
                    //val consult = jsonObject.getString("mainPassengerSeatPosition")
                    //主副驾座椅位置，暂无此功能
                } else if (!TextUtils.isEmpty(intentRearviewMirror)) {
                    val jsonObject = JSONObject(intentRearviewMirror)
                    //val consult = jsonObject.getString("rearviewMirror")
                    //外后视镜位置，暂无此功能
                } else if (!TextUtils.isEmpty(intentAtmosphereLamp)) {
                    val jsonObject = JSONObject(intentAtmosphereLamp)
                    val color = jsonObject.getString("color")
                    val lighting = jsonObject.getString("lighting")
                    ambientLightingManager.doSetProgress(
                        Progress.AMBIENT_LIGHT_BRIGHTNESS,
                        Integer.valueOf(lighting) + 0x1
                    )//亮度
                    ambientLightingManager.doSetProgress(
                        Progress.AMBIENT_LIGHT_COLOR,
                        Integer.valueOf(color)
                    )//颜色
                    Timber.d("color:$color,lighting:$lighting")
                } else if (!TextUtils.isEmpty(intentSoundEffects)) {
                    //均衡器
                    val jsonObject = JSONObject(intentSoundEffects)
                    val equalizerValue =
                        jsonObject.getString("equalizerValue").toList().toMutableList()
                    if (equalizerValue.isNotEmpty()) {
                        if (!VcuUtils.isAmplifier && equalizerValue.size != 9 && equalizerValue.size == 5) {
                            equalizerValue.add(0.toChar())
                            equalizerValue.add(0.toChar())
                            equalizerValue.add(0.toChar())
                            equalizerValue.add(0.toChar())
                        }
                    }
                    val map = equalizerValue.map { it.code }.toIntArray()
                    effectManager.doSetEQ(
                        6,
                        map
                    )

                    //音量平衡
                    val fadeValue = jsonObject.getString("fadeValue")
                    val balanceValue = jsonObject.getString("balanceValue")
                    effectManager.setAudioBalance(
                        Integer.valueOf(balanceValue) + OFFSET,
                        Integer.valueOf(fadeValue) + OFFSET
                    )

                    //系统提示
                    val systemHint = jsonObject.getString("systemHint");
                    //系统提示，暂无此功能

                    //速度音量补偿
                    val speedVolumeCompensation = jsonObject.getString("speedVolumeCompensation")
                    effectManager.doSetSwitchOption(
                        voiceManager.volumeSpeedSwitch,
                        speedVolumeCompensation == "true"
                    )

                    //响度控制
                    val loudnessControl = jsonObject.getString("loudnessControl")
                    effectManager.doSetSwitchOption(
                        SwitchNode.AUDIO_SOUND_LOUDNESS,
                        loudnessControl == "true"
                    )

                    //导航混音  功能删除
                    /*val navigationMixing = jsonObject.getString("navigationMixing")
                    effectManager.doSetRadioOption(
                        RadioNode.NAVI_AUDIO_MIXING,
                        Integer.valueOf(navigationMixing)
                    )*/
                } else {
                    Timber.d("解析失败")
                }

            } else if ("com.chinatsp.vcu.actions.ACOUSTIC_CONTROLER" == action) {
                controller.doParseSourceData(data)
                try {
                    val intent = Intent("com.chinatsp.systemui.interface")
                    intent.setPackage("com.android.systemui")
                    intent.putExtra("operation", "voice")
                    intent.putExtra("semantics", data)
                    startService(intent)
//                    Log.i("operation", )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Timber.d("解析失败")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class OuterControllerImpl : IOuterController.Stub() {

        private var resolver: IDataResolver? = null

        override fun doBindDataResolver(resolver: IDataResolver?) {
            this.resolver = resolver
        }

        override fun doAirControlCommand(cmd: AirCmd, callback: ICmdCallback?) {
            GlobalManager.instance.doAirControlCommand(cmd, callback)
        }

        override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
            GlobalManager.instance.doCarControlCommand(cmd, callback)
        }

        override fun isEngineStatus(packageName: String?): Boolean {
            return Constant.ENGINE_STATUS
        }

        fun doParseSourceData(data: String?) {
            Timber.d("doParseSourceData $data")
            if (!TextUtils.isEmpty(data)) {
                resolver?.doResolverData(data)
            }
        }

    }

}

