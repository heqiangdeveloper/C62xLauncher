package com.chinatsp.navigation.viewholder.lane;

import android.content.Context;
import android.view.View;

import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class LaneListAdapter extends BaseRcvAdapter<TrafficLaneModel.LaneInfo> {
    public LaneListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_layout_tbt_lane;
    }

    @Override
    protected BaseViewHolder<TrafficLaneModel.LaneInfo> createViewHolder(View view) {
        return new LaneViewHolder(view);
    }
}
