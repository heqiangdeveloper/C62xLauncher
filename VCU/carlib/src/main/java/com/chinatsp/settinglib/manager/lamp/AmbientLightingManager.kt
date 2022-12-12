package com.chinatsp.settinglib.manager.lamp

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.IProgressManager
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.CommandParcel
import com.chinatsp.settinglib.bean.RadioState
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.bean.Volume
import com.chinatsp.settinglib.listener.IBaseListener
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.manager.ICmdExpress
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.ISignal
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.settinglib.sign.Origin
import com.chinatsp.vehicle.controller.ICmdCallback
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.IPart
import com.chinatsp.vehicle.controller.bean.CarCmd
import com.chinatsp.vehicle.controller.utils.Keywords
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.random.Random

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
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }
    private val alcLockHint: SwitchState by lazy {
        val node = SwitchNode.ALC_LOCK_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcBreatheHint: SwitchState by lazy {
        val node = SwitchNode.ALC_BREATHE_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcComingHint: SwitchState by lazy {
        val node = SwitchNode.ALC_COMING_HINT
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcRelatedTopics: SwitchState by lazy {
        val node = SwitchNode.ALC_RELATED_TOPICS
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val frontLighting: SwitchState by lazy {
        val node = SwitchNode.FRONT_AMBIENT_LIGHTING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val backLighting: SwitchState by lazy {
        val node = SwitchNode.BACK_AMBIENT_LIGHTING
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val alcSmartMode: SwitchState by lazy {
        val node = SwitchNode.ALC_SMART_MODE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val speedRhythm: SwitchState by lazy {
        val node = SwitchNode.SPEED_RHYTHM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val musicRhythm: SwitchState by lazy {
        val node = SwitchNode.MUSIC_RHYTHM
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

    private val colourBreathe: SwitchState by lazy {
        val node = SwitchNode.COLOUR_BREATHE
        return@lazy createAtomicBoolean(node) { result, value ->
            doUpdateSwitch(node, result, value, this::doSwitchChanged)
        }
    }

//    private val ambientBrightness: AtomicInteger by lazy {
//        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
//        AtomicInteger(node.def).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            val value = if (result in node.min..node.max) result else node.def
//            Timber.d("getBrightness node:$node, result:$result,, value:$value")
//            this.set(value)
//        }
//    }
//
//    private val ambientColor: AtomicInteger by lazy {
//        val node = Progress.AMBIENT_LIGHT_COLOR
//        AtomicInteger(node.def).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            Timber.d("getAmbientColor signal:%s, value:%s", node.get.signal, value)
//            doUpdateProgress(node, this, value, null)
//        }
//    }


    private val ambientBrightness: Volume by lazy {
        initProgress(Progress.AMBIENT_LIGHT_BRIGHTNESS)
//        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
//        AtomicInteger(node.def).apply {
//            val result = readIntProperty(node.get.signal, node.get.origin)
//            val value = if (result in node.min..node.max) result else node.def
//            Timber.d("getBrightness node:$node, result:$result,, value:$value")
//            this.set(value)
//        }
    }

    private val ambientColor: Volume by lazy {
        initProgress(Progress.AMBIENT_LIGHT_COLOR)
//        val node = Progress.AMBIENT_LIGHT_COLOR
//        AtomicInteger(node.def).apply {
//            val value = readIntProperty(node.get.signal, node.get.origin)
//            Timber.d("getAmbientColor signal:%s, value:%s", node.get.signal, value)
//            doUpdateProgress(node, this, value, null)
//        }
    }

    private fun initProgress(type: Progress): Volume {
        val result = readIntProperty(type.get.signal, type.get.origin)
        val value = if (result in type.min..type.max) result else type.def
        Timber.d("initProgress type:$type, result:$result,, value:$value")
        return Volume(type, type.min, type.max, value)
    }

    override fun doGetRadioOption(node: RadioNode): RadioState? {
        return null
    }

    override fun doSetRadioOption(node: RadioNode, value: Int): Boolean {
        return false
    }

    override fun doGetVolume(progress: Progress): Volume? {
        return when (progress) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> ambientBrightness
            Progress.AMBIENT_LIGHT_COLOR -> ambientColor
            else -> null
        }

    }

    override fun doSetVolume(node: Progress, value: Int): Boolean {
        return when (node) {
            Progress.AMBIENT_LIGHT_BRIGHTNESS -> {
                val set = node.set
                val result = writeProperty(set.signal, value + 1, set.origin)
                Timber.d("setBrightness signal:${set.signal}, value:$value, result:$result")
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
            SwitchNode.ALC_DOOR_HINT -> alcDoorHint.deepCopy()
            SwitchNode.ALC_LOCK_HINT -> alcLockHint.deepCopy()
            SwitchNode.ALC_BREATHE_HINT -> alcBreatheHint.deepCopy()
            SwitchNode.ALC_COMING_HINT -> alcComingHint.deepCopy()
            SwitchNode.ALC_RELATED_TOPICS -> alcRelatedTopics.deepCopy()
            SwitchNode.FRONT_AMBIENT_LIGHTING -> frontLighting.deepCopy()
            SwitchNode.BACK_AMBIENT_LIGHTING -> backLighting.deepCopy()
            SwitchNode.ALC_SMART_MODE -> alcSmartMode.deepCopy()
            SwitchNode.SPEED_RHYTHM -> speedRhythm.deepCopy()
            SwitchNode.MUSIC_RHYTHM -> musicRhythm.deepCopy()
            SwitchNode.COLOUR_BREATHE -> colourBreathe.deepCopy()
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
            Timber.d("onAmbientBrightnessChanged value:%s", value)
            doUpdateProgress(ambientBrightness, value, true, this::doProgressChanged)

        }
    }

    private fun onAmbientColorChanged(property: CarPropertyValue<*>) {
        val value = property.value
        if (value is Int) {
            Timber.d("onAmbientColorChanged value:%s", value)
            val progress = Progress.AMBIENT_LIGHT_COLOR
            doUpdateProgress(ambientColor, value, true, this::doProgressChanged)
        }
    }

    private fun writeProperty(node: SwitchNode, value: Boolean, atomic: SwitchState): Boolean {
        val success = writeProperty(node.set.signal, node.value(value), node.set.origin)
        if (success && develop) {
            doUpdateSwitch(node, atomic, value) { _node, _value ->
                doSwitchChanged(_node, _value)
            }
        }
        return success
    }

    private fun interruptCommand(parcel: CommandParcel, coreEngine: Boolean = false): Boolean {
        val result = if (coreEngine) {
            !VcuUtils.isPower() || !VcuUtils.isEngineRunning()
        } else {
            !VcuUtils.isPower()
        }
        if (result) {
            parcel.command.message = "操作没有成功，请先启动发动机"
            parcel.callback?.onCmdHandleResult(parcel.command)
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

    private fun doUpdateAmbientRhythmMode(parcel: CommandParcel) {
        var modeName = ""
        val result: String
        val command = parcel.command
        val callback = parcel.callback
        val expect = Action.TURN_ON == command.action
        val option = if (expect) "开启" else "关闭"
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
        if (!isAmbient()) {
            command.message = "氛围灯未打开，暂无法${option}${modeName}"
            callback?.onCmdHandleResult(command)
            return
        }
        if (!alcSmartMode.get()) {
            command.message = "智能模式未打开，暂无法${option}${modeName}"
            callback?.onCmdHandleResult(command)
            return
        }
        if (SwitchNode.INVALID != node) {
            val resultStatus = doSetSwitchOption(node, expect)
            result = if (resultStatus) "成功" else "失败"
            command.message = "$modeName$option$result"
        } else {
            command.message = Keywords.COMMAND_FAILED
        }
        callback?.onCmdHandleResult(command)
    }

    private fun doAdjustAmbientColor(parcel: CommandParcel) {
        when (parcel.command.action) {
            Action.PLUS, Action.MINUS, Action.MIN, Action.MAX, Action.FIXED -> {
                attemptUpdateAmbientColor(parcel)
            }
            else -> {}
        }
    }

    private fun doAdjustAmbientBrightness(parcel: CommandParcel) {
        when (parcel.command.action) {
            Action.PLUS, Action.MINUS, Action.MIN, Action.MAX, Action.FIXED -> {
                attemptUpdateAmbientBrightness(parcel)
            }
            else -> {}
        }
    }

    private fun attemptUpdateAmbientBrightness(parcel: CommandParcel) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        val modelName = command.slots?.name ?: "氛围灯"
        if (!isAmbient()) {
            command.message = "${modelName}未开启，暂无法调整${modelName}亮度, 请您先打开$modelName"
            callback?.onCmdHandleResult(command)
            return
        }
        val node = Progress.AMBIENT_LIGHT_BRIGHTNESS
        val expect = computeLampBrightness(command, node.min, node.max) + 1
        val result = writeProperty(node.set.signal, expect, node.set.origin)
        if (!result) {
            command.message = Keywords.COMMAND_FAILED
        } else {
            val value = expect - 1
            command.message = when (value) {
                node.min -> "${modelName}亮度已设置为最暗了"
                node.max -> "${modelName}亮度已设置为最亮了"
                else -> "${modelName}亮度已设置为${value}档了"
            }
        }
        callback?.onCmdHandleResult(command)
    }

    private fun attemptUpdateAmbientColor(parcel: CommandParcel) {
        val command = parcel.command
        val callback = parcel.callback
        Timber.e("attemptUpdateAmbientColor mode:${command.slots?.mode}," +
                " name:${command.slots?.name}, color:${command.slots?.color}")
        val modelName = command.slots?.name ?: "氛围灯"
        if (!isAmbient()) {
            command.message = "${modelName}未开启，暂无法调整${modelName}颜色"
            callback?.onCmdHandleResult(command)
            return
        }
        val node = Progress.AMBIENT_LIGHT_COLOR
        var colorName = command.slots?.color ?: "随机"
        val colors = obtainSupportColor()
        var colorIndex: Int? = colors[colorName]
        if (null == colorIndex) {
            val randomIndex = Random.nextInt(colors.size)
            val colorList = colors.toList()
            val pair = colorList[randomIndex]
            colorIndex = pair.second
            colorName = pair.first
        }
        if (Constant.INVALID == colorIndex) {
            command.message = "${modelName}不支持该颜色"
        } else {
            val result = writeProperty(node.set.signal, colorIndex, node.set.origin)
            command.message =
                if (!result) Keywords.COMMAND_FAILED else "${modelName}颜色已设置为${colorName}了"
        }
        callback?.onCmdHandleResult(command)
    }

    private fun computeLampColor(command: CarCmd, min: Int, max: Int): Int {
        var expect = 0
        if (Action.PLUS == command.action) {
            val current = ambientColor.pos
            expect = current + command.step
        } else if (Action.MINUS == command.action) {
            val current = ambientColor.pos
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
            val current = ambientBrightness.pos
            expect = current + command.step
        } else if (Action.MINUS == command.action) {
            val current = ambientBrightness.pos
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
        command.lfExpect = expect
        return expect
    }

    private fun doSwitchAmbient(parcel: CommandParcel) {
        val command = parcel.command
        val callback = parcel.callback
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


    private fun obtainSupportColor(): Map<String, Int> {
        val map = HashMap<String, Int>()
        map["红色"] = 1
        map["橙色"] = 9
        map["黄色"] = 17
        map["白色"] = 22
        map["绿色"] = 30
        map["果绿色"] = 36
        map["冰蓝色"] = 42
        map["蓝色"] = 50
        map["紫色"] = 56
        map["玫红色"] = 64
        return map
    }

    override fun doCommandExpress(parcel: CommandParcel, fromUser: Boolean) {
        val command = parcel.command as CarCmd
        val callback = parcel.callback
        if (ICar.AMBIENT == command.car) {
            if (!interruptCommand(parcel)) {
                doSwitchAmbient(parcel)
            }
        } else if (ICar.BRIGHTNESS == command.car) {
            if (!interruptCommand(parcel)) {
                doAdjustAmbientBrightness(parcel)
            }
        } else if (ICar.COLOR == command.car) {
            if (!interruptCommand(parcel)) {
                doAdjustAmbientColor(parcel)
            }
        } else if (ICar.RHYTHM_MODE == command.car) {
            if (!interruptCommand(parcel)) {
                doUpdateAmbientRhythmMode(parcel)
            }
        }
    }

}