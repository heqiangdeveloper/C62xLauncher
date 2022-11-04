package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.view.WindowManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.fragment.dialog.DialogMaster
import com.chinatsp.vehicle.settings.fragment.dialog.SystemAlertDialog
import com.common.xui.utils.SystemDialogHelper
import timber.log.Timber

class SystemService : Service(), SystemDialogHelper.OnCountDownListener {
    //private val waitTime: Long = 1000 * 60 * 5//等待启动dialog时间
    private var waitTime: Long = 1000 //等待启动dialog时间
    private var type: String = ""
    private var contentStr = R.string.global_txt_close
    private var isShowing = true
    private var cancelable = true//是否可以点击外面消失
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            type = intent.getStringExtra("type").toString()
        }
        Timber.d("onStartCommand  type:=============$type")
        if (!TextUtils.isEmpty(type) && type == "ON") {
            contentStr = R.string.global_txt_close
            waitTime = 1000 * 60 * 5
            setDialogTime()
            cancelable = true
        } else if (!TextUtils.isEmpty(type) && type == "powerSupply") {
            contentStr = R.string.global_txt_power_supply
            waitTime = 100
            setDialogTime()
            cancelable = true
        }else if (!TextUtils.isEmpty(type) && type == "leve1") {
            contentStr = R.string.global_txt_close
            waitTime = 1000 * 60 * 15
            setDialogTime()
            cancelable = true
        }else if (!TextUtils.isEmpty(type) && type == "leve2") {
            contentStr = R.string.global_txt_close
            waitTime = 200
            setDialogTime()
            cancelable = true
        }else if(!TextUtils.isEmpty(type) && type == "transportMode"){
            /**运输模式*/
            //Toast.showToast(applicationContext, getString(R.string.transport_mode), true)
            contentStr = R.string.transport_mode
            waitTime = 100
            setDialogTime()
            cancelable = false
        }else if(!TextUtils.isEmpty(type) && type == "exhibitionMode"){
            /**展车模式*/
            //Toast.showToast(applicationContext, getString(R.string.exhibition_mode), true)
            contentStr = R.string.exhibition_mode
            waitTime = 100
            setDialogTime()
            cancelable = true
        }else if(!TextUtils.isEmpty(type) && type == "exhibitionModeError"){
            /**展车模式切换失败*/
            //Toast.showToast(applicationContext, getString(R.string.exhibition_mode), true)
            contentStr = R.string.exhibition_mode_error
            waitTime = 100
            setDialogTime()
            cancelable = true
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onFinished() {
        updateHintMessage()
    }

    private fun updateHintMessage() {
        val dialogMaster: DialogMaster = DialogMaster.create(
            applicationContext,
            { },
            { }, 740, 488
        )
        val editDialog: SystemAlertDialog = dialogMaster.dialog
        editDialog.setDetailsContent(contentStr)
        editDialog.setCancelable(cancelable)
        //6.0 TYPE_APPLICATION_OVERLAY    TYPE_STATUS_BAR_PANEL
        //editDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        editDialog.window?.setType(2014)
        editDialog.setOnDismissListener { isShowing = true }
        editDialog.show()
        editDialog.setIsConform(cancelable)
        editDialog.window?.setLayout(740, 488)
        isShowing = false

    }
    private fun setDialogTime(){
        //if (isShowing) {//避免几个弹窗同时出现，已跟产品沟通，可以同时出现
        val helper = SystemDialogHelper()
        helper.timeSchedule(waitTime, this)
        // }
    }
}