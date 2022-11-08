package com.chinatsp.drawer.weather;

import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.IWeatherDataCallback;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.utils.EasyLog;

class WeatherDrawerController {
    private static final String TAG = "WeatherDrawerController";
    private WeatherDrawerViewHelper mViewHelper;
    private WeatherRepository mWeatherRepository;

    WeatherDrawerController(WeatherDrawerViewHelper viewHelper) {
        mViewHelper = viewHelper;
        mWeatherRepository = WeatherRepository.getInstance();
        mWeatherRepository.init(viewHelper.getContext());
        mWeatherRepository.registerDataCallback(mWeatherDataCallback);
    }

    void requestWeatherInfo() {
        EasyLog.d(TAG, "requestWeatherInfo "+this.hashCode());
        mWeatherRepository.requestRefreshWeatherInfo(new IOnRequestListener<List<WeatherInfo>>() {
            @Override
            public void onSuccess(List<WeatherInfo> weatherInfoList) {
                EasyLog.d(TAG, "requestWeatherInfo onSuccess");
                refresh(weatherInfoList);
            }

            @Override
            public void onFail(String msg) {
                EasyLog.d(TAG, "requestWeatherInfo onFail:" + msg);
                mViewHelper.refreshDefault();
            }
        });
    }

    private void refresh(List<WeatherInfo> weatherInfoList) {
        if (weatherInfoList == null || weatherInfoList.isEmpty()) {
            mViewHelper.refreshDefault();
        } else {
            mViewHelper.refreshData(weatherInfoList.get(0));
        }
    }

    IWeatherDataCallback mWeatherDataCallback = new IWeatherDataCallback() {
        @Override
        public void onCityList(List<String> cityList) {

        }

        @Override
        public void onWeatherList(List<WeatherInfo> weatherInfoList) {
            if (weatherInfoList != null) {
                EasyLog.d(TAG, "onWeatherList DataCallback, list size: " + weatherInfoList.size());
            }
            refresh(weatherInfoList);
        }
    };

    private void removeCallback() {
        mWeatherRepository.unregisterDataCallback(mWeatherDataCallback);
    }
}
