package com.chinatsp.driveinfo;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.chinatsp.driveinfo.callback.IReadDriveInfoListener;
import com.chinatsp.driveinfo.callback.ILauncherWidgetCallback;
import com.cihon.client.CihonManager;
import com.cihon.client.ServiceConnectionListener;
import com.cihon.client.SmallCardCallback;
import com.cihon.client.WidgetCallback;

import launcher.base.utils.EasyLog;

public class DriveInfoRepository {
    private final String TAG = "DriveInfoRepository";
    private MutableLiveData<DriveInfo> mMutableLiveData = new MutableLiveData<>();
    private final CihonManager mCihonManager;
    private boolean mServiceConnect;
    private DriveInfo mCacheDriveInfo;

    private IReadDriveInfoListener mReadDriveInfoListener;
    private IReadDriveInfoListener mDrawerReadDriveInfoListener;

    private DriveInfoRepository() {
        mCihonManager = CihonManager.getInstance();
    }

    public static DriveInfoRepository getInstance() {
        return Holder.repository;
    }


    private static class Holder {
        private static DriveInfoRepository repository = new DriveInfoRepository();
    }

    public void bindService(Context context) {
        EasyLog.i(TAG, "start: bindService");
        if (mServiceConnect) {
            EasyLog.w(TAG, "start: bindService cancel : already connected.");
            return;
        }
        mCihonManager.setServiceConnectionListener(new ServiceConnectionListener() {
            @Override
            public void onServiceConnected() {
                mServiceConnect = true;
                EasyLog.i(TAG, "onServiceConnected-->连接成功");
                mCacheDriveInfo = readDriveInfo();
                notifyReadOnBindService(mCacheDriveInfo);
            }

            @Override
            public void onServiceDisconnected() {
                mServiceConnect = false;
                EasyLog.w(TAG, "onServiceConnected-->断开连接");
            }
        });
        mCihonManager.bindService(context);
    }

    private void notifyReadOnBindService(DriveInfo cacheDriveInfo) {
        if (mReadDriveInfoListener != null) {
            mReadDriveInfoListener.onSuccess(cacheDriveInfo);
        }
        if (mDrawerReadDriveInfoListener != null) {
            mDrawerReadDriveInfoListener.onSuccess(cacheDriveInfo);
        }
    }
    void setDriveInfoCallback(SmallCardCallback smallCardCallback) {
        mCihonManager.setSmallCardCallback(smallCardCallback);
    }

    public void setWidgetCallback(ILauncherWidgetCallback widgetCallback) {
        mCihonManager.setWidgetCallback(new WidgetCallback() {
            @Override
            public void onHealthyLevelChanged(String s) {
                if (widgetCallback != null) {
                    widgetCallback.onHealthyLevelChanged(s);
                }
            }

            @Override
            public void onMaintenanceMileageChanged(int i) {
                if (widgetCallback != null) {
                    widgetCallback.onMaintenanceMileageChanged(i);
                }
            }

            @Override
            public void onRankingChanged(int i) {
                if (widgetCallback != null) {
                    widgetCallback.onRankingChanged(i);
                }
            }
        });
    }


    public void setReadDriveInfoListener(IReadDriveInfoListener readDriveInfoListener) {
        mReadDriveInfoListener = readDriveInfoListener;
    }

    public void setDrawerReadDriveInfoListener(IReadDriveInfoListener drawerReadDriveInfoListener) {
        mDrawerReadDriveInfoListener = drawerReadDriveInfoListener;
    }

    public DriveInfo readDriveInfo() {
        EasyLog.i(TAG, "readDriveInfo start");
        float drivingMileage = 0;
        int drivingTime = 0;
        float oilConsumption = 0;
        int maintenanceMileage = 0;
        String healthyLevel = "";
        int ranking = 0;
        try {
            drivingMileage = mCihonManager.getDrivingMileage();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo drivingMileage fail: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            drivingTime = mCihonManager.getDrivingTime();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo drivingTime fail: " + e.getMessage());
        }
        try {
            oilConsumption = mCihonManager.getOilConsumption();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo oilConsumption fail: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            maintenanceMileage = mCihonManager.getMaintenanceMileage();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo maintenanceMileage fail: " + e.getMessage());
        }
        try {
            healthyLevel = mCihonManager.getHealthyLevel();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo healthyLevel fail: " + e.getMessage());
        }
        try {
            ranking = mCihonManager.getRanking();
        } catch (Exception e) {
            EasyLog.e(TAG, "readDriveInfo ranking fail: " + e.getMessage());
        }


        EasyLog.d(TAG, "readDriveInfo  drivingMileage: " + drivingMileage);
        EasyLog.d(TAG, "readDriveInfo  drivingTime: " + drivingTime);
        EasyLog.d(TAG, "readDriveInfo  oilConsumption: " + oilConsumption);
        EasyLog.d(TAG, "readDriveInfo  maintenanceMileage: " + maintenanceMileage);
        EasyLog.d(TAG, "readDriveInfo  healthyLevel: " + healthyLevel);
        EasyLog.d(TAG, "readDriveInfo  ranking: " + ranking);

        return new DriveInfo(drivingMileage, drivingTime, oilConsumption, maintenanceMileage, healthyLevel, ranking);
    }
}
