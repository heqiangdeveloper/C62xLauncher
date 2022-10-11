package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.hvac.CarHvacManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.AirCmdParcel
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.annotation.Action
import kotlin.math.roundToInt

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 19:49
 * @desc   :
 * @version: 1.0
 */
class AirSupplier(private val airManager: ACManager): IAirMaster {

    private val handler:Handler by lazy {
        val looperThread = HandlerThread("")
        looperThread.start()
        Handler(looperThread.looper, MessageInvoke())
    }

    private val delayed: Int get() = 300

    private fun hvacValue(signal: Int): Int{
        return airManager.readIntProperty(signal, Origin.HVAC)
    }
    private fun cabinValue(signal: Int): Int{
        return airManager.readIntProperty(signal, Origin.CABIN)
    }

    /**
     * 获取空调风量显示
     * MPU向MCU发送设置按键音信息
     * 0x0到0x1E（0-30）， 音量等级：0到30
     */
    private fun getWindQuantity(): Int {
        return hvacValue(CarHvacManager.ID_HAVC_AC_DIS_BLOWER_LEVEL)
    }

    /**
     * 获取空调左侧温度显示 （暂当成空调温度）
     * 左温度显示。依据功能规范，低于17℃时显示Low，高于31℃时显示High。
     * 0x00 : No Temperature Display
     * 0x01~0xF : Reserved
     * 0x10:Low
     * 0x11 :17℃ ………… 0x1F : 31℃
     * 0x20 : High
     */
    private fun getTempQuantity(): Int {
        return hvacValue(CarHvacManager.ID_HAVC_AC_DIS_LEFT_TEMP)
    }


    /**
     * 获取空调是否开启
     * 空调工作状态指示（仅适用于C40D）
     * 0x0: OFF
     * 0x1: On
     */
    override fun isAirEngine(): Boolean {
        val value = hvacValue(CarHvacManager.ID_HAVC_AC_SYS_ON_OFF_STATE)
        return 0x1 == value
    }

    /**
     * 休息模式状态
     * 0x0: Reserved    0x1: On   0x2: Off   0x3: Invalid
     */
    fun isRestModeStatus(): Boolean {
//        val value = cabinValue(CarHvacManager.ID_HAVC_AC_REST_MOD_STS)
//        return 0x1 == value
        return false
    }

    /**
     * 获取车辆发动机状态
     */
    fun isCarEngine(): Boolean {
        return true
    }

    override fun doStartAirEngine(parcel: AirCmdParcel): Boolean {
        val cmd = parcel.cmd
        val isCareCmd = Action.OPEN == cmd.action
        if (isAirEngine()) {
            //空调已开启
            if (isCareCmd) {
                //在空调已经开启的状态下 试图两次打开空调
                if (parcel.isRetry()) {
                    cmd.message = "空调已开启"
                } else {
                    //空调成功被打开
                }
                parcel.callback?.onCmdHandleResult(cmd)
            }
            return true
        }
        if (parcel.isRetry()) {
            handleAirSwitch(true)
            if (isCareCmd) {
                sendAirMessage(parcel.cmd.action, parcel, delayed)
                cmd.message = "好的，命令已执行"
                parcel.callback?.onCmdHandleResult(cmd)
            }
        } else {
            if (isCareCmd) {
                cmd.message = "命令执行失败了！"
                parcel.callback?.onCmdHandleResult(cmd)
            }
        }
        return false
    }

    override fun doCeaseAirEngine(airCmdParcel: AirCmdParcel) {
        if (!isAirEngine()) {
            //空调已关闭
            return
        }
        handleAirSwitch(false)
//        doAirControlCommand(cmd, callback, needLoop = false)
        return
    }

    override fun doAdjustAirDirection(parcel: AirCmdParcel) {
        val isAirEngine = doStartAirEngine(parcel)
        if (isAirEngine) {

        } else {
            sendAirMessage(parcel.cmd.action, parcel, delayed)
        }
    }

    override fun doAdjustAirWindSpeed(parcel: AirCmdParcel) {

    }

    override fun doAdjustAirTemperature(parcel: AirCmdParcel) {

    }

    private fun sendAirMessage(@Action what: Int, param: Any, delayed: Int = Constant.INVALID) {
        handler.removeMessages(what)
        val message = handler.obtainMessage(what)
        message.obj = param
        if (Constant.INVALID == delayed) {
            handler.sendMessage(message)
            return
        }
        handler.sendMessageDelayed(message, delayed.toLong())
    }

    inner class MessageInvoke: Handler.Callback{
        override fun handleMessage(message: Message): Boolean {
            val obj = message.obj
            if (obj is AirCmdParcel) {
                if (obj.cmd.action == message.what) {
                    obj.retryCount -= 1
                }
                doAirControlCommand(obj)
            }
            return true
        }

    }

    private fun handleAirSwitch(status: Boolean) {
        val launch = isAirEngine()
        if (launch xor status) {
//        压缩机if not set ,the value of signal is 0x0(inactive)
//        0x0: Inactive; 0x1: ON; 0x2: OFF; 0x3: Not used
            val value = if (status) 0x1 else 0x2
            airManager.writeProperty(CarHvacManager.ID_HVAC_AVN_KEY_AC, value, Origin.HVAC)
        }
    }

