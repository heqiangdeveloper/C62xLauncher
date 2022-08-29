package com.chinatsp.settinglib.manager.adas

import android.car.VehicleAreaSeat
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/7 10:50
 * @desc   :
 * @version: 1.0
 */
class LamplightManager : BaseManager(), ISwitchManager {

    companion object : ISignal {
        override val TAG: String = RoadSignManager::class.java.simpleName
        val instance: RoadSignManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RoadSignManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.ADAS_HMA.get.signal)
                add(SwitchNode.ADAS_TSR.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val hmaValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_HMA
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val tsrValue: AtomicBoolean by lazy {
        val node = SwitchNode.ADAS_TSR
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }


    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                hmaValue.get()
            }
            SwitchNode.ADAS_TSR -> {
                tsrValue.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_HMA -> {
                writeProperty(
                    node.set.signal,
                    node.value(status),
                    node.set.origin,
                    VehicleAreaSeat.SEAT_DRIVER
                )
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is ISwitchManager) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
            } finally {
                writeLock.unlock()
            }
            result = serial
        }
        return result
    }

}