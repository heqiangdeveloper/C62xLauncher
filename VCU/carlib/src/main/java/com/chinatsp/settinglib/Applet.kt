package com.chinatsp.settinglib

import android.car.hardware.cabin.CarCabinManager
import android.content.Intent
import android.graphics.Color
import com.chinatsp.settinglib.SettingManager.Companion.context
import com.chinatsp.settinglib.bean.ValueBean
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import kotlin.math.roundToInt


/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/8/9 11:33
 * @desc   :
 * @version: 1.0
 */
object Applet {

    val avmValueBean: ValueBean by lazy {
        val signal = CarCabinManager.ID_AVM_AVM_DISP_REQ
//        val signal = CarCabinManager.ID_VCS_KEY_AVM
        val value = GlobalManager.instance.readIntProperty(signal, Origin.CABIN)
        val valueBean = ValueBean()
        valueBean.setValue(value)
        notifyPanoramaStatus(VcuUtils.isAvmEngine(value), "byLazy")
        valueBean
    }

    fun updateAvmDisplay(value: Int, valueBean: ValueBean = avmValueBean) {
        val last = VcuUtils.isAvmEngine(valueBean.getValue())
        val next = VcuUtils.isAvmEngine(value)
        valueBean.setValue(value);
        if (last xor next) {
            notifyPanoramaStatus(next, "update")
        }
    }

    private fun notifyPanoramaStatus(status: Boolean, serial: String) {
        AppExecutors.get()?.networkIO()?.execute {
            val data =
                "{\"data\": {},\"activeStatus\": \"${if (status) "fg" else "bg"}\",\"sceneStatus\":\"default\",\"service\": \"carControl\",\"scene\": \"carControl\",\"default\": \"360\"}"
            Timber.d("send 360 state serial: $serial, json:$data")
            val intent: Intent = Intent()
                .setAction("com.iflytek.autofly.business.response")
                .setPackage("com.iflytek.autofly.voicecoreservice")
            intent.putExtra("data", data)
            context.startService(intent)
        }
    }

    private fun speedValue(): Float {
        /***
         * 获取车速
         * Vehicle speed calculated by EMS according to the message WHEEL SPEED from ESP.km/h 系数 0.1
         */
        val actual: Float = WheelManager.instance.readFloatProperty(
            CarCabinManager.ID_VEHICLE_SPEED_VALUE,
            Origin.CABIN
        )
        val speed = if (actual > 0 && actual <= 26f) {
            actual * 1.15f
        } else if (actual > 26f && actual <= 300f) {
            actual + 4
        } else {
            actual
        }
        Timber.d("Applet speed actual:$actual, speed:$speed")
        return speed.roundToInt().toFloat()
    }

    //    语音控制车窗
//    0:不支持(default)
//    1:仅主驾
//    2:四门
    val VOICE_CONTROL_WIN_SUPPORT: Int by lazy {
        VcuUtils.getConfigParameters(OffLine.VOICE_CONTROL_WIN_SUPPORT, 0x0)
    }

    fun isBelowCareSpeed(consult: Float): Boolean {
        val speed = speedValue()
        val result = speed < consult
        Timber.d("Applet isBelowSafeSpeed speed:$speed, consult:$consult, result:$result")
        return result
    }

    fun getLampSupportColor(): List<Color> {
        val colors = ArrayList<Color>(64)
        colors.add(Color.valueOf(255f, 1f, 1f))
        colors.add(Color.valueOf(255f, 14f, 1f))
        colors.add(Color.valueOf(255f, 26f, 1f))
        colors.add(Color.valueOf(255f, 38f, 1f))
        colors.add(Color.valueOf(255f, 51f, 1f))
        colors.add(Color.valueOf(255f, 63f, 1f))
        colors.add(Color.valueOf(255f, 74f, 1f))
        colors.add(Color.valueOf(255f, 87f, 1f))
        colors.add(Color.valueOf(255f, 100f, 1f))
        colors.add(Color.valueOf(255f, 118f, 1f))
        colors.add(Color.valueOf(255f, 138f, 1f))
        colors.add(Color.valueOf(255f, 157f, 1f))
        colors.add(Color.valueOf(255f, 177f, 1f))
        colors.add(Color.valueOf(255f, 197f, 1f))
        colors.add(Color.valueOf(255f, 217f, 1f))
        colors.add(Color.valueOf(255f, 236f, 5f))
        colors.add(Color.valueOf(255f, 239f, 45f))
        colors.add(Color.valueOf(255f, 243f, 83f))
        colors.add(Color.valueOf(255f, 245f, 122f))
        colors.add(Color.valueOf(255f, 247f, 161f))
        colors.add(Color.valueOf(255f, 241f, 200f))
        colors.add(Color.valueOf(255f, 254f, 240f))
        colors.add(Color.valueOf(235f, 249f, 246f))
        colors.add(Color.valueOf(201f, 240f, 232f))
        colors.add(Color.valueOf(200f, 240f, 231f))
        colors.add(Color.valueOf(134f, 222f, 203f))
        colors.add(Color.valueOf(100f, 213f, 190f))
        colors.add(Color.valueOf(66f, 204f, 175f))
        colors.add(Color.valueOf(33f, 195f, 161f))
        colors.add(Color.valueOf(0f, 186f, 147f))
        colors.add(Color.valueOf(5f, 189f, 142f))
        colors.add(Color.valueOf(8f, 192f, 136f))
        colors.add(Color.valueOf(13f, 195f, 131f))
        colors.add(Color.valueOf(17f, 198f, 126f))
        colors.add(Color.valueOf(21f, 201f, 120f))
        colors.add(Color.valueOf(26f, 205f, 117f))
        colors.add(Color.valueOf(28f, 209f, 138f))
        colors.add(Color.valueOf(30f, 215f, 159f))
        colors.add(Color.valueOf(32f, 219f, 180f))
        colors.add(Color.valueOf(34f, 224f, 201f))
        colors.add(Color.valueOf(36f, 229f, 222f))
        colors.add(Color.valueOf(39f, 234f, 243f))
        colors.add(Color.valueOf(38f, 227f, 254f))
        colors.add(Color.valueOf(33f, 205f, 253f))
        colors.add(Color.valueOf(28f, 183f, 253f))
        colors.add(Color.valueOf(23f, 161f, 251f))
        colors.add(Color.valueOf(18f, 139f, 250f))
        colors.add(Color.valueOf(13f, 116f, 248f))
        colors.add(Color.valueOf(8f, 94f, 247f))
        colors.add(Color.valueOf(21f, 89f, 248f))
        colors.add(Color.valueOf(37f, 87f, 249f))
        colors.add(Color.valueOf(55f, 86f, 250f))
        colors.add(Color.valueOf(72f, 84f, 252f))
        colors.add(Color.valueOf(88f, 83f, 253f))
        colors.add(Color.valueOf(105f, 81f, 254f))
        colors.add(Color.valueOf(122f, 79f, 254f))
        colors.add(Color.valueOf(138f, 72f, 249f))
        colors.add(Color.valueOf(155f, 66f, 244f))
        colors.add(Color.valueOf(172f, 60f, 239f))
        colors.add(Color.valueOf(189f, 53f, 234f))
        colors.add(Color.valueOf(206f, 47f, 229f))
        colors.add(Color.valueOf(222f, 40f, 223f))
        colors.add(Color.valueOf(238f, 33f, 218f))
        colors.add(Color.valueOf(255f, 27f, 213f))
        return colors
    }
}