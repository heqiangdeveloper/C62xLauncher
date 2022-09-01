package com.chinatsp.drawer.volcano;

import android.content.Context;
import android.view.View;

import com.chinatsp.volcano.videos.VolcanoVideo;
import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;

class VideoInfoAdapter extends BaseRcvAdapter<VolcanoVideo> {

    public VideoInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.drawer_item_volcano_video_item;
    }

    @Override
    protected BaseViewHolder<VolcanoVideo> createViewHolder(View view) {
        return new VideoItemViewHolder(view);
    }
}
