package com.chinatsp.volcano;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class VolcanoCardLargeView extends ConstraintLayout {
    private static final String TAG = "WeatherCardLargeView";

    public VolcanoCardLargeView(@NonNull Context context) {
        super(context);
        init();
    }

    public VolcanoCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolcanoCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VolcanoCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private RecyclerView mRcvCardWeatherWeek;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_volcano_large, this);
    }
}
