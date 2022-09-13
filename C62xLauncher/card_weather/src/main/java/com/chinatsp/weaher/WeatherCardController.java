package com.chinatsp.weaher;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.repository.WeatherRepository;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.LinkedList;
import java.util.List;

import launcher.base.ipc.IOnRequestListener;
import launcher.base.ipc.IRemoteDataCallback;

public class WeatherCardController {
    private WeatherCardView mCardView;
    private WeatherRepository mWeatherRepository;

    public WeatherCardController(WeatherCardView cardView) {
        mCardView = cardView;
        mWeatherRepository = WeatherRepository.getInstance();
        mWeatherRepository.init(mCardView.getContext());
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

    private <T> void refreshData(T t) {
        if (mCardView == null) {
            return;
        }
        List<WeatherInfo> result = cast(t);
        if (result == null || result.isEmpty()) {
            mCardView.refreshDefault();
        } else {
            mCardView.refreshData(result);
        }
    }

    void addDataCallback() {
        mWeatherRepository.registerDataCallback(iRemoteDataCallback);
    }

    void removeDataCallback() {
        mWeatherRepository.unregisterDataCallback(iRemoteDataCallback);
    }

    private List<WeatherInfo> cast(Object o) {
        if (o == null) {
            return null;
        }
        List<WeatherInfo> result = new LinkedList<>();

        try {
            if (o instanceof List) {
                result = (List<WeatherInfo>) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public WeatherBean convertWeatherBean(List<WeatherInfo> result) {
        if (result == null || result.isEmpty()) {
            return null;
        }
        WeatherInfo weatherInfo = result.get(0);
//        WeatherUtil.logD(weatherInfo.getWeather());
//        WeatherUtil.logD(weatherInfo.getAirData());
//        WeatherUtil.logD(weatherInfo.getAirQuality());
//        WeatherUtil.logD(weatherInfo.getWeatherType());
//        WeatherUtil.logD(weatherInfo.getArea());
//        WeatherUtil.logD(weatherInfo.getCity());
//        WeatherUtil.logD(weatherInfo.getCo());
//        WeatherUtil.logD(weatherInfo.getDate());
//        WeatherUtil.logD(weatherInfo.getDateLong());
//        WeatherUtil.logD(weatherInfo.getHigh());
//        WeatherUtil.logD(weatherInfo.getHumidity());
        WeatherUtil.logD("convertWeatherBean weatherInfo:" + weatherInfo);
        WeatherBean weatherBean = new WeatherBean(WeatherBean.TYPE_FOG, "22 ~ 35", "", "重庆");
        return weatherBean;
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
