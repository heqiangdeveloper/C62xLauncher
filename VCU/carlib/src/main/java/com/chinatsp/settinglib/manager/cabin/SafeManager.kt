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


    private val alcLockHint: SwitchState by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val lockFailedHint: SwitchState by lazy {
        val node = SwitchNode.LOCK_FAILED_AUDIO_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val lockSuccessHint: SwitchState by lazy {
        val node = SwitchNode.LOCK_SUCCESS_AUDIO_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val videoModeFunction: SwitchState by lazy {
        val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
        SwitchState(node.def).apply {
            val result = VcuUtils.getInt(
                key = Constant.DRIVE_VIDEO_PLAYING,
                value = node.value(node.def)
            )
            doUpdateSwitch(node, this, result)
        }
    }

    val version: AtomicInteger by lazy { AtomicInteger(0) }

    init {
//        ???????????????????????? ???????????????Globel???????????????????????????Key???DRIVE_VIDEO_PLAYING?????????on = 0x01, off = 0x00
        VcuUtils.addUriObserver(Constant.DRIVE_VIDEO_PLAYING,
            object : ContentObserver(BaseApp.instance.mainHandler) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    val node = SwitchNode.DRIVE_SAFE_VIDEO_PLAYING
                    val value = VcuUtils.getInt(key = Constant.DRIVE_VIDEO_PLAYING,
                        value = node.value(node.def))
                    Timber.d("observer onChange node:$node value:$value")
                    doUpdateSwitch(node,
                        videoModeFunction, node.isOn(value), instance::doSwitchChanged)
                }
            })
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                /**??????????????? ??????*/
                add(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING.get.signal)
                add(SwitchNode.LOCK_FAILED_AUDIO_HINT.get.signal)
                add(SwitchNode.LOCK_SUCCESS_AUDIO_HINT.get.signal)
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
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING.get.signal -> {
                onSwitchChanged(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, videoModeFunction, property)
            }
            SwitchNode.LOCK_FAILED_AUDIO_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.LOCK_FAILED_AUDIO_HINT, lockFailedHint, property)
            }
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.LOCK_SUCCESS_AUDIO_HINT, lockSuccessHint, property)
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
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> videoModeFunction.deepCopy()
            SwitchNode.LOCK_FAILED_AUDIO_HINT -> lockFailedHint.deepCopy()
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT -> lockSuccessHint.deepCopy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.LOCK_FAILED_AUDIO_HINT -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT -> {
                writeProperty(node.set.signal, node.value(status), node.set.origin)
            }
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> {
                VcuUtils.putInt(key = Constant.DRIVE_VIDEO_PLAYING, value = node.value(status))
            }
            else -> false
        }
    }

}