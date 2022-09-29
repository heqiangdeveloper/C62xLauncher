package com.chinatsp.apppanel.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.huawei.appmarket.launcheragent.AppStoreProxy;
//绑定应用市场服务
public class AppStoreService {
    private static final String TAG = AppStoreService.class.toString();
    private boolean bindService = false;
    private LauncherProxyServiceConnection connection;
    private Context mContext;
    public static AppStoreService appStoreService;
    public AppStoreProxy proxy;

    public AppStoreService(Context mContext) {
        this.mContext = mContext;
    }

    public static AppStoreService getInstance(Context context) {
        if(appStoreService == null){
            appStoreService = new AppStoreService(context);
        }
        return appStoreService;
    }

    public void bindService(){
        Intent intent = new Intent("com.appstore.intent.action.LAUNCHER_COMMAND");
        intent.setPackage("com.huawei.appmarket.car.landscape");
        intent.setClassName("com.huawei.appmarket.car.landscape",
                "com.huawei.appmarket.launcheragent.AppStoreService");
        connection = new LauncherProxyServiceConnection();
        try{
            if(!bindService){
                bindService = mContext.bindService(intent,connection,Context.BIND_AUTO_CREATE);
                Log.d(TAG,"bindService: " + bindService);
            }
        }catch (Exception e){
            bindService = false;
            Log.d(TAG,"bindService: " + e.getMessage());
        }
    }

    private class LauncherProxyServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected from appstore");
            bindService = true;
            proxy = AppStoreProxy.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected from appstore");
            bindService = false;
        }
    }

    public void unbindService(){
        mContext.unbindService(connection);
    }

    public void doCommand(int commandType, String pkgName) {
        if(bindService){
            if(proxy != null){
                try{
                    Log.d(TAG,"doCommand commandType: " + commandType + ",pkgName: " + pkgName);
                    proxy.doCommand(commandType,pkgName);
                }catch (Exception e){
                    Log.d(TAG,"doCommandException commandType: " + commandType + ",pkgName: " + pkgName);
                }
            }else {
                Log.d(TAG,"AppStoreProxy is null");
            }
        }else {
            Log.d(TAG,"bindService Disconnected from appstore");
        }
    }
}
