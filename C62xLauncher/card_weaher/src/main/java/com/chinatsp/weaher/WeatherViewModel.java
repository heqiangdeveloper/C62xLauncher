package com.chinatsp.weaher;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class WeatherViewModel {
    private WeatherViewModel() {
        mWeatherModelLiveData = new MutableLiveData<>();
        mWeatherModelLiveData.postValue(createUnknown());
    }
    private static class Holder{
        private static WeatherViewModel instance = new WeatherViewModel();
    }

    public static WeatherViewModel getInstance() {
        return Holder.instance;
    }

    private final MutableLiveData<WeatherBean> mWeatherModelLiveData ;

    public void register(LifecycleOwner owner, Observer<? super WeatherBean> observer) {
        mWeatherModelLiveData.observe(owner, observer);
    }
    public void unregister(Observer<? super WeatherBean> observer) {
        mWeatherModelLiveData.removeObserver(observer);
    }

    public void loadWeatherData() {
        WeatherDataLoader.OnLoadListener onLoadListener = new WeatherDataLoader.OnLoadListener() {
            @Override
            public void onSuccess(WeatherBean weatherBean) {
                mWeatherModelLiveData.postValue(weatherBean);
            }
        };
        WeatherDataLoader weatherDataLoader = new WeatherDataLoader();
        weatherDataLoader.setOnLoadListener(onLoadListener);
        weatherDataLoader.load();
    }

    public WeatherBean getTodayWeather() {
        return mWeatherModelLiveData.getValue();
    }

    public WeatherBean createUnknown() {
        return new WeatherBean(WeatherBean.TYPE_UNKNOWN, "-", "", "-");
    }
}
