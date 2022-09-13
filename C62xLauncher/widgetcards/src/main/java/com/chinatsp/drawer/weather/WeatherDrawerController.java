package com.chinatsp.drawer.weather;

import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

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
    }

    void requestWeatherInfo() {
        EasyLog.d(TAG, "requestWeatherInfo");
        mWeatherRepository.registerDataCallback(new IRemoteDataCallback<List<WeatherInfo>>() {
            @Override
            public void notifyData(List<WeatherInfo> weatherInfoList) {
                EasyLog.d(TAG, "requestWeatherInfo DataCallback, list size: "+weatherInfoList.size());
                refresh(weatherInfoList);
            }
        });
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
}
