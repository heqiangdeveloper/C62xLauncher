package com.chinatsp.drawer.weather;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.widgetcards.R;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

public class WeatherDrawerViewHelper {
    private final String TAG = "WeatherDrawerViewHelper";
    private View mRootView;
    private WeatherDrawerController mController;
    private ImageView mIvDrawerWeatherType;
    private ImageView ivDrawerWeatherTypeBG;
    private TextView mTvDrawerWeatherWord;
    private TextView mTvDrawerWeatherTemperature;
    private TextView mTvDrawerWeatherCity;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    public WeatherDrawerViewHelper(View rootView) {
        mRootView = rootView.findViewById(R.id.layoutDrawerWeather);
        mController = new WeatherDrawerController(this);
        mController.requestWeatherInfo();

        mIvDrawerWeatherType = mRootView.findViewById(R.id.ivDrawerWeatherType);
        mTvDrawerWeatherWord = mRootView.findViewById(R.id.tvDrawerWeatherWord);
        mTvDrawerWeatherTemperature = mRootView.findViewById(R.id.tvDrawerWeatherTemperature);
        mTvDrawerWeatherCity = mRootView.findViewById(R.id.tvDrawerWeatherCity);
        ivDrawerWeatherTypeBG = mRootView.findViewById(R.id.ivDrawerWeatherTypeBG);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherUtil.goApp(getContext());
            }
        });
    }

    public void refreshDefault() {
        EasyLog.d(TAG, "refreshDefault");
        mController.requestWeatherInfo();
    }

    public void refreshData(WeatherInfo weatherInfo) {
//        EasyLog.d(TAG, "refreshData:" + weatherInfo);
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                WeatherTypeRes weatherTypeRes = WeatherUtil.parseType(weatherInfo.getWeather());
                ivDrawerWeatherTypeBG.setImageResource(weatherTypeRes.getDrawerBg());
                WeatherUtil.setWeatherDesc(mTvDrawerWeatherWord, weatherInfo.getWeather());
                mIvDrawerWeatherType.setImageResource(weatherTypeRes.getIcon());
                mTvDrawerWeatherCity.setText(weatherInfo.getCity());
                String temperatureDesc = WeatherUtil.fixTemperatureDesc(weatherInfo.getTemp(), mRootView.getResources());
                mTvDrawerWeatherTemperature.setText(temperatureDesc);
            }
        });
    }

    public Context getContext() {
        return mRootView.getContext();
    }
}
