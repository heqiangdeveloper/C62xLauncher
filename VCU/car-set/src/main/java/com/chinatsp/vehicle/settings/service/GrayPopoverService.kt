package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.dialog.DialogMaster
import com.chinatsp.vehicle.settings.fragment.dialog.SystemAlertDialog

class GrayPopoverService : Service() {
    private var contentStr = R.string.global_txt_close
    private var isShowing = true
    private var cancelable = true//是否可以点击外面消失
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            contentStr = intent.getIntExtra("content", 0)
        }
        setDialogTime()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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
        //设置AlertDialog类型
        editDialog.window?.setType(2014)
        editDialog.setOnDismissListener { isShowing = true }
        editDialog.show()
        editDialog.setIsConform(cancelable)
        editDialog.window?.setLayout(740, 488)
        isShowing = false

    }

    private fun setDialogTime() {
        if (isShowing) updateHintMessage()
    }
}