package com.chinatsp.vehicle.settings.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.fragment.GlobalDialogFragment
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.common.xui.utils.SystemDialogHelper

class SystemService : Service(),SystemDialogHelper.OnCountDownListener{
    private val waitTime:Long = 5000//等待启动dialog时间
    override fun onBind(p0: Intent?): IBinder? {
      return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val helper:SystemDialogHelper
        /*helper.timeSchedule(waitTime,this)
        //supportFragmentManager
        helper.*/
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onFinished() {
      updateHintMessage(R.string.global_txt_close)
    }

    private fun updateHintMessage( content: Int) {
        HintHold.setContent(content)
        val fragment = GlobalDialogFragment()




    }
}