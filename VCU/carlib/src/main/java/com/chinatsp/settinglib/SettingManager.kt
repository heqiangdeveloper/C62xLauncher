package com.chinatsp.settinglib

import android.car.Car
import android.car.CarNotConnectedException
import android.car.VehicleAreaType
import android.car.VehiclePropertyIds
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
import android.car.hardware.property.CarPropertyManager
import android.car.media.CarAudioManager
import android.car.tbox.TboxManager
import android.car.tbox.TboxManager.TboxChangedListener
import android.car.tbox.aidl.NetworkInformation
import android.car.tbox.aidl.RemoteVedioInformation
import android.car.tbox.aidl.TruckInformation
import android.car.tbox.aidl.XCallInformation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.hardware.automotive.vehicle.V2_0.VehicleProperty
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import com.android.internal.app.LocalePicker
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.cabinSignal
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.hvacSignal
import com.chinatsp.settinglib.manager.RegisterSignalManager.Companion.mcuSignal
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.manager.lamp.BrightnessManager
import com.chinatsp.settinglib.manager.sound.VoiceManager
import com.chinatsp.settinglib.optios.Area
import com.chinatsp.settinglib.optios.SoundEffect
import com.chinatsp.settinglib.sign.Origin
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors


/**
 * @author
 */
class SettingManager private constructor() {

    private val mAudioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val productName: String by lazy {
        SystemProperties.get("ro.product.name")
    }

    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var mCarAudioManager: CarAudioManager? = null
    private var mCarMcuManager: CarMcuManager? = null
    private var mCarPowerManager: CarPowerManager? = null
    private var mBoxManager: TboxManager? = null
    private var mCarSensorManager: CarSensorManager? = null
    private var mCarCabinManager: CarCabinManager? = null
    private var hvacManager: CarHvacManager? = null
    private var mCarApi: Car? = null
    private var connectService = false


