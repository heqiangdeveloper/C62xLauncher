package com.chinatsp.weaher.repository;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.autofly.weather.IRefreshCallback;
import com.iflytek.autofly.weather.IRequestCallback;
import com.iflytek.autofly.weather.entity.WeatherInfo;
import com.iflytek.weathercontrol.IRemoteCallback;
import com.iflytek.weathercontrol.WeatherRemoteControl;

import java.util.List;

import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;
import launcher.base.utils.EasyLog;

public class WeatherConnectProxy implements RemoteProxy {

    private final WeatherRemoteControl mWeatherRemoteControl;
    private final int SERVICE_CONNECTED = 2;
    private IWeatherDataCallback mWeatherDataCallback;

    public WeatherConnectProxy(WeatherRemoteControl weatherRemoteControl) {
        mWeatherRemoteControl = weatherRemoteControl;
    }

    @Override
    public void setConnectListener(IConnectListener connectListener) {
        mWeatherRemoteControl.registerRemoteCallback(new IRemoteCallback() {
            @Override
            public void onServiceBindStateChanged(int i) {
                WeatherUtil.logD("WeatherConnectProxy onServiceBindStateChanged : "+i +"  connectListener:"+connectListener);
                if (i == SERVICE_CONNECTED) {
                    connectListener.onServiceConnected();
                } else {
                    connectListener.onServiceDisconnected();
                }
            }

            @Override
            public void onWeatherList(List<WeatherInfo> list) {
                WeatherUtil.logD("WeatherConnectProxy onWeatherList : "+list.size());
                if (mWeatherDataCallback != null) {
                    mWeatherDataCallback.onWeatherList(list);
                }
            }

            @Override
            public void onCityList(List<String> list) {
                WeatherUtil.logD("WeatherConnectProxy onCityList : "+list);
                if (mWeatherDataCallback != null) {
                    mWeatherDataCallback.onCityList(list);
                }
            }
        });
    }

    @Override
    public void setRemoteDataCallback(IRemoteDataCallback remoteCallback) {
        this.mWeatherDataCallback = (IWeatherDataCallback) remoteCallback;
    }


    @Override
    public void requestData(IOnRequestListener onRequestListener) {
        WeatherUtil.logD("WeatherConnectProxy requestData");
        mWeatherRemoteControl.getWeatherInfoList(new IRequestCallback.Stub() {
            @Override
            public void onSuccess(List<WeatherInfo> list) {
                WeatherUtil.logD("WeatherConnectProxy getWeatherInfoList onSuccess : "+list.size());
                for (WeatherInfo weatherInfo : list) {
                    WeatherUtil.logD("---> "+weatherInfo.getCity() +" "+weatherInfo.getWeather());
                }
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
        });
    }

    public void requestByCity(IOnRequestListener onRequestListener,String city) {
        WeatherUtil.logD("WeatherConnectProxy requestData");
        mWeatherRemoteControl.getWeatherInfoListForCity(city, new IRequestCallback.Stub() {
            @Override
            public void onSuccess(List<WeatherInfo> list) {
                WeatherUtil.logD("WeatherConnectProxy getWeatherInfoList onSuccess : "+list.size());
                for (WeatherInfo weatherInfo : list) {
                    WeatherUtil.logD("---> "+weatherInfo.getCity() +" "+weatherInfo.getWeather());
                }
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
        });
    }

    public void requestCityList() {
        mWeatherRemoteControl.requestCityList(new IRequestCallback() {
            @Override
            public void onSuccess(List<WeatherInfo> list) throws RemoteException {
                WeatherUtil.logD("WeatherConnectProxy requestCityList onSuccess : " + list);

            }

            @Override
            public void onFail(String s) throws RemoteException {
                WeatherUtil.logD("WeatherConnectProxy requestCityList onFail : " + s);

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
