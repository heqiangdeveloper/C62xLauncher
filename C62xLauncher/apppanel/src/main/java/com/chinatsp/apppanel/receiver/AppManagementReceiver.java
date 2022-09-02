package com.chinatsp.apppanel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.window.AppManagementWindow;

public class AppManagementReceiver extends BroadcastReceiver {
    private static final String TAG = "AppManagementReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constant.APPMANEGEMENTBROADCAST)) {
            Log.d(TAG, "onReceive com.chinatsp.launcher.appmanegement");
            int operation = intent.getIntExtra(Constant.APPMANAGEMENT_OPERATION,Constant.CLOSE_APPMANAGEMENT);
            boolean isShow = AppManagementWindow.getInstance(context).isShow();
            Log.d(TAG, "onReceive: operation = " + operation + ",isShow = " + isShow);
            if(operation == Constant.OPEN_APPMANAGEMENT){
                if(!isShow){
                    AppManagementWindow.getInstance(context).show();
                }
            }else {
                if(isShow){
                    AppManagementWindow.getInstance(context).hide();
                }
            }
        }
    }
}
