package com.chinatsp.weaher;

import com.chinatsp.weaher.repository.IWeatherDataCallback;
import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.LinkedList;
import java.util.List;

import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.utils.EasyLog;

public class WeatherCardController {
    private IWeatherCardView mCardView;
    private WeatherRepository mWeatherRepository;
    private String TAG = "WeatherCardController ";

    public WeatherCardController(IWeatherCardView cardView) {
        EasyLog.i(TAG, "WeatherCardController init " + hashCode());
        mCardView = cardView;
        mWeatherRepository = WeatherRepository.getInstance();
        mWeatherRepository.init(mCardView.getContext());
        addDataCallback();
        mWeatherRepository.registerConnectListener(mConnectListener);
        requestCityList();
    }

    void requestCityList() {
        WeatherUtil.logI(TAG + "requestCityList");
        List<String> cityFromCache = mWeatherRepository.getCityFromCache();
        if (cityFromCache != null && !cityFromCache.isEmpty()) {
            mCardView.refreshCityList(cityFromCache);
        }
        mWeatherRepository.requestCityList();
    }

    private IConnectListener mConnectListener = new IConnectListener() {
        @Override
        public void onServiceConnected() {
            WeatherUtil.logI(TAG + "onServiceConnected");
            requestCityList();
        }

        @Override
        public void onServiceDisconnected() {
            WeatherUtil.logE(TAG + "onServiceDisconnected");
        }

        @Override
        public void onServiceDied() {
            WeatherUtil.logE(TAG + "onServiceDied");
        }
    };

    private final IWeatherDataCallback mWeatherDataCallback = new IWeatherDataCallback() {
        @Override
        public void onCityList(List<String> cityList) {
            WeatherUtil.logI(TAG + "onCityList " + cityList +" , hashcode:"+WeatherCardController.this.hashCode());
            mWeatherRepository.saveToCache(cityList);
            mCardView.refreshCityList(cityList);
        }

        @Override
        public void onWeatherList(List<WeatherInfo> weatherList) {
            if (weatherList == null || weatherList.isEmpty()) {
                mCardView.refreshDefault();
            } else {
                EasyLog.d(TAG, TAG + "onWeatherList DataCallback, list size: " + weatherList.size());
                mCardView.refreshData(weatherList);
            }
        }
    };

    void addDataCallback() {
        mWeatherRepository.registerDataCallback(mWeatherDataCallback);
    }

    void removeDataCallback() {
        mWeatherRepository.unregisterDataCallback(mWeatherDataCallback);
    }

}
