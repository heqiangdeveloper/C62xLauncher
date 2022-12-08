package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.Hint
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.RechargeToast
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.fragment.dialog.DialogMaster
import com.chinatsp.vehicle.settings.fragment.dialog.SystemAlertDialog
import com.common.xui.utils.ResUtils
import com.common.xui.utils.SystemDialogHelper
import timber.log.Timber

class SystemService : Service(), SystemDialogHelper.OnCountDownListener, Handler.Callback {
    //private val waitTime: Long = 1000 * 60 * 5//等待启动dialog时间
    private val minute: Long = 1000 * 60
    private var dialog: SystemAlertDialog? = null

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper(), this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun handleMessageDelay(what: Int, delay: Long = 200L) {
        handler.removeMessages(what)
        handler.sendEmptyMessageDelayed(what, delay)
    }

    private fun removeMessage(vararg whats: Int) {
        whats.forEach { handler.removeMessages(it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val type = intent.getIntExtra(Hint.type, Constant.INVALID)
            val action = intent.getIntExtra(Hint.action, Constant.INVALID)
            Timber.d("onStartCommand SystemService type:$type, action:$action")
            if (type == Hint.ON) {
                handleMessageDelay(type, 5 * minute)
            } else if (type == Hint.powerSupply) {
                handleMessageDelay(type)
            } else if (type == Hint.leve1) {
                removeMessage(Hint.leve2)
                handleMessageDelay(type)
                handleMessageDelay(Hint.TOAST_HINT_CLOSE_SCREEN, 15 * minute)
            } else if (type == Hint.leve2) {
                removeMessage(Hint.leve1)
                handleMessageDelay(type)
                handleMessageDelay(Hint.TOAST_HINT_CLOSE_SCREEN)
            } else if (type == Hint.transportMode) {
                /**运输模式*/
                handleMessageDelay(type)
            } else if (type == Hint.exhibitionMode) {
                /**展车模式*/
                handleMessageDelay(type)
            } else if (type == Hint.exhibitionModeError) {
                /**展车模式切换失败*/
                handleMessageDelay(type)
            } else if (type == Hint.default) {
                /**正常模式*/
                handleMessageDelay(type)
            } else if (type == Hint.ALL_HINT) {
                removeMessage(Hint.ON, Hint.leve1, Hint.leve2, Hint.powerSupply)
                handleMessageDelay(type, delay = 10)
            } else if (type == Hint.wirelessChargingNormal) {
                handleMessageDelay(type)
            } else if (type == Hint.wirelessChargingAbnormal) {
                handleMessageDelay(type)
            } else if (type == Hint.wirelessChargingMetal) {
                handleMessageDelay(type)
            } else if (type == Hint.wirelessChargingTemperature) {
                handleMessageDelay(type)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onFinished() {

    }

    private fun updateHintContent(
        signal: Int, content: Int, cancelable: Boolean, careEngine: Boolean = false,
    ) {
        if (!careEngine || !VcuUtils.isEngineRunning()) {
            if (null == dialog || !dialog!!.isShowing) {
                val master = DialogMaster.create(applicationContext, { }, { }, 740, 488, signal)
                dialog = master.dialog
            }
            val current = dialog!!
            current.setDetailsContent(content)
            current.setCancelable(cancelable)
            //6.0 TYPE_APPLICATION_OVERLAY    TYPE_STATUS_BAR_PANEL
            //editDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            current.window?.setType(2014)
            current.setOnDismissListener { }
            current.setOnShowListener { }
            current.show()
            current.tag = signal
            current.setIsConform(cancelable)
            current.window?.setLayout(740, 488)
            BaseApp.instance.sendBroadcast("com.chinatsp.vehicle.actions.VCU_DIALOG_DISPLAY")
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        val signal = msg.what
        var cancelable = true
        var content = R.string.global_txt_close
        if (signal == Hint.ON) {
            content = R.string.global_txt_close
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.powerSupply) {
            content = R.string.global_txt_power_supply
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.leve1) {
            content = R.string.global_txt_power_supply
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.leve2) {
            content = R.string.global_txt_power_supply
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.transportMode) {
            /**运输模式*/
            content = R.string.transport_mode
            cancelable = false
            updateHintContent(signal, content, cancelable)

        } else if (signal == Hint.exhibitionMode) {
            /**展车模式*/
            //Toast.showToast(applicationContext, getString(R.string.exhibition_mode), true)
            content = R.string.exhibition_mode
            updateHintContent(signal, content, cancelable)

        } else if (signal == Hint.exhibitionModeError) {
            /**展车模式切换失败*/
            //Toast.showToast(applicationContext, getString(R.string.exhibition_mode), true)
            content = R.string.exhibition_mode_error
            updateHintContent(signal, content, cancelable)

        } else if (signal == Hint.default) {
            /**正常模式*/
            dialog?.let {
                if (it.isShowing && isVehicleModeHint(it.tag)) {
                    it.dismiss()
                    dialog = null
                }
            }
        } else if (signal == Hint.ALL_HINT) {
            dialog?.let {
                if (it.isShowing && !isVehicleModeHint(it.tag)) {
                    it.dismiss()
                    dialog = null
                }
            }
        } else if (signal == Hint.TOAST_HINT_CLOSE_SCREEN) {
            Toast.showToast(
                BaseApp.instance.applicationContext,
                ResUtils.getString(R.string.hint_low_voltage_close_screen), true
            )
        } else if (signal == Hint.wirelessChargingNormal) {
            dismiss(signal, coreSame = false)
            /**充电正常*/
            RechargeToast.showToast(
                BaseApp.instance.applicationContext,
                ResUtils.getString(R.string.cabin_other_wireless_charging_working_properly),
                true)
        } else if (signal == Hint.wirelessChargingAbnormal) {
            dismiss(signal)
            /**充电异常*/
            content = R.string.cabin_other_maintenance
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.wirelessChargingMetal) {
            dismiss(signal)
            /**检测到金属异物，请移开异物*/
            content = R.string.cabin_other_foreign_matter
            updateHintContent(signal, content, cancelable)
        } else if (signal == Hint.wirelessChargingTemperature) {
            dismiss(signal)
            /**无线充电温度过高，请移开手机*/
            content = R.string.cabin_other_temperatire
            updateHintContent(signal, content, cancelable)
        }
        return true
    }

    private fun dismiss(serial: Int, coreSame: Boolean = true) {
        if (null != dialog && dialog!!.isShowing) {
            val current = dialog!!
            val isChargingHint = isChargingHint(current.tag)
            val isFilter = if (coreSame) isSameSerial(current.tag, serial) else false
            if (isChargingHint && !isFilter) {
                current.dismiss()
                dialog = null
            }
        }
    }

    private fun isVehicleModeHint(serial: Int): Boolean {
        return Hint.transportMode == serial
                || Hint.exhibitionMode == serial
                || Hint.exhibitionModeError == serial
    }

    private fun isChargingHint(serial: Int): Boolean {
        return Hint.wirelessChargingNormal == serial
                || Hint.wirelessChargingAbnormal == serial
                || Hint.wirelessChargingMetal == serial
                || Hint.wirelessChargingTemperature == serial
    }

    private fun isSameSerial(actual: Int, expect: Int) = actual == expect

}