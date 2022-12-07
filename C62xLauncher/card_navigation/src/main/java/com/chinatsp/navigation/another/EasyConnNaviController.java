package com.chinatsp.navigation.another;

import android.content.Context;
import android.content.Intent;

import launcher.base.applists.AppLists;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.DebounceTask;

public class EasyConnNaviController {
    private Context mContext;
    private static final int TASK_INTERVAL_TIME = 1000;

    public static final String ACTION_SEND_STOP_NAVI = "net.easyconn.navigation.STOP_EASYCONN_NAVI";

    private static class Holder{
        private static EasyConnNaviController instance = new EasyConnNaviController();
    }

    private EasyConnNaviController() {
    }

    public static EasyConnNaviController getInstance() {
        return Holder.instance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    private DebounceTask mStopNaviTask = new DebounceTask(TASK_INTERVAL_TIME) {
        @Override
        public void execute() {
            realStopNavi(mContext);
        }
    };

    public void stopNavi() {
        mStopNaviTask.emit();
    }
    private void realStopNavi(Context context) {
        if (context == null) {
            return;
        }
        EasyLog.d("EasyConnNaviController","realStopNavi");
        Intent intent = new Intent();
        intent.setAction(ACTION_SEND_STOP_NAVI);
        intent.setPackage(AppLists.easyconn);
        context.sendBroadcast(intent);
    }
}