    private fun unBindService() {
        try {
            mCarMcuManager?.unregisterCallback(mcuEventListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            mCarCabinManager?.unregisterCallback(cabinEventListener)
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
        takeUnless { null != mCarApi && mCarApi!!.isConnected }?.onBindService()
    }

    private fun onBindService() {
        mCarApi = Car.createCar(context, CarServiceConnection())
        Optional.ofNullable(mCarApi).ifPresent { it.connect() }
        Timber.d("bind car service carApi:$mCarApi")
    }

    private val reConnectCarRunnable = Runnable {
        unBindService()
        bindVehicleService()
    }

    fun setDYCarbinProperty(id: Int, v: Int) {
        try {
            mCarCabinManager!!.setIntProperty(id, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, v)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mCarAdapterList: MutableList<CarAdapter> = ArrayList()
    fun addCarListener(carAdapter: CarAdapter) {
        if (!mCarAdapterList.contains(carAdapter)) {
            mCarAdapterList.add(carAdapter)
        }
    }

    fun removeCarListener(carAdapter: CarAdapter) {
        mCarAdapterList.remove(carAdapter)
    }

    private fun onRegisterMcuListener() {
        if (!connectService) {
            return
        }
        try {
            if (null == mCarMcuManager) {
                mCarMcuManager = mCarApi!!.getCarManager(Car.CAR_MCU_SERVICE) as CarMcuManager
            }
            if (null != mCarMcuManager) {
//                Set<Integer> signals = GlobalManager.Companion.getInstance().getConcernedSignal(SignalOrigin.MCU_SIGNAL);
                val signals = mcuSignal
                val signalArray =
                    signals.stream().filter { it != -1 }.mapToInt { obj: Int -> obj }.toArray()
                Arrays.stream(signalArray).forEach {
                    Timber.d("register MCU: hex propertyId:${Integer.toHexString(it)},  dec propertyId:$it")
                }
                mCarMcuManager!!.registerCallback(mcuEventListener, signalArray)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }//      return mCarDYCabinManager.getIntProperty(CarDYCabinManager.ID_AVM_DISPLAY_SWITCH, VEHICLE_AREA_TYPE_GLOBAL) == AVM_ON;

    //是否打开全景了
    val isAvmOn = false

    //            int value = mCarCabinManager.getIntProperty(CarCabinManager.ID_ADAS_APA_PARK_NOTICE_INFO, VEHICLE_AREA_TYPE_GLOBAL);
//            return value != 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    //是否打开了自动泊车
    val isApaOn = false
    private fun obtainSignals(type: Origin): Set<Int> {
//        val values: Collection<TabBlock> = TabSignManager.instance.tabSignMap.values
        val signalSet: MutableSet<Int> = HashSet()
//        values.stream().forEach { tab: TabBlock ->
//            tab.signals.stream().forEach { carSign: CarSign ->
//                if (type === carSign.type) {
//                    signalSet.addAll(carSign.signals)
//                }
//            }
//        }
        return signalSet
    }

    fun setGPSData(data: FloatArray?) {
        try {
            mCarCabinManager!!.setFloatArrayProperty(
                VehiclePropertyIds.DVR_CUR_LOCATION,
                VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL,
                data
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onRegisterCabinListener() {
        if (!connectService) {
            return
        }
        try {
            if (null == mCarCabinManager) {
                mCarCabinManager = mCarApi!!.getCarManager(Car.CABIN_SERVICE) as CarCabinManager
            }
            if (null != mCarCabinManager) {
//                    int[] signalArray = Arrays.stream(Cabin.values())
//                            .flatMapToInt(cabin -> Arrays.stream(cabin.getSignals())).distinct().toArray();
//                    Arrays.stream(signalArray).forEach(it -> LogManager.Companion.Timber.d("value:0x" + Integer.toHexString(it).toUpperCase()));
//                    Set<Integer> signals = GlobalManager.Companion.getInstance().getConcernedSignal(SignalOrigin.CABIN_SIGNAL);
                val signals = cabinSignal
                val signalArray =
                    signals.stream().filter { it != -1 }.mapToInt { obj: Int -> obj }.toArray()
                Arrays.stream(signalArray).forEach {
                    Timber.d("register cabin: hex propertyId:${Integer.toHexString(it)},  dec propertyId:$it")
                }
                mCarCabinManager!!.registerCallback(cabinEventListener, signalArray)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val cabinEventListener = object : CarCabinManager.CarCabinEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
            Timber.d("Cabin onChangeEvent propertyId hex:${Integer.toHexString(id)}, dec:$id value:${property.value}")
            GlobalManager.instance.onDispatchSignal(property, Origin.CABIN)
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.d("Cabin onErrorEvent:i:$i, i1:$i1")
        }
    }

    private val mcuEventListener = object : CarMcuEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
//            Timber.d("MCU onChangeEvent propertyId hex:${Integer.toHexString(id)}, dec:$id value:${property.value}")
            GlobalManager.instance.onDispatchSignal(property, Origin.MCU)
//            switch (property.getPropertyId()) {
//                case CarMcuManager.ID_VENDOR_MCU_POWER_MODE://电源状态
//                    int powerStatus = (Integer) property.getValue();
//                    LogManager.Companion.Timber.d("powerStatus= " + powerStatus);//6 8 4
//                    if (powerStatus == MCU.POWER_ON) {//6
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onAccStatusChange(true);
//                        }
//                    } else if (powerStatus == MCU.POWER_OFF) {//4
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onAccStatusChange(false);
//                        }
//                    }
//
//                    break;
//                case CarMcuManager.ID_REVERSE_SIGNAL://倒车状态
//                    int reverseStatus = (Integer) property.getValue();
//                    LogManager.Companion.Timber.d("ID_REVERSE_SIGNAL= " + reverseStatus);
//                    if (reverseStatus == VEHICLE.ON) {
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onReverseStatusChange(true);
//                        }
//                    } else if (reverseStatus == VEHICLE.OFF) {
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onReverseStatusChange(false);
//                        }
//                    }
//                    break;
//                case CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE://显示模式
//                    break;
//                case CarMcuManager.ID_MCU_ACC_STATE:
//                    int mcu_acc_state = (Integer) property.getValue();
//                    LogManager.Companion.Timber.d("ID_MCU_ACC_STATE= " + mcu_acc_state);
//                    if (mcu_acc_state == CarSensorEvent.IGNITION_STATE_ACC ||
//                            mcu_acc_state == CarSensorEvent.IGNITION_STATE_ON ||
//                            mcu_acc_state == CarSensorEvent.IGNITION_STATE_START) {
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onPowerStatusChange(mcu_acc_state);
//                        }
//                    } //else if (mcu_acc_state == CarSensorEvent.IGNITION_STATE_OFF) { }
//                    break;
//                case CarMcuManager.ID_MCU_LOST_CANID:
//                    Integer[] lostCanidStatus = (Integer[]) property.getValue();//CAN节点丢失 0x00-正常 0x01-丢失
//                    LogManager.Companion.Timber.d("ID_MCU_LOST_CANID= " + Arrays.toString(lostCanidStatus));
//                    break;
//                case CarMcuManager.ID_VENDOR_PHOTO_REQ:
//                    if (property.getAreaId() == VehicleAreaSeat.SEAT_ROW_1_CENTER) {
//                        LogManager.Companion.d("ID_VENDOR_PHOTO_REQ " + property.getValue());
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onPhotoReqChange(true);
//                        }
//                    }
//                    break;
//            }
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.d("Mcu onErrorEvent:i:$i, i1:$i1")
        }
    }

    private val hvacEventListener = object : CarHvacEventCallback {
        override fun onChangeEvent(property: CarPropertyValue<*>) {
            val id = property.propertyId
//            Timber.d("Hvac onChangeEvent propertyId hex:${Integer.toHexString(id)}, dec:$id value:${property.value}")
            GlobalManager.instance.onDispatchSignal(property, Origin.HVAC)
        }

        override fun onErrorEvent(i: Int, i1: Int) {
            Timber.d("Hvac onErrorEvent:i:$i, i1:$i1")
        }
    }

    //回调的点火状态
    val isIgnitionStateON: Boolean
        get() {
            try {
                return mCarSensorManager!!.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_IGNITION_STATE)
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
                var i = 0
                while (i < mCarAdapterList.size) {
                    mCarAdapterList[i].onLittleLightStatusChange(isNight)
                    i++
                }
            }
        }
    }
    val isLittleLightON: Boolean
        get() {
            try {
                val carSensorEvent =
                    mCarSensorManager!!.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_NIGHT)
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
                return mCarMcuManager!!.getIntProperty(
                    CarMcuManager.ID_MCU_ACC_STATE,
                    VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
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
                return mCarMcuManager!!.getIntProperty(
                    CarMcuManager.ID_REVERSE_SIGNAL,
                    VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
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
                val v = mCarMcuManager!!.getIntArrayProperty(
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
    val isCarServiceRunning: Boolean
        get() {
            val rel = mCarApi != null && mCarApi!!.isConnected
            Timber.d("isCarServiceRunning =$rel")
            if (!rel) {
                onBindService()
            }
            return rel
        }

    fun unInit() {
        Timber.d("unInit")
        mCarApi!!.disconnect()
    }

    fun registerVolumeChangeObserver(observer: ContentObserver?) {
        //mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("android.car.VOLUME_GROUP/"), true, observer);
        mCarAudioManager!!.registerVolumeChangeObserver(observer)
        Timber.d("registerVolumeChangeObserver")
    }

    fun unregisterVolumeChangeObserver(observer: ContentObserver?) {
        Timber.d("unregisterVolumeChangeObserver")
        mCarAudioManager!!.unregisterVolumeChangeObserver(observer)
        //mContext.getContentResolver().unregisterContentObserver(observer);
    }

    var isMobileNetworkON = false
        private set

    fun setMobileNetSwitch(on: Boolean, iMobileState: IMobileState) {
//        mBoxManager!!.addTBoxChangedListener(object : TboxChangedListener {
//            override fun onCallStatusChanged(i: Int, i1: Int) {}
//            override fun onTboxMobileSignalChanged(i: Int, i1: Int, i2: Int) {}
//            override fun onTboxMobileSwitchStateChanged(i: Int) {}
//            override fun onWifiStateChanged(s: String, i: Int, s1: String) {}
//        })
//        //view.setEnabled(false);
//        executorService.submit {
//            mBoxManager!!.setMobileState(on, object : INetworkCallBack {
//                @Synchronized
//                override fun onCompleted(i: Int, s: String) {
//                    Timber.d("setMobileState onCompleted $i $s")
//                    if (i == 1) {
//                        isMobileNetworkON = on
//                    }
//                    mHandler.post {
//                        if (i == 1) {
//                            iMobileState.onMobileStateChange(on)
//                        }
//                    }
//                }
//
//                @Synchronized
//                override fun onException(i: Int, s: String) {
//                    Timber.d("setMobileState onException $i $s")
//                    mHandler.postDelayed({ iMobileState.onMobileStateError() }, 1000)
//                }
//            })
//        }
    }

    fun doSetProperty(id: Int, value: Int, origin: Origin?, area: Area): Boolean {
        return if (!connectService) {
            false
        } else when (origin) {
            Origin.CABIN -> doSetCabinProperty(id, value, area.id)
            Origin.HVAC -> doSetHvacProperty(id, value, area.id)
            else -> false
        }
    }

    fun doSetProperty(id: Int, value: Int, origin: Origin?, areaValue: Int): Boolean {
        return if (!connectService) {
            false
        } else when (origin) {
            Origin.CABIN -> doSetCabinProperty(id, value, areaValue)
            Origin.HVAC -> doSetHvacProperty(id, value, areaValue)
            else -> false
        }
    }

    private fun doSetCabinProperty(id: Int, value: Int, areaValue: Int): Boolean {
        if (null != mCarCabinManager) {
            try {
                Timber.d("setCabinValue b hex propertyId:" + Integer.toHexString(id) + ", dec propertyId:" + id + ", value:" + value)
                mCarCabinManager!!.setIntProperty(id, areaValue, value)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun doSetHvacProperty(id: Int, value: Int, areaValue: Int): Boolean {
        if (null != hvacManager) {
            try {
                Timber.d("setHvacValue b hex propertyId:" + Integer.toHexString(id) + ", dec propertyId:" + id + ", value:" + value)
                hvacManager!!.setIntProperty(id, areaValue, value)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun writekk() {
        mBoxManager?.let {
            val truckInformation = TruckInformation()
            truckInformation.onOff = 1
            truckInformation.level = 1
            truckInformation.dist = 1
            TboxManager.getInstance().truckInformation = truckInformation
        }
    }

    private fun readCabinIntValue(id: Int, areaValue: Int): Int {
        var result = Constant.DEFAULT
        try {
            result = mCarCabinManager?.getIntProperty(id, areaValue) ?: Constant.INVALID
            Timber.d("readCabinIntValue propertyId:$id, result:$result, manager:$mCarCabinManager")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun readHvacIntValue(id: Int, areaValue: Int): Int {
        var result = Constant.DEFAULT
        try {
            result = hvacManager?.getIntProperty(id, areaValue) ?: Constant.INVALID
            Timber.d("readHvacIntValue propertyId:$id, result:$result, manager:$hvacManager")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun readIntProperty(id: Int, origin: Origin, area: Area): Int {
        return readIntProperty(id, origin, area.id)
    }

    fun readIntProperty(id: Int, origin: Origin, areaValue: Int): Int {
        var result = Constant.DEFAULT
        if (!connectService) {
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

    fun writeBoxValue() {
        try {
            mBoxManager?.truckInformation?.onOff
        } catch (e: Exception) {
            LogManager.e(TAG, e)
        }
    }

    interface IMobileState {
        fun onMobileStateChange(on: Boolean)
        fun onMobileStateError()
    }

    fun addMobileNetListener(iMobileState: IMobileState?) {
//        boxChangedListener = object : TboxChangedListener {
//            override fun onCallStatusChanged(i: Int, i1: Int) {}
//            override fun onTboxMobileSignalChanged(i: Int, i1: Int, i2: Int) {}
//            override fun onTboxMobileSwitchStateChanged(i: Int) {
//                Timber.d("onTboxMobileSwitchStateChanged $i")
//                iMobileState?.onMobileStateChange(i == 1)
//            }
//
//            override fun onWifiStateChanged(s: String, i: Int, s1: String) {
//                Timber.d("onWifiStateChanged $s $i $s1")
//            }
//        }
//        mBoxManager!!.addTBoxChangedListener(boxChangedListener)
    }

    fun removeMobileNetListener() {
        if (boxChangedListener != null) {
            mBoxManager!!.removeTBoxChangedListener(boxChangedListener)
        }
    }

    fun getMobileNetSwitch(iMobileState: IMobileState?) {
        Timber.d("getMobileState")
        executorService.submit {
//            mBoxManager!!.getMobileState(object : INetworkCallBack {
//                override fun onCompleted(i: Int, s: String) {
//                    Timber.d("getMobileState onCompleted $i $s")
//                    isMobileNetworkON = i == 1
//                    mHandler.post { iMobileState?.onMobileStateChange(i == 1) }
//                }
//
//                override fun onException(i: Int, s: String) {
//                    Timber.d("getMobileState onException $i $s")
//                    mHandler.post { iMobileState?.onMobileStateError() }
//                }
//            })
        }
    }//LanguageUtils.updateLanguage(locale);// 需要系统应用权限

    //0 zh;1 en
    var language: Int
        get() {
            val type: Int
            val lang = Locale.getDefault().language
            type = if ("en" == lang) {
                CarAdapter.Constants.Language_EN
            } else {
                CarAdapter.Constants.Language_ZH
            }
            return type
        }
        set(type) {
            var locale = Locale.CHINA
            if (type == CarAdapter.Constants.Language_EN) {
                locale = Locale.US
            }
            // 需要系统应用权限
            try {
                //LanguageUtils.updateLanguage(locale);
                LocalePicker.updateLocale(locale)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }// 需要系统级用户权限，为避免应用崩溃，添加异常处理

    //0 12;1 24
    //默认24小时
    var isTimeStyle12H: Boolean
        get() {
            val timeformat = Settings.System.getString(
                context!!.contentResolver, Settings.System.TIME_12_24
            )
            if ("12" == timeformat) {
                return true
            } else if ("24" == timeformat) {
                return false
            }
            return !DateFormat.is24HourFormat(context)
        }
        set(is12h) {
            if (is12h) {
                Settings.System.putString(
                    context!!.contentResolver,
                    Settings.System.TIME_12_24,
                    "12"
                )
            } else {
                Settings.System.putString(
                    context!!.contentResolver,
                    Settings.System.TIME_12_24,
                    "24"
                )
            }
            // 需要系统级用户权限，为避免应用崩溃，添加异常处理
            try {
                val localIntent = Intent(Intent.ACTION_TIME_CHANGED)
                context!!.sendBroadcast(localIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    private val STREAM_SYSTEM = AudioAttributes.USAGE_ASSISTANT //16
    private val STREAM_MEDIA = AudioAttributes.USAGE_MEDIA //1
    private val STREAM_PHONE = AudioAttributes.USAGE_VOICE_COMMUNICATION //2
    private val STREAM_NAVI = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE //12

    private fun getStreamMaxVolume(type: Int): Int {
        try {
            return mCarAudioManager!!.getGroupMaxVolume(
                mCarAudioManager!!.getVolumeGroupIdForUsage(type)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    val systemMaxVolume: Int
        get() = getStreamMaxVolume(STREAM_SYSTEM)
    val mediaMaxVolume: Int
        get() = getStreamMaxVolume(STREAM_MEDIA)
    val phoneMaxVolume: Int
        get() = getStreamMaxVolume(STREAM_PHONE)
    val naviMaxVolume: Int
        get() = getStreamMaxVolume(STREAM_NAVI)

    private fun setStreamVolume(type: Int, volume: Int) {
        try {
            mCarAudioManager!!.setGroupVolume(
                mCarAudioManager!!.getVolumeGroupIdForUsage(type),
                volume,
                0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getStreamVolume(type: Int): Int {
        try {
            return mCarAudioManager!!.getGroupVolume(
                mCarAudioManager!!.getVolumeGroupIdForUsage(
                    type
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    var mediaVolume: Int
        get() = getStreamVolume(STREAM_MEDIA)
        set(volume) {
            setStreamVolume(STREAM_MEDIA, volume)
        }
    var phoneVolume: Int
        get() = getStreamVolume(STREAM_PHONE)
        set(volume) {
            setStreamVolume(STREAM_PHONE, volume)
        }
    var naviVolume: Int
        get() = getStreamVolume(STREAM_NAVI)
        set(volume) {
            setStreamVolume(STREAM_NAVI, volume)
        }
    var systemVolume: Int
        get() = getStreamVolume(STREAM_SYSTEM)
        set(volume) {
            setStreamVolume(STREAM_SYSTEM, volume)
        }

    private val mContentObserver = object : ContentObserver(mHandler) {
        override fun deliverSelfNotifications(): Boolean {
            return super.deliverSelfNotifications()
        }

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            Timber.d("onChange getBrightness")
            for (i in mCarAdapterList.indices) {
                mCarAdapterList[i].onBrightnessChange(brightness)
            }
        }
    }

    fun setBrightnessChangeListener() {
        val uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
        context!!.contentResolver.registerContentObserver(uri, false, mContentObserver)
    }

    fun removeBrightnessChangeListener() {
        //mCarPowerManager.clearListener();
        context!!.contentResolver.unregisterContentObserver(mContentObserver)
    }

    //mCarMcuManager.setIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, brigness);
    //Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brigness);
    //ret = mCarMcuManager.getIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL);
    //ret= Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0);
    var brightness: Int
        get() {
            var ret = -1
            try {
                ret = mCarPowerManager!!.brightness
                //ret = mCarMcuManager.getIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL);
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //ret= Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0);
            Timber.d("getBrightness = $ret")
            return ret
        }
        set(brigness) {
            try {
                Timber.d("setBrightness = $brigness")
                mCarPowerManager!!.brightness = brigness
                //mCarMcuManager.setIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, brigness);
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brigness);
        }

    fun setScreenOn(on: Boolean) {
        try {
            if (on) {
                Timber.d("sendDisplayOn")
                mCarPowerManager!!.sendDisplayOn()
            } else {
                Timber.d("sendDisplayOff")
                mCarPowerManager!!.sendDisplayOff()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //return SystemProperties.get("persist.sys.tuid", "");
    val tUID: String
        get() {
            try {
                val tuid = Settings.System.getString(
                    context!!.contentResolver, "TUID"
                )
                Timber.d("" + tuid)
                return tuid
                //return SystemProperties.get("persist.sys.tuid", "");
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    //return SystemProperties.get("persist.sys.tuid", "");
    val vIN: String
        get() {
            try {
                val vin = Settings.System.getString(context!!.contentResolver, "VIN")
                Timber.d("" + vin)
                return vin
                //return SystemProperties.get("persist.sys.tuid", "");
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    val uUID: String
        get() {
            try {
                return Settings.System.getString(context!!.contentResolver, "UUID")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    val osVersion: String
        get() = Build.DISPLAY
    val blueToothVersion: String
        get() = ""

    fun getAppVersion(pkgName: String?): String {
        try {
            return getVerName(context, pkgName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    val mcuVersion: String
        get() = SystemProperties.get("persist.sys.mcu_version", "")





    fun setMute(isMute: Boolean) {
        try {
            val carPropertyManager =
                mCarApi!!.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
            carPropertyManager.setProperty(
                Int::class.java,
                VehicleProperty.VENDOR_AMPLIFIER_SWITCH_STATUS,
                0,
                if (isMute) 1 else 2
            ) //1关功放，2打开
            mCarAudioManager!!.setMasterMute(isMute, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //@IntDef
    var beepEffectStatus: Int
        get() {
            var index = -1
            try {
                val level = mCarAudioManager!!.beepLevel
                Timber.d("getBeepLevel:$level")
                when (level) {
                    CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE -> index =
                        CarAdapter.Constants.BEEP_VOLUME_LEVEL_CLOSE
                    CarAudioManager.BEEP_VOLUME_LEVEL_LOW -> index =
                        CarAdapter.Constants.BEEP_VOLUME_LEVEL_LOW
                    CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE -> index =
                        CarAdapter.Constants.BEEP_VOLUME_LEVEL_MIDDLE
                    CarAudioManager.BEEP_VOLUME_LEVEL_HIGH -> index =
                        CarAdapter.Constants.BEEP_VOLUME_LEVEL_HIGH
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return index
        }
        set(index) {
            var level = CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE
            when (index) {
                CarAdapter.Constants.BEEP_VOLUME_LEVEL_CLOSE -> level =
                    CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE
                CarAdapter.Constants.BEEP_VOLUME_LEVEL_LOW -> level =
                    CarAudioManager.BEEP_VOLUME_LEVEL_LOW
                CarAdapter.Constants.BEEP_VOLUME_LEVEL_MIDDLE -> level =
                    CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE
                CarAdapter.Constants.BEEP_VOLUME_LEVEL_HIGH -> level =
                    CarAudioManager.BEEP_VOLUME_LEVEL_HIGH
            }
            try {
                mCarAudioManager!!.beepLevel = level
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    /**
     * 获取音量随速
     *
     * @return 0(off);1(low);2(mid);3(high)
     */
    /**
     * 设置音量随速
     *
     * @param type 0(off);1(low);2(mid);3(high)
     */
    var audioVolumeSpeed: Int
        get() {
            var result = -1
            try {
                val level = mCarAudioManager!!.avcLevel
                Timber.d("getAvcLevel:$level")
                when (level) {
                    CarAudioManager.AVC_LEVEL_CLOSE -> result = 0
                    CarAudioManager.AVC_LEVEL_LOW -> result = 1
                    CarAudioManager.AVC_LEVEL_MIDDLE -> result = 2
                    CarAudioManager.AVC_LEVEL_HIGH -> result = 3
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
        set(type) {
            var level = CarAudioManager.AVC_LEVEL_CLOSE
            when (type) {
                0 -> level = CarAudioManager.AVC_LEVEL_CLOSE
                1 -> level = CarAudioManager.AVC_LEVEL_LOW
                2 -> level = CarAudioManager.AVC_LEVEL_MIDDLE
                3 -> level = CarAudioManager.AVC_LEVEL_HIGH
            }
            Timber.d("setAvcLevel:$level")
            try {
                mCarAudioManager!!.avcLevel = level
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    val executorService = Executors.newSingleThreadExecutor()
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
            var muiBalanceLevelValue = 0;
            var muiFadeLevelValue = 0;
            if(getAmpType() == 0){ //内置
                muiBalanceLevelValue = uiBalanceLevelValue+10;
                muiFadeLevelValue = uiFadeLevelValue+10;
            }else{
                muiBalanceLevelValue = uiBalanceLevelValue+6;
                muiFadeLevelValue = uiFadeLevelValue+6;
            }
            LogManager.d("setAudioBalance muiBalanceLevelValue=${muiBalanceLevelValue}  " +
                    " muiFadeLevelValue=${muiFadeLevelValue}")
            mCarAudioManager?.setBalFadBalance(muiBalanceLevelValue,muiFadeLevelValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAudioVoice(id: Int): Int {
        var result = -1
        try {
            result = mCarAudioManager!!.getAudioVoice(id)
            Timber.d("setAudioVoice result:$id result=$result")
        } catch (e: Throwable) {
            e.printStackTrace()
            Timber.d("e=" + e.message)
        }
        return result
    }

    //-7,7
    val audioFade: Int
        get() {
            try {
                mFadeLevelValue = mCarAudioManager!!.fadeTowardFront
                return mFadeLevelValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -100
        }

    fun getAudioEQ(): SoundEffect {
        var result = SoundEffect.POP
        try {
            val mode = mCarAudioManager!!.eqMode
            result = SoundEffect.getEffect(mode)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun setAudioCustomHML(high: Int, mid: Int, low: Int) {
        //  audioEQ = EQ_MODE_CUSTOM

        Timber.d("setAudioCustomHML:$high $mid $low")
        audioHighVoice = high
        audioMidVoice = mid
        audioLowVoice = low
    }

    fun setAudioEQ(mode: Int, high: Int, mid: Int, low: Int) {
        var m = CarAudioManager.EQ_MODE_FLAT
        when (mode) {
            EQ_MODE_STANDARD -> m = CarAudioManager.EQ_MODE_FLAT
            EQ_MODE_POP -> m = CarAudioManager.EQ_MODE_POP
            EQ_MODE_ROCK -> m = CarAudioManager.EQ_MODE_ROCK
            EQ_MODE_JAZZ -> m = CarAudioManager.EQ_MODE_JAZZ
            EQ_MODE_CLASSIC -> m = CarAudioManager.EQ_MODE_CLASSIC
            EQ_MODE_PEOPLE -> m = CarAudioManager.EQ_MODE_VOCAL
            EQ_MODE_CUSTOM -> {
                m = CarAudioManager.EQ_MODE_CUSTOM
                Timber.d("setAudioVoice:$high $mid $low")
                audioHighVoice = high
                audioMidVoice = mid
                audioLowVoice = low
            }
        }
        try {
            mCarAudioManager!!.eqMode = m
            Timber.d("setEqMode:$m")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var audioLowVoice: Int
        get() {
//            try {
//                return mCarAudioManager!!.audioLowVoice
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            return -1
        }
        set(value) {
//            try {
//                mCarAudioManager!!.audioLowVoice = value
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    var audioMidVoice: Int
        get() {
//            try {
//                return mCarAudioManager!!.audioMidVoice
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            return -1
        }
        set(value) {
//            try {
//                mCarAudioManager!!.audioMidVoice = value
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    var audioHighVoice: Int
        get() {
//            try {
//                return mCarAudioManager!!.audioHighVoice
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            return -1
        }
        set(value) {
//            try {
//                mCarAudioManager!!.audioHighVoice = value
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }

    fun setSoundEffect(effect: SoundEffect) {
        mCarAudioManager?.eqMode = effect.id
    }

    fun getSoundEffect(): Int {
        return if (null != mCarAudioManager)
            mCarAudioManager!!.eqMode
        else -1

    }

    fun setAudioEQ(mode: Int, lev1: Int, lev2: Int, lev3: Int, lev4: Int, lev5: Int) {
        var m = CarAudioManager.EQ_MODE_FLAT
        when (mode) {
            EQ_MODE_STANDARD -> m = CarAudioManager.EQ_MODE_FLAT
            CarAudioManager.EQ_MODE_POP -> m = CarAudioManager.EQ_MODE_POP
            CarAudioManager.EQ_MODE_ROCK -> m = CarAudioManager.EQ_MODE_ROCK
            CarAudioManager.EQ_MODE_JAZZ -> m = CarAudioManager.EQ_MODE_JAZZ
            CarAudioManager.EQ_MODE_CLASSIC -> m = CarAudioManager.EQ_MODE_CLASSIC
            EQ_MODE_PEOPLE -> m = CarAudioManager.EQ_MODE_VOCAL
            CarAudioManager.EQ_MODE_CUSTOM -> {
                m = CarAudioManager.EQ_MODE_CUSTOM
                Timber.d("setAudioVoice:$lev1 $lev2 $lev3 $lev4 $lev5")
                setAudioVoice(VOICE_LEVEL1, lev1)
                setAudioVoice(VOICE_LEVEL2, lev2)
                setAudioVoice(VOICE_LEVEL3, lev3)
                setAudioVoice(VOICE_LEVEL4, lev4)
                setAudioVoice(VOICE_LEVEL5, lev5)
            }
        }

        try {
            mCarAudioManager?.eqMode = m
            Timber.d("setEqMode:$m")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setAudioVoice(id: Int, value: Int) {
        try {
            mCarAudioManager!!.setAudioVoice(id, value)
            Timber.d("setAudioVoice:$id ")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    var isMicMute: Boolean
        get() = mAudioManager.isMicrophoneMute
        set(on) {
            mAudioManager.isMicrophoneMute = on
        }

    fun getAudioBalance(): Int {
        try {
            mBalanceLevelValue = mCarAudioManager!!.balanceTowardRight
            return mBalanceLevelValue
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return -100
    }

    //-7,7

    private inner class CarServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("onServiceConnected start")
            connectService = true
            onRegisterBoxListener()
            initAudioManager()
            onRegisterMcuListener()
            onRegisterSensorListener()
            onRegisterCabinListener()
            onRegisterHvacListener()
            onRegisterPowerListener()
            updateAdapterConnect()
            Timber.d("onServiceConnected end")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.e("onServiceDisconnected")
            connectService = false
            updateAdapterConnect()
            mHandler.removeCallbacks(reConnectCarRunnable)
            mHandler.postDelayed(reConnectCarRunnable, 1500)
        }

        override fun onBindingDied(name: ComponentName) {
            Timber.e("onBindingDied")
            connectService = false
            updateAdapterConnect()
        }
    }

    private fun onRegisterPowerListener() {
        if (!connectService) {
            return
        }
        try {
            if (null == mCarPowerManager) {
                mCarPowerManager = mCarApi!!.getCarManager(Car.POWER_SERVICE) as CarPowerManager
                BrightnessManager.instance.injectManager(mCarPowerManager!!)
            }
        } catch (e: CarNotConnectedException) {
            e.printStackTrace()
        }
    }

    private fun initAudioManager() {
        if (connectService && null == mCarAudioManager) {
            try {
                mCarAudioManager = mCarApi?.getCarManager(Car.AUDIO_SERVICE) as CarAudioManager
                VoiceManager.instance.injectAudioManager(mCarAudioManager!!)
            } catch (e: CarNotConnectedException) {
                e.printStackTrace()
            }
        }
    }

    private fun onRegisterHvacListener() {
        if (!connectService) {
            Timber.e("onRegisterHvacListener but app not connect to service!")
            return
        }
        try {
            if (null == hvacManager) {
                hvacManager = mCarApi!!.getCarManager(Car.HVAC_SERVICE) as CarHvacManager
            }
            if (null != hvacManager) {
                val signals = hvacSignal
                val signalArray = signals.stream().mapToInt { obj: Int -> obj }.toArray()
                hvacManager!!.registerCallback(hvacEventListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateAdapterConnect() {
        mCarAdapterList.stream().filter { obj: CarAdapter? -> Objects.nonNull(obj) }
            .forEach { it: CarAdapter -> it.onCarServiceBound(connectService) }
    }

    private fun onRegisterBoxListener() {
        try {
            if (null == mBoxManager) {
                val manager = mCarApi!!.getCarManager(Car.TBOX_SERVICE)
                if (manager is TboxManager) {
                    mBoxManager = manager
                }
                if (null == mBoxManager) {
                    mBoxManager = TboxManager.getInstance()
                }
                mBoxManager!!.addTBoxChangedListener(boxChangedListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onRegisterSensorListener() {
        try {
            if (null == mCarSensorManager) {
                mCarSensorManager = mCarApi!!.getCarManager(Car.SENSOR_SERVICE) as CarSensorManager
            }
            if (null != mCarSensorManager) {
                mCarSensorManager!!.registerListener(
                    sensorEventlistener, CarSensorManager.SENSOR_TYPE_IGNITION_STATE,
                    CarSensorManager.SENSOR_RATE_NORMAL
                ) //acc
                mCarSensorManager!!.registerListener(
                    sensorEventlistener, CarSensorManager.SENSOR_TYPE_NIGHT,
                    CarSensorManager.SENSOR_RATE_NORMAL
                ) //小灯，白天黑夜注册监听
            }
        } catch (e: CarNotConnectedException) {
            e.printStackTrace()
        }
    }

    fun getTrailerRemindSwitch(): Int? {
        try {
            return mBoxManager?.truckInformation?.onOff
        } catch (e: Throwable) {
        }
        return null
    }

    fun getTrailerSensitivity(): Int? {
        try {
            return mBoxManager?.truckInformation?.level
        } catch (e: Throwable) {
        }
        return null
    }

    fun getTrailerDistance(): Int? {
        try {
            return mBoxManager?.truckInformation?.dist
        } catch (e: Throwable) {
        }
        return null
    }

    fun setTrailerRemind(value: Int): Boolean {
        try {
            val truckInformation = mBoxManager?.truckInformation
            if (null != truckInformation) {
                truckInformation.onOff = value
                mBoxManager?.truckInformation = truckInformation
                return true
            }
        } catch (e: Throwable) {
        }
        return false
    }

    fun setTrailerDistance(value: Int): Boolean {
        try {
            val truckInformation = mBoxManager?.truckInformation
            if (null != truckInformation) {
                truckInformation.dist = value
                mBoxManager?.truckInformation = truckInformation
                return true
            }
        } catch (e: Throwable) {
        }
        return false
    }

    fun setTrailerSensitivity(value: Int): Boolean {
        try {
            val truckInformation = mBoxManager?.truckInformation
            if (null != truckInformation) {
                truckInformation.level = value
                mBoxManager?.truckInformation = truckInformation
                return true
            }
        } catch (e: Throwable) {
        }
        return false
    }

    private val boxChangedListener = object : TboxChangedListener {
        override fun onCallStatusChanged(p0: XCallInformation?) {

        }

        override fun onNetworkInfoChanged(p0: NetworkInformation?) {

        }

        override fun onTruckInfoChanged(truckInformation: TruckInformation?) {
            try {
                truckInformation?.let {
                    OtherManager.instance.onTrailerRemindChanged(it.onOff, it.level, it.dist)
                }
            }catch (e:Error){
                e.printStackTrace()
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
        fun getAmpType(): Int {
            try {
                var type = SystemProperties.getInt("persist.vendor.vehicle.amp", 0)
                LogManager.d("getAmpType type=${type}")
                return type;
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0;
        }
        fun getVerName(context: Context?, pkgName: String?): String {
            val manager = context!!.packageManager
            var name = ""
            try {
                //com.autonavi.amapauto
                val info = manager.getPackageInfo(pkgName!!, 0)
                //long code = info.getLongVersionCode();
                name = info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return name
        }

        fun getVerNameCode(pkgName: String?): String {
            val manager = context.packageManager
            var name = ""
            var code: Long = -1
            try {
                val info = manager.getPackageInfo(pkgName!!, 0)
                code = info.longVersionCode
                name = info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return "$name $code"
        }

        const val EQ_MODE_STANDARD = 0
        const val EQ_MODE_POP = 1
        const val EQ_MODE_ROCK = 2
        const val EQ_MODE_JAZZ = 3
        const val EQ_MODE_CLASSIC = 4
        const val EQ_MODE_PEOPLE = 5
        const val EQ_MODE_CUSTOM = 6
        const val EQ_MODE_TECHNO = 7 //电子


        //0XFF
        const val VOICE_LEVEL1 = CarAudioManager.EQ_AUDIO_VOICE_LEVEL1
        const val VOICE_LEVEL2 = CarAudioManager.EQ_AUDIO_VOICE_LEVEL2
        const val VOICE_LEVEL3 = CarAudioManager.EQ_AUDIO_VOICE_LEVEL3
        const val VOICE_LEVEL4 = CarAudioManager.EQ_AUDIO_VOICE_LEVEL4
        const val VOICE_LEVEL5 = CarAudioManager.EQ_AUDIO_VOICE_LEVEL5

    }

    init {
        bindVehicleService()
    }


}