package com.chinatsp.weaher.viewholder;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.type.C62WeatherType;
import com.chinatsp.weaher.type.C62WeatherTypeAdapter;
import com.chinatsp.weaher.type.MoJiWeatherType;
import com.chinatsp.weaher.type.WeatherTypeAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

public class WeatherSmallCardHolder extends WeatherCardHolder{


    private final TextView tvCardWeatherCity;
    private final TextView tvCardWeatherTemperature;
    private final TextView tvCardWeatherDate;
    private final ImageView ivCardWeatherIcon;
    private final ImageView ivWeatherBg;

    private ViewPager viewPager;

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
        tvCardWeatherDate.setText(WeatherUtil.getToday());
        WeatherTypeRes weatherTypeRes = new WeatherTypeRes(WeatherBean.TYPE_UNKNOWN);
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateWeather(WeatherInfo weatherInfo) {
        WeatherUtil.logD("WeatherSmallCardHolder updateWeather weatherInfo : "+weatherInfo);
        if (weatherInfo == null) {
            return;
        }
        Resources resources = mRootView.getResources();
        tvCardWeatherCity.setText(weatherInfo.getCity());
        tvCardWeatherTemperature.setText(WeatherUtil.getTemperatureRange(weatherInfo, resources));
        tvCardWeatherDate.setText(WeatherUtil.getToday());

        WeatherTypeRes weatherTypeRes = WeatherUtil.parseType(weatherInfo.getWeather());
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setVisibility(View.VISIBLE);
        ivWeatherBg.setImageResource(weatherTypeRes.getSmallCardBg());
    }
}
