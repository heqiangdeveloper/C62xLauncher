package com.chinatsp.weaher.repository;

import android.content.Context;
import android.os.IBinder;

import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.autofly.weather.IRequestCallback;
import com.iflytek.autofly.weather.entity.WeatherInfo;
import com.iflytek.weathercontrol.IRemoteCallback;
import com.iflytek.weathercontrol.WeatherRemoteControl;

import java.util.List;

import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;

public class WeatherConnectProxy implements RemoteProxy {

    private final WeatherRemoteControl mWeatherRemoteControl;
    private final int SERVICE_CONNECTED = 2;
    private IRemoteDataCallback mRemoteDataCallback;

    public WeatherConnectProxy(WeatherRemoteControl weatherRemoteControl) {
        mWeatherRemoteControl = weatherRemoteControl;
    }

    @Override
    public void setConnectListener(IConnectListener connectListener) {
        mWeatherRemoteControl.registerRemoteCallback(new IRemoteCallback() {
            @Override
            public void onServiceBindStateChanged(int i) {
                WeatherUtil.logD("WeatherConnectProxy onServiceBindStateChanged : "+i);
                if (i == SERVICE_CONNECTED) {
                    connectListener.onServiceConnected();
                } else {
                    connectListener.onServiceDisconnected();
                }
            }

            @Override
            public void onWeatherList(List<WeatherInfo> list) {
                WeatherUtil.logD("WeatherConnectProxy onWeatherList : "+list.size());
                if (mRemoteDataCallback != null) {
                    mRemoteDataCallback.notifyData(list);
                }
            }
        });
    }

    @Override
    public void setRemoteDataCallback(IRemoteDataCallback remoteCallback) {
        this.mRemoteDataCallback = remoteCallback;
    }

    @Override
    public void requestData(IOnRequestListener onRequestListener) {
        mWeatherRemoteControl.getWeatherInfoList(new IRequestCallback() {
            @Override
            public void onSuccess(List<WeatherInfo> list) {
                WeatherUtil.logD("WeatherConnectProxy getWeatherInfoList onSuccess : "+list.size());
                if (onRequestListener != null) {
                    onRequestListener.onSuccess(list);
                }
            }

            @Override
            public void onFail(String s) {
                WeatherUtil.logE("WeatherConnectProxy getWeatherInfoList fail");
                if (onRequestListener != null) {
                    onRequestListener.onFail(s);
                }
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        });
    }


    @Override
    public void connectRemoteService(Context context) {

    }

    @Override
    public void disconnectRemoteService() {

    }

}
