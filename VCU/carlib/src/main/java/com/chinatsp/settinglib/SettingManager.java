package com.chinatsp.settinglib;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.hardware.automotive.vehicle.V2_0.VehicleProperty.VENDOR_AMPLIFIER_SWITCH_STATUS;

import android.annotation.SuppressLint;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.constant.VEHICLE;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.mcu.CarMcuManager;
import android.car.hardware.power.CarPowerManager;
import android.car.hardware.property.CarPropertyManager;
import android.car.media.CarAudioManager;
import android.car.tbox.TboxManager;
import android.car.tbox.aidl.INetworkCallBack;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.android.internal.app.LocalePicker;
import com.chinatsp.settinglib.manager.GlobalManager;
import com.chinatsp.settinglib.manager.RegisterSignalManager;
import com.chinatsp.settinglib.manager.lamp.BrightnessManager;
import com.chinatsp.settinglib.manager.sound.VoiceManager;
import com.chinatsp.settinglib.optios.Area;
import com.chinatsp.settinglib.sign.Origin;
import com.chinatsp.settinglib.sign.TabBlock;
import com.chinatsp.settinglib.sign.TabSignManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author
 */
public class SettingManager {
    public static final String TAG = "CarManager";
    @SuppressLint("StaticFieldLeak")
    private static volatile SettingManager settingManager;
    private AudioManager mAudioManager;
    private CarAudioManager mCarAudioManager;
    private CarMcuManager mCarMcuManager;
    private CarPowerManager mCarPowerManager;
    private TboxManager mBoxManager;
    private CarSensorManager mCarSensorManager;
    private CarCabinManager mCarCabinManager;
    private CarHvacManager hvacManager;
    private String productName;
    private Car mCarApi;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private boolean connectService;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private SettingManager() {
        if (mContext == null) {
            LogManager.Companion.e(TAG, "context==null");
            return;
        }
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        productName = SystemProperties.get("ro.product.name");//判断是3y1还是f202
        LogManager.Companion.w(TAG, "productName:" + productName + " " + getOsVersion());
        if (null != mCarApi && mCarApi.isConnected()) {
            LogManager.Companion.d(TAG, "mCarApi.isConnected:" + true);
            return;
        }
        onBindCarService();
    }

