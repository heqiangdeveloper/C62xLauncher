package com.chinatsp.iquting.service;

import android.content.Context;
import android.util.Log;

import com.chinatsp.iquting.event.ContentConnectEvent;
import com.chinatsp.iquting.event.PlayConnectEvent;
import com.tencent.wecarflow.contentsdk.ConnectionListener;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.controlsdk.BindListener;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;

import org.greenrobot.eventbus.EventBus;

public class IqutingBindService {
    private static final String TAG = "IQuTingCardView";
    private static final String TAG_CONTENT = "heqqcontent";
    private Context mContext;
    private IqutingBindService() {}

    private static class Holder {
        public static IqutingBindService serice = new IqutingBindService();
    }

    public static IqutingBindService getInstance() {
        return Holder.serice;
    }

    //注册播放服务
    public void bindPlayService(Context context) {
        this.mContext = context;
        FlowPlayControl.InitParams params = new FlowPlayControl.InitParams();
        params.setAutoRebind(true);
        FlowPlayControl.getInstance().init(params);

        FlowPlayControl.getInstance().addBindListener(new BindListener() {
            @Override
            public void onServiceConnected() {
                Log.d(TAG,"onServiceConnected");
                EventBus.getDefault().post(new PlayConnectEvent(PlayConnectEvent.CONNECTED));
            }

            @Override
            public void onBindDied() {
                Log.d(TAG,"onBindDied");
            }

            @Override
            public void onServiceDisconnected() {
                Log.d(TAG,"onServiceDisconnected");
            }

            @Override
            public void onError(int i) {
                Log.d(TAG,"onError: " + i);
            }
        });
        FlowPlayControl.getInstance().bindPlayService(context);
    }

    //注册内容服务
    public void bindContentService(Context context) {
        this.mContext = context;
        ConnectionListener connListener = new ConnectionListener() {
            @Override
            public void onConnected() {
                Log.d(TAG_CONTENT,"bindContentService onConnected");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTED));
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG_CONTENT,"bindContentService onDisconnected");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.DISCONNECTED));
            }

            @Override
            public void onConnectionDied() {
                Log.d(TAG_CONTENT,"bindContentService onConnectionDied");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTIONDIED));
            }
        };
        ContentManager.getInstance().init(mContext,connListener);
    }
}
