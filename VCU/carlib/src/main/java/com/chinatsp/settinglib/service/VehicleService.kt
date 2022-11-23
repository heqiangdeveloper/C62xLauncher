package com.chinatsp.settinglib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.GlobalManager
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
            if ("com.chinatsp.vcu.actions.USER_SETTING_RECOVER" == action) {
                try {
                    val intentSeat = it.getStringExtra(seat)
                    val intentRearviewMirror = it.getStringExtra(rearviewMirror)
                    val intentAtmosphereLamp = it.getStringExtra(atmosphereLamp)
                    val intentSoundEffects = it.getStringExtra(soundEffects)
                    Timber.d(
                        " intentSeat:$intentSeat, intentRearviewMirror:$intentRearviewMirror, intentSoundEffects:$intentSoundEffects, intentAtmosphereLamp:%s",
                        intentAtmosphereLamp
                    )
                    if (intentSeat != null) {
                        //val jsonObject = JSONObject(intentSeat)
                        //val consult = jsonObject.optString("mainPassengerSeatPosition")
                        //主副驾座椅位置，暂无此功能
                    } else if (!TextUtils.isEmpty(intentRearviewMirror)) {
                        //val jsonObject = JSONObject(intentRearviewMirror)
                        //val consult = jsonObject.optString("rearviewMirror")
                        //外后视镜位置，暂无此功能
                    } else if (!TextUtils.isEmpty(intentAtmosphereLamp)) {
                        val jsonObject = JSONObject(intentAtmosphereLamp)
                        val color = jsonObject.optString("color")
                        val lighting = jsonObject.optString("lighting")
                        ambientLightingManager.doSetProgress(
                            Progress.AMBIENT_LIGHT_BRIGHTNESS,
                            Integer.valueOf(lighting)
                        )//亮度
                        ambientLightingManager.doSetProgress(
                            Progress.AMBIENT_LIGHT_COLOR,
                            Integer.valueOf(color)
                        )//颜色
                        Timber.d("color:$color,lighting:$lighting")
                    } else if (!TextUtils.isEmpty(intentSoundEffects)) {
                        //均衡器
                        val jsonObject = JSONObject(intentSoundEffects)
                        var value = jsonObject.optString("equalizerValue")
                        value = value.substring(0, value.length - 1)
                        value = value.substring(1, value.length)
                        val stringValue = value.split(",")
                        val map =
                            if (!VcuUtils.isAmplifier && stringValue.size != 9 && stringValue.size == 5) {
                                IntArray(9)
                            } else {
                                IntArray(stringValue.size)
                            }
                        for (i in stringValue.indices) {
                            val y = stringValue[i].toFloat()
                            map[i] = y.toInt()
                        }
                        if (!VcuUtils.isAmplifier && stringValue.size != 9 && stringValue.size == 5) {
                            map[5] = 0
                            map[6] = 0
                            map[7] = 0
                            map[8] = 0
                        }
                        effectManager.doSetEQ(
                            6,
                            map
                        )

                        //音量平衡
                        val fadeValue = jsonObject.optString("fadeValue")
                        val balanceValue = jsonObject.optString("balanceValue")
                        if (fadeValue != null && balanceValue != null) {
                            effectManager.setAudioBalance(
                                Integer.valueOf(balanceValue) + OFFSET,
                                Integer.valueOf(fadeValue) + OFFSET
                            )
                        }


                        //系统提示
                        //val systemHint = jsonObject.optString("systemHint");
                        //系统提示，暂无此功能

                        //速度音量补偿
                        val speedVolumeCompensation =
                            jsonObject.optString("speedVolumeCompensation")
                        if (speedVolumeCompensation != null) {
                            effectManager.doSetSwitchOption(
                                voiceManager.volumeSpeedSwitch,
                                speedVolumeCompensation == "true"
                            )
                        }


                        //响度控制
                        val loudnessControl = jsonObject.optString("loudnessControl")
                        if (loudnessControl != null) {
                            effectManager.doSetSwitchOption(
                                SwitchNode.AUDIO_SOUND_LOUDNESS,
                                loudnessControl == "true"
                            )
                        }

                        //导航混音  功能删除
                        /*val navigationMixing = jsonObject.optString("navigationMixing")
                        effectManager.doSetRadioOption(
                            RadioNode.NAVI_AUDIO_MIXING,
                            Integer.valueOf(navigationMixing)
                        )*/
                        Timber.d("equalizerValue:$value,fadeValue:$fadeValue,balanceValue:$balanceValue,speedVolumeCompensation:$speedVolumeCompensation,loudnessControl:$loudnessControl")
                    } else {
                        Timber.d("解析失败")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
            Timber.e("doParseSourceData $data")
            if (!TextUtils.isEmpty(data)) {
                resolver?.doResolverData(data)
            }
        }

    }

}

