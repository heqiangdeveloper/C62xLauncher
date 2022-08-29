package com.chinatsp.weaher.viewholder;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherBean;
import com.iflytek.autofly.weather.entity.WeatherInfo;

public class WeatherSmallCardHolder extends WeatherCardHolder{


    private final TextView tvCardWeatherCity;
    private final TextView tvCardWeatherTemperature;
    private final TextView tvCardWeatherDate;
    private final ImageView ivCardWeatherIcon;
    private final ImageView ivWeatherBg;

    public WeatherSmallCardHolder(View rootView) {
        super(rootView);
        tvCardWeatherCity = rootView.findViewById(R.id.tvCardWeatherCity);
        tvCardWeatherTemperature = rootView.findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherDate = rootView.findViewById(R.id.tvCardWeatherDate);
        ivCardWeatherIcon = rootView.findViewById(R.id.ivCardWeatherIcon);
        ivWeatherBg = rootView.findViewById(R.id.ivWeatherBg);
    }

    @Override
    public void updateDefault() {

    }

    @Override
    public void updateWeather(WeatherInfo weatherInfo) {
        WeatherUtil.logD("updateWeather weatherInfo");
        Resources resources = mRootView.getResources();
        tvCardWeatherCity.setText(weatherInfo.getCity());
        tvCardWeatherTemperature.setText(WeatherUtil.getTemperatureRange(weatherInfo, resources));
        tvCardWeatherDate.setText(WeatherUtil.getToday());

        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(getWeatherType(weatherInfo.getWeatherType()));
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setImageResource(weatherTypeRes.getSmallCardBg());
    }

    private int getWeatherType(String type) {
        try {
            return Integer.parseInt(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WeatherBean.TYPE_UNKNOWN;
    }
}
