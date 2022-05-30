package com.chinatsp.douyin;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.LinkedList;

import card.base.recyclerview.SimpleRcvDecoration;


public class DouyinCardLargeView extends ConstraintLayout {
    private static final String TAG = "WeatherCardLargeView";

    public DouyinCardLargeView(@NonNull Context context) {
        super(context);
        init();
    }

    public DouyinCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DouyinCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DouyinCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private RecyclerView mRcvCardWeatherWeek;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_douyin_large, this);
    }
}
