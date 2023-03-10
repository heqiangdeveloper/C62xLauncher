package com.chinatsp.apppanel.service;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.anarchy.classifyview.Bean.LocationBean;
import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.AppConfigs.Priorities;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.adapter.MyAppInfoAdapter;
import com.chinatsp.apppanel.db.MyAppDB;
import com.chinatsp.apppanel.event.CancelDownloadEvent;
import com.chinatsp.apppanel.event.DownloadEvent;
import com.chinatsp.apppanel.event.FailDownloadEvent;
import com.chinatsp.apppanel.event.StartDownloadEvent;
import com.chinatsp.apppanel.event.UpdateEvent;
import com.huawei.appmarket.launcheragent.ILauncherProxy;
import com.huawei.appmarket.launcheragent.StoreAppInfo;
import com.huawei.appmarket.launcheragent.launcher.AppState;
import com.huawei.appmarket.launcheragent.launcher.ErrorCodeLauncher;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherBinder extends ILauncherProxy.Stub {
    private static final String TAG = LauncherBinder.class.getName();
    public static LauncherBinder launcherBinder;
    private MyAppDB db;
    private LocationBean locationBean;
    private List<String> downloadPkgs = new ArrayList<>();
    private String appName;
    private String pkgName;
    private int appState;
    private int appType;
    private int updateType;
    private int downloadProgress;
    private int versionCode;
    private Context mContext;

    public LauncherBinder(Context mContext) {
        this.mContext = mContext;
        db = new MyAppDB(mContext);
    }

    public static LauncherBinder getInstance(Context mContext) {
        if(launcherBinder == null){
            launcherBinder = new LauncherBinder(mContext);
        }
        return launcherBinder;
    }

    @Override
    public void notifyAppInfos(List<StoreAppInfo> infos) throws RemoteException {
        Log.d(TAG,"notifyAppInfos");
    }

    /*
     *????????????
     */
    @Override
    public void notifyAppIcon(String pkgName, byte[] icon) throws RemoteException {
        Log.d(TAG,"notifyAppIcon " + pkgName);
        int num = db.isExistPackageInDownload(pkgName);
        String name = "????????????";//??????????????????notifyAppInfo()??????????????????
        if(num == 0){
            Log.d(TAG,"start download " + pkgName);
            locationBean = new LocationBean();
            //??????????????????parent_index??????????????????????????????????????????????????????sqlite???????????????0
            locationBean.setParentIndex(Constant.DOWNLOAD_PARENT_INDEX);
            locationBean.setChildIndex(-1);
            locationBean.setPackageName(pkgName);
            locationBean.setName(name);
            locationBean.setPriority(Priorities.MIN_PRIORITY);
            locationBean.setCanuninstalled(1);
            locationBean.setStatus(0);//????????????
            locationBean.setInstalled(AppState.DOWNLOADING);//?????????
            locationBean.setTitle("");
            locationBean.setImgByte(icon);
            locationBean.setImgDrawable(null);
            db.insertDownload(locationBean);
            EventBus.getDefault().post(new StartDownloadEvent(locationBean));
        }else {
            Log.d(TAG,"start download already in DB: " + pkgName);
        }
    }

    /*
    *???????????????????????????
     */
    @Override
    public void notifyAppInfo(StoreAppInfo info) throws RemoteException {
        appName = info.getAppName();
        pkgName = info.getPkgName();
        appState = info.getAppState();
        appType = info.getAppType();
        updateType = info.getUpdateType();
        downloadProgress = info.getDownloadProgress();
        versionCode = info.getVersionCode();
        Log.d(TAG,"notifyAppInfo " + "appName: " + appName + ",pkgName: " + pkgName + ",appState: " + appState +
                ",appType: " + appType + ",updateType: " + updateType + ",downloadProgress: " + downloadProgress +
                ",versionCode: " + versionCode);
        locationBean = new LocationBean();
        locationBean.setPackageName(pkgName);
        if(appState == AppState.DOWNLOADING && downloadProgress == 100){
            appState = AppState.INSTALLING;
        }
        locationBean.setInstalled(appState);
        locationBean.setCanuninstalled(1);
        locationBean.setName(appName);
        locationBean.setStatus(downloadProgress);
        locationBean.setReserve1(String.valueOf(versionCode));
        locationBean.setReserve2(String.valueOf(versionCode));
        if(appState == AppState.COULD_BE_CANCELED){
            db.deleteDownload(pkgName);
            EventBus.getDefault().post(new CancelDownloadEvent(pkgName));
        }else {
            int num = db.isExistPackageInDownload(pkgName);
            Log.d(TAG,"DownloadEvent " + pkgName + " num is: " + num);
            if(num == 0) {
                db.insertDownload(locationBean);
            }else {
                db.updateDownloadStatusInDownload(locationBean);
            }
            EventBus.getDefault().post(new DownloadEvent(locationBean));
        }
    }

    //??????????????????
    @Override
    public void notifyAppUpdate(String pkgName, int appVersionCode, int updateType) throws RemoteException {
        Log.d(TAG,"notifyAppUpdate " + pkgName + "," + appVersionCode + "," + updateType);
        locationBean = new LocationBean();
        locationBean.setPackageName(pkgName);
        locationBean.setCanuninstalled(1);
        locationBean.setInstalled(AppState.COULD_UPDATE);
        locationBean.setReserve3(String.valueOf(appVersionCode));
        int num = db.isExistPackageInDownload(pkgName);
        Log.d(TAG,"notifyAppUpdate " + pkgName + " num is: " + num);
        if(num == 0) {
            db.insertDownload(locationBean);
        }else {
            db.updateAppUpdateInDownload(locationBean);//??????Download???????????????????????????
        }
        EventBus.getDefault().post(new UpdateEvent(locationBean));
    }

    @Override
    public void notifyAppsUpdate(List<StoreAppInfo> infos) throws RemoteException {
        Log.d(TAG,"notifyAppsUpdate ");
    }

    //???????????????????????????
    @Override
    public void notifyAppError(String pkgName, int errorCode) throws RemoteException {
        Log.d(TAG,"notifyAppError " + pkgName + "," + errorCode);
        locationBean = new LocationBean();
        locationBean.setPackageName(pkgName);
        locationBean.setCanuninstalled(1);
        locationBean.setInstalled(AppState.DOWNLOAD_FAIL);
        int num = db.isExistPackageInDownload(pkgName);
        Log.d(TAG,"notifyAppError " + pkgName + " num is: " + num);
        if(num == 0) {
            db.insertDownload(locationBean);
        }else {
            db.updateFailDownloadInDownload(locationBean);//??????Download??????????????????
        }
        EventBus.getDefault().post(new FailDownloadEvent(locationBean));
    }
}
