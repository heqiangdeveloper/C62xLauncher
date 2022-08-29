package com.chinatsp.weaher.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.autofly.weather.entity.WeatherInfo;
import com.iflytek.weathercontrol.WeatherRemoteControl;

import java.util.List;

import launcher.base.ipc.BaseRepository;
import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;

public class WeatherRepository extends BaseRepository {

    private static class Holder{
        private static WeatherRepository instance = new WeatherRepository();
    }

    private WeatherRepository() {

    }

    public static WeatherRepository getInstance() {
        return Holder.instance;
    }

    private List<WeatherInfo> mData;

    IRemoteDataCallback mIRemoteDataCallback = new IRemoteDataCallback() {
        @Override
        public <T> void notifyData(T t) {
            if (t != null) {
                try {
                    mData = (List<WeatherInfo>) t;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    public void init(@NonNull Context context) {
        super.init(context);
        registerDataCallback(mIRemoteDataCallback);
    }

    @Override
    protected BaseRemoteConnector createRemoteConnector(Context context) {
        WeatherUtil.logD("WeatherRepository createRemoteConnector");
        WeatherRemoteControl.init(context);
        WeatherRemoteControl.setDebug(true);
        RemoteProxy remoteProxy = new WeatherConnectProxy(WeatherRemoteControl.getInstance());
        return new WeatherRemoteConnector(remoteProxy);
    }
    public void requestRefreshWeatherInfo(IOnRequestListener onRequestListener) {
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                WeatherUtil.logD("mRemoteConnector requestWeatherInfo....");
                mRemoteConnector.requestData(onRequestListener);
            } else {
                WeatherUtil.logW("mRemoteConnector disconnect.");
            }
        } else {
            WeatherUtil.logW("mRemoteConnector is NULL.");
        }
    }

    public List<WeatherInfo> getWeatherInfo() {
        return mData;
    }

    @Override
    protected void destroy() {
        super.destroy();
        unregisterDataCallback(mIRemoteDataCallback);
    }
}
