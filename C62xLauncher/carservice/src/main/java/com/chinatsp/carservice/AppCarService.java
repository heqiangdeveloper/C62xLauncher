package com.chinatsp.carservice;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.cabin.CarCabinManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Arrays;

import launcher.base.service.AppServiceManager;
import launcher.base.service.car.ICarService;
import launcher.base.service.car.OffLineConfig;
import launcher.base.service.platform.PlatformService;
import launcher.base.utils.EasyLog;
import launcher.base.utils.property.PropertyUtils;

public class AppCarService implements ICarService {
    private final String TAG = "AppCarService";
    private Car mCar;
    private CarCabinManager mCarCabinManager;
    private boolean mConnected;
    private Context mContext;
    //配置字
    public static final String DVR = "persist.vendor.vehicle.dvr";//DVR行车记录仪  0无 1有

    public AppCarService(Context context) {
        this.mContext = context;
        boolean isC62x = checkPlatform();
        if (isC62x) {
            mCar = Car.createCar(context, mServiceConnection);
            mCar.connect();
        }
    }

    private boolean checkPlatform() {
        PlatformService platformService = (PlatformService) AppServiceManager.getService(AppServiceManager.SERVICE_PLATFORM);
        return platformService.isC62x();
    }

    @Override
    public boolean isConnect() {
        return mConnected;
    }

    @Override
    public String getCarType() {
        EasyLog.d(TAG, "getCarType , is connect: " + mConnected);
        EasyLog.d(TAG, "getCarType , carbin : " + mCarCabinManager);
        return CarPropertyUtil.getCarModel(mCarCabinManager);
    }

    @Override
    public int getOffLineCfg() {
        return PropertyUtils.getInt(mContext, OffLineConfig.PROPERTY_NAME, 1);
    }

    @Override
    public String getVinCode() {
        return CarPropertyUtil.getVinCode(mContext);
    }

    @Override
    public boolean isHasDVR() {
        int getDVR = PropertyUtils.getInt(mContext, DVR, 0);
        EasyLog.d(TAG, "getDVR: " + getDVR);
        return getDVR == 1;
    }

    @Override
    public boolean doSwitchWindow(boolean isOpenCmd) {
        EasyLog.i(TAG, "doSwitchWindow  open: " + isOpenCmd);
        int value = isOpenCmd ? 0x01 : 0x02;
        CarPropertyUtil.writeWindowSwitch(mCarCabinManager, value);
        return true;
    }

    private void fetchCarCabinOnConnected(Car car) {
        if (car == null) {
            return;
        }
        try {
            mCarCabinManager = (CarCabinManager) car.getCarManager(Car.CABIN_SERVICE);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            EasyLog.i(TAG, "onServiceConnected");
            mConnected = true;
            fetchCarCabinOnConnected(mCar);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mConnected = false;
            EasyLog.e(TAG, "onServiceDisconnected");

        }
    };

    public CarCabinManager getCarCabinManager() {
        return mCarCabinManager;
    }

    /*
     * 设置律动数组
     *
     */
    public void setMusicFrequencyData(int[] value) {
        if (mCarCabinManager != null) {
            try {
                mCarCabinManager.setIntArrayProperty(CarCabinManager.ID_HUM_ALC_ALL_FREQUENCY, VEHICLE_AREA_TYPE_GLOBAL, value);
                EasyLog.d(TAG, "setIntProperty, propertyId = " + "value = " + Arrays.toString(value));
            } catch (Exception e) {
                //   CarUtil.getInstance(mContext).handleException(e);
            }
        }
    }

    /*
     * 律动开关获取
     *
     */
    public boolean isAlcMusicOn() {
        int result = getIntProperty(CarCabinManager.ID_ALC_MUSIC_RHY_SW_RESPONSE);
        EasyLog.d(TAG, "result = " + result);
        return result == 0x1;
    }

    private int getIntProperty(int propertyId) {
        if (mCarCabinManager != null) {
            try {
                int value = mCarCabinManager.getIntProperty(propertyId, VEHICLE_AREA_TYPE_GLOBAL);
                EasyLog.d(TAG, "getIntProperty, propertyId = " + propertyId + "(0x"
                        + Integer.toHexString(propertyId) + ")" + "value = " + value);
                return value;
            } catch (Exception e) {
                // CarUtil.getInstance(mContext).handleException(e);
            }
        }
        return -1;
    }
}
