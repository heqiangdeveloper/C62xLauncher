package com.chinatsp.weaher.repository;

import android.content.Context;

import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.weathercontrol.WeatherRemoteControl;

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

    @Override
    protected BaseRemoteConnector createRemoteConnector(Context context) {
        WeatherUtil.logD("WeatherRepository createRemoteConnector");
        WeatherRemoteControl.init(context);
        WeatherRemoteControl.setDebug(true);
        RemoteProxy remoteProxy = new WeatherConnectProxy(WeatherRemoteControl.getInstance());
        return new WeatherRemoteConnector(remoteProxy);
    }
    public void requestWeatherInfo(IOnRequestListener onRequestListener) {
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                mRemoteConnector.requestData(onRequestListener);
            } else {
                WeatherUtil.logW("mRemoteConnector disconnect.");
            }
        } else {
            WeatherUtil.logW("mRemoteConnector is NULL.");
        }
    }
}