    fun doAirControlCommand(parcel: AirCmdParcel) {
        when (parcel.cmd.action) {
            Action.OPEN -> {
                doStartAirEngine(parcel)
            }
            Action.CLOSE -> {
                doCeaseAirEngine(parcel)
            }
            Action.PLUS -> {
                tryPlusQuantity(parcel)
            }
            Action.MINUS -> {
                tryMinusQuantity(parcel)
            }
            Action.MIN -> {
                tryMinQuantity(parcel)
            }
            Action.MAX -> {
                tryMaxQuantity(parcel)
            }
            Action.FIXED -> {
                tryFixedQuantity(parcel)
            }
            Action.OPTION -> {

            }
            else -> {}
        }
    }

    private fun tryFixedQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        if (cmd.wind xor cmd.temp) {
            if (cmd.wind) {
                tryOperationWindQuantity(parcel)
            }
            if (cmd.temp) {
                tryOperationTempQuantity(parcel)
            }
        }
    }

    private fun tryMaxQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        if (cmd.wind xor cmd.temp) {
            if (cmd.wind) {
                tryOperationWindQuantity(parcel)
            }
            if (cmd.temp) {
                tryOperationTempQuantity(parcel)
            }
        }
    }

    private fun tryMinQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        if (cmd.wind xor cmd.temp) {
            if (cmd.wind) {
                tryOperationWindQuantity(parcel)
            }
            if (cmd.temp) {
                tryOperationTempQuantity(parcel)
            }
        }
    }

    private fun tryMinusQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        if (cmd.wind xor cmd.temp) {
            if (cmd.wind) {
                tryOperationWindQuantity(parcel)
            }
            if (cmd.temp) {
                tryOperationTempQuantity(parcel)
            }
        }
    }

    private fun tryPlusQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        if (cmd.wind xor cmd.temp) {
            if (cmd.wind) {
                tryOperationWindQuantity(parcel)
            }
            if (cmd.temp) {
                tryOperationTempQuantity(parcel)
            }
        }
    }

    /**
     * 左温度。依据功能规范，低于17℃时显示Low，高于31℃时显示High。
    if not set ,the value of signal is 0x0(inactive)
    0x00:Inactive
    0x01 : No Temperature Display
    0x02~0x0F : Reserved
    0x10:Low
    0x11 :17℃
    0x12 :18℃
    0x13 : 19℃
    0x14 : 20℃
    0x15 : 21℃
    0x16 : 22℃
    0x17 : 23℃
    0x18 : 24℃
    0x19 : 25℃
    0x1A : 26℃
    0x1B : 27℃
    0x1C : 28℃
    0x1D : 29℃
    0x1E : 30℃
    0x1F : 31℃
    0x20 : High
     */
    private fun tryOperationTempQuantity(parcel: AirCmdParcel) {
        val cmd = parcel.cmd
        val min = 0x10
        val max = 0x20
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val current = getTempQuantity()
            assert(current in 0x10..0x20)
            expect = current + cmd.step
        } else if (Action.MINUS == cmd.action) {
            val current = getWindQuantity()
            expect = current - cmd.step
        } else if (Action.FIXED == cmd.action) {
            expect = cmd.value
        } else if (Action.MIN == cmd.action) {
            expect = min
        } else if (Action.MAX == cmd.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        airManager.writeProperty(CarHvacManager.ID_HVAC_AVN_KEY_TEMP_LEFT, expect, Origin.HVAC)
        cmd.message = "已试图调温度到$expect"
        parcel.callback?.onCmdHandleResult(cmd)
    }

    private fun tryOperationWindQuantity(parcel: AirCmdParcel) {
//        风量 if not set ,the value of signal is 0x0(inactive)
//        0x0: Inactive
//        0x1: Level 0  …………  0x9: Level 8
//        0xA: Reserved ………… 0xE: Reserved
//        0xF: Error
        val cmd = parcel.cmd
        val min = 0x1
        val max = 0x9
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val current = getWindQuantity()
            expect = if (cmd.graded) {
                val actual = ((current * max).toFloat() / 0x1E).roundToInt()
                actual + cmd.step
            } else {
                val actual = (((current + cmd.step) * max).toFloat() / 0x1E).roundToInt()
                actual
            }
        } else if (Action.MINUS == cmd.action) {
            val current = getWindQuantity()
            expect = if (cmd.graded) {
                val actual = ((current * max).toFloat() / 0x1E).roundToInt()
                actual - cmd.step
            } else {
                val actual = (((current - cmd.step) * max).toFloat() / 0x1E).roundToInt()
                actual
            }
        } else if (Action.FIXED == cmd.action) {
            expect = if (cmd.graded) {
                cmd.value
            } else {
                ((cmd.value * max).toFloat() / 0x1E).roundToInt()
            }
        } else if (Action.MIN == cmd.action) {
            expect = min
        } else if (Action.MAX == cmd.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        airManager.writeProperty(CarHvacManager.ID_HVAC_AVN_KEY_BLOWER, expect, Origin.HVAC)
        cmd.message = "已试图调风量到$expect"
        parcel.callback?.onCmdHandleResult(cmd)
    }


}