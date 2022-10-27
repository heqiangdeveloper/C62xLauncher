package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.dialog.DialogMaster
import com.chinatsp.vehicle.settings.fragment.dialog.SystemAlertDialog
import com.common.xui.utils.SystemDialogHelper

class SystemService : Service(), SystemDialogHelper.OnCountDownListener {
    private val waitTime: Long = 1000 * 60 * 5//等待启动dialog时间
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val helper = SystemDialogHelper()
        helper?.timeSchedule(waitTime, this)
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

        val editDialog: SystemAlertDialog = dialogMaster.getDialog()
        editDialog.setDetailsContent(R.string.global_txt_close)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0 TYPE_APPLICATION_OVERLAY
            editDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            editDialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        editDialog.show()

    }
}