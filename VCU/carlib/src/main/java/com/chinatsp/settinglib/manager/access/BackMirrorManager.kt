package com.chinatsp.settinglib.manager.access

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
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
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class BackMirrorManager private constructor() : BaseManager(), ISwitchManager {

    private val backMirrorFold: AtomicBoolean by lazy {
        val node = SwitchNode.BACK_MIRROR_FOLD
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin, Area.GLOBAL)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val backMirrorDown: AtomicBoolean by lazy {
        val node = SwitchNode.BACK_MIRROR_DOWN
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin, Area.GLOBAL)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    companion object : ISignal {
        override val TAG: String = BackMirrorManager::class.java.simpleName
        val instance: BackMirrorManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BackMirrorManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.BACK_MIRROR_FOLD.get.signal)
                add(SwitchNode.BACK_MIRROR_DOWN.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun doGetSwitchOption(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> {
                backMirrorFold.get()
            }
            SwitchNode.BACK_MIRROR_DOWN -> {
                backMirrorDown.get()
            }
            else -> false
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.BACK_MIRROR_DOWN -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        if (listener is ISwitchListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore.put(serial, WeakReference(listener))
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
            SwitchNode.BACK_MIRROR_FOLD.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_MIRROR_FOLD, backMirrorFold, property)
            }
            SwitchNode.BACK_MIRROR_DOWN.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_MIRROR_DOWN, backMirrorDown, property)
            }
            else -> {}
        }
    }

}