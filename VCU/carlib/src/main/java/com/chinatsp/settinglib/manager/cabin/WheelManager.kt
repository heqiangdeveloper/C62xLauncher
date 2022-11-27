package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.content.ComponentName
import android.content.Intent
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ShareHandler
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class WheelManager private constructor() : BaseManager(), ISoundManager, ICmdExpress {

    private val swhFunction: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
        return@lazy createAtomicBoolean(node, Constant.STEERING_HEAT_SWITCH) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val epsMode: RadioState by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
        return@lazy createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, this::doOptionChanged)
        }
    }

    private val steeringSillTemp: Volume by lazy {
        initVolume(Progress.STEERING_ONSET_TEMPERATURE)
    }


    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.DRIVE_WHEEL_AUTO_HEAT.get.signal)
                add(RadioNode.DRIVE_EPS_MODE.get.signal)
                add(CarCabinManager.ID_SWS_KEY_USER_DEFINED)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private fun initVolume(type: Progress): Volume {
        val result = VcuUtils.getInt(key = Constant.STEERING_HEAT_TEMP, value = type.def)
        //        AppExecutors.get()?.singleIO()?.execute {
//            val result = VcuUtils.getInt(key = Constant.STEERING_HEAT_TEMP, value = pos)
//            doUpdateProgress(volume, result, true, instance::doProgressChanged)
//        }
        return Volume(type, type.min, type.max, result)
    }


    companion object : ISignal {

        override val TAG: String = WheelManager::class.java.simpleName

        val instance: WheelManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WheelManager()
        }

    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> epsMode.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> {
                writeProperty(node, value, epsMode)
            }
            else -> false
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> swhFunction.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> {
                val result = writeProperty(node, status, swhFunction)
                if (result) {
                    val value = node.value(status, isGet = true)
                    VcuUtils.putInt(key = Constant.STEERING_HEAT_SWITCH, value = value)
                    doUpdateSwitchValue(node, swhFunction, status, this::doSwitchChanged)
                }
                return result
            }
            else -> false
        }
    }

    override fun doGetVolume(progress: Progress): Volume? {
        return when (progress) {
            Progress.STEERING_ONSET_TEMPERATURE -> {
                steeringSillTemp
            }
            else -> null
        }
    }

    override fun doSetVolume(progress: Progress, position: Int): Boolean {
        return when (progress) {
            Progress.STEERING_ONSET_TEMPERATURE -> {
                val result = writeProperty(steeringSillTemp, position + 1)
                if (result) {
                    VcuUtils.putInt(key = Constant.STEERING_HEAT_TEMP, value = position)
                    steeringSillTemp.pos = position
                }
                return result
            }
            else -> false
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, swhFunction, property)
            }
            RadioNode.DRIVE_EPS_MODE.get.signal -> {
                onRadioChanged(RadioNode.DRIVE_EPS_MODE, epsMode, property)
            }
            CarCabinManager.ID_SWS_KEY_USER_DEFINED -> {
                doHandleCustomKeyboard(property)
            }
            else -> {}
        }
    }

    private fun doHandleCustomKeyboard(property: CarPropertyValue<*>) {
        val value = property.value
        if (value !is Int) {
            return
        }
        if (0x2 == value) {
            val intent = Intent(Constant.VCU_CUSTOM_KEYPAD)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            BaseApp.instance.startActivity(intent)
        } else if (0x1 == value) {
            val mode = VcuUtils.getInt(key = Constant.CUSTOM_KEYPAD, value = Constant.PRIVACY_MODE)
            Timber.d("execute custom keypad!!mode:$mode")
            when (mode) {
                Constant.NAVIGATION -> launchNavigation()
                Constant.PRIVACY_MODE -> switchPrivacyMode()
                Constant.TURN_OFF_SCREEN -> switchScreen()
            }
        }
    }

    private fun switchScreen() {
        val intent = Intent("com.chinatsp.START_STANDBY")
        intent.putExtra("OP_SCREEN", "OFF")
        intent.setPackage("com.chinatsp.settings")
        BaseApp.instance.startService(intent)
    }


    private fun switchPrivacyMode() {
        /**
         * Settings.System.getInt(mContext.getContentResolver(), KEY_INT_PRIVACY_MODE, OFF)
         * 1、打开  2、关闭
         */
        val key = "com.chinatsp.systemui.KEY_PRIVACY_MODE"
        val on = 0x1
        val off = 0x2
        val actual = VcuUtils.getInt(key = key, value = off, system = true)
        val expect = if (actual == on) off else on
        Timber.d("switchPrivacyMode actual:$actual, expect:$expect")
        VcuUtils.putInt(key = key, value = expect, system = true)
    }

    private fun launchNavigation() {
        try {
            val component = ComponentName("com.autonavi.amapauto",
                "com.autonavi.amapauto.MainMapActivity")
            val intent = Intent()
            intent.component = component
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            BaseApp.instance.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("launchNavigation exception:${e.message}")
        }
    }

    private fun writeProperty(volume: Volume, value: Int): Boolean {
        return writeProperty(volume.type.set.signal, value, Origin.CABIN)
    }

    private fun writeProperty(node: SwitchNode, status: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, status) { _node, _status ->
                doSwitchChanged(_node, _status)
            }
        }
        return success
    }

    private fun writeProperty(node: RadioNode, value: Int, atomic: RadioState): Boolean {
        val success = node.isValid(value, false)
                && writeProperty(node.set.signal, value, node.set.origin)
        if (success && develop) {
            val newValue = node.obtainSelectValue(value, false)
            doUpdateRadioValue(node, atomic, newValue) { _node, _value ->
                doOptionChanged(_node, _value)
            }
        }
        return success
    }

    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        if (ICar.WHEEL == command.car) {
            if (IStatus.INIT == command.status) {
                if (!VcuUtils.isSupport(OffLine.WHEEL_HEAT, 0x1)) {
                    command.message = "您的爱车暂时不支持方向盘加热功能"
                    callback?.onCmdHandleResult(command)
                    return
                }
                parcel.retryCount = 3
                command.resetSent(IPart.L_F)
            }
            val expect = Action.TURN_ON == command.action
            val heating = readIntProperty(CarCabinManager.ID_SWH_STATUS_KEY, Origin.CABIN)
//            0x0:not heating 0x1:heating
            val actual = 0x1 == heating
            val isNeedSendSignal = expect xor actual
            Timber.d("control wheel heat heating:$heating, actual:$actual, expect:$expect, isNeedSendSignal:$isNeedSendSignal")
            if (isNeedSendSignal && !command.isSent(IPart.L_F)) {
//                方向盘加热设置: 0x0: Inactive;  0x1: On;  0x2: Off; 0x3: Reserved
                val value = if (expect) 0x1 else 0x2
                writeProperty(CarCabinManager.ID_SWS_HEAT_SWT, value, Origin.CABIN)
                command.sent(IPart.L_F)
                command.status = IStatus.RUNNING
            }
            val isFinish = !isNeedSendSignal || !parcel.isRetry()
            if (!isFinish) {
                ShareHandler.loopParcel(parcel, ShareHandler.MID_DELAY)
                return
            }
            if (isNeedSendSignal) {
                command.message = Keywords.COMMAND_FAILED
            } else {
                val isSend = command.isSent(IPart.L_F)
                command.message = "方向盘加热${if (isSend) "" else "已经"}${if (expect) "打开" else "关闭"}了"
            }
            callback?.onCmdHandleResult(command)
        }
    }
}