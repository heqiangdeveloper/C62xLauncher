package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.view.WindowManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.dialog.DialogMaster
import com.chinatsp.vehicle.settings.fragment.dialog.SystemAlertDialog
import com.common.xui.utils.SystemDialogHelper

class SystemService : Service(), SystemDialogHelper.OnCountDownListener {
    //private val waitTime: Long = 1000 * 60 * 5//等待启动dialog时间
    private var waitTime: Long = 1000 //等待启动dialog时间
    private var type: String = ""
    private var contentStr = R.string.global_txt_close
    private var isShowing = true
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            type = intent.getStringExtra("type").toString()
        }
        if (!TextUtils.isEmpty(type) && type == "ON") {
            contentStr = R.string.global_txt_close
            waitTime = 1000 * 60 * 5
        } else if (!TextUtils.isEmpty(type) && type == "powerSupply") {
            contentStr = R.string.global_txt_power_supply
            waitTime = 100
        }else if (!TextUtils.isEmpty(type) && type == "leve1") {
            contentStr = R.string.global_txt_close
            waitTime = 1000 * 60 * 15
        }else if (!TextUtils.isEmpty(type) && type == "leve2") {
            contentStr = R.string.global_txt_close
            waitTime = 200
        }
        //if (isShowing) {//避免几个弹窗同时出现，已跟产品沟通，可以同时出现
            val helper = SystemDialogHelper()
            helper.timeSchedule(waitTime, this)
       // }
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
        //6.0 TYPE_APPLICATION_OVERLAY
        editDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        editDialog.setOnDismissListener { isShowing = true }
        editDialog.show()
        editDialog.window?.setLayout(740, 488)
        isShowing = false

    }
}