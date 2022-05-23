package com.chinatsp.settinglib;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.hardware.automotive.vehicle.V2_0.VehicleProperty.VENDOR_AMPLIFIER_SWITCH_STATUS;

import android.annotation.SuppressLint;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaSeat;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.constant.MCU;
import android.car.hardware.constant.VEHICLE;
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
import com.chinatsp.settinglib.optios.ACOption;
import com.chinatsp.settinglib.optios.Area;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author
 */
public class SettingManager {
    public static final String TAG = "SettingManager";
    @SuppressLint("StaticFieldLeak")
    private static volatile SettingManager settingManager;
    private AudioManager mAudioManager;
    private CarAudioManager mCarAudioManager;
    private CarMcuManager mCarMcuManager;
    private CarPowerManager mCarPowerManager;
    private TboxManager mTboxManager;
    private CarSensorManager mCarSensorManager;
    private CarCabinManager mCarCabinManager;
    private String productName;
    private Car mCarApi;
    private static Context mContext;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private SettingManager() {
        if (mContext == null) {
            LogUtils.e(TAG, "context==null");
            return;
            //throw new NullPointerException("context==null");
        }
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        productName = SystemProperties.get("ro.product.name");//判断是3y1还是f202
        LogUtils.w(TAG, "productName:" + productName + " " + getOsVersion());
        if (mCarApi != null && mCarApi.isConnected()) {
            LogUtils.d(TAG, "mCarApi.isConnected:" + true);
            return;
        }
        bindCarService();
    }

