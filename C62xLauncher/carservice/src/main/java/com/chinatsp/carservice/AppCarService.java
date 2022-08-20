package com.chinatsp.carservice;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.cabin.CarCabinManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import launcher.base.service.car.ICarService;
import launcher.base.utils.EasyLog;

public class AppCarService implements ICarService {
    private final String TAG = "AppCarService";
    private Car mCar;
    private CarCabinManager mCarCabinManager;
    private boolean mConnected;
    private Context mContext;

    public AppCarService(Context context) {
        this.mContext = context;
        mCar = Car.createCar(context, mServiceConnection);
        mCar.connect();
    }

    @Override
    public boolean isConnect() {
        return mConnected;
    }

    @Override
    public String getCarType() {
        EasyLog.d(TAG,"getCarType , is connect: "+mConnected);
        EasyLog.d(TAG,"getCarType , carbin : "+mCarCabinManager);
        return CarPropertyUtil.getCarModel(mCarCabinManager);
//        return CarPropertyUtil.getCarType2(mContext);
    }
    @Override
    public String getVinCode() {
        return CarPropertyUtil.getVinCode(mContext);
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
}
