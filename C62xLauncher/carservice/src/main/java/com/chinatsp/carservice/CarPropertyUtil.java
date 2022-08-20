package com.chinatsp.carservice;

import android.car.CarNotConnectedException;
import android.car.VehicleAreaType;
import android.car.hardware.cabin.CarCabinManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class CarPropertyUtil {
    private static final String TAG = "CarProperty";
    static String getCarModel(CarCabinManager carCabinManager) {
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
        return Settings.System.getString(context.getApplicationContext().getContentResolver(), KEY_VIN);
    }
}
