package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import android.content.Intent
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.sound.ISoundListener
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.navigation.RouterSerial
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class WheelManager private constructor() : BaseManager(), ISoundManager {

    private val swhFunction: SwitchState by lazy {
        val node = SwitchNode.DRIVE_WHEEL_AUTO_HEAT
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node, Constant.STEERING_HEAT_SWITCH) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val epsMode: RadioState by lazy {
        val node = RadioNode.DRIVE_EPS_MODE
//        AtomicInteger(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateRadioValue(node, this, result)
//        }
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
        val pos = 0x05
        val result = VcuUtils.getInt(key = Constant.STEERING_HEAT_TEMP, value = pos)
        val volume = Volume(type, type.min, type.max, result)
//        AppExecutors.get()?.singleIO()?.execute {
//            val result = VcuUtils.getInt(key = Constant.STEERING_HEAT_TEMP, value = pos)
//            doUpdateProgress(volume, result, true, instance::doProgressChanged)
//        }
        return volume
    }


    companion object : ISignal {

        override val TAG: String = WheelManager::class.java.simpleName

        val instance: WheelManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WheelManager()
        }

    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> epsMode.copy()
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
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> swhFunction.copy()
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
                }
                return result
            }
            else -> false
        }
    }

    override fun doGetVolume(type: Progress): Volume? {
        return when (type) {
            Progress.STEERING_ONSET_TEMPERATURE -> {
                steeringSillTemp
            }
            else -> null
        }
    }

    override fun doSetVolume(type: Progress, position: Int): Boolean {
        return when (type) {
            Progress.STEERING_ONSET_TEMPERATURE -> {
                val result = writeProperty(steeringSillTemp, position)
                if (result) {
                    VcuUtils.putInt(key = Constant.STEERING_HEAT_TEMP, value = position)
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
        if (value is Int) {
            if (0x02 == value) {
                doSendAction(Constant.VCU_CUSTOM_KEYPAD)
            } else if (0x01 == value) {

            }
        }
    }

    private fun doSendAction(action: String) {
        var location =
            RouterSerial.makeRouteSerial(4, 0, 1)
        val intent = Intent(action)
        Timber.e("doSendAction====================action:$action")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("ROUTE_SERIAL", location)
        intent.putExtra("DIALOG_SERIAL", "")
        BaseApp.instance.startActivity(intent)
    }

    private fun writeProperty(volume: Volume, value: Int): Boolean {
        val success =
            volume.isValid(value) && writeProperty(volume.type.set.signal, value, Origin.CABIN)
        if (success && develop) {
            volume.pos = value
//            doRangeChanged(volume)
        }
        return success
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

    private fun doRangeChanged(vararg array: Volume) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                if (null != listener && listener is ISoundListener) {
                    listener.onSoundVolumeChanged(*array)
                }
            }
        } finally {
            readLock.unlock()
        }
    }

}