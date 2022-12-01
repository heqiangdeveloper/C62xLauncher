package com.chinatsp.drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import launcher.base.utils.EasyLog;

public abstract class SystemWindowReceiver extends BroadcastReceiver {
    private static final String ACTION_VCU_DIALOG_DISPLAY = "com.chinatsp.vehicle.actions.VCU_DIALOG_DISPLAY";
    private static final String ACTION_VOICE_ICON = "com.chinatsp.systemui.ACTION_VOICE_ICON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        boolean needCollapse = checkCollapseDrawer(intent);
        if (needCollapse) {
            collapseDrawer();
        }
    }

    public abstract void collapseDrawer();

    public static IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_VCU_DIALOG_DISPLAY);
        intentFilter.addAction(ACTION_VOICE_ICON);
        return intentFilter;
    }


    public static boolean checkCollapseDrawer(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return false;
        }
        boolean need = false;
        String action = intent.getAction();
        EasyLog.d("SystemWindowReceiver", "checkCollapseDrawer:"+ action);
        if (action.equals(ACTION_VCU_DIALOG_DISPLAY)) {
            need = true;
        } else if (action.equals(ACTION_VOICE_ICON)) {
            int type = intent.getIntExtra("KEY_INT_TYPE", 0);
            EasyLog.d("SystemWindowReceiver", "checkCollapseDrawer , KEY_INT_TYPE:"+ type);
            // 当type==2时, 需要收起控件组. 参数来源: 北斗-刘翔
            if (type == 2) {
                need = true;
            }
        }
        return need;
    }
}
