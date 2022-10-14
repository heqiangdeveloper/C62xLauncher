package com.chinatsp.drawer.search.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinatsp.drawer.search.manager.SearchManager;

public class AppInstallStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInstallStatusReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //接受到安装新的APP及卸载新的APP将清空数据库并添加进去
        if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
            Log.d(TAG,"onReceive ACTION_PACKAGE_ADDED");
            SearchManager.getInstance().deleteDB();
            SearchManager.getInstance().insertDB();
        }else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
            Log.d(TAG,"onReceive ACTION_PACKAGE_REMOVED");
            SearchManager.getInstance().deleteDB();
            SearchManager.getInstance().insertDB();
        }
    }
}
