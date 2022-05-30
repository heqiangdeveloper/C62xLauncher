package com.chinatsp.weaher;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.weekday.DayWeatherBean;
import com.chinatsp.weaher.weekday.WeekDayAdapter;

import java.util.LinkedList;
import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.recyclerview.SimpleRcvDecoration;


public class WeatherCardView extends ConstraintLayout implements ICardStyleChange {

    public WeatherCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WeatherCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private int mSmallWidth;
    private int mLargeWidth;

    private RecyclerView mRcvCardWeatherWeek;
    private View mLargeCardView;
    private View mSmallCardView;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_weather, this);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
    }

    @Override
    public void expand() {
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_weather_large, this, false);
            mLargeCardView.setVisibility(View.GONE);
            initWeeklyWeather();
        }
        addView(mLargeCardView);
        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);
        changeWidth(mLargeWidth, this);

    }

    @Override
    public void collapse() {
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        changeWidth(mSmallWidth, this);
    }

    private void initWeeklyWeather() {
        mRcvCardWeatherWeek = mLargeCardView.findViewById(R.id.rcvCardWeatherWeek);
        WeekDayAdapter weekDayAdapter = new WeekDayAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()){
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
        mRcvCardWeatherWeek.setLayoutManager(layoutManager);
        weekDayAdapter.setDayWeatherList(createTest());
        mRcvCardWeatherWeek.setAdapter(weekDayAdapter);

    }

    private List<DayWeatherBean> createTest() {
        List<DayWeatherBean> testData = new LinkedList<>();
        testData.add(new DayWeatherBean("明天", 1, "多云", "13 ~ 22"+"℃"));
        testData.add(new DayWeatherBean("星期二", 1, "晴天", "21 ~ 31"+"℃"));
        testData.add(new DayWeatherBean("星期三", 1, "雷阵雨", "8 ~ 13"+"℃"));
        testData.add(new DayWeatherBean("星期四", 1, "多云", "16 ~ 25"+"℃"));
        testData.add(new DayWeatherBean("星期五", 1, "晴天", "12 ~ 22"+"℃"));
        testData.add(new DayWeatherBean("星期六", 1, "多云", "22 ~ 25"+"℃"));
        return testData;
    }

    private void changeWidth(int width, View... views) {
        for (View view : views) {
            view.getLayoutParams().width = width;
        }
    }

}
