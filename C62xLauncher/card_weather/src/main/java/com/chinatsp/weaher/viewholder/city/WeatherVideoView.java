package com.chinatsp.weaher.viewholder.city;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class WeatherVideoView extends VideoView {
    public WeatherVideoView(Context context) {
        super(context);
    }

    public WeatherVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeatherVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
