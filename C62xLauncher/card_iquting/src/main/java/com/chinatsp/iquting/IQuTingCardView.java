package com.chinatsp.douyin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class DouyinCardView extends ConstraintLayout {
    private static final String TAG = "WeatherCardLargeView";

    public DouyinCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public DouyinCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DouyinCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DouyinCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_douyin, this);
    }
}
