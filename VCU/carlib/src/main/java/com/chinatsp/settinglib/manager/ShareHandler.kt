package com.chinatsp.settinglib.manager

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.vehicle.controller.annotation.Action

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/11/6 10:50
 * @desc   :
 * @version: 1.0
 */
object ShareHandler {

    const val LOW_DELAY = 100
    const val MID_DELAY = 200
    const val HIG_DELAY = 300
    const val SEC_DELAY = 1000

    private val handler: Handler by lazy {
        val looperThread = HandlerThread("")
        looperThread.start()
        Handler(looperThread.looper, ParcelCallback())
    }

    fun loopParcel(@Action what: Int, param: Any, delayed: Int = Constant.INVALID) {
        handler.removeMessages(what)
        val message = handler.obtainMessage(what)
        message.obj = param
        if (Constant.INVALID == delayed) {
            handler.sendMessage(message)
            return
        }
        handler.sendMessageDelayed(message, delayed.toLong())
    }

    fun loopParcel(parcel: CommandParcel, delayed: Int = Constant.INVALID) {
        loopParcel(parcel.command.action, parcel, delayed)
    }

    fun dumpParcel(parcel: CommandParcel?) {
        if (null != parcel) {
            handler.removeMessages(parcel.command.action, parcel)
        }
    }

    class ParcelCallback : Handler.Callback {
        override fun handleMessage(message: Message): Boolean {
            val parcel = message.obj
            if (parcel is CommandParcel) {
                if (parcel.command.action == message.what) {
                    parcel.retryCount -= 1
                }
                parcel.receiver?.doCommandExpress(parcel, false)
            }
            return true
        }
    }
}