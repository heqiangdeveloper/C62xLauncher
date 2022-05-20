package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.weaher.WeatherCardLargeView;
import com.chinatsp.weaher.WeatherCardView;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.adapter.BaseCardEntity;

public class WeatherCardEntity extends BaseCardEntity {

    @Override
    public View getLayout(Context context) {
        return new WeatherCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new WeatherCardLargeView(context);
    }
}
