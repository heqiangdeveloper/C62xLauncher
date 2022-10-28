package com.chinatsp.weaher;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.LinkedList;
import java.util.List;

import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;

public class WeatherCardController {
    private WeatherCardView mCardView;
    private WeatherRepository mWeatherRepository;

    public WeatherCardController(WeatherCardView cardView) {
        mCardView = cardView;
        mWeatherRepository = WeatherRepository.getInstance();
        mWeatherRepository.init(mCardView.getContext());
        mWeatherRepository.registerConnectListener(mConnectListener);
    }

    void requestWeatherInfo() {
        WeatherUtil.logD("requestWeatherInfo");
        mWeatherRepository.requestRefreshWeatherInfo(new IOnRequestListener<List<WeatherInfo>>() {
            @Override
            public void onSuccess(List<WeatherInfo> weatherInfoList) {
                WeatherUtil.logD("requestWeatherInfo onSuccess");
                if (weatherInfoList == null || weatherInfoList.isEmpty()) {
                    mCardView.refreshDefault();
                } else {
                    mCardView.refreshData(weatherInfoList);
                }
            }

            @Override
            public void onFail(String msg) {
                WeatherUtil.logE("requestWeatherInfo onFail: " + msg);
                if (mCardView != null) {
                    mCardView.refreshDefault();
                }
            }
        });
    }

    private IConnectListener mConnectListener = new IConnectListener() {
        @Override
        public void onServiceConnected() {
            WeatherUtil.logI("onServiceConnected");
            requestWeatherInfo();
        }

        @Override
        public void onServiceDisconnected() {
            WeatherUtil.logE("onServiceDisconnected");
        }

        @Override
        public void onServiceDied() {
            WeatherUtil.logE("onServiceDied");
        }
    };

    private final IRemoteDataCallback<List<WeatherInfo>> iRemoteDataCallback = new IRemoteDataCallback<List<WeatherInfo>>() {
        @Override
        public void notifyData(List<WeatherInfo> weatherList) {
            if (weatherList == null || weatherList.isEmpty()) {
                mCardView.refreshDefault();
            } else {
                mCardView.refreshData(weatherList);
            }
        }
    };

    void addDataCallback() {
        mWeatherRepository.registerDataCallback(iRemoteDataCallback);
    }

    void removeDataCallback() {
        mWeatherRepository.unregisterDataCallback(iRemoteDataCallback);
    }

    private WeatherBean convert(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            return null;
        }
        WeatherBean weatherBean = new WeatherBean(WeatherBean.TYPE_FOG, "22 ~ 35", "", "重庆");
        return weatherBean;

    }

    public List<WeatherInfo> getWeatherList() {
        return mWeatherRepository.getWeatherInfo();
    }
}
