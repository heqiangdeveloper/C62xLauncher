package com.chinatsp.settinglib

import android.car.Car
import android.car.VehicleAreaType
import android.car.hardware.CarPropertyValue
import android.car.hardware.CarSensorEvent
import android.car.hardware.CarSensorManager
import android.car.hardware.cabin.CarCabinManager
import android.car.hardware.constant.VEHICLE
import android.car.hardware.hvac.CarHvacManager
import android.car.hardware.hvac.CarHvacManager.CarHvacEventCallback
import android.car.hardware.mcu.CarMcuManager
import android.car.hardware.mcu.CarMcuManager.CarMcuEventCallback
import android.car.hardware.power.CarPowerManager
import android.car.media.CarAudioManager
import android.car.tbox.TboxManager
import android.car.tbox.TboxManager.TboxChangedListener
import android.car.tbox.aidl.NetworkInformation
import android.car.tbox.aidl.RemoteVedioInformation
import android.car.tbox.aidl.TruckInformation
import android.car.tbox.aidl.XCallInformation
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.cabinSignal
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.hvacSignal
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.mcuSignal
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import java.util.*


/**
 * @author
 */
class SettingManager private constructor() {

    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var car: Car? = null
    private var isConnected = false
    private var audioManager: CarAudioManager? = null
    private var mcuManager: CarMcuManager? = null
    private var powerManager: CarPowerManager? = null
    private var mBoxManager: TboxManager? = null
    private var sensorManager: CarSensorManager? = null
    private var cabinManager: CarCabinManager? = null
    private var hvacManager: CarHvacManager? = null


