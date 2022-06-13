package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.listener.cabin.ISafeListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class SafeManager private constructor() : BaseManager() {


    private val fortifySoundStatus: AtomicBoolean by lazy {
        val node = SwitchNode.DRIVE_SAFE_FORTIFY_SOUND
        AtomicBoolean(node.isOn()).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            doUpdateSwitchValue(node, this, result)
        }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    override val concernedSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**设防提示音 开关*/
                add(CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE)
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun onHandleConcernedSignal(property: CarPropertyValue<*>, signalOrigin: Origin):
            Boolean {
        when (signalOrigin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            else -> {}
        }
        return false
    }

    override fun isConcernedSignal(signal: Int, signalOrigin: Origin): Boolean {
        val signals = getConcernedSignal(signalOrigin)
        return signals.contains(signal)
    }

    override fun getConcernedSignal(signalOrigin: Origin): Set<Int> {
        return concernedSerials[signalOrigin] ?: HashSet()
    }

    /**
     *
     * @param node 开关选项
     * @param status 开关期望状态
     */
    fun doSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //设防提示音
            CarCabinManager.ID_LOCK_SUCCESS_SOUND_STATUE -> {
                onSwitchChanged(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, property)
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, property: CarPropertyValue<*>) {
        when (switchNode) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> {
                val value = property.value
                if (value is Int) {
                    onSwitchChanged(
                        switchNode,
                        doUpdateSwitchValue(switchNode, fortifySoundStatus, value).get()
                    )
                }
            }
            else -> {}
        }
    }

    private fun onSwitchChanged(switchNode: SwitchNode, status: Boolean) {
        synchronized(listenerStore) {
            listenerStore.filterValues { null != it.get() }.forEach { (_, u) ->
                val listener = u.get()
                if (listener is ISafeListener) {
                    listener.onSwitchOptionChanged(status, switchNode)
                }
            }
        }
    }

    companion object : ISignal {
        override val TAG: String = SafeManager::class.java.simpleName
        val instance: SafeManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SafeManager()
        }
    }

}