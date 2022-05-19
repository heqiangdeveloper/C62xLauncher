package com.chinatsp.vehiclesetting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chinatsp.settinglib.LogUtils;
import com.chinatsp.settinglib.SettingManager;

public class HardKeyReceive extends BroadcastReceiver {

    private static final String ACTION_KEY_CHANGED = "com.coagent.intent.action.KEY_CHANGED";
    private static final String ACTION_KEY_SPLIT = "com.chinatsp.systemui.ACTION_START_SPLIT";
    private static final String ACTION_USER_OFF = "chinatsp.intent.broadcast.USER_OFF";
    public static final String ACTION_REMOVE_USER_OFF = "chinatsp.intent.broadcast.remove_USER_OFF";
    public static final String EXTRA_KEY_CODE = "Key_code";
    public static final String EXTRA_KEY_STATE = "Key_state";
    public static final String KEY_POWER = "POWER";
    public static final String KEY_TEL = "TEL";
    public static final String KEY_HANDUP = "HANDUP";
    public static final String KEY_PRE = "PRE";
    public static final String KEY_NEXT = "NEXT";
    public static final String KEY_SRC = "SRC";
    public static final String KEY_MUTE = "MUTE";
    public static final String KEY_HOME = "MAIN";
    public static final String KEY_BACK = "BACK";
    public static final String KEY_VOLDOWN = "VOLDOWN";
    public static final String KEY_VOLUP = "VOLUP";

    public static final String KEY_STATE_UP = "UP";
    public static final String KEY_STATE_DOWN = "DOWN";
    public static final String KEY_STATE_LONG_UP = "LONG_UP";
    public static final String KEY_STATE_LONG_EVENT = "LONG_EVENT";
    //Key_state: DOWN/UP/ LONG_EVENT/ LONG_UP(按下,抬起,长按,长按抬起)

    private static int powerClick = 0;
    private static final int MSG_MUTE = 0;
    public static boolean isUseOff = false;
    public static boolean isPhoneInCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d("action:" + action);
        SettingManager.init(context.getApplicationContext());
        if (action == null) {
            return;
        }

        //Intent intentStandby=new Intent(StandbyService.ACTION_STANDBY_START);
        if (action.equals(ACTION_KEY_CHANGED)) {
            String code = intent.getStringExtra(EXTRA_KEY_CODE);
            String state = intent.getStringExtra(EXTRA_KEY_STATE);
            LogUtils.d("KEY_code:" + code + " KEY_state:" + state);
            if (code.equals(KEY_POWER)) {
                LogUtils.d("KEY_POWER");
                if (isUseOff || isPhoneInCall) {
                    return;
                }

                if (state.equals(KEY_STATE_DOWN)) {
                    powerClick++;
                    LogUtils.d("KEY_STATE_DOWN powerClick=" + powerClick);
                } else if (state.equals(KEY_STATE_UP)) {
                    LogUtils.d("KEY_STATE_UP powerClick=" + powerClick);
                    if (powerClick > 0) {
                        if (System.currentTimeMillis() - time < 2000) {
                            LogUtils.d("return=");
                        } else {
                            LogUtils.d("setScreenOn");
                        }
                        //StandbyService.setScreenOn(context, StandbyService.isClockShowing());
                    }
                    powerClick = 0;
                } else if (state.equals(KEY_STATE_LONG_UP)) {
                    LogUtils.d("KEY_STATE_LONG_UP");
                    powerClick = 0;
                }
            } else {
                //LogUtils.d("KEY_code:" + code);
                if (state.equals(KEY_STATE_UP)) {

                } else if (state.equals(KEY_STATE_LONG_UP)) {

                } else if (state.equals(KEY_STATE_LONG_EVENT)) {


                } else if (state.equals(KEY_STATE_DOWN)) {
                    if (code.equals(KEY_MUTE)) {
                        muteDowntime = System.currentTimeMillis();
                        //mHandler.removeMessages(MSG_MUTE);//
                    }
                }
            }
        } else if (action.equals(ACTION_USER_OFF)) {
            boolean status = intent.getBooleanExtra("state", false);
            isUseOff = status;
            LogUtils.d("ACTION_USER_OFF:" + status);

        } else if (action.equals(ACTION_KEY_SPLIT)) {
            LogUtils.d("start SPLIT");
            time = System.currentTimeMillis();
        }
    }

    static long time;
    static long muteDowntime;
    final static int LONG_CLICK_DURING_TIME = 3;

    public static void removeUserOff(Context context) {
        LogUtils.d("removeUserOff:");
        Intent intent = new Intent(HardKeyReceive.ACTION_REMOVE_USER_OFF);
        context.sendBroadcast(intent);
    }

}
