package com.chinatsp.settinglib.manager.cabin.access

import android.car.hardware.CarPropertyValue
import android.car.hardware.cabin.CarCabinManager
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.listener.ISwitchListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IMirrorAction
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import java.lang.ref.WeakReference

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class BackMirrorManager private constructor() : BaseManager(), ISwitchManager {

    private val backMirrorFold: SwitchState by lazy {
        val node = SwitchNode.BACK_MIRROR_FOLD
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val backMirrorDown: SwitchState by lazy {
        val node = SwitchNode.BACK_MIRROR_DOWN
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
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
                //特殊增加 后视镜调节保存反馈信号
                add(CarCabinManager.ID_R_MIRROR_MEMORY_STS)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> backMirrorFold.deepCopy()
            SwitchNode.BACK_MIRROR_DOWN -> backMirrorDown.deepCopy()
            else -> null
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
        when (property.propertyId) {
            SwitchNode.BACK_MIRROR_FOLD.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_MIRROR_FOLD, backMirrorFold, property)
            }
            SwitchNode.BACK_MIRROR_DOWN.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_MIRROR_DOWN, backMirrorDown, property)
            }
            CarCabinManager.ID_R_MIRROR_MEMORY_STS -> {
                doNonstopValue(property.propertyId, property.value as Int)
            }
            else -> {}
        }
    }

    private fun doNonstopValue(signal: Int, value: Int) {
        val readLock = readWriteLock.readLock()
        try {
            readLock.lock()
            listenerStore.forEach { (_, ref) ->
                val listener = ref.get()
                listener?.takeIf { isCareSignal(signal) }?.doNonstopValue(signal, value)
            }
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 注1：倒车后视镜下翻记忆相关，涉及开关6“倒车后视镜照地位置设置”和7“倒车后视镜照地位置确定”。具体逻辑如下：
     * 1）	倒车后视镜下翻功能打开后，用户可选择进入到下一级菜单，然后界面显示有2分钟的调整时间是否启动开始记忆；
     * 2）	用户点击“开始记忆”，车机会发送信号HUM_R_MIRROR_SEE_G_SET==0x2，座椅记忆模块把当前的位置存储到RAM_1，然后车机启动倒计时2分钟；
     * 3）	用户可通过车内门把手功能按键区进行后视镜位置调节，调节完成后，点击“保存记忆”，车机发送
     *      HUM_R_MIRROR_MEMORY_CONFIRM==0x2，座椅记忆模块存储当前位置，发送记忆结果R_MIRROR_MEMORY_STS，
     *      车机根据信号R_MIRROR_MEMORY_STS==0x1 Memorize fail OR 0x2 Memorize success进行记忆成功/失败的提示。
     * 4）	座椅模块回归到开启前的位置（RAM_1中的位置，同时清空RAM_1）。
     */
    fun doBackMirrorAction(@IMirrorAction action: Int) {
        when (action) {
            Constant.ANGLE_ADJUST -> {
                val signal = CarCabinManager.ID_HUM_MIRROR_SEE_G_SET
                writeProperty(signal, 0x2, Origin.CABIN, Area.GLOBAL)
            }
            Constant.ANGLE_SAVE -> {
                val signal = CarCabinManager.ID_HUM_MIRROR_MEMORY_CONFIRM
                writeProperty(signal, 0x2, Origin.CABIN, Area.GLOBAL)
            }
            Constant.DEFAULT -> {

            }
        }
    }

}