    private void unBindCarService() {
        if (null != mCarMcuManager) {
            try {
                mCarMcuManager.unregisterCallback(mcuEventListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != mCarCabinManager) {
            try {
                mCarCabinManager.unregisterCallback(cabinEventListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogManager.Companion.d(TAG, "unbind car service");
    }

    private void onBindCarService() {
        mCarApi = Car.createCar(mContext, new CarServiceConnection());
        Optional.ofNullable(mCarApi).ifPresent(Car::connect);
        LogManager.Companion.d(TAG, "bind car service carApi:" + mCarApi);
    }

    private final Runnable reConnectCarRunnable = () -> {
        unBindCarService();
        onBindCarService();
    };

    public void setDYCarbinProperty(int id, int v) {
        try {
            mCarCabinManager.setIntProperty(id, VEHICLE_AREA_TYPE_GLOBAL, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final List<CarAdapter> mCarAdapterList = new ArrayList<>();

    public void addCarListener(CarAdapter carAdapter) {
        if (!mCarAdapterList.contains(carAdapter)) {
            mCarAdapterList.add(carAdapter);
        }
    }

    public void removeCarListener(CarAdapter carAdapter) {
        mCarAdapterList.remove(carAdapter);
    }

    private void onRegisterMcuListener() {
        if (!connectService) {
            return;
        }
        try {
            if (null == mCarMcuManager) {
                mCarMcuManager = (CarMcuManager) mCarApi.getCarManager(Car.CAR_MCU_SERVICE);
            }
            if (null != mCarMcuManager) {
//                Set<Integer> signals = GlobalManager.Companion.getInstance().getConcernedSignal(SignalOrigin.MCU_SIGNAL);
                Set<Integer> signals = RegisterSignalManager.Companion.getMcuSignal();
                int[] signalArray = signals.stream().mapToInt(Integer::intValue).toArray();
                mCarMcuManager.registerCallback(mcuEventListener, signalArray);
                LogManager.Companion.d(TAG, "registerCallback ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAVMOn = false, isAPAOn = false;

    private Set<Integer> obtainSignals(Origin type) {
        Collection<TabBlock> values = TabSignManager.Companion.getInstance().getTabSignMap().values();
        final Set<Integer> signalSet = new HashSet<>();
        values.stream().forEach(tab -> {
            tab.getSignals().stream().forEach(carSign -> {
                if (type == carSign.getType()) {
                    signalSet.addAll(carSign.getSignals());
                }
            });
        });
        return signalSet;
    }

    public void setGPSData(float[] data) {
        try {
            mCarCabinManager.setFloatArrayProperty(VehiclePropertyIds.DVR_CUR_LOCATION, VEHICLE_AREA_TYPE_GLOBAL, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRegisterCabinListener() {
        if (!connectService) {
            return;
        }
        try {
            if (null == mCarCabinManager) {
                mCarCabinManager = (CarCabinManager) mCarApi.getCarManager(Car.CABIN_SERVICE);
            }
            if (null != mCarCabinManager) {
//                    int[] signalArray = Arrays.stream(Cabin.values())
//                            .flatMapToInt(cabin -> Arrays.stream(cabin.getSignals())).distinct().toArray();
//                    Arrays.stream(signalArray).forEach(it -> LogManager.Companion.d(TAG, "value:0x" + Integer.toHexString(it).toUpperCase()));
//                    Set<Integer> signals = GlobalManager.Companion.getInstance().getConcernedSignal(SignalOrigin.CABIN_SIGNAL);
                Set<Integer> signals = RegisterSignalManager.Companion.getCabinSignal();
                int[] signalArray = signals.stream().mapToInt(Integer::intValue).toArray();
                mCarCabinManager.registerCallback(cabinEventListener, signalArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final CarCabinManager.CarCabinEventCallback cabinEventListener = new CarCabinManager.CarCabinEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue property) {
            int propertyId = property.getPropertyId();
            LogManager.Companion.d(TAG, "Cabin onChangeEvent propertyId:" + propertyId);
            GlobalManager.Companion.getInstance().onDispatchSignal(propertyId, property, Origin.CABIN);
        }

        @Override
        public void onErrorEvent(int i, int i1) {
            LogManager.Companion.d(TAG, "Cabin onErrorEvent:i:" + i + ", i1:" + i1);
        }
    };

    private final CarHvacManager.CarHvacEventCallback hvacEventListener = new CarHvacManager.CarHvacEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue property) {
            int propertyId = property.getPropertyId();
            LogManager.Companion.d(TAG, "Hvac onChangeEvent:propertyId:" + propertyId);
            GlobalManager.Companion.getInstance().onDispatchSignal(propertyId, property, Origin.HVAC);
        }

        @Override
        public void onErrorEvent(int i, int i1) {
            LogManager.Companion.d(TAG, "Hvac onErrorEvent:i:" + i + ", i1:" + i1);
        }
    };

    private final CarMcuManager.CarMcuEventCallback mcuEventListener = new CarMcuManager.CarMcuEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue property) {
            int propertyId = property.getPropertyId();
            LogManager.Companion.d(TAG, "Mcu onChangeEvent:propertyId:" + propertyId);
            GlobalManager.Companion.getInstance().onDispatchSignal(propertyId, property, Origin.MCU);
//            switch (property.getPropertyId()) {
//                case CarMcuManager.ID_VENDOR_MCU_POWER_MODE://电源状态
//                    int powerStatus = (Integer) property.getValue();
//                    LogManager.Companion.d(TAG, "powerStatus= " + powerStatus);//6 8 4
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
//                    LogManager.Companion.d(TAG, "ID_REVERSE_SIGNAL= " + reverseStatus);
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
//                    LogManager.Companion.d(TAG, "ID_MCU_ACC_STATE= " + mcu_acc_state);
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
//                    LogManager.Companion.d(TAG, "ID_MCU_LOST_CANID= " + Arrays.toString(lostCanidStatus));
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

        @Override
        public void onErrorEvent(int i, int i1) {
            LogManager.Companion.d(TAG, "Mcu onErrorEvent:i:" + i + ", i1:" + i1);
        }
    };

    public boolean isIgnitionStateON() {
        try {
            return mCarSensorManager.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_IGNITION_STATE).getIgnitionStateData(null).ignitionState > CarSensorEvent.IGNITION_STATE_OFF; //回调的点火状态
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private final CarSensorManager.OnSensorChangedListener sensorEventlistener = new CarSensorManager.OnSensorChangedListener() {
        @Override
        public void onSensorChanged(CarSensorEvent carSensorEvent) {
            //LogUtils.d(TAG, "onSensorChanged " + carSensorEvent.sensorType);
            switch (carSensorEvent.sensorType) {
                case CarSensorManager.SENSOR_TYPE_IGNITION_STATE:
                    //int mIgnitionData = carSensorEvent.getIgnitionStateData(null).ignitionState; //回调的点火状态
                    //LogUtils.d(TAG, "ignition state=" + mIgnitionData);
                    break;
                case CarSensorManager.SENSOR_TYPE_NIGHT:
                    boolean isNight = carSensorEvent.getNightData(null).isNightMode; //小灯，白天黑夜
                    LogManager.Companion.d(TAG, "littleLight change,isNight state=" + isNight);
                    for (int i = 0; i < mCarAdapterList.size(); i++) {
                        mCarAdapterList.get(i).onLittleLightStatusChange(isNight);
                    }
                    break;
            }
        }
    };

    public boolean isLittleLightON() {
        try {
            CarSensorEvent carSensorEvent = mCarSensorManager.getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_NIGHT);
            return carSensorEvent.getNightData(null).isNightMode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否打开全景了
    public boolean isAvmOn() {
//      return mCarDYCabinManager.getIntProperty(CarDYCabinManager.ID_AVM_DISPLAY_SWITCH, VEHICLE_AREA_TYPE_GLOBAL) == AVM_ON;
        return isAVMOn;
    }

    //下发关 -- 5，下发开 -- 6， 上报关 -- 7， 上报开 -- 8
    public void setAvmOff() {
        LogManager.Companion.d(TAG, "3Y1 setAvmOff");
        setDYCarbinProperty(CarCabinManager.ID_AVM_DISPLAY_SWITCH, VEHICLE.OFF);
    }

    //是否打开了自动泊车
    public boolean isApaOn() {
        return isAPAOn;
//        try {
//            int value = mCarCabinManager.getIntProperty(CarCabinManager.ID_ADAS_APA_PARK_NOTICE_INFO, VEHICLE_AREA_TYPE_GLOBAL);
//            return value != 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public boolean isAccOff() {
        try {
            return mCarMcuManager.getIntProperty(CarMcuManager.ID_MCU_ACC_STATE, VEHICLE_AREA_TYPE_GLOBAL) == CarSensorEvent.IGNITION_STATE_OFF;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否打开倒车
    public boolean isReverse() {
        try {
            return mCarMcuManager.getIntProperty(CarMcuManager.ID_REVERSE_SIGNAL, VEHICLE_AREA_TYPE_GLOBAL) == VEHICLE.ON;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否CAN丢失了,CAN节点丢失 0x00-正常 0x01-丢失* int[6]: AVM
    //     * int[7]: APA
    public boolean isCanIdLost() {
        try {
            int[] v = mCarMcuManager.getIntArrayProperty(CarMcuManager.ID_MCU_LOST_CANID, VEHICLE_AREA_TYPE_GLOBAL);
            //LogUtils.d("ID_MCU_LOST_CANID= "+ Arrays.toString(v));
            if (v[6] == 1 || v[7] == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCarServiceRunning() {
        boolean rel = mCarApi != null && mCarApi.isConnected();
        LogManager.Companion.d(TAG, "isCarServiceRunning =" + rel);
        if (!rel) {
            onBindCarService();
        }
        return rel;
    }

    public void unInit() {
        LogManager.Companion.d(TAG, "unInit");
        mCarApi.disconnect();
        mContext = null;
        settingManager = null;
    }

    public static void init(Context context) {
        LogManager.Companion.d(TAG, "init");
        mContext = context;
    }

    public Context getMainContext() {
        return mContext;
    }

    public static SettingManager getInstance() {
        if (settingManager == null) {
            synchronized (SettingManager.class) {
                if (settingManager == null) {
                    settingManager = new SettingManager();
                }
            }
        }
        return settingManager;
    }

    public void registerVolumeChangeObserver(ContentObserver observer) {
        //mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("android.car.VOLUME_GROUP/"), true, observer);
        mCarAudioManager.registerVolumeChangeObserver(observer);
        LogManager.Companion.d(TAG, "registerVolumeChangeObserver");
    }

    public void unregisterVolumeChangeObserver(ContentObserver observer) {
        LogManager.Companion.d(TAG, "unregisterVolumeChangeObserver");
        mCarAudioManager.unregisterVolumeChangeObserver(observer);
        //mContext.getContentResolver().unregisterContentObserver(observer);
    }

    private boolean isMobileON = false;

    public boolean isMobileNetworkON() {
        return isMobileON;
    }

    public void setMobileNetSwitch(final boolean on, final IMobileState iMobileState) {
        mBoxManager.addTBoxChangedListener(new TboxManager.TboxChangedListener() {
            @Override
            public void onCallStatusChanged(int i, int i1) {

            }

            @Override
            public void onTboxMobileSignalChanged(int i, int i1, int i2) {

            }

            @Override
            public void onTboxMobileSwitchStateChanged(int i) {

            }

            @Override
            public void onWifiStateChanged(String s, int i, String s1) {

            }
        });
        //view.setEnabled(false);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mBoxManager.setMobileState(on, new INetworkCallBack() {
                    @Override
                    public synchronized void onCompleted(final int i, String s) {
                        LogManager.Companion.d(TAG, "setMobileState onCompleted " + i + " " + s);
                        if (i == 1) {
                            isMobileON = on;
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1) {
                                    iMobileState.onMobileStateChange(on);
                                }
                            }
                        });
                    }

                    @Override
                    public synchronized void onException(int i, String s) {
                        LogManager.Companion.d(TAG, "setMobileState onException " + i + " " + s);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iMobileState.onMobileStateError();
                            }
                        }, 1000);
                    }
                });
            }
        });
    }

    public boolean doSetProperty(int id, int value, Origin origin, @NotNull Area area) {
        if (!connectService) {
            return false;
        }
        switch (origin) {
            case CABIN:
                return doSetCabinProperty(id, value, area.getId());
            case HVAC:
                return doSetHvacProperty(id, value, area.getId());
            default:
                return false;
        }
    }

    public boolean doSetProperty(int id, int value, Origin origin, int areaValue) {
        if (!connectService) {
            return false;
        }
        switch (origin) {
            case CABIN:
                return doSetCabinProperty(id, value, areaValue);
            case HVAC:
                return doSetHvacProperty(id, value, areaValue);
            default:
                return false;
        }
    }


    public boolean doSetCabinProperty(int id, int value, int areaValue) {
        if (null != mCarCabinManager) {
            try {
                LogManager.Companion.d(TAG, "doSetCabinProperty b hex propertyId:" + Integer.toHexString(id) + ", value:" + value);
                mCarCabinManager.setIntProperty(id, areaValue, value);
                LogManager.Companion.d(TAG, "doSetCabinProperty e dec propertyId:" + id + ", value:" + value);
                return true;
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean doSetHvacProperty(int id, int value, int areaValue) {
        if (null != hvacManager) {
            try {
                LogManager.Companion.d(TAG, "doSetHvacProperty b hex propertyId:" + Integer.toHexString(id) + ", value:" + value);
                hvacManager.setIntProperty(id, areaValue, value);
                LogManager.Companion.d(TAG, "doSetHvacProperty e dec propertyId:" + id + ", value:" + value);
                return true;
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private int doGetCabinIntProperty(int id, @NotNull Area area) {
        int result = -1;
        if (null != mCarCabinManager) {
            try {
                result = mCarCabinManager.getIntProperty(id, area.getId());
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        LogManager.Companion.d("doGetHvacIntProperty propertyId:" + id + ", result:" + result + ", manager:" + mCarCabinManager);
        return result;
    }

    private int doGetHvacIntProperty(int id, @NotNull Area area) {
        int result = -1;
        if (null != hvacManager) {
            try {
                result = hvacManager.getIntProperty(id, area.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogManager.Companion.d("doGetHvacIntProperty propertyId:" + id + ", result:" + result + ", manager:" + hvacManager);
        return result;
    }

    public int readIntProperty(int id, @NotNull Origin origin, @NotNull Area area) {
        int result = -1;
        if (!connectService) {
            LogManager.Companion.e("doGetIntProperty propertyId:" + id + ", origin:" + origin + ", connectService: false!");
            return result;
        }
        if (Origin.CABIN == origin) {
            result = doGetCabinIntProperty(id, area);
        } else if (Origin.HVAC == origin) {
            result = doGetHvacIntProperty(id, area);
        }
        return result;
    }

    public interface IMobileState {
        void onMobileStateChange(boolean on);

        void onMobileStateError();
    }

    private TboxManager.TboxChangedListener boxChangedListener;

    public void addMobileNetListener(final IMobileState iMobileState) {
        boxChangedListener = new TboxManager.TboxChangedListener() {
            @Override
            public void onCallStatusChanged(int i, int i1) {

            }

            @Override
            public void onTboxMobileSignalChanged(int i, int i1, int i2) {

            }

            @Override
            public void onTboxMobileSwitchStateChanged(int i) {
                LogManager.Companion.d(TAG, "onTboxMobileSwitchStateChanged " + i);
                if (iMobileState != null) {
                    iMobileState.onMobileStateChange(i == 1);
                }
            }

            @Override
            public void onWifiStateChanged(String s, int i, String s1) {
                LogManager.Companion.d(TAG, "onWifiStateChanged " + s + " " + i + " " + s1);
            }
        };
        mBoxManager.addTBoxChangedListener(boxChangedListener);
    }

    public void removeMobileNetListener() {
        if (boxChangedListener != null) {
            mBoxManager.removeTBoxChangedListener(boxChangedListener);
        }
    }

    public void getMobileNetSwitch(final IMobileState iMobileState) {
        LogManager.Companion.d(TAG, "getMobileState");
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mBoxManager.getMobileState(new INetworkCallBack() {
                    @Override
                    public void onCompleted(final int i, String s) {
                        LogManager.Companion.d(TAG, "getMobileState onCompleted " + i + " " + s);
                        isMobileON = (i == 1);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (iMobileState != null) {
                                    iMobileState.onMobileStateChange(i == 1);
                                }
                            }
                        });
                    }

                    @Override
                    public void onException(int i, String s) {
                        LogManager.Companion.d(TAG, "getMobileState onException " + i + " " + s);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (iMobileState != null) {
                                    iMobileState.onMobileStateError();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public int getLanguage() {
        int type;
        String lang = Locale.getDefault().getLanguage();
        if ("en".equals(lang)) {
            type = CarAdapter.Constants.Language_EN;
        } else {
            type = CarAdapter.Constants.Language_ZH;
        }
        return type;
    }

    //0 zh;1 en
    public void setLanguage(int type) {
        Locale locale = Locale.CHINA;
        if (type == CarAdapter.Constants.Language_EN) {
            locale = Locale.US;
        }
        // 需要系统应用权限
        try {
            //LanguageUtils.updateLanguage(locale);
            LocalePicker.updateLocale(locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //默认24小时
    public boolean isTimeStyle12H() {
        String timeformat = Settings.System.getString(mContext.getContentResolver(), Settings.System.TIME_12_24);
        if ("12".equals(timeformat)) {
            return true;
        } else if ("24".equals(timeformat)) {
            return false;
        }
        return !DateFormat.is24HourFormat(mContext);
    }

    //0 12;1 24
    public void setTimeStyle12H(boolean is12h) {
        if (is12h) {
            Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, "12");
        } else {
            Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, "24");
        }
        // 需要系统级用户权限，为避免应用崩溃，添加异常处理
        try {
            Intent localIntent = new Intent(Intent.ACTION_TIME_CHANGED);
            mContext.sendBroadcast(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final int STREAM_SYSTEM = AudioAttributes.USAGE_ASSISTANT;//16
    private final int STREAM_MEDIA = AudioAttributes.USAGE_MEDIA;//1
    private final int STREAM_PHONE = AudioAttributes.USAGE_VOICE_COMMUNICATION;//2
    private final int STREAM_NAVI = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE;//12

    private int getStreamMaxVolume(int type) {
        try {
            return mCarAudioManager.getGroupMaxVolume(mCarAudioManager.getVolumeGroupIdForUsage(type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getSystemMaxVolume() {
        return getStreamMaxVolume(STREAM_SYSTEM);
    }

    public int getMediaMaxVolume() {
        return getStreamMaxVolume(STREAM_MEDIA);
    }

    public int getPhoneMaxVolume() {
        return getStreamMaxVolume(STREAM_PHONE);
    }

    public int getNaviMaxVolume() {
        return getStreamMaxVolume(STREAM_NAVI);
    }

    private void setStreamVolume(int type, int volume) {
        try {
            mCarAudioManager.setGroupVolume(mCarAudioManager.getVolumeGroupIdForUsage(type), volume, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaVolume(int volume) {
        setStreamVolume(STREAM_MEDIA, volume);
    }

    public void setPhoneVolume(int volume) {
        setStreamVolume(STREAM_PHONE, volume);
    }

    public void setNaviVolume(int volume) {
        setStreamVolume(STREAM_NAVI, volume);
    }

    public void setSystemVolume(int volume) {
        setStreamVolume(STREAM_SYSTEM, volume);
    }

    private int getStreamVolume(int type) {
        try {
            return mCarAudioManager.getGroupVolume(mCarAudioManager.getVolumeGroupIdForUsage(type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getMediaVolume() {
        return getStreamVolume(STREAM_MEDIA);
    }

    public int getPhoneVolume() {
        return getStreamVolume(STREAM_PHONE);
    }

    public int getNaviVolume() {
        return getStreamVolume(STREAM_NAVI);
    }

    public int getSystemVolume() {
        return getStreamVolume(STREAM_SYSTEM);
    }

    private final ContentObserver mContentObserver = new ContentObserver(mHandler) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            LogManager.Companion.d(TAG, "onChange getBrightness");
            for (int i = 0; i < mCarAdapterList.size(); i++) {
                mCarAdapterList.get(i).onBrightnessChange(getBrightness());
            }
        }
    };

    public void setBrightnessChangeListener() {
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        mContext.getContentResolver().registerContentObserver(uri, false, mContentObserver);
    }

    public void removeBrightnessChangeListener() {
        //mCarPowerManager.clearListener();
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    public void setBrightness(int brigness) {
        try {
            LogManager.Companion.d(TAG, "setBrightness = " + brigness);
            mCarPowerManager.setBrightness(brigness);
            //mCarMcuManager.setIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, brigness);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brigness);
    }

    public int getBrightness() {
        int ret = -1;
        try {
            ret = mCarPowerManager.getBrightness();
            //ret = mCarMcuManager.getIntProperty(CarMcuManager.ID_DISPLAY_BRIGHTNESS, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ret= Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0);
        LogManager.Companion.d(TAG, "getBrightness = " + ret);
        return ret;
    }


    public void setScreenOn(boolean on) {
        try {
            if (on) {
                LogManager.Companion.d(TAG, "sendDisplayOn");
                mCarPowerManager.sendDisplayOn();
            } else {
                LogManager.Companion.d(TAG, "sendDisplayOff");
                mCarPowerManager.sendDisplayOff();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTUID() {
        try {
            String tuid = Settings.System.getString(mContext.getContentResolver(), "TUID");
            LogManager.Companion.d(TAG, "" + tuid);
            return tuid;
            //return SystemProperties.get("persist.sys.tuid", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getVIN() {
        try {
            String vin = Settings.System.getString(mContext.getContentResolver(), "VIN");
            LogManager.Companion.d(TAG, "" + vin);
            return vin;
            //return SystemProperties.get("persist.sys.tuid", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getUUID() {
        try {
            return Settings.System.getString(mContext.getContentResolver(), "UUID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getOsVersion() {
        return Build.DISPLAY;
    }

    public String getBlueToothVersion() {
        return "";
    }

    public String getAppVersion(String pkgName) {
        try {
            return getVerName(mContext, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVerName(Context context, String pkgName) {
        PackageManager manager = context.getPackageManager();
        String name = "";
        try {
            //com.autonavi.amapauto
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            //long code = info.getLongVersionCode();
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getVerNameCode(String pkgName) {
        PackageManager manager = mContext.getPackageManager();
        String name = "";
        long code = -1;
        try {
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            code = info.getLongVersionCode();
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name + " " + code;
    }

    public String getMcuVersion() {
        return SystemProperties.get("persist.sys.mcu_version", "");
    }

    public void setMute(boolean isMute) {
        try {
            CarPropertyManager carPropertyManager = (CarPropertyManager) mCarApi.getCarManager(Car.PROPERTY_SERVICE);
            carPropertyManager.setProperty(Integer.class, VENDOR_AMPLIFIER_SWITCH_STATUS, 0, isMute ? 1 : 2);//1关功放，2打开
            mCarAudioManager.setMasterMute(isMute, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getBeepEffectStatus() {
        int index = -1;
        try {
            int level = mCarAudioManager.getBeepLevel();
            LogManager.Companion.d(TAG, "getBeepLevel:" + level);
            switch (level) {
                case CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE:
                    index = CarAdapter.Constants.BEEP_VOLUME_LEVEL_CLOSE;
                    break;
                case CarAudioManager.BEEP_VOLUME_LEVEL_LOW:
                    index = CarAdapter.Constants.BEEP_VOLUME_LEVEL_LOW;
                    break;
                case CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE:
                    index = CarAdapter.Constants.BEEP_VOLUME_LEVEL_MIDDLE;
                    break;
                case CarAudioManager.BEEP_VOLUME_LEVEL_HIGH:
                    index = CarAdapter.Constants.BEEP_VOLUME_LEVEL_HIGH;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    //@IntDef
    public void setBeepEffectStatus(int index) {
        int level = CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE;
        switch (index) {
            case CarAdapter.Constants.BEEP_VOLUME_LEVEL_CLOSE:
                level = CarAudioManager.BEEP_VOLUME_LEVEL_CLOSE;
                break;
            case CarAdapter.Constants.BEEP_VOLUME_LEVEL_LOW:
                level = CarAudioManager.BEEP_VOLUME_LEVEL_LOW;
                break;
            case CarAdapter.Constants.BEEP_VOLUME_LEVEL_MIDDLE:
                level = CarAudioManager.BEEP_VOLUME_LEVEL_MIDDLE;
                break;
            case CarAdapter.Constants.BEEP_VOLUME_LEVEL_HIGH:
                level = CarAudioManager.BEEP_VOLUME_LEVEL_HIGH;
                break;
        }
        try {
            mCarAudioManager.setBeepLevel(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取音量随速
     *
     * @return 0(off);1(low);2(mid);3(high)
     */
    public int getAudioVolumeSpeed() {
        int result = -1;
        try {
            int level = mCarAudioManager.getAvcLevel();
            LogManager.Companion.d(TAG, "getAvcLevel:" + level);
            switch (level) {
                case CarAudioManager.AVC_LEVEL_CLOSE:
                    result = 0;
                    break;
                case CarAudioManager.AVC_LEVEL_LOW:
                    result = 1;
                    break;
                case CarAudioManager.AVC_LEVEL_MIDDLE:
                    result = 2;
                    break;
                case CarAudioManager.AVC_LEVEL_HIGH:
                    result = 3;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置音量随速
     *
     * @param type 0(off);1(low);2(mid);3(high)
     */
    public void setAudioVolumeSpeed(int type) {
        int level = CarAudioManager.AVC_LEVEL_CLOSE;
        switch (type) {
            case 0:
                level = CarAudioManager.AVC_LEVEL_CLOSE;
                break;
            case 1:
                level = CarAudioManager.AVC_LEVEL_LOW;
                break;
            case 2:
                level = CarAudioManager.AVC_LEVEL_MIDDLE;
                break;
            case 3:
                level = CarAudioManager.AVC_LEVEL_HIGH;
                break;
        }
        LogManager.Companion.d(TAG, "setAvcLevel:" + level);
        try {
            mCarAudioManager.setAvcLevel(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    private int mBalanceLevelValue, mFadeLevelValue;

    /**
     * 传入-7到7的值
     *
     * @param uiBalanceLevelValue 传入-7到7的值
     * @param uiFadeLevelValue    传入-7到7的值
     */
    public void setAudioBalance(final int uiBalanceLevelValue, final int uiFadeLevelValue) {
        try {
            if (uiBalanceLevelValue != mBalanceLevelValue) {
                mCarAudioManager.setBalanceTowardRight(uiBalanceLevelValue);
                mBalanceLevelValue = uiBalanceLevelValue;
                LogManager.Companion.d(TAG, "setAudio Balance " + uiBalanceLevelValue);
            }
            if (uiFadeLevelValue != mFadeLevelValue) {
                mCarAudioManager.setFadeTowardFront(uiFadeLevelValue);
                mFadeLevelValue = uiFadeLevelValue;
                LogManager.Companion.d(TAG, "setAudio Fade " + uiFadeLevelValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAudioBalance() {
        try {
            mBalanceLevelValue = mCarAudioManager.getBalanceTowardRight();
            return mBalanceLevelValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -100;
    }

    //-7,7
    public int getAudioFade() {
        try {
            mFadeLevelValue = mCarAudioManager.getFadeTowardFront();
            return mFadeLevelValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -100;
    }

    public static final int EQ_MODE_STANDARD = 0;
    public static final int EQ_MODE_POP = 1;
    public static final int EQ_MODE_ROCK = 2;
    public static final int EQ_MODE_JAZZ = 3;
    public static final int EQ_MODE_CLASSIC = 4;
    public static final int EQ_MODE_PEOPLE = 5;
    public static final int EQ_MODE_CUSTOM = 6;
    public static final int EQ_MODE_TECHNO = 7;//电子


    public int getAudioEQ() {
        int result = -1;
        try {
            int mode = mCarAudioManager.getEqMode();
            switch (mode) {
                case CarAudioManager.EQ_MODE_FLAT:
                    result = EQ_MODE_STANDARD;
                    break;
                case CarAudioManager.EQ_MODE_POP:
                    result = EQ_MODE_POP;
                    break;
                case CarAudioManager.EQ_MODE_ROCK:
                    result = EQ_MODE_ROCK;
                    break;
                case CarAudioManager.EQ_MODE_JAZZ:
                    result = EQ_MODE_JAZZ;
                    break;
                case CarAudioManager.EQ_MODE_CLASSIC:
                    result = EQ_MODE_CLASSIC;
                    break;
                case CarAudioManager.EQ_MODE_VOCAL:
                    result = EQ_MODE_PEOPLE;
                    break;
                case CarAudioManager.EQ_MODE_CUSTOM:
                    result = EQ_MODE_CUSTOM;
                    break;
            }
            LogManager.Companion.d(TAG, "getEqMode:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setAudioCustomHML(int high, int mid, int low) {
        setAudioEQ(EQ_MODE_CUSTOM);
        LogManager.Companion.d(TAG, "setAudioCustomHML:" + high + " " + mid + " " + low);
        setAudioHighVoice(high);
        setAudioMidVoice(mid);
        setAudioLowVoice(low);
    }

    public void setAudioEQ(int mode) {
        int m = CarAudioManager.EQ_MODE_FLAT;
        switch (mode) {
            case EQ_MODE_STANDARD:
                m = CarAudioManager.EQ_MODE_FLAT;
                break;
            case EQ_MODE_POP:
                m = CarAudioManager.EQ_MODE_POP;
                break;
            case EQ_MODE_ROCK:
                m = CarAudioManager.EQ_MODE_ROCK;
                break;
            case EQ_MODE_JAZZ:
                m = CarAudioManager.EQ_MODE_JAZZ;
                break;
            case EQ_MODE_CLASSIC:
                m = CarAudioManager.EQ_MODE_CLASSIC;
                break;
            case EQ_MODE_PEOPLE:
                m = CarAudioManager.EQ_MODE_VOCAL;
                break;
            case EQ_MODE_CUSTOM:
                m = CarAudioManager.EQ_MODE_CUSTOM;
                break;
        }
        try {
            mCarAudioManager.setEqMode(m);
            LogManager.Companion.d(TAG, "setEqMode:" + mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioEQ(int mode, int high, int mid, int low) {
        int m = CarAudioManager.EQ_MODE_FLAT;
        switch (mode) {
            case EQ_MODE_STANDARD:
                m = CarAudioManager.EQ_MODE_FLAT;
                break;
            case EQ_MODE_POP:
                m = CarAudioManager.EQ_MODE_POP;
                break;
            case EQ_MODE_ROCK:
                m = CarAudioManager.EQ_MODE_ROCK;
                break;
            case EQ_MODE_JAZZ:
                m = CarAudioManager.EQ_MODE_JAZZ;
                break;
            case EQ_MODE_CLASSIC:
                m = CarAudioManager.EQ_MODE_CLASSIC;
                break;
            case EQ_MODE_PEOPLE:
                m = CarAudioManager.EQ_MODE_VOCAL;
                break;
            case EQ_MODE_CUSTOM:
                m = CarAudioManager.EQ_MODE_CUSTOM;
                LogManager.Companion.d(TAG, "setAudioVoice:" + high + " " + mid + " " + low);
                setAudioHighVoice(high);
                setAudioMidVoice(mid);
                setAudioLowVoice(low);
                break;
        }
        try {
            mCarAudioManager.setEqMode(m);
            LogManager.Companion.d(TAG, "setEqMode:" + m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioLowVoice(int value) {
        try {
            mCarAudioManager.setAudioLowVoice(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioMidVoice(int value) {
        try {
            mCarAudioManager.setAudioMidVoice(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioHighVoice(int value) {
        try {
            mCarAudioManager.setAudioHighVoice(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAudioLowVoice() {
        try {
            return mCarAudioManager.getAudioLowVoice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getAudioMidVoice() {
        try {
            return mCarAudioManager.getAudioMidVoice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getAudioHighVoice() {
        try {
            return mCarAudioManager.getAudioHighVoice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setMicMute(boolean on) {
        mAudioManager.setMicrophoneMute(on);
    }

    public boolean isMicMute() {
        return mAudioManager.isMicrophoneMute();
    }


    private final class CarServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogManager.Companion.d(TAG, "onServiceConnected start");
            connectService = true;
            initCarManager();
            initAudioManager();
            onRegisterMcuListener();
            onRegisterSensorListener();
            onRegisterCabinListener();
            onRegisterHvacListener();
            onRegisterPowerListener();

            updateAdapterConnect();
            LogManager.Companion.d(TAG, "onServiceConnected end");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogManager.Companion.e(TAG, "onServiceDisconnected");
            connectService = false;
            updateAdapterConnect();
            mHandler.removeCallbacks(reConnectCarRunnable);
            mHandler.postDelayed(reConnectCarRunnable, 1500);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            LogManager.Companion.e(TAG, "onBindingDied");
            connectService = false;
            updateAdapterConnect();
        }
    }

    private void onRegisterPowerListener() {
        if (!connectService) {
            return;
        }
        try {
            if (null == mCarPowerManager) {
                mCarPowerManager = (CarPowerManager) mCarApi.getCarManager(Car.POWER_SERVICE);
                BrightnessManager.Companion.getInstance().injectManager(mCarPowerManager);
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }

    }

    private void initAudioManager() {
        if (connectService) {
            if (null == mCarAudioManager) {
                try {
                    mCarAudioManager = (CarAudioManager) mCarApi.getCarManager(Car.AUDIO_SERVICE);
//                    SoundManager.getInstance().initAudioManager(mCarAudioManager);
                    VoiceManager.Companion.getInstance().injectAudioManager(mCarAudioManager);
                } catch (CarNotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onRegisterHvacListener() {
        if (!connectService) {
            LogManager.Companion.e("onRegisterHvacListener but app not connect to service!");
            return;
        }
        try {
            if (null == hvacManager) {
                hvacManager = (CarHvacManager) mCarApi.getCarManager(Car.HVAC_SERVICE);
            }
            if (null != hvacManager) {
                Set<Integer> signals = RegisterSignalManager.Companion.getHvacSignal();
                int[] signalArray = signals.stream().mapToInt(Integer::intValue).toArray();
                hvacManager.registerCallback(hvacEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAdapterConnect() {
        mCarAdapterList.stream().filter(Objects::nonNull)
                .forEach(it -> it.onCarServiceBound(connectService));
    }

    private void initCarManager() {
        try {
//            if (null == mCarAudioManager) {
//                mCarAudioManager = (CarAudioManager) mCarApi.getCarManager(Car.AUDIO_SERVICE);
//            }
//            if (null == mCarPowerManager) {
//                mCarPowerManager = (CarPowerManager) mCarApi.getCarManager(Car.POWER_SERVICE);
//            }
            if (null == mBoxManager) {
                mBoxManager = (TboxManager) mCarApi.getCarManager(Car.TBOX_SERVICE);
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void onRegisterSensorListener() {
        try {
            if (null == mCarSensorManager) {
                mCarSensorManager = (CarSensorManager) mCarApi.getCarManager(Car.SENSOR_SERVICE);
            }
            if (null != mCarSensorManager) {
                mCarSensorManager.registerListener(sensorEventlistener, CarSensorManager.SENSOR_TYPE_IGNITION_STATE,
                        CarSensorManager.SENSOR_RATE_NORMAL);//acc
                mCarSensorManager.registerListener(sensorEventlistener, CarSensorManager.SENSOR_TYPE_NIGHT,
                        CarSensorManager.SENSOR_RATE_NORMAL);//小灯，白天黑夜注册监听
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }


}
