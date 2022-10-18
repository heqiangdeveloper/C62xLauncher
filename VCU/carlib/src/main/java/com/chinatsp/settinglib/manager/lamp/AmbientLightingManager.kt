package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.IProgressManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.AirCmd
import com.chinatsp.vehicle.controller.bean.CarCmd
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class AmbientLightingManager private constructor() : BaseManager(), IOptionManager,
    IProgressManager {

    companion object : ISignal {
        override val TAG: String = AmbientLightingManager::class.java.simpleName
        val instance: AmbientLightingManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AmbientLightingManager()
        }
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val cabinSet = HashSet<Int>().apply {
                add(SwitchNode.FRONT_AMBIENT_LIGHTING.get.signal)
                add(SwitchNode.BACK_AMBIENT_LIGHTING.get.signal)
                add(SwitchNode.ALC_DOOR_HINT.get.signal)
                add(SwitchNode.ALC_LOCK_HINT.get.signal)
                add(SwitchNode.ALC_BREATHE_HINT.get.signal)
                add(SwitchNode.ALC_COMING_HINT.get.signal)
                add(SwitchNode.ALC_RELATED_TOPICS.get.signal)
                add(SwitchNode.ALC_SMART_MODE.get.signal)

                add(SwitchNode.SPEED_RHYTHM.get.signal)
                add(SwitchNode.MUSIC_RHYTHM.get.signal)
                add(SwitchNode.COLOUR_BREATHE.get.signal)

                /**【反馈】全车氛围灯亮度响应反馈*/
                add(Progress.AMBIENT_LIGHT_BRIGHTNESS.get.signal)
                add(Progress.AMBIENT_LIGHT_COLOR.get.signal)
            }
            put(Origin.CABIN, cabinSet)
        }
    }

    private val alcDoorHint: SwitchState by lazy {
        val node = SwitchNode.ALC_DOOR_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }
    private val alcLockHint: SwitchState by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcBreatheHint: SwitchState by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcComingHint: SwitchState by lazy {
        val node = SwitchNode.ALC_COMING_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcRelatedTopics: SwitchState by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val frontLighting: SwitchState by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val backLighting: SwitchState by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcSmartMode: SwitchState by lazy {
        val node = SwitchNode.ALC_SMART_MODE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val speedRhythm: SwitchState by lazy {
        val node = SwitchNode.SPEED_RHYTHM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val musicRhythm: SwitchState by lazy {
        val node = SwitchNode.MUSIC_RHYTHM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val colourBreathe: SwitchState by lazy {
        val node = SwitchNode.COLOUR_BREATHE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitchValue(node, result, value, this::doSwitchChanged)
        }
    }

    private val ambientBrightness: AtomicInteger by lazy {
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        AtomicInteger(node.def).apply {
            val result = readIntProperty(node.get.signal, node.get.origin)
            val value = if (result in node.min..node.max) result else node.def
            Timber.d("getBrightness node:$node, result:$result,, value:$value")
            this.set(value)
        }
    }

    private val ambientColor: AtomicInteger by lazy {
        val node = Progress.AMBIENT_LIGHT_COLOR
        AtomicInteger(node.def).apply {
            val value = readIntProperty(node.get.signal, node.get.origin)
            Timber.d("getAmbientColor signal:%s, value:%s", node.get.signal, value)
            doUpdateProgress(node, this, value, instance::doProgressChanged)
        }
    }


    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return null
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }

    override fun doGetProgress(node: Progress): Int {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                ambientBrightness.get()
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                ambientColor.get()
            }
            else -> -1
        }
    }

    override fun doSetProgress(node: Progress, value: Int): Boolean {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                val set = node.set
                val result = writeProperty(set.signal, value, set.origin)
                Timber.d("setBrightness signal:%s, newValue:%s, result:%s",
                    set.signal,
                    value,
                    result)
                result
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                val set = node.set
                val result = writeProperty(set.signal, value, set.origin)
                Timber.d("setBrightness signal:%s, newValue:%s, result:%s",
                    set.signal,
                    value,
                    result)
                result
            }
            else -> false
        }
    }

    override fun onRegisterVcuListener(priority: Int, listener: IBaseListener): Int {
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

    override fun doGetSwitchOption(node: SwitchNode): SwitchState? {
        return when (node) {
            SwitchNode.ALC_DOOR_HINT -> alcDoorHint.copy()
            SwitchNode.ALC_LOCK_HINT -> alcLockHint.copy()
            SwitchNode.ALC_BREATHE_HINT -> alcBreatheHint.copy()
            SwitchNode.ALC_COMING_HINT -> alcComingHint.copy()
            SwitchNode.ALC_RELATED_TOPICS -> alcRelatedTopics.copy()
            SwitchNode.FRONT_AMBIENT_LIGHTING -> frontLighting.copy()
            SwitchNode.BACK_AMBIENT_LIGHTING -> backLighting.copy()
            SwitchNode.ALC_SMART_MODE -> alcSmartMode.copy()
            SwitchNode.SPEED_RHYTHM -> speedRhythm.copy()
            SwitchNode.MUSIC_RHYTHM -> musicRhythm.copy()
            SwitchNode.COLOUR_BREATHE -> colourBreathe.copy()
            else -> null
        }
    }

    override fun doSetSwitchOption(node: SwitchNode, status: Boolean): Boolean {
        return when (node) {
            SwitchNode.ALC_DOOR_HINT -> {
                writeProperty(node, status, alcDoorHint)
            }
            SwitchNode.ALC_LOCK_HINT -> {
                writeProperty(node, status, alcLockHint)
            }
            SwitchNode.ALC_BREATHE_HINT -> {
                writeProperty(node, status, alcBreatheHint)
            }
            SwitchNode.ALC_COMING_HINT -> {
                writeProperty(node, status, alcComingHint)
            }
            SwitchNode.ALC_RELATED_TOPICS -> {
                writeProperty(node, status, alcRelatedTopics)
            }
            SwitchNode.FRONT_AMBIENT_LIGHTING -> {
                writeProperty(node, status, frontLighting)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING -> {
                writeProperty(node, status, backLighting)
            }
            SwitchNode.ALC_SMART_MODE -> {
                writeProperty(node, status, alcSmartMode)
            }
            SwitchNode.SPEED_RHYTHM -> {
                writeProperty(node, status, speedRhythm)
            }
            SwitchNode.MUSIC_RHYTHM -> {
                writeProperty(node, status, musicRhythm)
            }
            SwitchNode.COLOUR_BREATHE -> {
                writeProperty(node, status, colourBreathe)
            }
            else -> false
        }
    }

    override fun onHvacPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            else -> {}
        }
    }

    override fun onCabinPropertyChanged(property: CarPropertyValue<*>) {
        when (property.propertyId) {
            SwitchNode.ALC_DOOR_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_DOOR_HINT, alcDoorHint, property)
            }
            SwitchNode.ALC_LOCK_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_LOCK_HINT, alcLockHint, property)
            }
            SwitchNode.ALC_BREATHE_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_BREATHE_HINT, alcBreatheHint, property)
            }
            SwitchNode.ALC_COMING_HINT.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_COMING_HINT, alcComingHint, property)
            }
            SwitchNode.ALC_RELATED_TOPICS.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_RELATED_TOPICS, alcRelatedTopics, property)
            }
            SwitchNode.FRONT_AMBIENT_LIGHTING.get.signal -> {
                onSwitchChanged(SwitchNode.FRONT_AMBIENT_LIGHTING, frontLighting, property)
            }
            SwitchNode.BACK_AMBIENT_LIGHTING.get.signal -> {
                onSwitchChanged(SwitchNode.BACK_AMBIENT_LIGHTING, backLighting, property)
            }
            SwitchNode.ALC_SMART_MODE.get.signal -> {
                onSwitchChanged(SwitchNode.ALC_SMART_MODE, alcSmartMode, property)
            }

            SwitchNode.SPEED_RHYTHM.get.signal -> {
                onSwitchChanged(SwitchNode.SPEED_RHYTHM, speedRhythm, property)
            }
            SwitchNode.MUSIC_RHYTHM.get.signal -> {
                onSwitchChanged(SwitchNode.MUSIC_RHYTHM, musicRhythm, property)
            }
            SwitchNode.COLOUR_BREATHE.get.signal -> {
                onSwitchChanged(SwitchNode.COLOUR_BREATHE, colourBreathe, property)
            }
            Progress.AMBIENT_LIGHT_BRIGHTNESS.get.signal -> {
                onAmbientBrightnessChanged(property)
            }
            Progress.AMBIENT_LIGHT_COLOR.get.signal -> {
                onAmbientColorChanged(property)
            }
            else -> {}
        }
    }

    private fun onAmbientBrightnessChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientBrightnessChanged value%s", value)
            doUpdateProgress(
                Progress.AMBIENT_LIGHT_BRIGHTNESS,
                ambientBrightness,
                value,
                this::doProgressChanged
            )
        }
    }

    private fun onAmbientColorChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientBrightnessChanged value%s", value)
            doUpdateProgress(
                Progress.AMBIENT_LIGHT_COLOR,
                ambientColor,
                value,
                this::doProgressChanged
            )
        }
    }

    private fun writeProperty(node: SwitchNode, value: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(value), node.set.origin)
        if (success && develop) {
            doUpdateSwitchValue(node, atomic, value) { _node, _value ->
                doSwitchChanged(_node, _value)
            }
        }
        return success
    }


    override fun doCarControlCommand(cmd: CarCmd, callback: ICmdCallback?) {
        if (ICar.AMBIENT == cmd.car) {
            doSwitchAmbient(cmd, callback)
        } else if (ICar.BRIGHTNESS == cmd.car) {
            doAdjustAmbientBrightness(cmd, callback)
        } else if (ICar.COLOR == cmd.car) {
            doAdjustAmbientColor(cmd, callback)
        } else if (ICar.RHYTHM_MODE == cmd.car) {
            doUpdateAmbientRhythmMode(cmd, callback)
        }
    }

    private fun isAmbient(): Boolean {
        return frontLighting.get() || backLighting.get()
    }

    private fun doUpdateAmbientRhythmMode(command: CarCmd, callback: ICmdCallback?) {
        if (!isAmbient() || !alcSmartMode.get()) {
            command.message = "智能模式已关闭，暂无法修改律动模式"
        } else {
            val expect = Action.TURN_ON == command.action
            val option = if (expect) "开启" else "关闭"
            var modeName = ""
            var result = ""
            var node: SwitchNode = SwitchNode.INVALID
            if (command.value == 1) {
                modeName = "音乐律动"
                node = SwitchNode.MUSIC_RHYTHM
            } else if (command.value == 2) {
                modeName = "车速律动"
                node = SwitchNode.SPEED_RHYTHM
            } else if (command.value == 3) {
                modeName = "色彩呼吸"
                node = SwitchNode.COLOUR_BREATHE
            }
            if (SwitchNode.INVALID != node) {
                val resultStatus = doSetSwitchOption(node, expect)
                result = if (resultStatus) "成功" else "失败"
                command.message = "$modeName$option$result"
            } else {
                command.message = "我还不会这个操作"
            }
        }
        callback?.onCmdHandleResult(command)
    }

    private fun doAdjustAmbientColor(command: CarCmd, callback: ICmdCallback?) {
        when (command.action) {
            Action.PLUS,
            Action.MINUS,
            Action.MIN,
            Action.MAX,
            Action.FIXED -> {
                attemptUpdateAmbientColor(command, callback)
            }
        }
    }

    private fun doAdjustAmbientBrightness(command: CarCmd, callback: ICmdCallback?) {
        when (command.action) {
            Action.PLUS,
            Action.MINUS,
            Action.MIN,
            Action.MAX,
            Action.FIXED -> {
                attemptUpdateAmbientBrightness(command, callback)
            }
        }
    }

    private fun attemptUpdateAmbientBrightness(command: CarCmd, callback: ICmdCallback?) {
        frontLighting.set(true)
        if (!isAmbient()) {
            command.message = "氛围灯已关闭，无法调节其亮度"
        } else {
            val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
            val expect = computeExpectBrightness(command, node.min, node.max, IPart.HEAD)
            val result = writeProperty(node.set.signal, expect, node.set.origin)
            if (result) {
                command.message = "氛围灯亮度已设置为$expect"
            } else {
                command.message = "氛围灯亮度设置失败"
            }
        }
        callback?.onCmdHandleResult(command)
    }

    private fun attemptUpdateAmbientColor(command: CarCmd, callback: ICmdCallback?) {
        frontLighting.set(true)
        if (!isAmbient()) {
            command.message = "氛围灯已关闭，无法调节其颜色"
        } else {
            val node = Progress.AMBIENT_LIGHT_COLOR
            val expect = computeExpectBrightness(command, node.min, node.max, IPart.HEAD)
            val result = writeProperty(node.set.signal, expect, node.set.origin)
            if (result) {
                command.message = "氛围灯颜色已设置为${command.color}"
            } else {
                command.message = "氛围灯颜色设置失败"
            }
        }
        callback?.onCmdHandleResult(command)
    }


    private fun computeExpectBrightness(cmd: CarCmd, min: Int, max: Int, @IPart part: Int): Int {
        var expect = 0
        if (Action.PLUS == cmd.action) {
            val current = ambientBrightness.get()
            expect = current + cmd.step
        } else if (Action.MINUS == cmd.action) {
            val current = ambientBrightness.get()
            expect = current - cmd.step
        } else if (Action.FIXED == cmd.action) {
            expect = cmd.value
        } else if (Action.MIN == cmd.action) {
            expect = min
        } else if (Action.MAX == cmd.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        return expect
    }

    private fun doSwitchAmbient(cmd: CarCmd, callback: ICmdCallback?) {
        var name = "氛围灯"
        var status = false
        if (Action.TURN_ON == cmd.action) {
            status = true
        }
        if (Action.TURN_OFF == cmd.action) {
            status = false
        }
        var mask = IPart.HEAD
        var fSuccess = mask != (mask and cmd.part)
        mask = IPart.TAIL
        var bSuccess = mask != (mask and cmd.part)
        if ((IPart.HEAD or IPart.TAIL) == cmd.part) {
            fSuccess = doSetSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, status)
            bSuccess = doSetSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, status)
        }
        if (IPart.HEAD == cmd.part) {
            fSuccess = doSetSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, status)
            name = "前排氛围灯"
        }
        if (IPart.TAIL == cmd.part) {
            bSuccess = doSetSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, status)
            name = "后排氛围灯"
        }
        val intent = if (status) "打开" else "关闭"
        var result = if (fSuccess && bSuccess) "成功" else "失败"
        cmd.message = "$name$intent$result"
        callback?.onCmdHandleResult(cmd)
    }
}