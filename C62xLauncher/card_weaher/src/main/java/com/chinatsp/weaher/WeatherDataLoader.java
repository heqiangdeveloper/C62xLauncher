package com.chinatsp.weaher;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.chinatsp.weaher.mock.WeatherMock;

public class WeatherDataLoader {
    private OnLoadListener mOnLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    public void load() {
        // todo: load data.
        // mock data here.
        mockLoader();
    }

    private void mockLoader() {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                WeatherBean weatherBean = WeatherMock.getWeatherBean();
                mOnLoadListener.onSuccess(weatherBean);
            }
        }.start();
    }

    interface OnLoadListener{
        void onSuccess(WeatherBean weatherBean);
    }
}
