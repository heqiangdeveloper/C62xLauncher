package com.chinatsp.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.navigation.view.IconTextLayout;


public class NaviCardLargeView extends ConstraintLayout {
    public NaviCardLargeView(@NonNull Context context) {
        super(context);
        init();
    }

    public NaviCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NaviCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NaviCardLargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private IconTextLayout mDistanceView;
    private IconTextLayout mNaviTimeView;
    private IconTextLayout mNaviUsedTimeView;


    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_navigation_large, this);
        initBottomNaviInfo();
    }

    private void initBottomNaviInfo() {
        mDistanceView = findViewById(R.id.layoutBottomNaviDistance);
        mNaviTimeView = findViewById(R.id.layoutBottomNaviTime);
        mNaviUsedTimeView = findViewById(R.id.layoutBottomNaviUsedTime);

        mDistanceView.setImageResource(R.drawable.card_common_icon_location);
        mNaviTimeView.setImageResource(R.drawable.card_common_icon_clock);
        mNaviUsedTimeView.setImageResource(R.drawable.card_common_icon_flag);

        mDistanceView.setText("20 km");
        mNaviTimeView.setText("00:30 h");
        mNaviUsedTimeView.setText("22:03");

    }

}
