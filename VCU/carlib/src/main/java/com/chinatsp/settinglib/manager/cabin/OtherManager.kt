package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.Hint
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class OtherManager private constructor() : BaseManager(), IOptionManager {

    /**
     * 拖车提醒开关（此信号走TBox 协议而非CAN信号，所以需要特殊处理）
     */
    private val trailerRemind: SwitchState by lazy {
        val node = SwitchNode.DRIVE_TRAILER_REMIND
        SwitchState(node.default).apply {
            val value = SettingManager.instance.getTrailerSwitch()
            val result = if (null != value) node.isOn(value) else node.default
            doUpdateSwitchValue(node, this, result)
        }
    }

    private val batteryOptimize: SwitchState by lazy {
        val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val wirelessCharging: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val wirelessChargingLamp: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val sensitivity: RadioState by lazy {
        val node = RadioNode.DEVICE_TRAILER_SENSITIVITY
        RadioState(node.def).apply {
            val value = SettingManager.instance.getTrailerLevel()
            val result = value ?: node.def
            doUpdateRadioValue(node, this, result)
        }
    }

    private val distance: RadioState by lazy {
        val node = RadioNode.DEVICE_TRAILER_DISTANCE
        RadioState(node.def).apply {
            val value = SettingManager.instance.getTrailerDist()
            val result = value ?: node.def
            doUpdateRadioValue(node, this, result)
        }
    }

    private val wirelessChargingState: RadioState by lazy {
        val node = RadioNode.WIRELESS_CHARGING_STATE
        createAtomicInteger(node) { result, value ->
            doUpdateRadioValue(node, result, value, null)
        }
    }

    companion object : ISignal {
        override val TAG: String = OtherManager::class.java.simpleName
        val instance: OtherManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OtherManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.DRIVE_TRAILER_REMIND.get.signal)
                add(SwitchNode.DRIVE_BATTERY_OPTIMIZE.get.signal)
                add(SwitchNode.DRIVE_WIRELESS_CHARGING.get.signal)
                add(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP.get.signal)
                add(RadioNode.WIRELESS_CHARGING_STATE.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DRIVE_TRAILER_REMIND -> trailerRemind.deepCopy()
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> batteryOptimize.deepCopy()
            SwitchNode.DRIVE_WIRELESS_CHARGING -> wirelessCharging.deepCopy()
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> wirelessChargingLamp.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_TRAILER_REMIND -> {
                val result = SettingManager.instance.setTrailerRemind(node.value(status))
                if (result) {
                    trailerRemind.set(status)
                    doSwitchChanged(node, trailerRemind)
                }
                result
            }
            else -> false
        }
    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> distance.deepCopy()
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> sensitivity.deepCopy()
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        val result = when (node) {
            RadioNode.DEVICE_TRAILER_DISTANCE -> {
                val result = SettingManager.instance.setTrailerDistance(value)
                if (result) {
                    distance.set(value)
                    doOptionChanged(node, distance)
                }
                result
            }
            RadioNode.DEVICE_TRAILER_SENSITIVITY -> {
                val result = SettingManager.instance.setTrailerSensitivity(value)
                if (result) {
                    distance.set(value)
                    doOptionChanged(node, distance)
                }
                result
            }
            else -> false
        }
        return result
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is ISwitchListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore[serial] = WeakReference(listener)
            } finally {
                writeLock.unlock()
            }
            return serial
        }
        return -1
    }


    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            RadioNode.WIRELESS_CHARGING_STATE.get.signal -> {
                val node = RadioNode.WIRELESS_CHARGING_STATE
                val value = property.value as Int
                onRadioChanged(node, wirelessChargingState, value, this::doUpdateRadioValue) { _, state ->
                    onWirelessChargingModeChanged(state)
                }
            }
            SwitchNode.DRIVE_TRAILER_REMIND.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_TRAILER_REMIND, trailerRemind, property)
            }
            SwitchNode.DRIVE_BATTERY_OPTIMIZE.get.signal -> {
                val node = SwitchNode.DRIVE_BATTERY_OPTIMIZE
                var convert = convert(property, node.get.off, 0x5)
                if (null == convert) convert = property
                onSwitchChanged(node, batteryOptimize, convert)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_WIRELESS_CHARGING, wirelessCharging, property)
            }
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP.get.signal -> {
                onSwitchChanged(
                    SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, wirelessChargingLamp, property)
            }
            else -> {}
        }
    }

    /**
     * 无线充电状态
     * 0x0: WCM处于关机状态及CDC或DA的显示
    WCM关机状态需要满足以下条件：
    1）	常电上电、触发电未上电/WCM开关处于关闭状态
    满足以上条件， WCM进入关机状态，WCM发送“WcmWSts：OX00”信号，CDC或DA在显示屏右上角的无线充电状态标识无显示！
     * 0x1: WCM处于待机状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM未检测到接收端（手机）；
    满足以上条件， WCM进入待机状态即WCM可以进行无线充电，但此时未检测到接收端（手机）.WCM发送“WcmWSts：OX01”信号，HUM在显示屏上显示此状态；
     * 0x2: WCM处于充电中状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM无故障信息；
    3）	检测到接收端（手机）。
    满足以上条件， WCM进入充电中状态.WCM发送“WcmWSts：OX02信号，则音响显示屏上显示此状态
     * 0x3: WCM处于过压状态及CDC或DA的显示
    1）	常电上电、触发电上电、WCM开关处于打开状态、PEPS不在寻钥匙状态；
    2）	WCM检测到输入电压过高（19.2V以上）。
    满足以上条件， WCM进入过压状态.WCM发送“WcmWSts：OX03信号，音响显示屏上显示此状态
    同时HUM屏幕额外通过图片的文字提示故障内容。
    HUM的警告面策略：HUM弹出的警告画面会有“无线充电异常”的文字。
     * 0x4: WCM处于欠压状态及CDC或DA的显示
    4）	WCM检测到输入电压过低（8.5V以下）。
    满足以上条件， WCM进入欠压状态.WCM发送“WcmWSts：OX04信号，HUM在显示屏上显示此状态
    HUM弹出的警告画面会有“无线充电异常”
     * 0x5: WCM处于检测到异物（FOD）状态
    WCM进入FOD状态.WCM发送“WcmWSts：OX05信号
    HUM弹出的警告画面会有“检测到金属异物，请移开异物”的文字
     * 0x6: WCM处于过流状态
    WCM进入过流状态.WCM发送“WcmWSts：OX06信号
    HUM弹出的警告画面会有“无线充电异常”的文字。
     * 0x7: WCM处于过温状态
    WCM进入过温状态.WCM发送“WcmWSts：OX07信号
    HUM弹出的警告画面会有“无线充电温度过高，请移开手机”的文字
     * 0x8: WCM处于过功率状态
     * 0x9:
     * 0xA:
     * 0xB:
     * @param state
     */
    private fun onWirelessChargingModeChanged(state: RadioState) {
        when (state.get()) {
            /**无线充电正常*/
            0x2 -> VcuUtils.startDialogService(Hint.wirelessChargingNormal)
            /**无线充电异常*/
            0x3, 0x4, 0x6 -> VcuUtils.startDialogService(Hint.wirelessChargingAbnormal)
            /**检测到金属异物，请移开异物*/
            0x5 -> VcuUtils.startDialogService(Hint.wirelessChargingMetal)
            /**无线充电温度过高，请移开手机*/
            0x7 -> VcuUtils.startDialogService(Hint.wirelessChargingTemperature)
        }
    }

    fun onTrailerRemindChanged(onOff: Int, level: Int, dist: Int) {
        Timber.d("onTrailerRemindChanged statusValue:%s, level:%s, distance:%s", onOff, level, dist)
        doUpdateSwitchValue(
            SwitchNode.DRIVE_TRAILER_REMIND, trailerRemind, onOff, this::doSwitchChanged)
        doUpdateRadioValue(
            RadioNode.DEVICE_TRAILER_SENSITIVITY, sensitivity, level, this::doOptionChanged)
        doUpdateRadioValue(
            RadioNode.DEVICE_TRAILER_DISTANCE, distance, dist, this::doOptionChanged)
    }


}