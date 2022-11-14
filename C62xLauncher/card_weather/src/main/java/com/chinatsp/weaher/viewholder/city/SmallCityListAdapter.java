package com.chinatsp.weaher.viewholder.city;

import android.content.Context;
import android.view.View;

import com.chinatsp.weaher.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class SmallCityListAdapter extends BaseRcvAdapter<String> {
    public SmallCityListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.card_weather_small_city;
    }

    @Override
    protected BaseViewHolder<String> createViewHolder(View view) {
        return new SmallCityItemViewHolder(view);
    }
}
