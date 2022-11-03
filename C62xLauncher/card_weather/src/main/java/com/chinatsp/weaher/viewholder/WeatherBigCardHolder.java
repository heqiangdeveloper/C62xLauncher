package com.chinatsp.weaher.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherTypeRes;
import com.chinatsp.weaher.WeatherUtil;
import com.chinatsp.weaher.repository.WeatherBean;
import com.chinatsp.weaher.weekday.WeekDayAdapter;
import com.iflytek.autofly.weather.entity.WeatherInfo;

import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.recent.RecentAppHelper;

public class WeatherBigCardHolder extends WeatherCardHolder{
    private final Resources mResources;
    private RecyclerView mRcvCardWeatherWeek ;;
    private WeekDayAdapter mWeekDayAdapter;

    private TextView tvCardWeatherLocation;
    private TextView tvCardWeatherWord;
    private TextView tvCardWeatherAirValue;
    private TextView tvCardWeatherAirDesc;
    private TextView tvCardWeatherPmLabel2;
    private TextView tvCardWeatherTemperature;
    private TextView tvCardWeatherTemperatureRange;
    private ImageView ivCardWeatherIcon;
    private ImageView ivWeatherBg;

    public WeatherBigCardHolder(View rootView) {
        super(rootView);
        mResources = rootView.getResources();
        mRcvCardWeatherWeek = rootView.findViewById(R.id.rcvCardWeatherWeek);
        tvCardWeatherLocation = rootView.findViewById(R.id.tvCardWeatherLocation);
        tvCardWeatherWord = rootView.findViewById(R.id.tvCardWeatherWord);
        tvCardWeatherAirValue = rootView.findViewById(R.id.tvCardWeatherAirValue);
        tvCardWeatherAirDesc = rootView.findViewById(R.id.tvCardWeatherAirDesc);
        tvCardWeatherPmLabel2 = rootView.findViewById(R.id.tvCardWeatherPmLabel2);
        tvCardWeatherTemperature = rootView.findViewById(R.id.tvCardWeatherTemperature);
        tvCardWeatherTemperatureRange = rootView.findViewById(R.id.tvCardWeatherTemperatureRange);
        ivCardWeatherIcon = rootView.findViewById(R.id.ivCardWeatherIcon);
        ivWeatherBg = rootView.findViewById(R.id.ivWeatherBg);
        initWeeklyWeather();
    }


    private void initWeeklyWeather() {
        Context context = mRootView.getContext();
        mWeekDayAdapter = new WeekDayAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        if (mRcvCardWeatherWeek.getItemDecorationCount() == 0) {
            SimpleRcvDecoration decoration = new SimpleRcvDecoration(44, layoutManager);
            mRcvCardWeatherWeek.addItemDecoration(decoration);
        }
        //点击RecyclerView空白区域，跳转至天气
        mRcvCardWeatherWeek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId() != 0){
                    RecentAppHelper.launchApp(v.getContext(),"com.iflytek.autofly.weather");
                }
                return false;
            }
        });
        mRcvCardWeatherWeek.setLayoutManager(layoutManager);
        mRcvCardWeatherWeek.setAdapter(mWeekDayAdapter);
    }

    @Override
    public void updateDefault() {

    }

    @Override
    public void updateWeather(WeatherInfo weatherInfo) {
        tvCardWeatherLocation.setText(weatherInfo.getCity());
        tvCardWeatherWord.setText(weatherInfo.getWeather());
        tvCardWeatherAirValue.setText(weatherInfo.getAirData());
        tvCardWeatherAirDesc.setText("空气质量 "+weatherInfo.getAirQuality());
        tvCardWeatherTemperature.setText(weatherInfo.getTemp());
        tvCardWeatherTemperatureRange.setText(WeatherUtil.getTemperatureRange(weatherInfo, mResources));
        WeatherTypeRes weatherTypeRes = WeatherUtil.parseType(weatherInfo.getWeather());
        ivCardWeatherIcon.setImageResource(weatherTypeRes.getIcon());
        ivWeatherBg.setImageResource(weatherTypeRes.getBigCardBg());

    }
    public void updateWeatherList(List<WeatherInfo> weatherInfoList) {
        WeatherUtil.logI("WeatherBigCardHolder updateWeatherList : "+weatherInfoList);
        mWeekDayAdapter.setDayWeatherList(weatherInfoList);
        if (weatherInfoList == null || weatherInfoList.isEmpty()) {
            return;
        }
        updateWeather(weatherInfoList.get(0));
    }

}
