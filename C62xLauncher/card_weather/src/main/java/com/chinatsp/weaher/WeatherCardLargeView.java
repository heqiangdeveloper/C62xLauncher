package com.chinatsp.weaher;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.weaher.weekday.DayWeatherBean;
import com.chinatsp.weaher.weekday.WeekDayAdapter;

import java.util.LinkedList;
import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;


public class WeatherCardLargeView extends ConstraintLayout {
    private static final String TAG = "WeatherCardLargeView";

    public WeatherCardLargeView(@NonNull Context context) {
        super(context);
        init();
    }

    public WeatherCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WeatherCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private RecyclerView mRcvCardWeatherWeek;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_weather_large, this);
        findViews();
    }

    private void findViews() {
        Log.d(TAG, "findViews: ");
        mRcvCardWeatherWeek = findViewById(R.id.rcvCardWeatherWeek);
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


}
