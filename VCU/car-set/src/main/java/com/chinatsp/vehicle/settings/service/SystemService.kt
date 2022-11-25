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
                removeMessage(Hint.TOAST_HINT_CLOSE_SCREEN)
                handleMessageDelay(type)
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
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onFinished() {

    }

    private fun updateHintContent(
        signal: Int, content: Int, cancelable: Boolean, careEngine: Boolean = false) {
        if (!careEngine || !VcuUtils.isEngineRunning()) {
            if (null == dialog) {
                val master = DialogMaster.create(applicationContext, { }, { }, 740, 488)
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
                }
            }
        } else if (signal == Hint.ALL_HINT) {
            dialog?.let {
                if (it.isShowing && !isVehicleModeHint(it.tag)) {
                    it.dismiss()
                }
            }
        } else if (signal == Hint.TOAST_HINT_CLOSE_SCREEN) {
            Toast.showToast(BaseApp.instance.applicationContext,
                ResUtils.getString(R.string.hint_low_voltage_close_screen), true)
        }
        return true
    }

    private fun isVehicleModeHint(signal: Int): Boolean {
        return Hint.transportMode == signal
                || Hint.exhibitionMode == signal
                || Hint.exhibitionModeError == signal
    }
}