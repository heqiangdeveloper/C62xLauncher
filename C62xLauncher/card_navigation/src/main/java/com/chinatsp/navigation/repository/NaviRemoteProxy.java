package com.chinatsp.navigation.repository;

import android.content.Context;

import com.autonavi.amapauto.jsonsdk.IJsonProtocolReceive;
import com.autonavi.amapauto.jsonsdk.IServiceConnectListener;
import com.autonavi.amapauto.jsonsdk.JsonProtocolManager;
import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;

import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;

public class NaviRemoteProxy implements RemoteProxy {
    private IConnectListener mConnectListener;
    private IRemoteDataCallback mIRemoteDataCallback;
    @Override
    public void setConnectListener(IConnectListener connectListener) {
        this.mConnectListener = connectListener;
    }

    @Override
    public void setRemoteDataCallback(IRemoteDataCallback remoteCallback) {
        this.mIRemoteDataCallback = remoteCallback;
    }

    @Override
    public void connectRemoteService(Context context) {
        JsonProtocolManager jsonProtocolManager = JsonProtocolManager.getInstance();
        jsonProtocolManager.setServiceConnectListener(new IServiceConnectListener() {
            @Override
            public void onServiceConnected() {
                if (mConnectListener != null) {
                    mConnectListener.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected() {
                if (mConnectListener != null) {
                    mConnectListener.onServiceDisconnected();
                }
            }

            @Override
            public void onServiceDied() {
                if (mConnectListener != null) {
                    mConnectListener.onServiceDied();
                }
            }
        });
        jsonProtocolManager.setJsonProtocolReceive(new IJsonProtocolReceive() {
            @Override
            public void received(String s) {
                if (mIRemoteDataCallback != null) {
                    mIRemoteDataCallback.notifyData(s);
                }
            }

            @Override
            public String receivedSync(String s) {
                if (mIRemoteDataCallback != null) {
                    mIRemoteDataCallback.notifyData(s);
                }
                return s;
            }
        });
        jsonProtocolManager.init(context);
    }



    @Override
    public void disconnectRemoteService() {

    }

    @Override
    public void requestData(IOnRequestListener onRequestListener) {

    }

}
