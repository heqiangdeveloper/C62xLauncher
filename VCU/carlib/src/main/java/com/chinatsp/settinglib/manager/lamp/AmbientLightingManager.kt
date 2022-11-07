package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.*
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
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
    IProgressManager, ICmdExpress {

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
                val newValue = value + 1
                val result = writeProperty(set.signal, newValue, set.origin)
                Timber.d("setBrightness signal:${set.signal}, newValue:$newValue, result:$result")
                result
            }
            Progress.AMBIENT_LIGHT_COLOR -> {
                val set = node.set
                val result = writeProperty(set.signal, value, set.origin)
                Timber.d("setLightColor signal:${set.signal}, value:$value, result:$result")
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
            val progress = Progress.AMBIENT_LIGHT_BRIGHTNESS
            doUpdateProgress(progress, ambientBrightness, value, this::doProgressChanged)
        }
    }

    private fun onAmbientColorChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientBrightnessChanged value%s", value)
            val progress = Progress.AMBIENT_LIGHT_COLOR
            doUpdateProgress(progress, ambientColor, value, this::doProgressChanged)
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

    private fun interruptCommand(
        command: CarCmd,
        callback: ICmdCallback?,
        coreEngine: Boolean = false,
    ): Boolean {
        val result = if (coreEngine) {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        } else {
            !VcuUtils.isPower()
        }
        if (result) {
            command.message = "操作没有成功，请先启动发动机"
            callback?.onCmdHandleResult(command)
        }
        return result
    }


    override fun doCarControlCommand(command: CarCmd, callback: ICmdCallback?, fromUser: Boolean) {
        val parcel = CommandParcel(command, callback, receiver = this)
        doCommandExpress(parcel)
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
            val result: String
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
            Action.PLUS, Action.MINUS, Action.MIN, Action.MAX, Action.FIXED -> {
                attemptUpdateAmbientColor(command, callback)
            }
            else ->{}
        }
    }

    private fun doAdjustAmbientBrightness(command: CarCmd, callback: ICmdCallback?) {
        when (command.action) {
            Action.PLUS, Action.MINUS, Action.MIN, Action.MAX, Action.FIXED -> {
                attemptUpdateAmbientBrightness(command, callback)
            }
            else -> {}
        }
    }

    private fun attemptUpdateAmbientBrightness(command: CarCmd, callback: ICmdCallback?) {
        frontLighting.set(true)
        val modelName = command.slots?.name ?: "氛围灯"
        if (!isAmbient()) {
            command.message = "${modelName}已关闭，无法调节其亮度"
        } else {
            val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
            val expect = computeLampBrightness(command, node.min, node.max) + 1
            val result = writeProperty(node.set.signal, expect, node.set.origin)
            if (result) {
                val message = when (expect) {
                    node.min -> "${modelName}亮度已设置为最暗了"
                    node.max -> "${modelName}亮度已设置为最亮了"
                    else -> "${modelName}亮度已设置为${expect}档了"
                }
                command.message = message
            } else {
                command.message = "氛围灯亮度设置失败"
            }
        }
        callback?.onCmdHandleResult(command)
    }

    private fun attemptUpdateAmbientColor(command: CarCmd, callback: ICmdCallback?) {
        Timber.e("attemptUpdateAmbientColor mode:${command.slots?.mode}," +
                " name:${command.slots?.name}, color:${command.slots?.color}")
        frontLighting.set(true)
        val modelName = command.slots?.name ?: "氛围灯"
        if (!isAmbient()) {
            command.message = "${modelName}已关闭，无法调节其颜色"
        } else {
            val node = Progress.AMBIENT_LIGHT_COLOR
//            val colors = arrayOf("红色", "紫色", "冰蓝色", "橙色", "绿色", "玫红色", "果绿色", "黄色", "蓝色", "白色")
//            val expect = computeLampColor(command, node.min, node.max)
            val serialNumber = findColorSerialNumberByColorName(command.slots?.color ?: "")
            if (Constant.INVALID == serialNumber) {
                command.message = "${modelName}不支持该颜色"
            } else {
                val result = writeProperty(node.set.signal, serialNumber, node.set.origin)
                if (result) {
                    command.message = "${modelName}颜色已设置为${command.color}了"
                } else {
                    command.message = "${modelName}颜色设置失败"
                }
            }
        }
        callback?.onCmdHandleResult(command)
    }

    private fun computeLampColor(command: CarCmd, min: Int, max: Int): Int {
        var expect = 0
        if (Action.PLUS == command.action) {
            val current = ambientColor.get()
            expect = current + command.step
        } else if (Action.MINUS == command.action) {
            val current = ambientColor.get()
            expect = current - command.step
        } else if (Action.FIXED == command.action) {
            expect = command.value
        } else if (Action.MIN == command.action) {
            expect = min
        } else if (Action.MAX == command.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        return expect
    }


    private fun computeLampBrightness(command: CarCmd, min: Int, max: Int): Int {
        var expect = 0
        if (Action.PLUS == command.action) {
            val current = ambientBrightness.get()
            expect = current + command.step
        } else if (Action.MINUS == command.action) {
            val current = ambientBrightness.get()
            expect = current - command.step
        } else if (Action.FIXED == command.action) {
            expect = command.value
        } else if (Action.MIN == command.action) {
            expect = min
        } else if (Action.MAX == command.action) {
            expect = max
        }
        if (expect > max) expect = max
        if (expect < min) expect = min
        return expect
    }

    private fun doSwitchAmbient(command: CarCmd, callback: ICmdCallback?) {
        val name = command.slots?.name ?: "氛围灯"
        var status = false
        if (Action.TURN_ON == command.action) {
            status = true
        }
        if (Action.TURN_OFF == command.action) {
            status = false
        }
        var mask = IPart.HEAD
        var fSuccess = mask != (mask and command.part)
        mask = IPart.TAIL
        var bSuccess = mask != (mask and command.part)
        if (!fSuccess) {
            fSuccess = doSetSwitchOption(SwitchNode.FRONT_AMBIENT_LIGHTING, status)
        }
        if (!bSuccess) {
            bSuccess = doSetSwitchOption(SwitchNode.BACK_AMBIENT_LIGHTING, status)
        }
        val intent = if (status) "打开" else "关闭"
        val result = if (fSuccess && bSuccess) "成功了" else "失败了"
        command.message = "$name$intent$result"
        callback?.onCmdHandleResult(command)
    }

    private fun findColorSerialNumberByColorName(colorName: String): Int{
        return when (colorName) {
            "红色" -> 1
            "橙色" -> 9
            "黄色" -> 17
            "白色" -> 22
            "绿色" -> 30
            "果绿色" -> 36
            "冰蓝色" -> 42
            "蓝色" -> 50
            "紫色" -> 56
            "玫红色" -> 64
            else -> Constant.INVALID
        }
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        if (ICar.AMBIENT == command.car) {
            if (!interruptCommand(command, callback)) {
                doSwitchAmbient(command, callback)
            }
        } else if (ICar.BRIGHTNESS == command.car) {
            if (!interruptCommand(command, callback)) {
                doAdjustAmbientBrightness(command, callback)
            }
        } else if (ICar.COLOR == command.car) {
            if (!interruptCommand(command, callback)) {
                doAdjustAmbientColor(command, callback)
            }
        } else if (ICar.RHYTHM_MODE == command.car) {
            if (!interruptCommand(command, callback)) {
                doUpdateAmbientRhythmMode(command, callback)
            }
        }
    }

}