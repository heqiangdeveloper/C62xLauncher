package com.chinatsp.navigation.another;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NavigationRes;

import com.chinatsp.navigation.repository.NaviRepository;

import launcher.base.utils.EasyLog;

public class EasyConnNaviReceiver extends BroadcastReceiver {
    private static final String TAG = "EasyConnNaviReceiver";
    public static final String ACTION_EASYCONN_START_NAVIGATION = "net.easyconn.navigation.STARTED";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        EasyLog.d(TAG, "onReceive action: " + action);
        stopGaoDeNavigation();
    }

    private void stopGaoDeNavigation() {
        NaviRepository.getInstance().exitNaiveStatus();
    }
}
