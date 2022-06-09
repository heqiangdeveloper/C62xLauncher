package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.LogManager
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.listener.access.IWindowListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.SignalOrigin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class WindowManager private constructor() : BaseManager(), IWindowManager {


    private val autoCloseWinInRain: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_AUTO_CLOSE_WIN_IN_RAIN
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
            val result = signalService.doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            set(switchNode.isOn(result))
        }
    }

    private val autoCloseWinAtLock: AtomicBoolean by lazy {
        val switchNode = SwitchNode.AS_AUTO_CLOSE_WIN_AT_LOCK
        AtomicBoolean(switchNode.isOn()).apply {
            val signal = CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS
            val result = signalService.doGetIntProperty(signal, switchNode.origin, Area.GLOBAL)
            set(switchNode.isOn(result))
        }
    }

    companion object : ISignal {
        override val TAG: String = WindowManager::class.java.simpleName
        val instance: WindowManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WindowManager()
        }
    }

    override val concernedSerials: Map<SignalOrigin, Set<Int>> by lazy {
        HashMap<SignalOrigin, Set<Int>>().apply {
            val cabinSet = HashSet<Int> ().apply {
                /**雨天自动关窗*/
                add(CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS)
                /**锁车自动关窗*/
                add(CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS)
            }
            put(SignalOrigin.CABIN_SIGNAL, cabinSet)
        }
    }

    override fun onHandleConcernedSignal(
        property: CarPropertyValue<*>,
        signalOrigin: SignalOrigin
    ): Boolean {
        when (signalOrigin) {
            SignalOrigin.CABIN_SIGNAL -> {
                onCabinPropertyChanged(property)
            }
            SignalOrigin.HVAC_SIGNAL -> {
                onHvacPropertyChanged(property)
            }
            else -> {}
        }
        return true
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: SignalOrigin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: SignalOrigin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    override fun doGetSwitchOption(switchNode: SwitchNode): Boolean {
        TODO("Not yet implemented")
    }

    override fun doSetSwitchOption(switchNode: SwitchNode, status: Boolean): Boolean {
        return when (switchNode) {
            SwitchNode.AS_AUTO_CLOSE_WIN_IN_RAIN -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.AS_AUTO_CLOSE_WIN_AT_LOCK -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.AS_REMOTE_RISE_AND_FALL -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            SwitchNode.AS_RAIN_WIPER_REPAIR -> {
                doSetProperty(switchNode.signal, switchNode.obtainValue(status), switchNode.origin)
            }
            else -> false
        }
    }

    override fun unRegisterVcuListener(serial: Int, callSerial: Int): Boolean {
        LogManager.d(TAG, "unRegisterVcuListener serial:$serial, callSerial:$callSerial")
        synchronized(listenerStore) {
            listenerStore.let {
                if (it.containsKey(serial)) it else null
            }?.remove(serial)
        }
        return true
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is IWindowListener) {
            val serial: Int = System.identityHashCode(listener)
            synchronized(listenerStore) {
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            }
            return serial
        }
        return -1
    }



    /**
     * 【设置】解锁车门
     * @param value 0x1: unlock FL door 0x2: unlock all doors(default)   0x3: FunctionDisable
     */
    fun doUpdateUnlockDoorOption(value: Int): Boolean {
        val isValid = listOf(0x01, 0x02, 0x03).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarCabinManager.ID_CUT_OFF_UNLOCK_DOORS
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
    }

    /**
     * 【设置】解锁车门
     * @param value 0x1: unlock FL door 0x2: unlock all doors(default)   0x3: FunctionDisable
     */
    fun doUpdateDriveLockOption(value: Int): Boolean {
        val isValid = listOf(0x01, 0x02, 0x03, 0x04, 0x05).any { it == value }
        if (!isValid) {
            return false
        }
        val signal = CarCabinManager.ID_VSPEED_LOCK
        return doSetProperty(signal, value, SignalOrigin.CABIN_SIGNAL, Area.GLOBAL)
    }

    /**
     *
     * @param switchNode 开关选项
     */
    fun doGetSwitchStatus(switchNode: SwitchNode): Boolean {
        return when (switchNode) {
            SwitchNode.AS_AUTO_CLOSE_WIN_IN_RAIN -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.AS_AUTO_CLOSE_WIN_AT_LOCK -> {
                val signal = CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS
                val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.AS_REMOTE_RISE_AND_FALL -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL, switchNode.area)
                switchNode.isOn(value)
            }
            SwitchNode.AS_RAIN_WIPER_REPAIR -> {
                val signal = CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS
                val value = doGetIntProperty(signal, SignalOrigin.CABIN_SIGNAL, switchNode.area)
                switchNode.isOn(value)
            }
            else -> false
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        /**雨天自动关窗*/
        when (property.propertyId) {
            CarCabinManager.ID_BCM_RAIN_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_AUTO_CLOSE_WIN_IN_RAIN, property)
            }
            CarCabinManager.ID_BCM_WIN_CLOSE_FUN_STS -> {
                onSwitchChanged(SwitchNode.AS_AUTO_CLOSE_WIN_AT_LOCK, property)
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            /**
             * 车门车窗-车窗-雨天自动关窗 (状态下发)
             * 0x0: Disable
             * 0x1: Enable
             */
            SwitchNode.AS_AUTO_CLOSE_WIN_IN_RAIN -> {
                var value = property.value
                if (value is Int) {
                    value += 1
                    autoCloseWinInRain.set(switchNode.isOn(value))
                    onSwitchChanged(switchNode, autoCloseWinInRain.get())
                }
            }
            /**
             * 车门车窗-车窗-锁车自动关窗 (状态上报)
             * 0x0: No action when locking（default）
             * 0x1: close windows when locking door
             * 0x2: reserved
             * 0x3: Reserved
             */
            SwitchNode.AS_AUTO_CLOSE_WIN_AT_LOCK -> {
                var value = property.value
                if (value is Int) {
                    value += 1
                    autoCloseWinAtLock.set(switchNode.isOn(value))
                    onSwitchChanged(switchNode, autoCloseWinAtLock.get())
                }
            }
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.filterValues { null != it.get() }
                .forEach{
                    val listener = it.value.get()
                    if (null != listener && listener is ISwitchListener) {
                        listener.onSwitchOptionChanged(status, switchNode)
                    }
                }
        }
    }

}