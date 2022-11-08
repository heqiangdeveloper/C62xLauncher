package com.chinatsp.weaher.repository;

import androidx.annotation.NonNull;

import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.async.AsyncSchedule;
import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.ipc.RemoteProxy;
import launcher.base.utils.EasyLog;

public class WeatherRemoteConnector extends BaseRemoteConnector {
    public WeatherRemoteConnector(@NonNull RemoteProxy remoteProxy) {
        super(remoteProxy);
    }

    @Override
    protected IRemoteDataCallback createRemoteDataCallback() {
        return new IWeatherDataCallback() {
            @Override
            public void onCityList(List<String> cityList) {
                AsyncSchedule.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyCityList(cityList);
                    }
                });
            }

            @Override
            public void onWeatherList(List<WeatherInfo> weatherInfoList) {
                AsyncSchedule.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyWeatherList(weatherInfoList);
                    }
                });
            }
        };
    }

    protected  void notifyWeatherList(List<WeatherInfo> weatherInfoList) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                EasyLog.i(TAG, "notifyDataCallback , listeners:" + mRemoteDataCallbacks);
                for (IRemoteDataCallback remoteDataCallback : mRemoteDataCallbacks) {
                    if (remoteDataCallback instanceof IWeatherDataCallback) {
                        ((IWeatherDataCallback) remoteDataCallback).onWeatherList(weatherInfoList);
                    }
                }
            }
        });
    }

    protected void notifyCityList(List<String> cityList) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                EasyLog.i(TAG, "notifyDataCallback , listeners:" + mRemoteDataCallbacks);
                for (IRemoteDataCallback remoteDataCallback : mRemoteDataCallbacks) {
                    if (remoteDataCallback instanceof IWeatherDataCallback) {
                        ((IWeatherDataCallback) remoteDataCallback).onCityList(cityList);
                    }
                }
            }
        });
    }

    public void requestCityList() {
        if (mRemoteProxy instanceof WeatherConnectProxy) {
            ((WeatherConnectProxy) mRemoteProxy).requestCityList();
        }
    }
    public void requestCityWeather(IOnRequestListener onRequestListener,String city) {
        if (mRemoteProxy instanceof WeatherConnectProxy) {
            ((WeatherConnectProxy) mRemoteProxy).requestByCity(onRequestListener, city);
        }
    }
}
