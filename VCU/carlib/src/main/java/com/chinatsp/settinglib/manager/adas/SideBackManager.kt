package com.chinatsp.settinglib.manager.adas

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.IOptionListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/6/2 11:34
 * @desc   :
 * @version: 1.0
 */
class SideBackManager : BaseManager(), IOptionManager {

    companion object : ISignal {
        override val TAG: String = SideBackManager::class.java.simpleName
        val instance: SideBackManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SideBackManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.ADAS_DOW.get.signal)
                add(SwitchNode.ADAS_BSC.get.signal)
                add(SwitchNode.ADAS_BSD.get.signal)
                add(SwitchNode.ADAS_MEB.get.signal)
                add(SwitchNode.ADAS_GUIDES.get.signal)

                add(RadioNode.ADAS_SIDE_BACK_SHOW_AREA.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val showAreaValue: RadioState by lazy {
        val node = RadioNode.ADAS_SIDE_BACK_SHOW_AREA
        RadioState(node.def).apply {
            val value = VcuUtils.getInt(
                key = Constant.SHOW_AREA,
                value = node.get.values[0]
            )
            doUpdateRadioValue(node, this, value)
        }
    }

    private val dowValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_DOW
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val bsdValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_BSD
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val bscValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_BSC
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val mebValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_MEB
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val guidesValue: SwitchState by lazy {
        val node = SwitchNode.ADAS_GUIDES
        SwitchState(node.default).apply {
            val result = VcuUtils.getInt(
                key = Constant.AUXILIARY_LINE,
                value = node.get.on
            )
            doUpdateSwitchValue(node, this, result)
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ADAS_DOW.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_DOW, dowValue, property)
            }
            SwitchNode.ADAS_BSC.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_BSC, bscValue, property)
            }
            SwitchNode.ADAS_BSD.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_BSD, bsdValue, property)
            }
            SwitchNode.ADAS_GUIDES.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_GUIDES, guidesValue, property)
            }
            SwitchNode.ADAS_MEB.get.signal -> {
                onSwitchChanged(SwitchNode.ADAS_MEB, mebValue, property)
            }
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA.get.signal -> {
                onRadioChanged(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, showAreaValue, property)
            }
            else -> {}
        }

    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                showAreaValue.copy()
            }
            else -> null
        }
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> {
                node.isValid(value, false)
                        && VcuUtils.putInt(key = Constant.SHOW_AREA, value = value)
            }
            else -> false
        }
    }


    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
        var result = -1
        if (listener is IOptionListener) {
            val serial: Int = System.identityHashCode(listener)
            val writeLock = readWriteLock.writeLock()
            try {
                writeLock.lock()
                unRegisterVcuListener(serial, identity)
                listenerStore[serial] = WeakReference(listener)
            } finally {
                writeLock.unlock()
            }
            result = serial
        }
        return result
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.ADAS_DOW -> dowValue.copy()
            SwitchNode.ADAS_BSD -> bsdValue.copy()
            SwitchNode.ADAS_BSC -> bscValue.copy()
            SwitchNode.ADAS_MEB -> mebValue.copy()
            SwitchNode.ADAS_GUIDES -> guidesValue.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ADAS_DOW -> {
                doSetSwitchOption(node, status, dowValue)
            }
            SwitchNode.ADAS_BSD -> {
                doSetSwitchOption(node, status, bsdValue)
            }
            SwitchNode.ADAS_BSC -> {
                doSetSwitchOption(node, status, bscValue)
            }
            SwitchNode.ADAS_MEB -> {
                doSetSwitchOption(node, status, mebValue)
            }
            SwitchNode.ADAS_GUIDES -> {
                VcuUtils.putInt(key = Constant.AUXILIARY_LINE, value = node.value(status))
            }
            else -> false
        }
    }

    fun doSetSwitchOption(node: SwitchNode, status: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(status), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, status) { _node, _status ->
                doSwitchChanged(_node, _status)
            }
        }
        return success
    }

}