package com.chinatsp.volcano.videos;

import android.content.Context;
import android.view.View;


import com.chinatsp.volcano.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

public class VolcanoVideoAdapter extends BaseRcvAdapter<VolcanoVideo> {

    public VolcanoVideoAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_volcano_video;
    }

    @Override
    protected BaseViewHolder<VolcanoVideo> createViewHolder(View view) {
        return new VolcanoVideoViewHolder(view);
    }
}
