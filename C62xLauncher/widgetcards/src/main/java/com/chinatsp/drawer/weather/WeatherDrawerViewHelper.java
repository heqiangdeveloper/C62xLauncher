package com.chinatsp.drawer.weather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.widgetcards.R;

public class WeatherDrawerViewHelper {
    private View mRootView;

    public WeatherDrawerViewHelper(View rootView) {
        mRootView = rootView.findViewById(R.id.layoutDrawerWeather);
        refreshUI();
    }

    private void refreshUI() {
//        WeatherViewModel mWeatherViewModel = WeatherViewModel.getInstance();
//        WeatherBean todayWeather = mWeatherViewModel.getTodayWeather();
        WeatherBean todayWeather = null;
        if (todayWeather == null) {
            return;
        }
        if (mRootView == null) {
            return;
        }
        ImageView ivDrawerWeatherType = mRootView.findViewById(R.id.ivDrawerWeatherType);
        TextView tvDrawerWeatherWord = mRootView.findViewById(R.id.tvDrawerWeatherWord);
        TextView tvDrawerWeatherTemperature = mRootView.findViewById(R.id.tvDrawerWeatherTemperature);
        TextView tvDrawerWeatherCity = mRootView.findViewById(R.id.tvDrawerWeatherCity);

        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(todayWeather.getType());
        mRootView.setBackgroundResource(weatherTypeRes.getDrawerBg());
        tvDrawerWeatherWord.setText(weatherTypeRes.getDesc());
        ivDrawerWeatherType.setImageResource(weatherTypeRes.getIcon());
        tvDrawerWeatherCity.setText(todayWeather.getCity());
        String temperatureDesc = WeatherUtil.fixTemperatureDesc(todayWeather.getTemperatureDesc(), mRootView.getResources());
        tvDrawerWeatherTemperature.setText(temperatureDesc);
    }
}