    private void unBindCarService() {
        LogUtils.d(TAG, "unBindCarService");
        mCarMcuManager.unregisterCallback(mCarMcuEventCallback);
        try {
            mCarCabinManager.unregisterCallback(mCarCabinEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mCarApi.disconnect();
    }

    private void bindCarService() {
        mCarApi = Car.createCar(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    LogUtils.d(TAG, "Car onServiceConnected");
                    mCarAudioManager = (CarAudioManager) mCarApi.getCarManager(Car.AUDIO_SERVICE);
                    mCarMcuManager = (CarMcuManager) mCarApi.getCarManager(Car.CAR_MCU_SERVICE);
                    mCarPowerManager = (CarPowerManager) mCarApi.getCarManager(Car.POWER_SERVICE);
                    registerCallback();

                    mCarSensorManager = (CarSensorManager) mCarApi.getCarManager(Car.SENSOR_SERVICE);
                    mCarSensorManager.registerListener(listener, CarSensorManager.SENSOR_TYPE_IGNITION_STATE,
                            CarSensorManager.SENSOR_RATE_NORMAL);//acc
                    mCarSensorManager.registerListener(listener, CarSensorManager.SENSOR_TYPE_NIGHT,
                            CarSensorManager.SENSOR_RATE_NORMAL);//小灯，白天黑夜注册监听

                    mCarCabinManager = (CarCabinManager) mCarApi.getCarManager(Car.CABIN_SERVICE);
                    setCarDYBinCallback();

                    mTboxManager = (TboxManager) mCarApi.getCarManager(Car.TBOX_SERVICE);
                    for (int i = 0; i < mCarAdapterList.size(); i++) {
                        mCarAdapterList.get(i).onCarServiceBound(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d(TAG, "Car not connected " + e.getMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtils.e(TAG, "Car onServiceDisconnected");
                for (int i = 0; i < mCarAdapterList.size(); i++) {
                    mCarAdapterList.get(i).onCarServiceBound(false);
                }
                mHandler.removeCallbacks(reConnectCarRunnable);
                mHandler.postDelayed(reConnectCarRunnable, 1500);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                LogUtils.e(TAG, "Car onBindingDied");
                for (int i = 0; i < mCarAdapterList.size(); i++) {
                    mCarAdapterList.get(i).onCarServiceBound(false);
                }
            }
        });
        LogUtils.d(TAG, "mCarApi to connect()");
        mCarApi.connect();
    }

    private final Runnable reConnectCarRunnable = new Runnable() {
        @Override
        public void run() {
            unBindCarService();
            bindCarService();
        }
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

    private void registerCallback() {
        try {
            LogUtils.d(TAG, "registerCallback");
            mCarMcuManager.registerCallback(mCarMcuEventCallback, new int[]{
                    CarMcuManager.ID_REVERSE_SIGNAL,
                    CarMcuManager.ID_MCU_LOST_CANID,
                    CarMcuManager.ID_MCU_ACC_STATE,
                    //CarMcuManager.ID_SHUTDOWN_WARING_INFO,
                    CarMcuManager.ID_VENDOR_MCU_POWER_MODE,
                    CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE,
                    CarMcuManager.ID_NIGHT_MODE,
                    CarMcuManager.ID_VENDOR_PHOTO_REQ
            });

            LogUtils.d(TAG, "registerCallback ok");
        } catch (Exception e) {
            LogUtils.d(TAG, "Car is not connected!");
        }
    }

    private boolean isAVMOn = false, isAPAOn = false;

    private void setCarDYBinCallback() {
        if (null != mCarCabinManager) {
            try {
                List<Integer> acConcernIdList = ACManager.getConcernIdList();
                int[] acConcernArray = acConcernIdList.stream().mapToInt(it -> it).toArray();
                mCarCabinManager.registerCallback(mCarCabinEventCallback, acConcernArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setGPSData(float[] data) {
        try {
            mCarCabinManager.setFloatArrayProperty(VehiclePropertyIds.DVR_CUR_LOCATION, VEHICLE_AREA_TYPE_GLOBAL, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final CarCabinManager.CarCabinEventCallback mCarCabinEventCallback = new CarCabinManager.CarCabinEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            int propertyId = carPropertyValue.getPropertyId();
            if (ACManager.getConcernIdList().contains(propertyId)) {
                ACManager.Companion.getInstance().onPropertyChanged(carPropertyValue);
            }

//            switch (carPropertyValue.getPropertyId()) {
//                case CarCabinManager.ID_AVM_DISPLAY_SWITCH:
//                    int avm_switch = (Integer) carPropertyValue.getValue();
//                    LogUtils.d(TAG, "ID_AVM_DISPLAY_SWITCH = " + avm_switch);
//                    if (avm_switch == VEHICLE.ON) {
//                        isAVMOn = true;
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onAvmStatusChange(true);
//                        }
//                    } else if (avm_switch == VEHICLE.OFF) {
//                        isAVMOn = false;
//                        for (int i = 0; i < mCarAdapterList.size(); i++) {
//                            mCarAdapterList.get(i).onAvmStatusChange(false);
//                        }
//                    }
//                    break;
//            }
        }

        @Override
        public void onErrorEvent(int i, int i1) {

        }
    };

    private final CarMcuManager.CarMcuEventCallback mCarMcuEventCallback = new CarMcuManager.CarMcuEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            //LogUtils.d(TAG, "CarMcuManager onChangeEvent id = " + carPropertyValue.getPropertyId());
            switch (carPropertyValue.getPropertyId()) {
                case CarMcuManager.ID_VENDOR_MCU_POWER_MODE://电源状态
                    int powerStatus = (Integer) carPropertyValue.getValue();
                    LogUtils.d(TAG, "powerStatus= " + powerStatus);//6 8 4
                    if (powerStatus == MCU.POWER_ON) {//6
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onAccStatusChange(true);
                        }
                    } else if (powerStatus == MCU.POWER_OFF) {//4
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onAccStatusChange(false);
                        }
                    }

                    break;
                case CarMcuManager.ID_REVERSE_SIGNAL://倒车状态
                    int reverseStatus = (Integer) carPropertyValue.getValue();
                    LogUtils.d(TAG, "ID_REVERSE_SIGNAL= " + reverseStatus);
                    if (reverseStatus == VEHICLE.ON) {
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onReverseStatusChange(true);
                        }
                    } else if (reverseStatus == VEHICLE.OFF) {
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onReverseStatusChange(false);
                        }
                    }
                    break;
                case CarMcuManager.ID_VENDOR_LIGHT_NIGHT_MODE_STATE://显示模式
                    break;
                case CarMcuManager.ID_MCU_ACC_STATE:
                    int mcu_acc_state = (Integer) carPropertyValue.getValue();
                    LogUtils.d(TAG, "ID_MCU_ACC_STATE= " + mcu_acc_state);
                    if (mcu_acc_state == CarSensorEvent.IGNITION_STATE_ACC ||
                            mcu_acc_state == CarSensorEvent.IGNITION_STATE_ON ||
                            mcu_acc_state == CarSensorEvent.IGNITION_STATE_START) {
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onPowerStatusChange(mcu_acc_state);
                        }
                    } //else if (mcu_acc_state == CarSensorEvent.IGNITION_STATE_OFF) { }
                    break;
                case CarMcuManager.ID_MCU_LOST_CANID:
                    Integer[] lostCanidStatus = (Integer[]) carPropertyValue.getValue();//CAN节点丢失 0x00-正常 0x01-丢失
                    LogUtils.d(TAG, "ID_MCU_LOST_CANID= " + Arrays.toString(lostCanidStatus));
                    break;
                case CarMcuManager.ID_VENDOR_PHOTO_REQ:
                    if (carPropertyValue.getAreaId() == VehicleAreaSeat.SEAT_ROW_1_CENTER) {
                        LogUtils.d("ID_VENDOR_PHOTO_REQ " + carPropertyValue.getValue());
                        for (int i = 0; i < mCarAdapterList.size(); i++) {
                            mCarAdapterList.get(i).onPhotoReqChange(true);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onErrorEvent(int i, int i1) {

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

    private final CarSensorManager.OnSensorChangedListener listener = new CarSensorManager.OnSensorChangedListener() {
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
                    LogUtils.d(TAG, "littleLight change,isNight state=" + isNight);
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
        LogUtils.d(TAG, "3Y1 setAvmOff");
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
        LogUtils.d(TAG, "isCarServiceRunning =" + rel);
//        if (!rel) {
//            bindCarService();
//        }
        return rel;
    }

    public void unInit() {
        LogUtils.d(TAG, "unInit");
        mCarApi.disconnect();
        mContext = null;
        settingManager = null;
    }

    public static void init(Context context) {
        LogUtils.d(TAG, "init");
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
        LogUtils.d(TAG, "registerVolumeChangeObserver");
    }

    public void unregisterVolumeChangeObserver(ContentObserver observer) {
        LogUtils.d(TAG, "unregisterVolumeChangeObserver");
        mCarAudioManager.unregisterVolumeChangeObserver(observer);
        //mContext.getContentResolver().unregisterContentObserver(observer);
    }

    private boolean isMobileON = false;

    public boolean isMobileNetworkON() {
        return isMobileON;
    }

    public void setMobileNetSwitch(final boolean on, final IMobileState iMobileState) {
        mTboxManager.addTBoxChangedListener(new TboxManager.TboxChangedListener() {
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
                mTboxManager.setMobileState(on, new INetworkCallBack() {
                    @Override
                    public synchronized void onCompleted(final int i, String s) {
                        LogUtils.d(TAG, "setMobileState onCompleted " + i + " " + s);
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
                        LogUtils.d(TAG, "setMobileState onException " + i + " " + s);
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

    public boolean doCabinPropertyIntent(int id, int value, @NotNull Area area) {
        try {
            if (null != mCarCabinManager) {
                mCarCabinManager.setIntProperty(id, area.getId(), value);
                return true;
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getIntValueByProperty(int id, Area area) {
        if (null != mCarCabinManager) {
            try {
                mCarCabinManager.getIntProperty(id, area.getId());
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public interface IMobileState {
        void onMobileStateChange(boolean on);

        void onMobileStateError();
    }

    private TboxManager.TboxChangedListener tboxChangedListener;

    public void addMobileNetListener(final IMobileState iMobileState) {
        tboxChangedListener = new TboxManager.TboxChangedListener() {
            @Override
            public void onCallStatusChanged(int i, int i1) {

            }

            @Override
            public void onTboxMobileSignalChanged(int i, int i1, int i2) {

            }

            @Override
            public void onTboxMobileSwitchStateChanged(int i) {
                LogUtils.d(TAG, "onTboxMobileSwitchStateChanged " + i);
                if (iMobileState != null) {
                    iMobileState.onMobileStateChange(i == 1);
                }
            }

            @Override
            public void onWifiStateChanged(String s, int i, String s1) {
                LogUtils.d(TAG, "onWifiStateChanged " + s + " " + i + " " + s1);
            }
        };
        mTboxManager.addTBoxChangedListener(tboxChangedListener);
    }

    public void removeMobileNetListener() {
        if (tboxChangedListener != null) {
            mTboxManager.removeTBoxChangedListener(tboxChangedListener);
        }
    }

    public void getMobileNetSwitch(final IMobileState iMobileState) {
        LogUtils.d(TAG, "getMobileState");
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mTboxManager.getMobileState(new INetworkCallBack() {
                    @Override
                    public void onCompleted(final int i, String s) {
                        LogUtils.d(TAG, "getMobileState onCompleted " + i + " " + s);
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
                        LogUtils.d(TAG, "getMobileState onException " + i + " " + s);
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
            LogUtils.d(TAG, "onChange getBrightness");
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
            LogUtils.d(TAG, "setBrightness = " + brigness);
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
        LogUtils.d(TAG, "getBrightness = " + ret);
        return ret;
    }



    public void setScreenOn(boolean on) {
        try {
            if (on) {
                LogUtils.d(TAG, "sendDisplayOn");
                mCarPowerManager.sendDisplayOn();
            } else {
                LogUtils.d(TAG, "sendDisplayOff");
                mCarPowerManager.sendDisplayOff();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTUID() {
        try {
            String tuid = Settings.System.getString(mContext.getContentResolver(), "TUID");
            LogUtils.d(TAG, "" + tuid);
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
            LogUtils.d(TAG, "" + vin);
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
            LogUtils.d(TAG, "getBeepLevel:" + level);
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
            LogUtils.d(TAG, "getAvcLevel:" + level);
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
        LogUtils.d(TAG, "setAvcLevel:" + level);
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
                LogUtils.d(TAG, "setAudio Balance " + uiBalanceLevelValue);
            }
            if (uiFadeLevelValue != mFadeLevelValue) {
                mCarAudioManager.setFadeTowardFront(uiFadeLevelValue);
                mFadeLevelValue = uiFadeLevelValue;
                LogUtils.d(TAG, "setAudio Fade " + uiFadeLevelValue);
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
            LogUtils.d(TAG, "getEqMode:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setAudioCustomHML(int high, int mid, int low) {
        setAudioEQ(EQ_MODE_CUSTOM);
        LogUtils.d(TAG, "setAudioCustomHML:" + high + " " + mid + " " + low);
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
            LogUtils.d(TAG, "setEqMode:" + mode);
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
                LogUtils.d(TAG, "setAudioVoice:" + high + " " + mid + " " + low);
                setAudioHighVoice(high);
                setAudioMidVoice(mid);
                setAudioLowVoice(low);
                break;
        }
        try {
            mCarAudioManager.setEqMode(m);
            LogUtils.d(TAG, "setEqMode:" + m);
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










}