    private fun unBindService() {
        try {
            mcuManager?.unregisterCallback(mcuEventListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            cabinManager?.unregisterCallback(cabinEventListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            hvacManager?.unregisterCallback(hvacEventListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Timber.d("unbind car service")
    }

    private fun bindVehicleService() {
        takeUnless { null != car && car!!.isConnected }?.onBindService()
    }

    private fun onBindService() {
        car = Car.createCar(context, CarServiceConnection())
        Optional.ofNullable(car).ifPresent { it.connect() }
        Timber.d("bind car service carApi:$car")
    }

    private val reconnection = Runnable {
        unBindService()
        bindVehicleService()
    }

    private fun setDYCarbinProperty(id: Int, v: Int) {
        try {
            cabinManager!!.setIntProperty(id, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, v)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun onRegisterMcuListener() {
        try {
            checkConnected("register mcu listener")
            mcuManager = car!!.getCarManager(Car.CAR_MCU_SERVICE) as CarMcuManager
            mcuManager?.let { manager ->
                val signals = mcuSignal.stream().filter { it != Constant.INVALID }
                    .mapToInt { obj: Int -> obj }.toArray()
                logSignal(signals, "mcu")
                manager.registerCallback(mcuEventListener, signals)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }

    private fun logSignal(signals: IntArray, serial: String) {
        Arrays.stream(signals).forEach {
            val hex = Integer.toHexString(it)
            Timber.tag(Constant.VehicleSignal)
                .d("register $serial: hex:${hex},  dec:$it, ${VcuUtils.V_N}")
        }
    }


    private fun onRegisterCabinListener() {
        try {
            checkConnected("register cabin listener")
            cabinManager = car!!.getCarManager(Car.CABIN_SERVICE) as CarCabinManager
            cabinManager?.let { manager ->
                val signals = cabinSignal.stream().filter { it != Constant.INVALID }
                    .mapToInt { obj: Int -> obj }.toArray()
                logSignal(signals, "cabin")
                manager.registerCallback(cabinEventListener, signals)
                BrightnessManager.instance.initDarkLightMode()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }

    private val cabinEventListener = object : CarCabinManager.CarCabinEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
            Timber.tag(Constant.VehicleSignal)
                .d("doActionSignal receive-cabin hex-id::${Integer.toHexString(id)}, dec-id:$id value:${property.value}, ${VcuUtils.V_N}")
            GlobalManager.instance.onDispatchSignal(property, Origin.CABIN)
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.tag(Constant.VehicleSignal).e("Cabin onErrorEvent:i:$i, i1:$i1")
        }
    }

    private val mcuEventListener = object : CarMcuEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
            Timber.tag(Constant.VehicleSignal)
                .d("doActionSignal receive-mcu hex-id::${Integer.toHexString(id)}, dec-id:$id value:${property.value}, ${VcuUtils.V_N}")
            GlobalManager.instance.onDispatchSignal(property, Origin.MCU)
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.tag(Constant.VehicleSignal).e("Mcu onErrorEvent:i:$i, i1:$i1")
        }
    }

    private val hvacEventListener = object : CarHvacEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
            Timber.tag(Constant.VehicleSignal)
                .d("doActionSignal receive-hvac hex-id::${Integer.toHexString(id)}, dec-id:$id value:${property.value}")
            GlobalManager.instance.onDispatchSignal(property, Origin.HVAC)
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.tag(Constant.VehicleSignal).e("Hvac onErrorEvent:i:$i, i1:$i1")
        }
    }

    //回调的点火状态
    val isIgnitionStateON: Boolean
        get() {
            try {
                return sensorManager!!.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_IGNITION_STATE)
                    .getIgnitionStateData(null).ignitionState > CarSensorEvent.IGNITION_STATE_OFF //回调的点火状态
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    private val sensorEventlistener = CarSensorManager.OnSensorChangedListener { carSensorEvent ->
        //LogUtils.Timber.d("onSensorChanged " + carSensorEvent.sensorType);
        when (carSensorEvent.sensorType) {
            CarSensorManager.SENSOR_TYPE_IGNITION_STATE -> {}
            CarSensorManager.SENSOR_TYPE_NIGHT -> {
                val isNight = carSensorEvent.getNightData(null).isNightMode //小灯，白天黑夜
                Timber.d("littleLight change,isNight state=$isNight")
            }
        }
    }
    val isLittleLightON: Boolean
        get() {
            try {
                val carSensorEvent =
                    sensorManager!!.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_NIGHT)
                return carSensorEvent.getNightData(null).isNightMode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    //下发关 -- 5，下发开 -- 6， 上报关 -- 7， 上报开 -- 8
    fun setAvmOff() {
        Timber.d("3Y1 setAvmOff")
        setDYCarbinProperty(CarCabinManager.ID_AVM_DISPLAY_SWITCH, VEHICLE.OFF)
    }

    val isAccOff: Boolean
        get() {
            try {
                return mcuManager!!.getIntProperty(
                    CarMcuManager.ID_MCU_ACC_STATE, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
                ) == CarSensorEvent.IGNITION_STATE_OFF
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    //是否打开倒车
    val isReverse: Boolean
        get() {
            try {
                return mcuManager!!.getIntProperty(
                    CarMcuManager.ID_REVERSE_SIGNAL, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
                ) == VEHICLE.ON
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }//LogUtils.d("ID_MCU_LOST_CANID= "+ Arrays.toString(v));

    //是否CAN丢失了,CAN节点丢失 0x00-正常 0x01-丢失* int[6]: AVM
    //     * int[7]: APA
    val isCanIdLost: Boolean
        get() {
            try {
                val v = mcuManager!!.getIntArrayProperty(
                    CarMcuManager.ID_MCU_LOST_CANID,
                    VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
                )
                //LogUtils.d("ID_MCU_LOST_CANID= "+ Arrays.toString(v));
                if (v[6] == 1 || v[7] == 1) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }


    fun doSetProperty(id: Int, value: IntArray, origin: Origin?, area: Area): Boolean {
        return if (!isConnected) {
            false
        } else when (origin) {
            Origin.MCU -> doSetMcuProperty(id, value, area.id)
            Origin.CABIN -> doSetCabinProperty(id, value, area.id)
            else -> false
        }
    }

    fun doSetProperty(id: Int, value: Int, origin: Origin?, area: Area): Boolean {
        return if (!isConnected) {
            false
        } else when (origin) {
            Origin.CABIN -> doSetCabinProperty(id, value, area.id)
            Origin.HVAC -> doSetHvacProperty(id, value, area.id)
            else -> false
        }
    }

    fun doSetProperty(id: Int, value: Int, origin: Origin?, areaValue: Int): Boolean {
        return if (!isConnected) {
            false
        } else when (origin) {
            Origin.CABIN -> doSetCabinProperty(id, value, areaValue)
            Origin.HVAC -> doSetHvacProperty(id, value, areaValue)
            else -> false
        }
    }

    private fun doSetCabinProperty(id: Int, value: Int, areaValue: Int): Boolean {
        if (null != cabinManager) {
            AppExecutors.get()?.networkIO()?.execute {
                try {
                    val hasManager = null != audioManager
                    Timber.tag(Constant.VehicleSignal)
                        .d("doActionSignal-cabin send-cabin hex-id:${Integer.toHexString(id)}, dec-id:$id, value:$value, has:$hasManager, V_N:${VcuUtils.V_N}")
                    cabinManager?.setIntProperty(id, areaValue, value)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            return true
        }
        return false
    }

    private fun doSetMcuProperty(id: Int, value: IntArray, areaValue: Int): Boolean {
        if (null != mcuManager) {
            AppExecutors.get()?.networkIO()?.execute {
                try {
                    val hasManager = null != mcuManager
                    Timber.tag(Constant.VehicleSignal)
                        .d("doActionSignal send-mcu hex-id:${Integer.toHexString(id)}, dec-id:$id, value:${
                            convert(value)
                        }, has:$hasManager, V_N:${VcuUtils.V_N}")
                    mcuManager?.setIntArrayProperty(id, areaValue, value)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            return true
        }
        return false
    }

    private fun doSetCabinProperty(id: Int, value: IntArray, areaValue: Int): Boolean {
        if (null != cabinManager) {
            AppExecutors.get()?.networkIO()?.execute {
                try {
                    val hasManager = null != cabinManager
                    Timber.tag(Constant.VehicleSignal)
                        .d("doActionSignal send-cabin hex-id:${Integer.toHexString(id)}, dec-id:$id, value:${
                            convert(value)
                        }, has:$hasManager, V_N:${VcuUtils.V_N}")
                    cabinManager?.setIntArrayProperty(id, areaValue, value)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            return true
        }
        return false
    }

    private fun convert(value: IntArray): String {
        val builder = StringBuilder()
        value.forEach { builder.append(it).append(",") }
        return builder.toString()
    }

    private fun doSetHvacProperty(id: Int, value: Int, areaValue: Int): Boolean {
        if (null != hvacManager) {
            AppExecutors.get()?.networkIO()?.execute {
                try {
                    val hasManager = null != hvacManager
                    Timber.tag(Constant.VehicleSignal)
                        .d("doActionSignal-hvac send-hvac hex-id:${Integer.toHexString(id)}, dec-id:$id, value:$value, has:$hasManager, V_N:${VcuUtils.V_N}")
                    hvacManager?.setIntProperty(id, areaValue, value)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            return true
        }
        return false
    }

    private fun readCabinIntValue(id: Int, areaValue: Int): Int {
        var result = Constant.DEFAULT
        try {
            result = cabinManager?.getIntProperty(id, areaValue) ?: Constant.INVALID
            Timber.d("readCabinIntValue propertyId:$id, result:$result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun readCabinFloatValue(id: Int, areaValue: Int): Float {
        var result = Constant.DEFAULT.toFloat()
        try {
            result = cabinManager?.getFloatProperty(id, areaValue) ?: result
            Timber.d("readCabinFloatValue propertyId:$id, result:$result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun readHvacIntValue(id: Int, areaValue: Int): Int {
        var result = Constant.DEFAULT
        try {
            result = hvacManager?.getIntProperty(id, areaValue) ?: Constant.INVALID
            Timber.d("readHvacIntValue propertyId:$id, result:$result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun readMcuIntArray(id: Int, areaValue: Int): IntArray {
        var result = IntArray(0)
        try {
            result = mcuManager?.getIntArrayProperty(id, areaValue) ?: result
            Timber.d("readMcuIntArray propertyId:$id, result:$result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun readIntProperty(id: Int, origin: Origin, area: Area): Int {
        return readIntProperty(id, origin, area.id)
    }

    fun readIntArray(id: Int, origin: Origin, area: Area): IntArray {
        return readIntArray(id, origin, area.id)
    }

    fun readIntProperty(id: Int, origin: Origin, areaValue: Int): Int {
        var result = Constant.DEFAULT
        if (!isConnected) {
            Timber.d("readIntProperty propertyId:$id, origin:$origin, connectService: false!")
            return result
        }
        if (Origin.CABIN === origin) {
            result = readCabinIntValue(id, areaValue)
        } else if (Origin.HVAC === origin) {
            result = readHvacIntValue(id, areaValue)
        }
        return result
    }

    fun readFloatProperty(id: Int, origin: Origin, area: Area): Float {
        return readFloatProperty(id, origin, area.id)
    }

    private fun readFloatProperty(id: Int, origin: Origin, areaValue: Int): Float {
        var result = Constant.DEFAULT.toFloat()
        if (!isConnected) {
            Timber.d("readIntProperty propertyId:$id, origin:$origin, connectService: false!")
            return result
        }
        if (Origin.CABIN === origin) {
            result = readCabinFloatValue(id, areaValue)
        }
        return result
    }

    private fun readIntArray(id: Int, origin: Origin, areaValue: Int): IntArray {
        var result: IntArray = IntArray(0)
        if (!isConnected) {
            Timber.d("readIntProperty propertyId:$id, origin:$origin, connectService: false!")
            return result
        }
        if (Origin.MCU === origin) {
            result = readMcuIntArray(id, areaValue)
        } else if (Origin.CABIN == origin) {
            result = readCabinIntArray(id, areaValue)
        }
        return result
    }

    private fun readCabinIntArray(id: Int, areaValue: Int): IntArray {
        var result = IntArray(0)
        try {
            result = cabinManager?.getIntArrayProperty(id, areaValue) ?: result
            Timber.d("readCabinIntArray propertyId:$id, result:${result.size}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun readProperty(id: Int, origin: Origin, area: Area, block: ((Int) -> Unit)) {
        readProperty(id, origin, area.id, block)
    }

    private fun readProperty(id: Int, origin: Origin, areaValue: Int, block: ((Int) -> Unit)) {
        if (!isConnected) {
            Timber.d("readIntProperty propertyId:$id, origin:$origin, connectService: false!")
//            block(Constant.DEFAULT)
            return
        }
        AppExecutors.get()?.networkIO()?.execute {
            if (Origin.CABIN === origin) {
                val result = readCabinIntValue(id, areaValue)
                block(result)
            } else if (Origin.HVAC === origin) {
                val result = readHvacIntValue(id, areaValue)
                block(result)
            }
        }
    }

    fun writeBoxValue() {
        try {
            mBoxManager?.truckInformation?.onOff
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    interface IMobileState {
        fun onMobileStateChange(on: Boolean)
        fun onMobileStateError()
    }


    private var mBalanceLevelValue = 0
    private var mFadeLevelValue = 0

    /**
     * 传入-9到9的值 // 1->19 内置
     * -5 -> 5 转化为整数  1 -> 11 外置
     *
     * @param uiBalanceLevelValue 传入-9到9的值 // -5 -> 5
     * @param uiFadeLevelValue    传入-9到9的值   // -5 -> 5
     */
    fun setAudioBalance(uiBalanceLevelValue: Int, uiFadeLevelValue: Int) {
        try {
            val muiBalanceLevelValue = uiBalanceLevelValue;
            val muiFadeLevelValue = uiFadeLevelValue;
//            if (VcuUtils.isAmplifier) { //内置
            //    muiBalanceLevelValue = uiBalanceLevelValue + 10;
            //   muiFadeLevelValue = uiFadeLevelValue + 10;
//            } else {
            //     muiBalanceLevelValue = uiBalanceLevelValue + 6;
            //      muiFadeLevelValue = uiFadeLevelValue + 6;
//            }
            Timber.d(
                "setAudioBalance muiBalanceLevelValue=${muiBalanceLevelValue}  " +
                        " muiFadeLevelValue=${muiFadeLevelValue}"
            )
            audioManager?.setBalFadBalance(muiBalanceLevelValue, muiFadeLevelValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAudioVoice(id: Int): Int {
        var result = Constant.INVALID
        try {
            result = audioManager?.getAudioVoice(id) ?: Constant.INVALID
//            Timber.d("getAudioVoice id:$id result:$result")
        } catch (e: Throwable) {
            Timber.d("e=" + e.message)
        }
        return result
    }

    //-7,7
    val audioFade: Int
        get() {
            try {
                mFadeLevelValue = audioManager!!.fadeTowardFront
                return mFadeLevelValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -100
        }

    fun getEQ(): Int? {
        return audioManager?.eqMode
    }

    fun setAudioEQ(eqMode: Int, optionId: Int, eqValues: IntArray) {
        try {
            audioManager?.let {
                it.eqMode = eqMode
                val isCustom = RadioNode.SYSTEM_SOUND_EFFECT.get.values.last() == optionId
                val builder = StringBuilder()
                builder.append("setAudioEQ mode:$eqMode, isCustom:$isCustom")
                if (isCustom && Constant.EQ_SIZE == eqValues.size) {
                    Constant.EQ_LEVELS.forEachIndexed { index, eqId ->
                        setAudioVoice(eqId, eqValues[index])
                        builder.append(", lev${eqId}:${eqValues[index]}")
                    }
                }
                Timber.d(builder.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAudioEQ(eqMode: Int) {
        try {
            Timber.d("setAudioEQ-----------------eqMode=$eqMode")
            audioManager?.eqMode = eqMode
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAudioVoice(id: Int, value: Int) {
        try {
            audioManager?.setAudioVoice(id, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAudioBalance(): Int {
        try {
            mBalanceLevelValue = audioManager!!.balanceTowardRight
            return mBalanceLevelValue
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return -100
    }

    private inner class CarServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("vcu-system-service onServiceConnected start")
            isConnected = true
            onRegisterBoxListener()
            initAudioManager()
            onRegisterMcuListener()
            onRegisterSensorListener()
            onRegisterCabinListener()
            onRegisterHvacListener()
            onRegisterPowerListener()
            Timber.d("vcu-system-service onServiceConnected end")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.e("vcu-system-service onServiceDisconnected")
            isConnected = false
            mHandler.removeCallbacks(reconnection)
            mHandler.postDelayed(reconnection, 1500)
        }

        override fun onBindingDied(name: ComponentName) {
            Timber.e("vcu-system-service onBindingDied")
            isConnected = false
            mHandler.removeCallbacks(reconnection)
            mHandler.postDelayed(reconnection, 1500)
        }
    }

    private fun checkConnected(serial: String) {
        if (!isConnected) throw IllegalStateException("system service is not connected! $serial")
    }

    private fun onRegisterPowerListener() {
        try {
            checkConnected("register power listener")
            powerManager = car!!.getCarManager(Car.POWER_SERVICE) as CarPowerManager
            powerManager?.let { BrightnessManager.instance.injectManager(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }

    private fun initAudioManager() {
        try {
            checkConnected("register audio listener")
            audioManager = car?.getCarManager(Car.AUDIO_SERVICE) as CarAudioManager
            audioManager?.let { VoiceManager.instance.injectAudioManager(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }

    private fun onRegisterHvacListener() {
        try {
            checkConnected("register hvac listener")
            hvacManager = car!!.getCarManager(Car.HVAC_SERVICE) as CarHvacManager
            hvacManager?.let {
                val signals = hvacSignal.stream().mapToInt { obj: Int -> obj }.toArray()
                logSignal(signals, "hvac")
                it.registerCallback(hvacEventListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }


    private fun onRegisterBoxListener() {
        try {
            if (null == mBoxManager) {
                val manager = car!!.getCarManager(Car.TBOX_SERVICE)
                if (manager is TboxManager) {
                    mBoxManager = manager
                    Timber.d("TBOX --- obtain boxManager from carApi mBoxManager:$mBoxManager")
                }
                if (null == mBoxManager) {
                    mBoxManager = TboxManager.getInstance()
                    Timber.d("TBOX --- obtain boxManager from getInstance:$mBoxManager")
                }
                mBoxManager!!.addTBoxChangedListener(boxChangedListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onRegisterSensorListener() {
        try {
            checkConnected("register sensor listener")
            sensorManager = car!!.getCarManager(Car.SENSOR_SERVICE) as CarSensorManager
            sensorManager?.let { manager ->
                var rate = CarSensorManager.SENSOR_RATE_NORMAL
                var type = CarSensorManager.SENSOR_TYPE_IGNITION_STATE
                manager.registerListener(sensorEventlistener, type, rate) //acc
                rate = CarSensorManager.SENSOR_RATE_NORMAL
                type = CarSensorManager.SENSOR_TYPE_NIGHT
                manager.registerListener(sensorEventlistener, type, rate) //小灯，白天黑夜注册监听
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e.message)
        }
    }

    fun getTrailerSwitch(): Int? {
        try {
            return obtainTrailer(serial = "getSwitch")?.onOff
        } catch (e: Throwable) {
            e.printStackTrace()
            Timber.e("getTrailerSwitch exception:${e.message}")
        }
        return null
    }

    fun getTrailerLevel(): Int? {
        try {
            return obtainTrailer(serial = "getLevel")?.level
        } catch (e: Throwable) {
            e.printStackTrace()
            Timber.e("getTrailerLevel exception:${e.message}")
        }
        return null
    }

    fun getTrailerDist(): Int? {
        try {
            return obtainTrailer(serial = "getDist")?.dist
        } catch (e: Throwable) {
            e.printStackTrace()
            Timber.e("getTrailerDist exception:${e.message}")
        }
        return null
    }

    private fun obtainTrailer(serial: String): TruckInformation? {
        if (null == mBoxManager) {
            Timber.e("obtainTrailer but manager is null!! serial:$serial")
            return null
        }
        val trailer = mBoxManager!!.truckInformation
        if (null == trailer) {
            Timber.e("obtainTrailer but trailer is null!! serial:$serial")
            return null
        }
        Timber.e("obtainTrailer switch:${trailer.onOff}, level:${trailer.level}, dist:${trailer.dist}! serial:$serial")
        return trailer
    }

    fun updateTrailer(swt: Int = 0, dist: Int = 0, level: Int = 0, serial: String) {
        AppExecutors.get()?.networkIO()?.execute {
            try {
                val trailer = obtainTrailer(serial)
                val isTrailer = null != trailer
                val hasManager = null != mBoxManager
                Timber.d("updateTrailer isTrailer $isTrailer, hasManager:$hasManager" +
                        ", swt:$swt, dist:$dist, level:$level, serial:$serial")
                if (isTrailer && hasManager) {
                    trailer!!.dist = dist
                    trailer!!.onOff = swt
                    trailer!!.level = level
                    mBoxManager!!.truckInformation = trailer
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private val boxChangedListener = object : TboxChangedListener {
        override fun onCallStatusChanged(p0: XCallInformation?) {

        }

        override fun onNetworkInfoChanged(p0: NetworkInformation?) {

        }

        override fun onTruckInfoChanged(truckInformation: TruckInformation?) {
            truckInformation?.let {
                OtherManager.instance.onTrailerRemindChanged(it.onOff, it.level, it.dist)
            }
        }

        override fun onRemoteVedioInfoChanged(p0: RemoteVedioInformation?) {

        }

    }

    companion object {
        const val TAG = "CarCabinManager"

        val context: Context by lazy {
            BaseApp.instance.applicationContext
        }

        val instance: SettingManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SettingManager()
        }

    }

    init {
        bindVehicleService()
    }


}