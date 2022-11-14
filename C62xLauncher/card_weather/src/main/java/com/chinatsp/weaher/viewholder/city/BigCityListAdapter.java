package com.chinatsp.weaher.viewholder.city;

import android.content.Context;
import android.view.View;

import com.chinatsp.weaher.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class BigCityListAdapter extends BaseRcvAdapter<String> {
    public BigCityListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.card_weather_large_city;
    }

    @Override
    protected BaseViewHolder<String> createViewHolder(View view) {
        return new BigCityItemViewHolder(view);
    }
}
