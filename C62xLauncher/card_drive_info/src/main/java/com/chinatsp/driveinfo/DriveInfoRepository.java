package com.chinatsp.driveinfo;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.chinatsp.driveinfo.callback.ILauncherWidgetCallback;
import com.chinatsp.driveinfo.callback.IReadDriveInfoListener;
import com.cihon.client.CihonManager;
import com.cihon.client.ServiceConnectionListener;
import com.cihon.client.SmallCardCallback;
import com.cihon.client.WidgetCallback;

import java.util.HashSet;
import java.util.Set;

import launcher.base.async.AsyncSchedule;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.PollingTask;

public class DriveInfoRepository {
    private final String TAG = "DriveInfoRepository";
    private MutableLiveData<DriveInfo> mMutableLiveData = new MutableLiveData<>();
    private final CihonManager mCihonManager;

    private volatile boolean mServiceConnect;
    private DriveInfo mCacheDriveInfo;

    private final Set<IReadDriveInfoListener> mReadDriveInfoListeners = new HashSet<>();
    private final Set<IReadDriveInfoListener> mDrawerReadDriveInfoListener = new HashSet<>() ;
    private PollingTask mServiceConnectTask;
    private final Set<SmallCardCallback> mSmallCardCallbacks = new HashSet<>();
    private final Set<ILauncherWidgetCallback> mWidgetCallbacks = new HashSet<>();

    private DriveInfoRepository() {
        mCihonManager = CihonManager.getInstance();
    }

    public static DriveInfoRepository getInstance() {
        return Holder.repository;
    }


    private static class Holder {
        private static DriveInfoRepository repository = new DriveInfoRepository();
    }

    private volatile boolean startBindService = false;
    public synchronized void bindServiceAsync(Context context) {
        if (startBindService) {
            return;
        }
        startBindService = true;
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                bindService(context);
            }
        });
        mCihonManager.setSmallCardCallback(mOriginSmallCardCallback);
        mCihonManager.setWidgetCallback(rootWidgetCallback);
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
                mCacheDriveInfo = readDriveInfo();
                notifyReadOnBindService(null);
            }
        });
        // 执行轮询任务: 每2秒执行一次绑定服务的任务, 直到退出
        mServiceConnectTask = new PollingTask(0, 2000, TAG) {
            @Override
            protected void executeTask() {
                mCihonManager.bindService(context);
            }

            @Override
            protected boolean enableExit() {
                return mServiceConnect;
            }
        };
        mServiceConnectTask.execute();
    }


    private void notifyReadOnBindService(DriveInfo cacheDriveInfo) {
        for (IReadDriveInfoListener readDriveInfoListener : mReadDriveInfoListeners) {
            readDriveInfoListener.onSuccess(cacheDriveInfo);
        }
        for (IReadDriveInfoListener readDriveInfoListener : mDrawerReadDriveInfoListener) {
            readDriveInfoListener.onSuccess(cacheDriveInfo);

        }
    }

    private SmallCardCallback mOriginSmallCardCallback = new SmallCardCallback() {
        @Override
        public void onOilChanged(float v) {
            if (mSmallCardCallbacks != null) {
                for (SmallCardCallback mSmallCardCallback : mSmallCardCallbacks) {
                    mSmallCardCallback.onOilChanged(v);
                }
            }
        }

        @Override
        public void onDrivingTimeChanged(int i) {
            if (mSmallCardCallbacks != null) {
                for (SmallCardCallback mSmallCardCallback : mSmallCardCallbacks) {
                    mSmallCardCallback.onDrivingTimeChanged(i);
                }
            }
        }

        @Override
        public void onDrivingMileageChanged(float v) {
            if (mSmallCardCallbacks != null) {
                for (SmallCardCallback mSmallCardCallback : mSmallCardCallbacks) {
                    mSmallCardCallback.onDrivingMileageChanged(v);
                }
            }
        }
    };

    void addDriveInfoCallback(SmallCardCallback smallCardCallback) {
        EasyLog.i(TAG, "setDriveInfoCallback SmallCardCallback: " + smallCardCallback.hashCode());
        this.mSmallCardCallbacks.add(smallCardCallback);
    }
    void removeDriveInfoCallback(SmallCardCallback smallCardCallback) {
        EasyLog.i(TAG, "setDriveInfoCallback SmallCardCallback: " + smallCardCallback.hashCode());
        this.mSmallCardCallbacks.remove(smallCardCallback);
    }

    private final WidgetCallback rootWidgetCallback = new WidgetCallback() {
        @Override
        public void onHealthyLevelChanged(String s) {
            for (ILauncherWidgetCallback mWidgetCallback : mWidgetCallbacks) {
                mWidgetCallback.onHealthyLevelChanged(s);
            }
        }

        @Override
        public void onMaintenanceMileageChanged(int i) {
            for (ILauncherWidgetCallback mWidgetCallback : mWidgetCallbacks) {
                mWidgetCallback.onMaintenanceMileageChanged(i);
            }
        }

        @Override
        public void onRankingChanged(int i) {
            for (ILauncherWidgetCallback mWidgetCallback : mWidgetCallbacks) {
                mWidgetCallback.onRankingChanged(i);
            }
        }
    };

    public void addWidgetCallback(ILauncherWidgetCallback widgetCallback) {
        if (widgetCallback != null) {
            // 由于控件组并未使用自定义View, 而且是唯一的, 所以当添加一个观察者时, 就将之前的观察者删除,  避免内存泄漏.
            this.mWidgetCallbacks.clear();
            this.mWidgetCallbacks.add(widgetCallback);
        }
    }
    public void deleteWidgetCallback(ILauncherWidgetCallback widgetCallback) {
        if (widgetCallback != null) {
            this.mWidgetCallbacks.remove(widgetCallback);
        }
    }

    public void addReadDriveInfoListener(IReadDriveInfoListener readDriveInfoListener) {
        if (readDriveInfoListener != null) {
            mReadDriveInfoListeners.add(readDriveInfoListener);
        }
    }
    public void removeReadDriveInfoListener(IReadDriveInfoListener readDriveInfoListener) {
        if (readDriveInfoListener != null) {
            mReadDriveInfoListeners.remove(readDriveInfoListener);
        }
    }

    public void addDrawerReadDriveInfoListener(IReadDriveInfoListener drawerReadDriveInfoListener) {
        if (drawerReadDriveInfoListener != null) {
            // // 由于控件组并未使用自定义View, 而且是唯一的, 所以当添加一个观察者时, 就将之前的观察者删除,  避免内存泄漏.
            mDrawerReadDriveInfoListener.clear();
            mDrawerReadDriveInfoListener.add(drawerReadDriveInfoListener);
        }
    }

    public DriveInfo readDriveInfo() {
        EasyLog.i(TAG, "readDriveInfo start");
        if (!mServiceConnect) {
            return null;
        }
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

    public boolean isServiceConnect() {
        return mServiceConnect;
    }

}
