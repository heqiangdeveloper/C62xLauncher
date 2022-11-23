package com.chinatsp.settinglib.manager.cabin

import android.car.hardware.CarPropertyValue
import android.database.ContentObserver
import android.net.Uri
import com.chinatsp.settinglib.BaseApp
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */

class SafeManager private constructor() : BaseManager(), ISwitchManager {

    private val fortifyToneFunction: SwitchState by lazy {
        val node = SwitchNode.DRIVE_SAFE_FORTIFY_SOUND
//        AtomicBoolean(node.default).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, result)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcLockHint: SwitchState by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
//        AtomicBoolean(node.default).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            doUpdateSwitchValue(node, this, value)
//        }
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val videoModeFunction: SwitchState by lazy {
        val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
        SwitchState(node.default).apply {
            val result = VcuUtils.getInt(
                key = Constant.DRIVE_VIDEO_PLAYING,
                value = node.value(node.default)
            )
            doUpdateSwitchValue(node, this, result)
        }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    init {
//        行车播放视频开关 采用写系统Globel属性的方式，属性的Key为DRIVE_VIDEO_PLAYING，值：on = 0x01, off = 0x00
        VcuUtils.addUriObserver(Constant.DRIVE_VIDEO_PLAYING,
            object : ContentObserver(BaseApp.instance.mainHandler) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
                    val value = VcuUtils.getInt(key = Constant.DRIVE_VIDEO_PLAYING,
                        value = node.value(node.default))
                    Timber.d("observer onChange node:$node value:$value")
                    doUpdateSwitchValue(node,
                        videoModeFunction, node.isOn(value), instance::doSwitchChanged)}
            })
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**设防提示音 开关*/
                add(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND.get.signal)
                add(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }


    override fun onHandleSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        when (origin) {
            Origin.CABIN -> {
                onCabinPropertyChanged(property)
            }
            else -> {}
        }
        return false
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val signals = getOriginSignal(origin)
        return signals.contains(signal)
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        return careSerials[origin] ?: HashSet()
    }


    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            //设防提示音
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, fortifyToneFunction, property)
            }
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, videoModeFunction, property)
            }
            SwitchNode.ALC_LOCK_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_LOCK_HINT, alcLockHint, property)
            }
            else -> {}
        }
    }


    companion object : ISignal {
        override val TAG: String = SafeManager::class.java.simpleName
        val instance: SafeManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SafeManager()
        }
    }

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> fortifyToneFunction.deepCopy()
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> videoModeFunction.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> {
                VcuUtils.putInt(key = Constant.DRIVE_VIDEO_PLAYING, value = node.value(status))
//                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            else -> false
        }
    }

}