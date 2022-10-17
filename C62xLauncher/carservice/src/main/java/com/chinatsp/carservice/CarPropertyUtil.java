package com.chinatsp.carservice;

import android.car.CarNotConnectedException;
import android.car.VehicleAreaType;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.cabin.CarCabinManager;
import android.content.Context;
import android.hardware.automotive.vehicle.V2_0.VehicleArea;
import android.hardware.automotive.vehicle.V2_0.VehicleAreaConfig;
import android.provider.Settings;
import android.util.Log;

import launcher.base.utils.EasyLog;

public class CarPropertyUtil {
    private static final String TAG = "CarPropertyUtil";
    static String getCarModel(CarCabinManager carCabinManager) {
        //配置字persist.vendor.vehicle.car_type
        if (carCabinManager != null) {
            try {
                return carCabinManager.getStringProperty(CarCabinManager.ID_CAR_VEHICLE_TYPE, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL);
            } catch (Error | CarNotConnectedException e) {
                // Occur java.lang.NoSuchMethodError maybe
                Log.d(TAG, "getCarModel fail: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return "C62X-F06";
    }

    public static String getCarType2(Context context) {
        String KEY = "persist.vendor.vehicle.car_type";
        if (context == null) return "";
        return Settings.System.getString(context.getApplicationContext().getContentResolver(), KEY);
    }

    public static String getVinCode(Context context) {
        String KEY_VIN = "VIN";
        if (context == null) return "";
        String vin = Settings.System.getString(context.getApplicationContext().getContentResolver(), KEY_VIN);
        if (vin == null) {
            vin = "";
        }
        return vin;
    }

    public static void writeWindowSwitch(CarCabinManager carCabinManager , int value) {
        if (carCabinManager == null) {
            EasyLog.w(TAG, "writeWindowSwitch error, carCabinManager is null");
            return;
        }
        try {
            carCabinManager.setIntProperty(CarCabinManager.ID_ONE_KEY_CLICK_ALL_WINDOW_SW, VehicleArea.GLOBAL, value);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }
}
