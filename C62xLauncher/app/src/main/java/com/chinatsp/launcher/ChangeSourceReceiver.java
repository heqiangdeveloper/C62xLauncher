package com.chinatsp.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.window.AppManagementWindow;
import com.chinatsp.iquting.callback.IQueryIqutingLoginStatus;
import com.chinatsp.iquting.configs.IqutingConfigs;
import com.chinatsp.iquting.service.IqutingBindService;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.QueryCallback;

public class ChangeSourceReceiver extends BroadcastReceiver {
    private static final String TAG = "ChangeSourceReceiver";
    public static final String AQT_PLAY_ACTION = "com.aiquting.play"; //通知爱趣听 播放
    public static final String HARD_KEY_ACTION = "com.coagent.intent.action.KEY_CHANGED"; //方控广播
    public static final String EXTRA_KEY_CODE = "Key_code";
    public static final String EXTRA_KEY_STATE = "Key_state";
    public static final String KEY_STATE_UP = "UP";
    public static final String KEY_STATE_DOWN = "DOWN";
    public static final String KEY_STATE_LONG_UP = "LONG_UP";
    public static final String KEY_PRE = "PRE";
    public static final String KEY_NEXT = "NEXT";
    public static final String KEY_PLAY_PAUSE = "PAUSE_PLAY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AQT_PLAY_ACTION)) {
            boolean isOpen = intent.getBooleanExtra(IqutingConfigs.AQT_PARAM, true);
            Log.d(TAG, "onReceive: " + AQT_PLAY_ACTION + ",isOpen: " + isOpen);
            command(context, isOpen);
        } else if (intent.getAction().equals(HARD_KEY_ACTION)) {
            String code = intent.getStringExtra(EXTRA_KEY_CODE);
            String state = intent.getStringExtra(EXTRA_KEY_STATE);
            String source = Settings.System.getString(context.getContentResolver(), IqutingConfigs.SAVE_SOURCE);
            Log.d(TAG, "KEY_code:" + code + " KEY_state:" + state + " source:" + source);

            if (!IqutingConfigs.AQT.equals(source) || KEY_STATE_DOWN.equals(state)) {
                return;
            }
            if (code.equals(KEY_PRE)) {
                FlowPlayControl.getInstance().doPre();
            } else if (code.equals(KEY_NEXT)) {
                FlowPlayControl.getInstance().doNext();
            } else if (code.equals(KEY_PLAY_PAUSE)) {
                FlowPlayControl.getInstance().queryPlaying(new QueryCallback<Boolean>() {
                    @Override
                    public void onError(int i) {

                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            FlowPlayControl.getInstance().doPause();
                        } else {
                            FlowPlayControl.getInstance().doPlay();
                        }
                    }
                });
            }
        }
    }

    private void command(Context context, boolean isOpen) {
        if (IqutingBindService.getInstance().isServiceConnect()) {
            Log.d(TAG, "Connect iquting service");
            //查询用户登录状态
            IqutingBindService.getInstance().checkLoginStatus(new IQueryIqutingLoginStatus() {
                @Override
                public void onSuccess(boolean mIsLogin) {
                    Log.d(TAG, "check iquting LoginStatus: " + mIsLogin);
                    if (mIsLogin) {
                        FlowPlayControl.getInstance().doPlay();
                        Settings.System.putString(context.getContentResolver(), IqutingConfigs.SAVE_SOURCE, IqutingConfigs.AQT);
                    } else {
                        Toast.makeText(context, context.getString(R.string.play_iquting_warning), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (isOpen) {
                FlowPlayControl.getInstance().startPlayActivity(context);
            }
        } else {
            Log.d(TAG, "disConnect iquting service");
        }
    }
}
