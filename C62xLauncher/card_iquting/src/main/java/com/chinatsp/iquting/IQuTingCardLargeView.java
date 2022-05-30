package com.chinatsp.iquting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class IQuTingCardLargeView extends ConstraintLayout {
    private static final String TAG = "WeatherCardLargeView";

    public IQuTingCardLargeView(@NonNull Context context) {
        super(context);
        init();
    }

    public IQuTingCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IQuTingCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IQuTingCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private RecyclerView mRcvCardWeatherWeek;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting_large, this);
    }
}
