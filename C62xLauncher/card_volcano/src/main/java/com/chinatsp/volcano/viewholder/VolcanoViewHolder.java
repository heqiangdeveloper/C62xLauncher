package com.chinatsp.volcano.viewholder;

import android.view.View;

import com.chinatsp.volcano.api.response.VideoListData;

public abstract class VolcanoViewHolder {
    protected View mRootView;

    public VolcanoViewHolder(View rootView) {
        mRootView = rootView;
    }

    public abstract void showNormal();


    public abstract void updateList(VideoListData videoListData);

    public abstract void init();

    public abstract void onChangeSource(String source);

    public abstract void showLoadingView();

    public abstract void hideLoadingView();

    public abstract void showNetworkError();

    public abstract void hideNetworkError();

    public abstract void showDataError();
}
