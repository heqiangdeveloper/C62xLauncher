package com.chinatsp.weaher.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinatsp.weaher.WeatherUtil;
import com.iflytek.autofly.weather.entity.WeatherInfo;
import com.iflytek.weathercontrol.WeatherRemoteControl;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import launcher.base.ipc.BaseRepository;
import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;
import launcher.base.utils.flowcontrol.PollingTask;

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
    private List<String> mCityList;

    private IWeatherDataCallback mWeatherDataCallback;

    @Override
    public void init(@NonNull Context context) {
        super.init(context);
    }

    @Override
    protected BaseRemoteConnector createRemoteConnector(Context context) {
        WeatherUtil.logD("WeatherRepository createRemoteConnector");
        WeatherRemoteControl.init(context);
        WeatherRemoteControl.setDebug(true);
        WeatherConnectProxy remoteProxy = new WeatherConnectProxy(WeatherRemoteControl.getInstance());
        return new WeatherRemoteConnector(remoteProxy);
    }
    public void requestRefreshWeatherInfo(IOnRequestListener onRequestListener) {
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                WeatherUtil.logD("mRemoteConnector requestWeatherInfo....");
                mRemoteConnector.requestData(new IOnRequestListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mData = (List<WeatherInfo>) o;
                        if (onRequestListener != null) {
                            onRequestListener.onSuccess(mData);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        if (onRequestListener != null) {
                            onRequestListener.onFail(msg);
                        }
                    }
                });
            } else {
                String disconnectMsg = "mRemoteConnector disconnect.";
                WeatherUtil.logW(disconnectMsg);
                if (onRequestListener != null) {
                    onRequestListener.onFail(disconnectMsg);
                }
            }
        } else {
            String errMsg = "mRemoteConnector is NULL.";
            WeatherUtil.logW(errMsg);
            if (onRequestListener != null) {
                onRequestListener.onFail(errMsg);
            }
        }
    }

    public void requestRefreshWeatherInfo(IOnRequestListener onRequestListener, String city) {
        if (mRemoteConnector != null) {
            if (mRemoteConnector.isServiceConnect()) {
                WeatherUtil.logD("mRemoteConnector requestWeatherInfo , city:"+city);
                if (mRemoteConnector instanceof WeatherRemoteConnector) {
                    ((WeatherRemoteConnector) mRemoteConnector).requestCityWeather(onRequestListener, city);
                }
            } else {
                String disconnectMsg = "mRemoteConnector disconnect.";
                WeatherUtil.logW(disconnectMsg);
                if (onRequestListener != null) {
                    onRequestListener.onFail(disconnectMsg);
                }
            }
        } else {
            String errMsg = "mRemoteConnector is NULL.";
            WeatherUtil.logW(errMsg);
            if (onRequestListener != null) {
                onRequestListener.onFail(errMsg);
            }
        }
    }

    public void requestCityList() {
        if (mRemoteConnector instanceof WeatherRemoteConnector) {
            ((WeatherRemoteConnector) mRemoteConnector).requestCityList();
        }
    }

    public List<WeatherInfo> getWeatherInfo() {
        return mData;
    }

    public void registerDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.registerRemoteDataCallbacks(remoteDataCallback);
        }
    }
    public void unregisterDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.unregisterRemoteDataCallbacks(remoteDataCallback);
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
    }
}
